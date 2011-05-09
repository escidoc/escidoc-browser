package org.escidoc.browser.repository;

import java.util.Collection;
import java.util.List;

import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.versionhistory.Version;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public interface ContainerProxy extends ResourceProxy {

    @Override
    String getDescription();

    @Override
    String getStatus();

    @Override
    String getCreator();

    @Override
    String getCreatedOn();

    @Override
    String getModifier();

    @Override
    String getModifiedOn();

    @Override
    List<String> getRelations();

    VersionHistory getPreviousVersion();

    MetadataRecords getMedataRecords();

    Collection<Version> getVersionHistory();

    Resource getContext();

}