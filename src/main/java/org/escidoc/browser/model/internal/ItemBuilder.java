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
package org.escidoc.browser.model.internal;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.item.Item;
import de.escidoc.core.resources.om.item.ItemProperties;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;
import de.escidoc.core.resources.om.item.component.Components;

public class ItemBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(ItemBuilder.class);

    private final Item item = new Item();

    private final MetadataRecords metadataList = new MetadataRecords();

    private final MetadataRecord itemMetadata = new MetadataRecord(AppConstants.ESCIDOC);

    private final ItemProperties itemProps = new ItemProperties();

    private final Components componentList = new Components();

    private final Component component = new Component();

    private final ComponentContent content = new ComponentContent();

    private final ComponentProperties componentProps = new ComponentProperties();

    private final ContextRef contextRef;

    private final ContentModelRef contentModelRef;

    private URL contentUrl;

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
        this.contentUrl = contentUrl;
        return this;
    }

    public ItemBuilder withMimeType(String mimeType) {
        if (mimeType == null) {
            return this;
        }
        Preconditions.checkArgument(!mimeType.isEmpty(), "mimeType can not be empty String");
        componentProps.setMimeType(mimeType);
        return this;
    }

    public Item build(String itemName) {
        return tryBuildNewItem(itemName);
    }

    private Item tryBuildNewItem(String itemName) {
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

    private void setItemName(String itemName) throws ParserConfigurationException {
        addDefaultMetadata(createNewDocument(), itemName);
    }

    private Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private void setItemProperties() {
        itemProps.setContext(contextRef);
        itemProps.setContentModel(contentModelRef);
        item.setProperties(itemProps);
    }

    private void addDefaultMetadata(final Document doc, String itemName) {
        buildDefaultMetadata(doc, itemName);
        final MetadataRecords itemMetadataList = new MetadataRecords();
        itemMetadataList.add(itemMetadata);
        item.setMetadataRecords(itemMetadataList);
    }

    private void buildDefaultMetadata(final Document doc, String itemName) {
        itemMetadata.setName(AppConstants.ESCIDOC);
        if (strMedataData.isEmpty()) {
            itemMetadata.setContent(buildContentForItemMetadata(doc, itemName));
        }
        else {
            itemMetadata.setContent(buildContentForItemMetadataFromUploadedFile());
        }

    }

    private Element buildContentForItemMetadata(final Document doc, String itemName) {
        final Element element = doc.createElementNS(AppConstants.DC_NAMESPACE, "dc");
        final Element titleElmt = doc.createElementNS(AppConstants.DC_NAMESPACE, "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(itemName);
        element.appendChild(titleElmt);
        return element;
    }

    private void addComponents(final Item item, final URL contentRef, String itemName)
        throws ParserConfigurationException {
        setComponentProperties(component, contentRef);
        setComponentContent(component, contentRef);
        setComponentTitle(component, itemName);
        componentList.add(component);
        item.setComponents(componentList);
    }

    private void setComponentTitle(Component component, String itemName) throws ParserConfigurationException {
        final Document doc = createNewDocument();
        itemMetadata.setName(AppConstants.ESCIDOC);
        final Element element = buildContentForItemMetadata(doc, itemName);
        itemMetadata.setContent(element);
        metadataList.add(itemMetadata);
        component.setMetadataRecords(metadataList);
    }

    private void setComponentContent(final Component component, final URL contentRef) {
        content.setXLinkHref(contentRef.toString());
        content.setStorage(StorageType.INTERNAL_MANAGED);
        component.setContent(content);
    }

    private void setComponentProperties(final Component component, final URL contentRef) {
        componentProps.setDescription("Description?");
        // TODO FIx this so the name is dynamic
        componentProps.setFileName("ComponentName");
        componentProps.setVisibility("isVisible?");
        componentProps.setContentCategory("which one?");
        component.setProperties(componentProps);
    }

    private Element buildContentForItemMetadataFromUploadedFile() {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        try {
            builder = factory.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(strMedataData));
            Document d;
            try {
                d = builder.parse(is);
                return d.getDocumentElement();
            }
            catch (SAXException e) {
                return null;
            }
            catch (IOException e) {
                return null;
            }
        }
        catch (ParserConfigurationException e) {
            return null;
        }
    }
}
