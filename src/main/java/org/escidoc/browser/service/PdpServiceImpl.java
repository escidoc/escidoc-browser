package org.escidoc.browser.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.escidoc.browser.AppConstants;

import com.google.common.base.Preconditions;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.RequestCtx;
import com.sun.xacml.ctx.Subject;

import de.escidoc.core.client.PolicyDecisionPointHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.aa.pdp.Decision;
import de.escidoc.core.resources.aa.pdp.Requests;
import de.escidoc.core.resources.aa.pdp.Results;

public class PdpServiceImpl implements PdpService {

    private final PolicyDecisionPointHandlerClient client;

    private Set<Attribute> actionAttrs = new HashSet<Attribute>();

    private Set<Attribute> resourceAttrs = new HashSet<Attribute>();

    private Set<Subject> subjects = new HashSet<Subject>();

    public PdpServiceImpl(final URL serviceAddress) {
        client = new PolicyDecisionPointHandlerClient(serviceAddress);
    }

    @Override
    public PdpService isAction(final String actionId) throws URISyntaxException {
        actionAttrs = new HashSet<Attribute>();
        actionAttrs
            .add(new Attribute(new URI(AppConstants.XACML_ACTION_ID), null, null, new StringAttribute(actionId)));
        return this;
    }

    @Override
    public PdpService forResource(final String resourceId) throws URISyntaxException {
        resourceAttrs = new HashSet<Attribute>();
        resourceAttrs
            .add(new Attribute(new URI(AppConstants.RESOURCE_ID), null, null, new StringAttribute(resourceId)));
        return this;
    }

    @Override
    public PdpService forUser(final String userId) throws URISyntaxException {
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
        requests.add(new RequestCtx(subjects, resourceAttrs, actionAttrs, Requests.DEFAULT_ENVIRONMENT));
        return getDecisionFrom(client.evaluate(requests)).equals(Decision.PERMIT);
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
