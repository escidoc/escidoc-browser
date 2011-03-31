package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.resources.common.versionhistory.Version;

public interface ContainerProxy extends ResourceProxy {
    String getDescription();

    String getStatus();

    String getCreator();

    String getCreatedOn();

    String getModifier();

    String getModifiedOn();

    List<String> getRelations();

    Boolean hasPreviousVersion();

    List<String> getMedataRecords();

    List<Version> getVersionHistory();

}