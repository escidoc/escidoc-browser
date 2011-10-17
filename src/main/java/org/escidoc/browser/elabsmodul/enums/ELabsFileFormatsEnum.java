/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.elabsmodul.enums;

import java.util.ArrayList;
import java.util.List;

public enum ELabsFileFormatsEnum {
    BLANK("Choose one format, please."), GALAXY_SPC("GALAXY_SPC"), FMF("FMF"), ZIP("ZIP Archive"), TEXT("Text Data"), BYTE(
        "Byte Data");

    private static final int NUMBER_OF_TYPES = 6;

    private static final List<String> list = new ArrayList<String>(NUMBER_OF_TYPES);

    private final String format;

    private ELabsFileFormatsEnum(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return format;
    }

    public static List<String> toList() {
        if (list.isEmpty()) {
            list.add(ELabsFileFormatsEnum.BLANK.toString());
            list.add(ELabsFileFormatsEnum.FMF.toString());
            list.add(ELabsFileFormatsEnum.GALAXY_SPC.toString());
            list.add(ELabsFileFormatsEnum.ZIP.toString());
            list.add(ELabsFileFormatsEnum.BYTE.toString());
            list.add(ELabsFileFormatsEnum.TEXT.toString());
        }
        return list;
    }

}