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
import org.escidoc.browser.util.Utils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.om.item.StorageType;
import de.escidoc.core.resources.om.item.component.Component;
import de.escidoc.core.resources.om.item.component.ComponentContent;
import de.escidoc.core.resources.om.item.component.ComponentProperties;

/**
 * CDDL HEADER START
 * 
 * The contents of this file are subject to the terms of the Common Development and Distribution License, Version 1.0
 * only (the "License"). You may not use this file except in compliance with the License.
 * 
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions and limitations under the License.
 * 
 * When distributing Covered Code, include this CDDL HEADER in each file and include the License file at
 * license/ESCIDOC.LICENSE. If applicable, add the following below this CDDL HEADER, with the fields enclosed by
 * brackets "[]" replaced with your own identifying information: Portions Copyright [yyyy] [name of copyright owner]
 * 
 * CDDL HEADER END
 * 
 * 
 * 
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft fuer wissenschaftlich-technische Information mbH and
 * Max-Planck- Gesellschaft zur Foerderung der Wissenschaft e.V. All rights reserved. Use is subject to license terms.
 */

public class ComponentBuilder {

    private final Component component = new Component();

    private final ComponentProperties componentProperties = new ComponentProperties();

    private final ComponentContent componentContent = new ComponentContent();

    private final MetadataRecord componentMetadata = new MetadataRecord(AppConstants.ESCIDOC);

    private final String fileName;

    private final StorageType storageType;

    private String contentUri;

    public ComponentBuilder(final String fileName, final URL contentUrl, final StorageType storageType) {
        Preconditions.checkNotNull(fileName, "fileName is null: %s", fileName);
        Preconditions.checkNotNull(contentUrl, "contentUri is null: %s", contentUrl);
        Preconditions.checkNotNull(storageType, "storageType is null: %s", storageType);
        this.fileName = fileName;
        contentUri = contentUrl.toString();
        this.storageType = storageType;
    }

    public ComponentBuilder(final String fileName, final StorageType storageType) {
        Preconditions.checkNotNull(fileName, "fileName is null: %s", fileName);
        Preconditions.checkNotNull(storageType, "storageType is null: %s", storageType);
        this.fileName = fileName;
        this.storageType = storageType;
    }

    public ComponentBuilder withDescription(final String description) {
        Preconditions.checkNotNull(description, "description is null: %s", description);
        componentProperties.setDescription(description);
        return this;
    }

    public ComponentBuilder withMimeType(final String mimeType) {
        Preconditions.checkNotNull(mimeType, "mimeType is null: %s", mimeType);
        componentProperties.setMimeType(mimeType);
        return this;
    }

    private void setComponentProperties() {
        componentProperties.setFileName(fileName);
        componentProperties.setVisibility(ComponentVisibility.PUBLIC.label());
        componentProperties.setContentCategory("Content Category?");
        component.setProperties(componentProperties);
    }

    private void setComponentContent() {
        componentContent.setXLinkHref(contentUri);
        componentContent.setStorageType(storageType);
        component.setContent(componentContent);
    }

    public Component build() throws ParserConfigurationException {
        setComponentName();
        setComponentProperties();
        setComponentContent();
        return component;
    }

    private void setComponentName() throws ParserConfigurationException {
        addDefaultMetadata(Utils.createNewDocument());
    }

    private void addDefaultMetadata(final Document doc) {
        buildDefaultMetadata(doc);
        final MetadataRecords componentMetadataList = new MetadataRecords();
        componentMetadataList.add(componentMetadata);
        component.setMetadataRecords(componentMetadataList);
    }

    private void buildDefaultMetadata(final Document doc) {
        componentMetadata.setName(AppConstants.ESCIDOC);
        componentMetadata.setContent(buildContentForItemMetadata(doc));
    }

    private Element buildContentForItemMetadata(final Document doc) {
        final Element element = doc.createElementNS(AppConstants.DC_NAMESPACE, "dc");
        final Element titleElmt = doc.createElementNS(AppConstants.DC_NAMESPACE, "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(fileName);
        element.appendChild(titleElmt);
        return element;
    }

    public ComponentBuilder withContentUrl(final URL contentUrl) {
        contentUri = contentUrl.toString();
        return this;
    }
}