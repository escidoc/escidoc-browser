package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public interface ItemProxy extends ResourceProxy {
    String getDescription();

    String getStatus();

    String getCreator();

    String getCreatedOn();

    String getModifier();

    String getModifiedOn();

    List<String> getRelations();

    List<String> getMedataRecords();

    VersionHistory getPreviousVersion();

    String getContentUrl();
}