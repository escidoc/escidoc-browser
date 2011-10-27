package org.escidoc.browser.ui.helper.internal;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vaadin.terminal.gwt.server.HttpServletRequestListener;

public class CookieHandlerImpl extends CookieHandler implements HttpServletRequestListener {
    HttpServletResponse response;

    private String eSciDocUserHandle;

    @Override
    public Map<String, List<String>> get(URI arg0, Map<String, List<String>> arg1) throws IOException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void put(URI arg0, Map<String, List<String>> arg1) throws IOException {
        // TODO Auto-generated method stub

    }

    @Override
    public void onRequestStart(HttpServletRequest request, HttpServletResponse response) {
        this.response = response;
        if (eSciDocUserHandle == null) {
            Cookie[] cookies = request.getCookies();
            for (int i = 0; i < cookies.length; i++) {
                if ("username".equals(cookies[i].getName()))
                    eSciDocUserHandle = cookies[i].getValue();
            }
        }

    }

    @Override
    public void onRequestEnd(HttpServletRequest request, HttpServletResponse response) {
        // TODO Auto-generated method stub

    }

}
