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
package org.escidoc.browser.repository.internal;

import com.google.common.base.Preconditions;

import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.repository.PdpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import de.escidoc.core.client.PolicyDecisionPointHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.pdp.Decision;
import de.escidoc.core.resources.aa.pdp.Requests;
import de.escidoc.core.resources.aa.pdp.Results;

public class PdpRepositoryImpl implements PdpRepository {

    private final static Logger LOG = LoggerFactory.getLogger(PdpRepositoryImpl.class);

    private final PolicyDecisionPointHandlerClient client;

    private Set<Attribute> actionAttrs = new HashSet<Attribute>();

    private Set<Attribute> resourceAttrs = new HashSet<Attribute>();

    private Set<Subject> subjects = new HashSet<Subject>();

    public PdpRepositoryImpl(final URL serviceAddress) {
        client = new PolicyDecisionPointHandlerClient(serviceAddress);
    }

    @Override
    public PdpRepository isAction(final String actionId) throws URISyntaxException {
        actionAttrs = new HashSet<Attribute>();
        actionAttrs
            .add(new Attribute(new URI(AppConstants.XACML_ACTION_ID), null, null, new StringAttribute(actionId)));
        return this;
    }

    @Override
    public PdpRepository forResource(final String resourceId) throws URISyntaxException {
        resourceAttrs = new HashSet<Attribute>();
        resourceAttrs
            .add(new Attribute(new URI(AppConstants.RESOURCE_ID), null, null, new StringAttribute(resourceId)));
        return this;
    }

    @Override
    public PdpRepository forUser(final String userId) throws URISyntaxException {
        final Set<Attribute> subjectAttributes = new HashSet<Attribute>();

        if (userId == null) {
            subjectAttributes
                .add(new Attribute(new URI(AppConstants.SUBJECT_ID), null, null, new StringAttribute(" ")));
        }
        else {
            subjectAttributes.add(new Attribute(new URI(AppConstants.SUBJECT_ID), null, null, new StringAttribute(
                userId)));
        }
        subjects = new HashSet<Subject>();
        subjects.add(new Subject(Subject.DEFAULT_CATEGORY, subjectAttributes));
        return this;

    }

    @Override
    public boolean permitted() throws EscidocClientException {
        Preconditions.checkArgument(subjects.size() <= 1, "more than one subjects are not allowed");
        Preconditions.checkArgument(resourceAttrs.size() <= 1, "more than one subjects are not allowed");
        Preconditions.checkArgument(actionAttrs.size() == 1, "more than one subjects are not allowed");
        final Requests requests = new Requests();
        checkForResource();
        requests.add(new RequestCtx(subjects, resourceAttrs, actionAttrs, Requests.DEFAULT_ENVIRONMENT));
        return getDecisionFrom(client.evaluate(requests)).equals(Decision.PERMIT);
    }

    private void checkForResource() {
        if (resourceAttrs == null) {
            try {
                forResource("");
            }
            catch (final URISyntaxException e) {
                LOG.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private Decision getDecisionFrom(final Results results) {
        return results.get(0).getInterpretedDecision();
    }

    @Override
    public boolean denied() throws EscidocClientException {
        return !permitted();
    }

    @Override
    public void loginWith(final String token) {
        client.setHandle(token);
    }
}
