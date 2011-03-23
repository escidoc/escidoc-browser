package org.escidoc.browser.model;

import java.util.List;

public interface ResourceProxy {

    String getDescription();

    String getStatus();

    String getCreator();

    String getCreatedOn();

    String getModifier();

    String getModifiedOn();

    List<String> getRelations();

}
