package org.escidoc.browser.repository;

import com.google.common.base.Preconditions;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ModelConverter;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.helper.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.util.List;
import java.util.UUID;

import de.escidoc.core.client.UserGroupHandlerClient;
import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.interfaces.UserGroupHandlerClientInterface;
import de.escidoc.core.resources.aa.usergroup.Selector;
import de.escidoc.core.resources.aa.usergroup.SelectorType;
import de.escidoc.core.resources.aa.usergroup.Selectors;
import de.escidoc.core.resources.aa.usergroup.UserGroup;
import de.escidoc.core.resources.aa.usergroup.UserGroupProperties;
import de.escidoc.core.resources.common.Relations;
import de.escidoc.core.resources.common.TaskParam;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public class GroupRepository implements Repository {

    private static final String ORGANIZATIONL_UNIT = "organizational-unit";

    private static final String FIZ_OU_ID = "escidoc:14";

    private final static Logger LOG = LoggerFactory.getLogger(GroupRepository.class);

    private UserGroupHandlerClientInterface c;

    public GroupRepository(EscidocServiceLocation esl) throws MalformedURLException {
        Preconditions.checkNotNull(esl, "esl is null: %s", esl);
        c = new UserGroupHandlerClient(esl.getEscidocUrl());
    }

    @Override
    public void loginWith(String handle) throws InternalClientException {
        c.setHandle(handle);
    }

    @Override
    public List<ResourceModel> findAll() throws EscidocClientException {
        return ModelConverter.groupToList(c.retrieveUserGroupsAsList(Util.createEmptyFilter()));
    }

    @Override
    public List<ResourceModel> findTopLevelMembersById(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public ResourceProxy findById(String id) throws EscidocClientException {
        UserGroup userGroup = c.retrieve(id);
        return new UserGroupModel(userGroup);
    }

    @Override
    public VersionHistory getVersionHistory(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public Relations getRelations(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public List<ResourceModel> filterUsingInput(String query) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public void delete(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    @Override
    public String getAsXmlString(String id) throws EscidocClientException {
        throw new UnsupportedOperationException("not-yet-implemented.");
    }

    public UserGroup createGroup(String name, List<ResourceModel> list) throws EscidocClientException {
        UserGroup ug = new UserGroup();

        addProperties(name, ug);

        Selectors s = new Selectors();
        for (ResourceModel rm : list) {
            s.add(new Selector(rm.getId(), ORGANIZATIONL_UNIT, SelectorType.INTERNAL));
        }
        ug.setSelectors(s);

        UserGroup created = c.create(ug);

        TaskParam tp = new TaskParam();
        tp.setLastModificationDate(created.getLastModificationDate());

        c.addSelectors(created, tp);
        LOG.debug("A user group is created with an id: " + created.getObjid());
        return created;
    }

    private static void addProperties(String name, UserGroup ug) {
        UserGroupProperties p = new UserGroupProperties();
        p.setName(name);
        p.setLabel(name + "_" + UUID.randomUUID());
        ug.setProperties(p);

        p.setDescription("Description, automated generated .");
        p.setEmail("E-mail, automated generated email.");
        p.setType("Group type, automated generated");
    }
}