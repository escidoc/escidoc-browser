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
package org.escidoc.browser.ui.listeners;

import com.vaadin.ui.Upload.Receiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;

@SuppressWarnings("serial")
public class MetadataFileReceiver implements Receiver {

    private static final Logger LOG = LoggerFactory.getLogger(MetadataFileReceiver.class);

    private String fileName;

    private String mimeType;

    private boolean sleep;

    private int total = 0;

    private boolean isWellFormed;

    private StringBuffer filecontent = new StringBuffer();

    @Override
    public OutputStream receiveUpload(final String fileName, final String mimeType) {
        this.fileName = fileName;
        this.mimeType = mimeType;
        filecontent = new StringBuffer();
        return new OutputStream() {
            @Override
            public void write(final int b) throws IOException {
                total++;
                filecontent.append((char) b);
                if (sleep && total % 10000 == 0) {
                    try {
                        Thread.sleep(100);
                        LOG.debug(Integer.toString(b));
                    }
                    catch (final InterruptedException e) {
                        LOG.warn(e.getMessage());
                    }
                }
            }
        };
    }

    public String getFileContent() {
        return filecontent.toString();
    }

    public void clearBuffer() {
        filecontent.delete(0, filecontent.length());
    }

    public String getFileName() {
        return fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setSlow(final boolean value) {
        sleep = value;
    }

    public void setWellFormed(final boolean isWellFormed) {
        this.isWellFormed = isWellFormed;
    }

    public boolean isWellFormed() {
        return isWellFormed;
    }
}