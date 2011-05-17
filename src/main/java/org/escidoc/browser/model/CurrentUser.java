package org.escidoc.browser.model;

public interface CurrentUser {

    boolean isGuest();

    String getToken();

    String getLoginName();
}
