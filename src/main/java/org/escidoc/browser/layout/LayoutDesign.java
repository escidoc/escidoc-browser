/**
 * 
 */
package org.escidoc.browser.layout;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

import com.vaadin.ui.Component;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;

/**
 * @author ajb More should be added here
 */
public interface LayoutDesign {

    void init(
        Window mainWindow, EscidocServiceLocation serviceLocation, BrowserApplication app, CurrentUser currentUser,
        Repositories repositories, Router router) throws EscidocClientException;

    void openView(Component cmp, String title);

}
