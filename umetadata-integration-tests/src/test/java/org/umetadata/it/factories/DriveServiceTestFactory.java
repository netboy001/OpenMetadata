package org.umetadata.it.factories;

import java.util.UUID;
import org.umetadata.it.util.SdkClients;
import org.umetadata.it.util.TestNamespace;
import org.umetadata.schema.api.services.CreateDriveService;
import org.umetadata.schema.api.services.CreateDriveService.DriveServiceType;
import org.umetadata.schema.entity.services.DriveService;
import org.umetadata.schema.services.connections.drive.GoogleDriveConnection;
import org.umetadata.schema.type.DriveConnection;
import org.umetadata.sdk.exceptions.UMetadataException;
import org.umetadata.sdk.network.HttpMethod;

public class DriveServiceTestFactory {

  public static DriveService createGoogleDrive(TestNamespace ns) {
    String uniqueId = UUID.randomUUID().toString().substring(0, 8);
    return createGoogleDrive(ns, "googleDrive_" + uniqueId);
  }

  public static DriveService createGoogleDrive(TestNamespace ns, String suffix) {
    String name = ns.prefix(suffix);

    GoogleDriveConnection googleConn = new GoogleDriveConnection();

    DriveConnection conn = new DriveConnection().withConfig(googleConn);

    CreateDriveService request =
        new CreateDriveService()
            .withName(name)
            .withServiceType(DriveServiceType.GoogleDrive)
            .withConnection(conn)
            .withDescription("Test GoogleDrive service");

    try {
      return SdkClients.adminClient()
          .getHttpClient()
          .execute(HttpMethod.POST, "/v1/services/driveServices", request, DriveService.class);
    } catch (UMetadataException e) {
      throw new RuntimeException("Failed to create GoogleDrive service", e);
    }
  }

  public static DriveService getById(String id) {
    try {
      return SdkClients.adminClient()
          .getHttpClient()
          .execute(HttpMethod.GET, "/v1/services/driveServices/" + id, null, DriveService.class);
    } catch (UMetadataException e) {
      throw new RuntimeException("Failed to get DriveService by id: " + id, e);
    }
  }
}
