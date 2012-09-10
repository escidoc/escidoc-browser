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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class HierarchyContentModelParserSpec {

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
        System.out.println("input: " + simpleJson);
        // when:
        Map<String, Object> o =
            new ObjectMapper(new JsonFactory()).readValue(simpleJson, new TypeReference<HashMap<String, Object>>() {
                // empty
            });
        System.out.println("Got " + o);
        // then:
        assertEquals("It's not equal, Jim.", "bar", o.get("foo"));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldParseComplexJsonString() throws JsonParseException, JsonMappingException, IOException {
        // given:
        InputStream is = getClass().getClassLoader().getResourceAsStream("complex.json");
        assertTrue("It's null, ", is != null);

        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, "UTF-8");
        String theString = writer.toString();
        System.out.println(theString);

        // when:
        // TODO why do we need to convert to string?
        Map<String, Object> o =
            new ObjectMapper(new JsonFactory()).readValue(theString, new TypeReference<HashMap<String, Object>>() {
                // empty
            });
        System.out.println("Got " + o);
        // then:
        assertEquals("It's not equal, Jim.", Integer.valueOf(1), o.get("id"));
        assertEquals("It's not equal, Jim.", "Foo", o.get("name"));

        System.out.println("res: " + ((Map<String, Integer>) o.get("stock")).get("warehouse").intValue());
        assertTrue("It's not equal", ((Map<String, Integer>) o.get("stock")).get("warehouse").intValue() == 300);

        Object object = o.get("tags");
        System.out.println("res: " + object);

        assertEquals("It's not equal, Jim.", new String[] { "Bar", "Eek" }, o.get("tags"));
    }
}