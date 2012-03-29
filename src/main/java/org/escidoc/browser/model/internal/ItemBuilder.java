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
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.model.internal;

import com.google.common.base.Preconditions;

import org.escidoc.browser.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.om.item.component.ComponentProperties;

public class ItemBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ItemBuilder.class);

    private final Item item = new Item();

    private final MetadataRecord itemMetadata = new MetadataRecord(AppConstants.ESCIDOC);

    private final ItemProperties itemProps = new ItemProperties();

    private final ComponentProperties componentProps = new ComponentProperties();

    private final ContextRef contextRef;

    private final ContentModelRef contentModelRef;

    private final String strMedataData;

    public ItemBuilder(final ContextRef contextRef, final ContentModelRef contentModelRef, final String strMetaData) {
        Preconditions.checkNotNull(contextRef, "contextRef is null: %s", contextRef);
        Preconditions.checkNotNull(contentModelRef, "contentModelRef is null: %s", contentModelRef);
        this.contextRef = contextRef;
        this.contentModelRef = contentModelRef;
        this.strMedataData = strMetaData;
    }

    public ItemBuilder withContent(final URL contentUrl) {
        Preconditions.checkNotNull(contentUrl, "contentUrl is null: %s", contentUrl);
        return this;
    }

    public ItemBuilder withMimeType(final String mimeType) {
        if (mimeType == null) {
            return this;
        }
        Preconditions.checkArgument(!mimeType.isEmpty(), "mimeType can not be empty String");
        componentProps.setMimeType(mimeType);
        return this;
    }

    public Item build(final String itemName) {
        return tryBuildNewItem(itemName);
    }

    private Item tryBuildNewItem(final String itemName) {
        try {
            setItemName(itemName);
            setItemProperties();
            // setComponent();
            return item;
        }
        catch (final ParserConfigurationException e) {
            LOG.error("Error creating a XML document. " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

    }

    private void setItemName(final String itemName) throws ParserConfigurationException {
        addDefaultMetadata(createNewDocument(), itemName);
    }

    private static Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private void setItemProperties() {
        itemProps.setContext(contextRef);
        itemProps.setContentModel(contentModelRef);
        item.setProperties(itemProps);
    }

    private void addDefaultMetadata(final Document doc, final String itemName) {
        buildDefaultMetadata(doc, itemName);
        final MetadataRecords itemMetadataList = new MetadataRecords();
        itemMetadataList.add(itemMetadata);
        item.setMetadataRecords(itemMetadataList);
    }

    private void buildDefaultMetadata(final Document doc, final String itemName) {
        itemMetadata.setName(AppConstants.ESCIDOC);
        if (strMedataData.isEmpty()) {
            itemMetadata.setContent(buildContentForItemMetadata(doc, itemName));
        }
        else {
            itemMetadata.setContent(buildContentForItemMetadataFromUploadedFile());
        }

    }

    private static Element buildContentForItemMetadata(final Document doc, final String itemName) {
        final Element element = doc.createElementNS(AppConstants.DC_NAMESPACE, "dc");
        final Element titleElmt = doc.createElementNS(AppConstants.DC_NAMESPACE, "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(itemName);
        element.appendChild(titleElmt);
        return element;
    }

    private Element buildContentForItemMetadataFromUploadedFile() {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            final InputSource is = new InputSource(new StringReader(strMedataData));
            Document d;
            try {
                d = builder.parse(is);
                return d.getDocumentElement();
            }
            catch (final SAXException e) {
                return null;
            }
            catch (final IOException e) {
                return null;
            }
        }
        catch (final ParserConfigurationException e) {
            return null;
        }
    }
}
