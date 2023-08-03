/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package io.soebes.jupiter.extension.e2e;


import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.time.Duration;

class Application {

  private int port;

  public Application(int port) {
    this.port = port;
  }

  /**
   * Will check if the application is available (healthy).
   * @return {@code true}, in case the application is available, {@code false} otherwise.
   */
  boolean isReady() {
    RestTemplate restTemplateBuilder = new RestTemplateBuilder()
        .setReadTimeout(Duration.ofMillis(100))
        .build();

    try {
      ResponseEntity<String> forEntity = restTemplateBuilder.getForEntity(URI.create("http://localhost:" + port + "/actuator/health"), String.class);
      return forEntity.getStatusCode() == HttpStatus.OK;
    } catch (RestClientException e) {
      return false;
    }
  }

}
