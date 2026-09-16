package org.umetadata.sdk.network;

import java.util.concurrent.CompletableFuture;
import org.umetadata.sdk.exceptions.UMetadataException;

public interface HttpClient {
  <T> T execute(HttpMethod method, String path, Object requestBody, Class<T> responseClass)
      throws UMetadataException;

  <T> T execute(
      HttpMethod method,
      String path,
      Object requestBody,
      Class<T> responseClass,
      RequestOptions options)
      throws UMetadataException;

  <T> CompletableFuture<T> executeAsync(
      HttpMethod method, String path, Object requestBody, Class<T> responseClass);

  <T> CompletableFuture<T> executeAsync(
      HttpMethod method,
      String path,
      Object requestBody,
      Class<T> responseClass,
      RequestOptions options);

  String executeForString(HttpMethod method, String path, Object requestBody)
      throws UMetadataException;

  String executeForString(
      HttpMethod method, String path, Object requestBody, RequestOptions options)
      throws UMetadataException;

  CompletableFuture<String> executeForStringAsync(
      HttpMethod method, String path, Object requestBody);

  CompletableFuture<String> executeForStringAsync(
      HttpMethod method, String path, Object requestBody, RequestOptions options);
}
