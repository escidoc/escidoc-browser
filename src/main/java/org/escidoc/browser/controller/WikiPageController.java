package org.escidoc.browser.controller;

import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.WikiPageView;

/**
 * 
 * @author ajb <br />
 *         org.escidoc.browser.Controller=org.escidoc.browser.WikiPage;
 *         http://www.w3.org/1999/02/22-rdf-syntax-ns#type=org.escidoc.resources.Item;
 */
public class WikiPageController extends Controller {

    public WikiPageController(Repositories repositories, Router router, ResourceProxy resourceProxy) {
        super(repositories, router, resourceProxy);
        createView();
    }

    @Override
    public void createView() {
        view = new WikiPageView(getRouter(), getResourceProxy(), this);

    }

}
