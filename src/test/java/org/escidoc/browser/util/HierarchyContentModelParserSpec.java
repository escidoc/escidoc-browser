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

import org.junit.Test;

import java.io.IOException;
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
}