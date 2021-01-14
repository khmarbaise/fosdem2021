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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Karl Heinz Marbaise
 */
class ApplicationExecutor {

  private static final Logger LOGGER = Logger.getLogger(ApplicationExecutor.class.getName());

  private final File targetDirectory;

  private final File applicationExecutable;

  private final List<String> jvmArguments;

  ApplicationExecutor(File targetDirectory, File applicationExecutable) {
    this.targetDirectory = targetDirectory;
    this.applicationExecutable = applicationExecutable;
    this.jvmArguments = List.of(
        "-Xlog:gc*=debug:file=gc.log:utctime,uptime,tid,level:filecount=3,filesize=16m",
        "-XX:+HeapDumpOnOutOfMemoryError", "-XX:HeapDumpPath=heapdump.hprof"
    );
  }

  Process start(List<String> startArguments) throws IOException {

    String javaHome = System.getProperty("java.home");
    String javaBin = javaHome + "/bin/java";

    failIfApplicationDoesNotExist();

    List<String> applicationArguments = new ArrayList<>();

    applicationArguments.add(javaBin);
    applicationArguments.addAll(jvmArguments);
    applicationArguments.add("-jar");
    applicationArguments.add(applicationExecutable.getAbsolutePath());
    applicationArguments.addAll(startArguments);
    applicationArguments.forEach(s -> LOGGER.info("args = " + s));

    ProcessBuilder pb = new ProcessBuilder(applicationArguments);
    pb.redirectError(new File(targetDirectory, "application-stderr.log"));
    pb.redirectOutput(new File(targetDirectory, "application-stdout.log"));
    pb.directory(targetDirectory);
    return pb.start();
  }

  private void failIfApplicationDoesNotExist() {
    if (!this.applicationExecutable.exists()) {
      throw new IllegalStateException("The " + applicationExecutable + " does not exist.");
    }
  }

}
