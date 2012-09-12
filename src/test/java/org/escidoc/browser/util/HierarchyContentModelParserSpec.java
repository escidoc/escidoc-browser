/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License"). You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved. Use is subject to license terms.
 */
package org.escidoc.browser.util;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HierarchyContentModelParserSpec {

    private final static Logger LOG = LoggerFactory.getLogger(HierarchyContentModelParserSpec.class);

    /*
     * class Foo {
     * 
     * public static Object parseJson(String simpleJson) { throw new
     * UnsupportedOperationException("not-yet-implemented."); }
     * 
     * }
     */

    @Test
    public void shouldParseSimpleJsonString() throws JsonParseException, JsonMappingException, IOException {
        // given:
        String simpleJson = "{\"foo\" : \"bar\"}";
        LOG.debug("input: " + simpleJson);
        Map<String, Object> o = jsonToMap(simpleJson);
        LOG.debug("Got " + o);
        // then:
        assertEquals("It's not equal, Jim.", "bar", o.get("foo"));
    }

    @SuppressWarnings({ "unchecked", "serial" })
    @Test
    public void shouldParseComplexJsonString() throws JsonParseException, JsonMappingException, IOException {
        // given:
        String theString = fileToString("complex.js");

        Map<String, Object> o = jsonToMap(theString);
        LOG.debug("Got " + o);
        // then:
        assertEquals("It's not equal, Jim.", Integer.valueOf(1), o.get("id"));
        assertEquals("It's not equal, Jim.", "Foo", o.get("name"));

        LOG.debug("res: " + ((Map<String, Integer>) o.get("stock")).get("warehouse").intValue());
        assertTrue("It's not equal", ((Map<String, Integer>) o.get("stock")).get("warehouse").intValue() == 300);

        LOG.debug("res: " + o.get("tags"));
        assertEquals("It's not equal, Jim.", new ArrayList<String>() {
            {
                add("Bar");
                add("Eek");
            }
        }, o.get("tags"));
    }

    private String fileToString(String fileName) throws IOException {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        assertTrue("It's null, ", is != null);

        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        String theString = writer.toString();
        LOG.debug(theString);
        return theString;
    }

    @Test
    public void shouldParseEkinematikCmDescription() throws Exception {
        // Given X0 && ...Xn
        Map<String, Object> o = jsonToMap(fileToString("ekinematik.js"));
        LOG.debug("Got " + o);
        // ensure that
        Map<?, ?> map = assertEkinematik(o);
        assertResourcetype(map);

        // ensure that
        List<?> children = assertChildren(map);
        List<String> childId = new ArrayList<String>(children.size());
        for (Object childObject : children) {
            Map<?, ?> child = logChildName(childObject);
            Map<?, ?> childContentModel = getChildContentModel(child);
            logChildType(child);
            Object idObject = logChildContentModelId(childContentModel);
            logChildContentModelUri(childContentModel);

            if (idObject instanceof String) {
                childId.add((String) idObject);
            }
        }

        // ensure that
        assertEquals("It's not same, Jim ", 3, childId.size());
    }

    private static void logChildType(Map<?, ?> child) {
        String childType = (String) child.get("type");
        LOG.debug("with the type " + childType);
    }

    private static void logChildContentModelUri(Map<?, ?> childContentModel) {
        Object linkObject = childContentModel.get("link");
        LOG.debug("located in " + linkObject);
    }

    private static Object logChildContentModelId(Map<?, ?> childContentModel) {
        Object idObject = childContentModel.get("id");
        LOG.debug("using content model with id " + idObject);
        return idObject;
    }

    private static Map<?, ?> getChildContentModel(Map<?, ?> child) {
        Object childContentModelObject = child.get("content-model");
        if (!(childContentModelObject instanceof Map<?, ?>)) {
            Collections.emptyMap();
        }
        Map<?, ?> childContentModel = (Map<?, ?>) childContentModelObject;
        return childContentModel;
    }

    private static Map<?, ?> logChildName(Object childObject) {
        if (!(childObject instanceof Map)) {
            Collections.emptyMap();
        }
        Map<?, ?> child = (Map<?, ?>) childObject;

        String childName = (String) child.get("name");
        LOG.debug("Should create a resource with the name " + childName);
        return child;
    }

    private static Map<?, ?> assertEkinematik(Map<String, Object> o) {
        Object val = o.get("ekinematik");
        if (!(val instanceof HashMap<?, ?>)) {
            return Collections.emptyMap();
        }

        Map<?, ?> ekinematik = (Map<?, ?>) val;
        assertTrue("Does not contain key: type", ekinematik.containsKey("type"));
        return ekinematik;
    }

    private static List<?> assertChildren(Map<?, ?> map) {
        Object childrenValue = map.get("children");
        if (!(childrenValue instanceof List)) {
            return Collections.emptyList();
        }

        List<?> children = (List<?>) childrenValue;

        assertEquals("Children length is not 3", 3, children.size());
        return children;
    }

    private static void assertResourcetype(Map<?, ?> map) {
        Object val = map.get("type");
        if (!(val instanceof String)) {
            return;
        }

        String resourceType = (String) val;
        LOG.debug("Jim, the type is a " + resourceType);

        assertEquals("It's not a container", "container", resourceType);
    }

    private static Map<String, Object> jsonToMap(String theString) throws IOException, JsonParseException,
        JsonMappingException {
        Map<String, Object> o =
            new ObjectMapper(new JsonFactory()).readValue(theString, new TypeReference<HashMap<String, Object>>() {
                // empty
            });
        return o;
    }

    @Test(expected = JsonParseException.class)
    public void shouldThrowParseExceptionIfNotEkinematikContentModel() throws Exception {
        // Given X0 && ...Xn
        String s = fileToString("invalid.js");
        // When
        jsonToMap(s);
        // Then ensure that

    }
}