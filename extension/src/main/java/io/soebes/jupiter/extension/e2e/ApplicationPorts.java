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

import org.springframework.util.SocketUtils;

import java.util.SortedSet;

/**
 * @author Karl Heinz Marbaise
 */
public class ApplicationPorts {

  private final int applicationPort;

  private final int actuatorPort;

  ApplicationPorts() {
    SortedSet<Integer> availableTcpPorts = SocketUtils.findAvailableTcpPorts(2);
    this.applicationPort = availableTcpPorts.first();
    this.actuatorPort = availableTcpPorts.last();
  }

  public int getApplicationPort() {
    return applicationPort;
  }

  public int getActuatorPort() {
    return actuatorPort;
  }
}
