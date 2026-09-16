package org.umetadata.service.search;

import lombok.Getter;

@Getter
public class SearchHealthStatus {
  public SearchHealthStatus(String status) {
    this.status = status;
  }

  String status;
}
