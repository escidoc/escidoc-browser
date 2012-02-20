package org.escidoc.browser.ui.helper;

import com.vaadin.ui.Label;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.ViewConstants;

public class ViewHelper {

    public static Label buildCreateAndModifyLabel(ResourceProxy resourceProxy) {
        final Label createAndMofidyLabel =
            new Label(ViewConstants.CREATED_BY + " " + resourceProxy.getCreator() + " on "
                + resourceProxy.getCreatedOn() + "<br/>" + ViewConstants.LAST_MODIFIED_BY + " "
                + resourceProxy.getModifier() + " on " + resourceProxy.getModifiedOn(), Label.CONTENT_XHTML);

        createAndMofidyLabel.setStyleName("floatright columnheight50");
        createAndMofidyLabel.setWidth("65%");

        return createAndMofidyLabel;
    }

}