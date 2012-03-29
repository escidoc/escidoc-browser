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
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.container.ContainerProperties;

public class ContainerBuilder {

    private final Container container = new Container();

    private final MetadataRecord containerMetadata = new MetadataRecord(AppConstants.ESCIDOC);

    private final ContainerProperties containerProps = new ContainerProperties();

    private final ContextRef contextRef;

    private final ContentModelRef contentModelRef;

    private final String strMedataData;

    public ContainerBuilder(final ContextRef contextRef, final ContentModelRef contentModelRef, final String strMetadata) {

        Preconditions.checkNotNull(contextRef, "contextRef is null: %s", contextRef);
        Preconditions.checkNotNull(contentModelRef, "contentModelRef is null: %s", contentModelRef);
        this.contextRef = contextRef;
        this.contentModelRef = contentModelRef;
        strMedataData = strMetadata;
    }

    public Container build(final String containerName) {
        return tryBuildNewContainer(containerName);
    }

    private Container tryBuildNewContainer(final String containerName) {
        try {
            setContainerName(containerName);
            setContainerProperties();
            return container;
        }
        catch (final ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private void setContainerName(final String containerName) throws ParserConfigurationException {
        addDefaultMetadata(createNewDocument(), containerName);
    }

    private static Document createNewDocument() throws ParserConfigurationException {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    }

    private void setContainerProperties() {
        containerProps.setContext(contextRef);
        containerProps.setContentModel(contentModelRef);
        container.setProperties(containerProps);
    }

    private void addDefaultMetadata(final Document doc, final String containerName) {
        buildDefaultMetadata(doc, containerName);
        final MetadataRecords containerMetadataList = new MetadataRecords();
        containerMetadataList.add(containerMetadata);
        container.setMetadataRecords(containerMetadataList);
    }

    private void buildDefaultMetadata(final Document doc, final String containerName) {
        containerMetadata.setName(AppConstants.ESCIDOC);
        if (strMedataData.isEmpty()) {
            containerMetadata.setContent(buildContentForContainerMetadata(doc, containerName));
        }
        else {
            containerMetadata.setContent(buildContentForContainerMetadataFromUploadedFile());
        }
    }

    private Element buildContentForContainerMetadataFromUploadedFile() {
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

    private static Element buildContentForContainerMetadata(final Document doc, final String containerName) {
        final Element element = doc.createElementNS(AppConstants.DC_NAMESPACE, "dc");
        final Element titleElmt = doc.createElementNS(AppConstants.DC_NAMESPACE, "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(containerName);
        element.appendChild(titleElmt);
        return element;
    }
}