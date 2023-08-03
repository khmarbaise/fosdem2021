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

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;
import java.util.logging.Logger;

import static io.soebes.jupiter.extension.e2e.Directories.getTargetDir;
import static org.awaitility.Awaitility.await;

/**
 * @author Karl Heinz Marbaise
 */
class E2EExtension implements BeforeAllCallback,
    BeforeEachCallback, AfterEachCallback, ParameterResolver {

  private static final Logger LOGGER = Logger.getLogger(E2EExtension.class.getName());

  private static final String THE_APPLICATION = "dependency/application-exec.jar";

  private static final Duration APPLICATION_STARTUP_TIMEOUT = Duration.ofSeconds(60);

  private static final Namespace E2E_NAMESPACE = Namespace.create(E2EExtension.class.getName());

  private ExtensionContext.Store getContext(ExtensionContext context) {
    return context.getStore(E2E_NAMESPACE);
  }

  private void store(ExtensionContext context, StoragePrefix prefix, Object clazz) {
    getContext(context).put(prefix + context.getUniqueId(), clazz);
  }

  private <V> V get(ExtensionContext context, StoragePrefix prefix, Class<V> clazz) {
    return getContext(context).get(prefix + context.getUniqueId(), clazz);
  }

  private void storeClass(ExtensionContext context, Object clazz) {
    getContext(context).put(StoragePrefix.TEST_CLASS_DIRECTORY + context.getRequiredTestClass().getCanonicalName(), clazz);
  }

  private File getClass(ExtensionContext context) {
    return getContext(context).get(StoragePrefix.TEST_CLASS_DIRECTORY + context.getRequiredTestClass().getCanonicalName(), File.class);
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    String resultingPath = context.getRequiredTestClass().getCanonicalName().replace('.', '/');
    File e2eBaseDirectory = new File(getTargetDir(), "e2e/" + resultingPath);
    LOGGER.info("beforeAll: Name: " + context.getUniqueId());
    LOGGER.info("beforeAll: resultingPath: " + e2eBaseDirectory);

    e2eBaseDirectory.mkdirs();
    storeClass(context, e2eBaseDirectory);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    LOGGER.info("beforeEach: Name: " + context.getUniqueId());

    DatabaseContainer databaseContainer = new DatabaseContainer();
    databaseContainer.start();

    store(context, StoragePrefix.DATABASE_CONTAINER, databaseContainer);

    File testClassBaseDirectory = getClass(context);

    Method requiredTestMethod = context.getRequiredTestMethod();
    File testCaseBaseDirectory = new File(testClassBaseDirectory, requiredTestMethod.getName());
    testCaseBaseDirectory.mkdirs();

    ApplicationPorts ports = new ApplicationPorts();
    List<String> configurationArguments = List.of(
        "--spring.datasource.url=" + databaseContainer.dataSourceUrl(),
        "--spring.datasource.username=" + databaseContainer.getUsername(),
        "--spring.datasource.password=" + databaseContainer.getPassword(),
        "--spring.hibernate.dialect=" + databaseContainer.getHibernateDialect(),
        "--server.port=" + ports.getApplicationPort(),
        "--management.server.port=" + ports.getActuatorPort()
    );
    ApplicationExecutor executor = new ApplicationExecutor(testCaseBaseDirectory, new File(getTargetDir(), THE_APPLICATION));

    store(context, StoragePrefix.APPLICATION_PORTS, ports);
    Process applicationProcess = executor.start(configurationArguments);
    store(context, StoragePrefix.APPLICATION_PROCESS, applicationProcess);

    Application application = new Application(ports.getActuatorPort());

    //Identify that the application has been started
    await()
        .atMost(APPLICATION_STARTUP_TIMEOUT)
        .until(application::isReady);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    LOGGER.info("afterEach: Name: " + context.getUniqueId());

    DatabaseContainer databaseContainer = get(context, StoragePrefix.DATABASE_CONTAINER, DatabaseContainer.class);
    databaseContainer.stop();

    Process applicationProcess = get(context, StoragePrefix.APPLICATION_PROCESS, Process.class);
    //Send signal to shutdown the application.
    applicationProcess.destroy();
    //Wait until the application has been shut down.
    applicationProcess.onExit().get();

    getContext(context).remove(StoragePrefix.TEST_CLASS_DIRECTORY);
    getContext(context).remove(StoragePrefix.APPLICATION_PROCESS);
    getContext(context).remove(StoragePrefix.APPLICATION_PORTS);
    getContext(context).remove(StoragePrefix.DATABASE_CONTAINER);
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    if (extensionContext.getElement().isEmpty()) {
      return false;
    }

    Class<?> type = parameterContext.getParameter().getType();
    if (type == ApplicationPorts.class || type == DatabaseContainer.class) {
      return true;
    }
    return false;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    Class<?> type = parameterContext.getParameter().getType();
    if (type == ApplicationPorts.class) {
      return get(extensionContext, StoragePrefix.APPLICATION_PORTS, ApplicationPorts.class);
    }
    if (type == DatabaseContainer.class) {
      return get(extensionContext, StoragePrefix.DATABASE_CONTAINER, DatabaseContainer.class);
    }
    return null;
  }

  private enum StoragePrefix {
    DATABASE_CONTAINER,
    APPLICATION_PROCESS,
    APPLICATION_PORTS,
    TEST_CLASS_DIRECTORY,
  }

}
