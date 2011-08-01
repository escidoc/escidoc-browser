/**
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development
 * and Distribution License, Version 1.0 only (the "License"). You may not use
 * this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or
 * https://www.escidoc.org/license/ESCIDOC.LICENSE . See the License for the
 * specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and
 * include the License file at license/ESCIDOC.LICENSE. If applicable, add the
 * following below this CDDL HEADER, with the fields enclosed by brackets "[]"
 * replaced with your own identifying information: Portions Copyright [yyyy]
 * [name of copyright owner]
 * 
 * CDDL HEADER END
 * 
 * 
 * 
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer
 * wissenschaftlich-technische Information mbH and Max-Planck- Gesellschaft zur
 * Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to
 * license terms.
 */
package org.escidoc.browser.ui.listeners;

import java.io.StringWriter;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.w3c.dom.Element;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

import de.escidoc.core.resources.common.MetadataRecord;

public class MetadataRecBehavour implements ClickListener {

    private static final String NAME = "Name :";

    private static final String LINK = "Link ";

    private static final String CONTENT = "Content ";

    private final Window mainWindow;

    MetadataRecord metadataRecord;

    private final EscidocServiceLocation escidocServiceLocation;

    public MetadataRecBehavour(final MetadataRecord metadataRecord, final Window mainWindow,
        final EscidocServiceLocation escidocServiceLocation) {
        this.mainWindow = mainWindow;
        this.metadataRecord = metadataRecord;
        this.escidocServiceLocation = escidocServiceLocation;
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        final Window subwindow = new Window("eSciDoc Metadata");
        subwindow.setWidth("600px");
        subwindow.setModal(true);

        final StringBuilder builder = new StringBuilder();
        builder.append(NAME);
        builder.append(metadataRecord.getName());
        builder.append("<br />");
        builder.append(LINK);
        builder.append("<a href='");
        builder.append(escidocServiceLocation.getEscidocUri());
        builder.append(metadataRecord.getXLinkHref());
        builder.append("' target='_blank'>");
        builder.append(metadataRecord.getXLinkTitle());
        builder.append("</a><br />" + CONTENT);
        String mtRecinfo = builder.toString();

        final Label msgWindow = new Label(mtRecinfo, Label.CONTENT_RAW);
        final Label msgMetaDataXml =
            new Label(getContentAsString(metadataRecord.getContent()), Label.CONTENT_PREFORMATTED);

        subwindow.addComponent(msgWindow);
        subwindow.addComponent(msgMetaDataXml);
        if (subwindow.getParent() != null) {
            mainWindow.showNotification("Window is already open");
        }
        else {
            mainWindow.addWindow(subwindow);
        }
    }

    private String getContentAsString(Element el) {

        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer;
        try {
            transformer = transFactory.newTransformer();
            StringWriter buffer = new StringWriter();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(new DOMSource(el), new StreamResult(buffer));
            return buffer.toString();
        }
        catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;

    }
}