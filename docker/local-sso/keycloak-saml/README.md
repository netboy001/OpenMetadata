# Keycloak SAML Fixture

Local SAML IdP fixture for the Playwright SSO login spec.

```bash
docker compose -f docker/local-sso/keycloak-saml/docker-compose.yml up -d
```

It imports one realm for an UMetadata server running at `http://localhost:8585`:

- `om-azure-saml`
  - User: `azure.saml@umetadata.local`
  - Password: `UMetadata@123`

Use the matching Playwright provider type:

```bash
SSO_PROVIDER_TYPE=keycloak-azure-saml \
SSO_USERNAME=azure.saml@umetadata.local \
SSO_PASSWORD=UMetadata@123 \
npx playwright test playwright/e2e/Auth/SSOLogin.spec.ts --project=sso-auth --workers=1
```
