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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serial;
import java.net.ServerSocket;

/**
 * @author Karl Heinz Marbaise
 */
public class ApplicationPorts {

  private static final Logger LOG = LoggerFactory.getLogger(ApplicationPorts.class);

  private final int applicationPort;
  private final int actuatorPort;

  public ApplicationPorts() {
      this.applicationPort = reserveApplicationPort();
      this.actuatorPort = reserveActuatorPort();
  }

  public int getApplicationPort() {
    return this.applicationPort;
  }
  public int getActuatorPort() {
    return this.actuatorPort;
  }
  private int reserveApplicationPort() {
    try (var serverSocket = new ServerSocket(0)) {
      var localPort = serverSocket.getLocalPort();
      LOG.info("ApplicationPort: {}", localPort);
      return localPort;
    } catch (IOException e) {
      throw new ApplicationPortException(e);
    }
  }

  private int reserveActuatorPort() {
    try (var serverSocket = new ServerSocket(0)) {
      var localPort = serverSocket.getLocalPort();
      LOG.info("ActuatorPort: {}", localPort);
      return localPort;
    } catch (IOException e) {
      throw new ApplicationPortException(e);
    }
  }

  static class ApplicationPortException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public ApplicationPortException() {
    }

    public ApplicationPortException(String message) {
      super(message);
    }

    public ApplicationPortException(String message, Throwable cause) {
      super(message, cause);
    }

    public ApplicationPortException(Throwable cause) {
      super(cause);
    }

    public ApplicationPortException(String message, Throwable cause, boolean enableSuppression,
                                    boolean writableStackTrace) {
      super(message, cause, enableSuppression, writableStackTrace);
    }
  }
}