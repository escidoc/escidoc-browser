package org.escidoc.browser.model;

import java.util.List;

public interface ResourceProxy extends ResourceModel {

    String getDescription();

    // Status: Pending, Release,...
    // TODO implement status as enumeration
    String getStatus();

    String getCreator();

    String getCreatedOn();

    String getModifier();

    String getModifiedOn();

    List<String> getRelations();

}
