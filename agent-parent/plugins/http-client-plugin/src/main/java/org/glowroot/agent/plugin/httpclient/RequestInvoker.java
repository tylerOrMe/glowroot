/*
 * Copyright 2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.glowroot.agent.plugin.httpclient;

import java.lang.reflect.Method;
import java.net.URI;

import javax.annotation.Nullable;

import org.glowroot.agent.plugin.api.Agent;
import org.glowroot.agent.plugin.api.Logger;

public class RequestInvoker {

    private static final Logger logger = Agent.getLogger(RequestInvoker.class);

    private final @Nullable Method getMethodMethod;

    private final @Nullable Method getOriginalURIMethod;

    public RequestInvoker(Class<?> clazz) {
        Class<?> requestClass = getRequestClass(clazz);
        getMethodMethod = Invokers.getMethod(requestClass, "getMethod");
        getOriginalURIMethod = Invokers.getMethod(requestClass, "getOriginalURI");
    }

    String getMethod(Object request) {
        return Invokers.invoke(getMethodMethod, request, "");
    }

    // TODO report checker framework issue that occurs without this warning suppression
    @SuppressWarnings("return.type.incompatible")
    @Nullable
    URI getOriginalURI(Object request) {
        return Invokers.invoke(getOriginalURIMethod, request, null);
    }

    private static @Nullable Class<?> getRequestClass(Class<?> clazz) {
        try {
            return Class.forName("com.ning.http.client.Request", false, clazz.getClassLoader());
        } catch (ClassNotFoundException e) {
            logger.warn(e.getMessage(), e);
        }
        return null;
    }
}
