//package org.escidoc.browser.ui.helper;
//
//import javax.servlet.http.HttpSession;
//
//public class SessionHandlerImpl {
//    private final HttpSession session;
//
//    public SessionHandlerImpl(final HttpSession session) {
//        this.session = session;
//    }
//
//    /**
//     * Here we set a cookie in case of a succesfull login The cookie should
//     * contain the username? TODO Ask Michael for description of is inside the
//     * esceidochandler
//     */
//    public void doLogin(final String handler) {
//        if (session.isNew()) {
//            session.setAttribute("eSDB-handler", handler);
//            System.out.println("Setting the cookie, doch" + handler);
//        }
//        else {
//            session.setAttribute("eSDB-handler", handler);
//            System.out.println("Session already set - No need to set session");
//            System.out.println(geteSDBHandlervalue());
//        }
//
//    }
//
//    public String geteSDBHandlervalue() {
//        try {
//            return (String) session.getAttribute("eSDB-handler");
//        }
//        catch (final Exception e) {
//            return "Problem";
//        }
//    }
//
//    public boolean isLoggedin() {
//        if (session.getAttribute("eSDB-handler") == null) {
//            return false;
//        }
//        else {
//            return true;
//            // session.getAttribute("eSDB-handler");
//        }
//    }
//
//    public void doLogout() {
//        session.setAttribute("eSDB-handler", null);
//    }
//
// }
