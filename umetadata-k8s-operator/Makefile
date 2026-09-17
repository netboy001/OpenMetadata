# UMetadata Kubernetes Operator Makefile

# Variables
IMAGE_NAME ?= docker.wondersgroup.com/umetadata/omjob-operator
VERSION ?= 1.12.0-SNAPSHOT
JAR_FILE = target/umetadata-k8s-operator-$(VERSION)-boot.jar

# Default target
.PHONY: help
help:
	@echo "Available targets:"
	@echo "  compile     - Compile the operator"
	@echo "  test        - Run unit tests"
	@echo "  package     - Build fat JAR"
	@echo "  docker      - Build Docker image"
	@echo "  push        - Push Docker image to registry"
	@echo "  clean       - Clean build artifacts"
	@echo "  run-local   - Run operator locally"

# Build targets
.PHONY: compile
compile:
	cd .. && mvn compile -pl umetadata-k8s-operator

.PHONY: test
test:
	cd .. && mvn test -pl umetadata-k8s-operator

.PHONY: package
package:
	cd .. && mvn package -pl umetadata-k8s-operator

# Docker targets
.PHONY: docker
docker: package
	@echo "Building Docker image: $(IMAGE_NAME):$(VERSION)"
	docker build -t $(IMAGE_NAME):$(VERSION) .
	docker tag $(IMAGE_NAME):$(VERSION) $(IMAGE_NAME):latest

.PHONY: push
push:
	@echo "Pushing Docker image: $(IMAGE_NAME):$(VERSION)"
	docker push $(IMAGE_NAME):$(VERSION)
	docker push $(IMAGE_NAME):latest

# Development targets
.PHONY: run-local
run-local: package
	@echo "Running operator locally..."
	java -jar $(JAR_FILE)

.PHONY: clean
clean:
	cd .. && mvn clean -pl umetadata-k8s-operator
	docker rmi $(IMAGE_NAME):$(VERSION) $(IMAGE_NAME):latest 2>/dev/null || true

# Integration targets
.PHONY: verify
verify: test
	@echo "Running additional verification..."
	cd .. && mvn verify -pl umetadata-k8s-operator

# Development convenience
.PHONY: dev
dev: clean compile test
	@echo "Development build complete"