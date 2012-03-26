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

import org.escidoc.browser.AppConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import de.escidoc.core.resources.common.MetadataRecord;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.oum.OrganizationalUnit;
import de.escidoc.core.resources.oum.OrganizationalUnitProperties;
import de.escidoc.core.resources.oum.Parent;
import de.escidoc.core.resources.oum.Parents;
import de.escidoc.core.resources.oum.Predecessor;
import de.escidoc.core.resources.oum.PredecessorForm;
import de.escidoc.core.resources.oum.Predecessors;

public class OrgUnitBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(OrgUnitBuilder.class);

    private OrganizationalUnit oldOrgUnit = new OrganizationalUnit();

    public OrgUnitBuilder(final OrganizationalUnit orgUnit) {
        oldOrgUnit = orgUnit;
    }

    public OrgUnitBuilder() {
        // default constructor
    }

    public OrgUnitBuilder update(final OrganizationalUnit orgUnit, final String title, final String description)
        throws ParserConfigurationException, SAXException, IOException {

        oldOrgUnit = orgUnit;
        final MetadataRecords mdRecords = new MetadataRecords();
        mdRecords.add(eSciDocMdRecord(title, description));
        oldOrgUnit.setMetadataRecords(mdRecords);

        return this;
    }

    public OrgUnitBuilder with(final String title, final String description) throws ParserConfigurationException,
        SAXException, IOException {

        oldOrgUnit.setProperties(new OrganizationalUnitProperties());

        // add mdRecord to set
        final MetadataRecords mdRecords = new MetadataRecords();
        mdRecords.add(eSciDocMdRecord(title, description));

        // add metadata-records to OU
        oldOrgUnit.setMetadataRecords(mdRecords);

        return this;
    }

    public OrganizationalUnit build() {
        return oldOrgUnit;
    }

    private Document doc;

    private Element mpdlMdRecord; // NOPMD by CHH on 9/16/10 6:41 PM

    private MetadataRecord eSciDocMdRecord(final String title, final String description)
        throws ParserConfigurationException, SAXException, IOException {

        final MetadataRecord mdRecord = new MetadataRecord(AppConstants.ESCIDOC_DEFAULT_METADATA_NAME);
        buildNewDocument();
        mpdlMdRecord =
            doc.createElementNS("http://purl.org/escidoc/metadata/profiles/0.1/organizationalunit",
                "organizational-unit");
        mpdlMdRecord.setPrefix("mdou");

        mdRecord.setContent(mpdlMdRecord);

        title(title);
        description(description);

        return mdRecord;
    }

    private void buildNewDocument() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        factory.setCoalescing(true);
        factory.setValidating(true);
        final DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.newDocument();
    }

    // TODO refactor this: a lot of duplication
    private Node title(final String title) {
        final Element titleElmt = doc.createElementNS("http://purl.org/dc/elements/1.1/", "title");
        titleElmt.setPrefix("dc");
        titleElmt.setTextContent(title);
        mpdlMdRecord.appendChild(titleElmt);
        return mpdlMdRecord;
    }

    private Node description(final String description) {
        final Element descriptionElmt = doc.createElementNS("http://purl.org/dc/elements/1.1/", "description");
        descriptionElmt.setPrefix("dc");
        descriptionElmt.setTextContent(description);
        mpdlMdRecord.appendChild(descriptionElmt);
        return mpdlMdRecord;
    }

    public OrgUnitBuilder identifier(final String identifier) {
        if (isNotSet(identifier)) {
            return this; // NOPMD by CHH on 9/16/10 6:41 PM
        }

        assertNotEmpty(identifier);

        final Element identifierElmt = doc.createElementNS("http://purl.org/dc/elements/1.1/", "identifier");
        identifierElmt.setPrefix("dc");
        identifierElmt.setTextContent(identifier);
        mpdlMdRecord.appendChild(identifierElmt);
        return this;
    }

    private boolean isNotSet(final String identifier) {
        return identifier == null || identifier.isEmpty();
    }

    public OrgUnitBuilder alternative(final String alternative) {
        final Element identifierElmt = doc.createElementNS("http://purl.org/dc/terms/", "alternative");
        identifierElmt.setPrefix("dcterms");
        identifierElmt.setTextContent(alternative);
        mpdlMdRecord.appendChild(identifierElmt);
        return this;
    }

    private void assertNotEmpty(final String value) {
        assert value != null : "Null reference ";
        assert !value.isEmpty() : "Empty string";
    }

    public OrgUnitBuilder country(final String country) {
        final Element element = doc.createElementNS(AppConstants.ESCIDOC_METADATA_TERMS_NS, "country");
        element.setPrefix("eterms");
        element.setTextContent(country);
        mpdlMdRecord.appendChild(element);
        return this;
    }

    public OrgUnitBuilder city(final String city) {
        final Element element = doc.createElementNS(AppConstants.ESCIDOC_METADATA_TERMS_NS, "city");
        element.setPrefix("eterms");
        element.setTextContent(city);
        mpdlMdRecord.appendChild(element);
        return this;
    }

    public OrgUnitBuilder type(final String orgType) {
        final Element element = doc.createElementNS(AppConstants.ESCIDOC_METADATA_TERMS_NS, "organization-type");
        element.setPrefix("eterms");
        element.setTextContent(orgType);
        mpdlMdRecord.appendChild(element);
        return this;
    }

    public OrgUnitBuilder coordinates(final String coordinates) {
        final Element element = doc.createElementNS("http://www.opengis.net/kml/2.2", "coordinates");
        element.setPrefix("kml");
        element.setTextContent(coordinates);
        mpdlMdRecord.appendChild(element);
        return this;
    }

    public OrgUnitBuilder parents(final Set<String> parentObjectIds) {
        final Parents parents = new Parents();

        if (parentObjectIds != null && !parentObjectIds.isEmpty()) {
            for (final String parentObjectId : parentObjectIds) {
                parents.add(new Parent(parentObjectId));
            }
        }

        oldOrgUnit.setParents(parents);
        return this;
    }

    public OrgUnitBuilder predecessors(final Set<String> predecessorsObjectIds, final PredecessorForm predecessorType) {

        if (predecessorsObjectIds == null || predecessorsObjectIds.isEmpty()) {
            LOG.info("empty predecessor.");
            return this;
        }

        final Predecessors predecessor = new Predecessors();
        for (final String predecessorId : predecessorsObjectIds) {
            predecessor.add(new Predecessor(predecessorId, predecessorType));
        }

        oldOrgUnit.setPredecessors(predecessor);
        return this;
    }
}