/*
 * Copyright 2005-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.ws.transport.http;

import javax.servlet.http.HttpServletResponse;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.util.FileCopyUtils;
import org.springframework.xml.xsd.SimpleXsdSchema;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.custommonkey.xmlunit.XMLAssert.assertXMLEqual;

public class XsdSchemaHandlerAdapterTest {

    private XsdSchemaHandlerAdapter adapter;

    private MockHttpServletRequest request;

    private MockHttpServletResponse response;

    @Before
    public void setUp() throws Exception {
        adapter = new XsdSchemaHandlerAdapter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    public void testGetLastModified() throws Exception {
        Resource single = new ClassPathResource("single.xsd", getClass());
        SimpleXsdSchema schema = new SimpleXsdSchema(single);
        schema.afterPropertiesSet();
        long lastModified = single.getFile().lastModified();
        Assert.assertEquals("Invalid last modified", lastModified, adapter.getLastModified(null, schema));
    }

    @Test
    public void testHandleGet() throws Exception {
        request.setMethod(HttpTransportConstants.METHOD_GET);
        Resource single = new ClassPathResource("single.xsd", getClass());
        SimpleXsdSchema schema = new SimpleXsdSchema(single);
        schema.afterPropertiesSet();
        adapter.handle(request, response, schema);
        String expected = new String(FileCopyUtils.copyToByteArray(single.getFile()));
        assertXMLEqual(expected, response.getContentAsString());
    }

    @Test
    public void testHandleNonGet() throws Exception {
        request.setMethod(HttpTransportConstants.METHOD_POST);
        adapter.handle(request, response, null);
        Assert.assertEquals("METHOD_NOT_ALLOWED expected", HttpServletResponse.SC_METHOD_NOT_ALLOWED,
                response.getStatus());
    }
}