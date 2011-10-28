package org.escidoc.browser.ui.helper.internal;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.BrowserApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CookieHandler {
    static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private BrowserApplication app;

    private HttpServletResponse response;

    private HttpServletRequest request;

    public CookieHandler(BrowserApplication app) {
        this.app = app;
        this.response = app.getResponse();
        this.request = app.getRequest();
    }

    public void setCookie(String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        // Use a fixed path
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(AppConstants.COOKIE_MAX_AGE); // One hour
        response.addCookie(cookie);
    }

    public String getCookieValue(String cookieName) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(AppConstants.COOKIE_NAME)) {
                LOG.debug("E GJETA COOKIE " + cookie.getValue());
                return cookie.getValue();
            }
        }
        return null;
    }

    public void eraseCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setPath(AppConstants.COOKIE_PATH);
        cookie.setMaxAge(0); // Delete
        response.addCookie(cookie);
    }
}
