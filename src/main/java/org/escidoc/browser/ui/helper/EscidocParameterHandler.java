package org.escidoc.browser.ui.helper;

import org.escidoc.browser.model.EscidocServiceLocation;

import com.vaadin.terminal.ParameterHandler;

public interface EscidocParameterHandler extends ParameterHandler {

    EscidocServiceLocation getServiceLocation();

}
