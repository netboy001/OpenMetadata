package org.umetadata.sdk.services.dataassets;

import org.umetadata.schema.api.data.CreateMlModel;
import org.umetadata.schema.entity.data.MlModel;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpClient;
import org.umetadata.sdk.network.HttpMethod;
import org.umetadata.sdk.services.EntityServiceBase;

public class MlModelService extends EntityServiceBase<MlModel> {
  public MlModelService(HttpClient httpClient) {
    super(httpClient, "/v1/mlmodels");
  }

  @Override
  protected Class<MlModel> getEntityClass() {
    return MlModel.class;
  }

  // Create mlmodel using CreateMlModel request
  public MlModel create(CreateMlModel request) throws UMetadataException {
    return httpClient.execute(HttpMethod.POST, basePath, request, MlModel.class);
  }
}
