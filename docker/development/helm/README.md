# UMetadata Helm Chart Local Testing

Helper to test changes from https://10.1.98.30:5080/umd/umetadata/umetadata-helm-charts with local images while developing.

## Prerequisites

### Required Tools
```bash
# Install required tools
brew install helm kubectl minikube docker-compose

# Or on Ubuntu/Debian:
# sudo snap install helm kubectl minikube
# sudo apt-get install docker-compose
```

### Required Resources
- **CPU**: 4+ cores recommended
- **Memory**: 8GB+ RAM recommended  
- **Storage**: 20GB+ free space

## Quick Start

### 1. Start Local Dependencies

First, start the required database and search services:

```bash
cd docker/development/helm

# Start PostgreSQL and OpenSearch
docker-compose -f docker-compose-deps.yml up -d

# Wait for services to be ready (2-3 minutes)
docker-compose -f docker-compose-deps.yml logs -f

# Verify services are running
curl http://localhost:9200/_cluster/health
docker exec umetadata_postgres_test psql -U umetadata_user -d umetadata_db -c "SELECT 1"
```

### 2. Start Kubernetes Cluster

```bash
# Start minikube with sufficient resources
minikube start --cpus 4 --memory 8192 --driver docker

# Enable ingress addon (optional)
minikube addons enable ingress

# Verify cluster is ready
kubectl cluster-info
kubectl get nodes
```

### 3. Create Required Secrets

```bash
# Create secrets that the chart expects
kubectl create secret generic postgres-secrets \
  --from-literal=umetadata-postgres-password=umetadata_password

kubectl create secret generic airflow-secrets \
  --from-literal=umetadata-airflow-password=admin
```

## Test Scenarios

### Scenario A: Test Kubernetes Native Pipeline Client (NEW)

Test the new K8s native pipeline execution without Airflow dependencies.

```bash
# Use local chart directly, e.g.,
CHART_PATH="/Users/pmbrull/github/umetadata-helm-charts/charts/umetadata"

# Install with K8s native configuration
helm install umetadata-k8s-test $CHART_PATH --values values-k8s-test.yaml

# Check deployment status
kubectl get pods -A
kubectl get jobs -n umetadata-pipelines-test
kubectl get serviceaccounts,roles,rolebindings -n umetadata-pipelines-test

# Check logs
kubectl logs -l app.kubernetes.io/name=umetadata -f

# Test access
minikube tunnel  # In separate terminal
curl http://localhost:8585/api/v1/system/health
```

### Scenario B: Test Migrated Airflow Configuration

Test that the new nested Airflow configuration still works.

```bash
# Install with migrated Airflow configuration  
helm install umetadata-airflow-test $CHART_PATH \
  --values values-airflow-test.yaml \
  --timeout 10m \
  --wait

# Check deployment
kubectl get pods -A
kubectl logs -l app.kubernetes.io/name=umetadata -f
```

## Validation Checklist

### ✅ Basic Functionality

1. **Pod Health**
```bash
# Check all pods are running
kubectl get pods -A

# Check UMetadata pod logs
kubectl logs deployment/umetadata-k8s-test -f

# Check database connectivity
kubectl exec deployment/umetadata-k8s-test -- curl -f http://postgres:5432 || echo "DB connection test"
```

2. **API Health**
```bash
# Access health endpoint
curl http://localhost:8585/api/v1/system/health

# Check API response
curl http://localhost:8585/api/v1/system/config
```

### ✅ K8s Native Pipeline Features

1. **RBAC Resources**
```bash
# Check namespace creation
kubectl get namespace umetadata-pipelines-test

# Check service account
kubectl get serviceaccount -n umetadata-pipelines-test umetadata-ingestion-test

# Check permissions
kubectl auth can-i create jobs \
  --as=system:serviceaccount:umetadata-pipelines-test:umetadata-ingestion-test \
  -n umetadata-pipelines-test
```

2. **Configuration Validation**
```bash
# Check environment variables in pod
kubectl exec deployment/umetadata-k8s-test -- env | grep K8S_

# Check secrets
kubectl get secret umetadata-k8s-test-pipeline-secret -o yaml
kubectl get secret umetadata-k8s-test-pipeline-secret -o jsonpath='{.data}' | base64 -d
```

3. **Pipeline Job Testing**
```bash
# Create a test pipeline job (manual)
cat <<EOF | kubectl apply -f -
apiVersion: batch/v1
kind: Job
metadata:
  name: test-ingestion-job
  namespace: umetadata-pipelines-test
spec:
  template:
    spec:
      serviceAccountName: umetadata-ingestion-test
      containers:
      - name: test
        image: docker.wondersgroup.com/umetadata/ingestion:latest
        command: ["echo", "Test pipeline job works"]
      restartPolicy: Never
EOF

# Check job execution
kubectl get jobs -n umetadata-pipelines-test
kubectl logs job/test-ingestion-job -n umetadata-pipelines-test
```

### ✅ Failure Diagnostics Testing

1. **Create Failing Job**
```bash
# Create a job that will fail
cat <<EOF | kubectl apply -f -
apiVersion: batch/v1
kind: Job  
metadata:
  name: test-failing-job
  namespace: umetadata-pipelines-test
  labels:
    app.kubernetes.io/pipeline: test-pipeline
    app.kubernetes.io/run-id: test-123
spec:
  template:
    spec:
      serviceAccountName: umetadata-ingestion-test
      containers:
      - name: main
        image: docker.wondersgroup.com/umetadata/ingestion:latest
        command: ["sh", "-c", "echo 'Starting ingestion...'; sleep 10; echo 'Something went wrong!'; exit 1"]
      restartPolicy: Never
EOF

# Watch for diagnostic job creation
kubectl get jobs -n umetadata-pipelines-test -w

# Check diagnostic job logs
kubectl logs -n umetadata-pipelines-test -l app.kubernetes.io/component=diagnostics
```

### ✅ Configuration Migration Testing

1. **Test Both Configurations**
```bash
# Test that both airflow and k8s configs are valid
helm template test-airflow $CHART_PATH --values values-airflow-test.yaml > /tmp/airflow-manifest.yaml
helm template test-k8s $CHART_PATH --values values-k8s-test.yaml > /tmp/k8s-manifest.yaml

# Validate manifests
kubectl apply --dry-run=client -f /tmp/airflow-manifest.yaml
kubectl apply --dry-run=client -f /tmp/k8s-manifest.yaml
```

2. **Test Breaking Changes**
```bash
# Test old configuration format (should fail validation or show warnings)
cat > /tmp/old-values.yaml << EOF
umetadata:
  config:
    pipelineServiceClientConfig:
      enabled: true
      className: "org.umetadata.service.clients.pipeline.airflow.AirflowRESTClient"
      apiEndpoint: http://test
EOF

helm template test-old $CHART_PATH --values /tmp/old-values.yaml
```

### Debug Commands

```bash
# Get all resources
kubectl get all -A

# Describe UMetadata deployment
kubectl describe deployment umetadata-k8s-test

# Get events
kubectl get events --sort-by='.lastTimestamp' -A

# Check helm release
helm list
helm status umetadata-k8s-test
helm get values umetadata-k8s-test
```

## Cleanup

### Remove Test Deployments
```bash
# Remove helm releases
helm uninstall umetadata-k8s-test
helm uninstall umetadata-airflow-test

# Clean up namespaces
kubectl delete namespace umetadata-pipelines-test

# Clean up secrets
kubectl delete secret postgres-secrets airflow-secrets
```

## Expected Results

### Successful Deployment Indicators

1. ✅ **All pods running**: `kubectl get pods -A` shows all pods in `Running` state
2. ✅ **Health check passing**: `curl http://localhost:8585/api/v1/system/health` returns 200
3. ✅ **RBAC created**: K8s resources created in `umetadata-pipelines-test` namespace
4. ✅ **Configuration loaded**: Environment variables properly set in pods
5. ✅ **No errors in logs**: `kubectl logs deployment/umetadata-k8s-test` shows successful startup

### Performance Benchmarks

- **Startup time**: < 5 minutes for full deployment
- **Memory usage**: < 4GB total (including dependencies)
- **CPU usage**: < 2 cores under normal load
- **Job creation**: < 30 seconds for pipeline job creation and execution

This comprehensive testing suite validates both the new K8s native functionality and ensures backward compatibility with existing Airflow configurations.
