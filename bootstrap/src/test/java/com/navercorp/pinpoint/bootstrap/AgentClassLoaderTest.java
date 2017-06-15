/*
 * Copyright 2014 NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.pinpoint.bootstrap;


import com.navercorp.pinpoint.bootstrap.config.DefaultProfilerConfig;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;

import static org.mockito.Mockito.mock;

/**
 * @author emeroad
 */
public class AgentClassLoaderTest {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Test
    public void boot() {
        AgentClassLoader agentClassLoader = new AgentClassLoader(new URL[0]);
        agentClassLoader.setBootClass("com.navercorp.pinpoint.bootstrap.DummyAgent");
        Instrumentation instrumentation = mock(Instrumentation.class);
        AgentOption option = new DefaultAgentOption(instrumentation, "testCaseAgent", "testCaseAppName", new DefaultProfilerConfig(), Collections.<String>emptyList(), null);
        Agent boot = agentClassLoader.boot(option);

        boot.stop();
    }

    private String getProjectLibDir() {
        // not really necessary, but useful for testing protectionDomain
        ProtectionDomain protectionDomain = AgentClassLoader.class.getProtectionDomain();
        CodeSource codeSource = protectionDomain.getCodeSource();
        URL location = codeSource.getLocation();

        logger.debug("lib location:{}", location);
        String path = location.getPath();
        // file:/D:/nhn_source/pinpoint_project/pinpoint-tomcat-profiler/target/classes/
        int dirPath = path.lastIndexOf("target/classes/");
        if (dirPath == -1) {
            throw new RuntimeException("target/classes/ not found");
        }
        String projectDir = path.substring(1, dirPath);
        return projectDir + "src/test/lib";
    }
}