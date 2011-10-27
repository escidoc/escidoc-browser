package org.escidoc.browser.ui.helper;

import com.vaadin.terminal.gwt.server.HttpServletRequestListener;

public interface CookieHandler extends HttpServletRequestListener {

    String getESciDocUserHandle();

}
