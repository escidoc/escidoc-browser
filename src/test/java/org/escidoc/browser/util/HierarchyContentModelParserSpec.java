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

    @Test
    public void shouldParseInvalidJson() throws Exception {
        // Given X0 && ...n
        String theString = fileToString("invalid.js");
        Map<String, Object> o = jsonToMap(theString);
        LOG.debug("Got " + o);
        // When
        // Then ensure that
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

    // prompt the user to enter the resource name
    // set resource_name
    // prompt the user to select the content model(CM)
    // find the selected CM by its id
    // IF NOT found
    // display the error message
    // OTHERWISE
    // look up the CM description
    // find '{ "ekinematik"'
    // IF NOT found
    // handle cases
    // OTHERWISE
    // find 'type'
    // create resource of type 'type' with the resource_name
    // IF NOT succesful
    // display the error message
    // OTHERWISE
    // set parent_resource
    // look up 'children'
    // IF NOT found
    // display the error message
    // OTHERWISE
    // set children to value of 'children'
    // FOR each child of the children
    // set child_resource_name to value of name
    // set child_cm_id to value of content-model.id
    // find content model by it child_cm_id
    // IF NOT found
    // display the error message
    // OTHERWISE
    // look up the CM description
    // find controller value
    // IF NOT found
    // display the error message
    // OTHERWISE
    // create resource(item/container) with the child_resource_name using the controller
    // IF NOT succesful
    // display the error message
    // OTHERWISE
    // set child_resource
    // set parent-child relationship, parent_resource is parent
    // of child_resource

    @Test
    public void shouldParseEkinematikCmDescription() throws Exception {
        // Given X0 && ...Xn
        // When
        Map<String, Object> o = jsonToMap(fileToString("ekinematik.js"));
        LOG.debug("Got " + o);

        // Then ensure that

        Map<?, ?> map = assertEkinematik(o);
        assertResourcetype(map);

        // Then ensure that
        List<?> children = assertChildren(map);
        for (Object childObject : children) {
            if (!(childObject instanceof Map)) {
                return;
            }
            Map<?, ?> child = (Map<?, ?>) childObject;
            String childName = (String) child.get("name");
            LOG.debug("Should create a resource with the name " + childName);
            Object childContentModelObject = child.get("content-model");
            if (!(childContentModelObject instanceof Map<?, ?>)) {
                return;
            }
            Map<?, ?> childContentModel = (Map<?, ?>) childContentModelObject;
            Object idObject = childContentModel.get("id");
            LOG.debug("using content model with id " + idObject);
            Object linkObject = childContentModel.get("link");

            LOG.debug("located in " + linkObject);
        }
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
}