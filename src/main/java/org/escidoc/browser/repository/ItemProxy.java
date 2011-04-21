package org.escidoc.browser.repository;

import java.util.List;

import org.escidoc.browser.model.ResourceProxy;

import de.escidoc.core.resources.common.MetadataRecords;
import de.escidoc.core.resources.common.versionhistory.VersionHistory;

public interface ItemProxy extends ResourceProxy {
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

    MetadataRecords getMedataRecords();

    VersionHistory getPreviousVersion();

    String getContentUrl();

}