/**
 * CDDL HEADER START
 *
 * The contents of this file are subject to the terms of the
 * Common Development and Distribution License, Version 1.0 only
 * (the "License").  You may not use this file except in compliance
 * with the License.
 *
 * You can obtain a copy of the license at license/ESCIDOC.LICENSE
 * or https://www.escidoc.org/license/ESCIDOC.LICENSE .
 * See the License for the specific language governing permissions
 * and limitations under the License.
 *
 * When distributing Covered Code, include this CDDL HEADER in each
 * file and include the License file at license/ESCIDOC.LICENSE.
 * If applicable, add the following below this CDDL HEADER, with the
 * fields enclosed by brackets "[]" replaced with your own identifying
 * information: Portions Copyright [yyyy] [name of copyright owner]
 *
 * CDDL HEADER END
 *
 *
 *
 * Copyright 2012 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import com.google.common.base.Preconditions;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import org.escidoc.browser.layout.LayoutDesign;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;

/**
 * @author ARB
 * 
 */
@SuppressWarnings("serial")
public class SimpleSearch extends VerticalLayout {

    private TextField searchField = new TextField();

    private final Button searchBtn;

    private final Button advancedBtn;

    private final Router router;

    private final EscidocServiceLocation serviceLocation;

    private final Repositories repositories;

    private LayoutDesign layout;

    public SimpleSearch(final Router router, LayoutDesign layout, final EscidocServiceLocation serviceLocation,
        final Repositories repositories) {
        Preconditions.checkNotNull(router, "mainSite is null: %s", router);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.layout = layout;
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;

        final CustomLayout custom = new CustomLayout("simplesearch");
        addComponent(custom);

        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        searchField = new TextField();
        searchField.setWidth("268px");
        searchField.setHeight("-1px");
        searchField.setImmediate(false);

        // button_1
        searchBtn = new Button("Search", this, "onClick");
        searchBtn.setImmediate(true);

        // Advanced
        advancedBtn = new Button("Advanced Search", this, "onClickAdvSearch");
        advancedBtn.setStyleName(BaseTheme.BUTTON_LINK);
        advancedBtn.setImmediate(true);

        custom.addComponent(advancedBtn, "btnAdvanced");
        custom.addComponent(searchField, "searchfld");
        custom.addComponent(searchBtn, "searchbtn");

    }

    /**
     * Handle the Search Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(final Button.ClickEvent event) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        router.openTab(new SearchResultsView(router, layout, "null", serviceLocation, repositories), "Search Results");

    }

    /**
     * Handle the Advanced Search Event! At the moment a new window is opened to escidev6 for login TODO consider
     * including the window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClickAdvSearch(final Button.ClickEvent event) {
        final SearchAdvancedView advSearch = new SearchAdvancedView(router, serviceLocation);
        router.openTab(advSearch, "Search Results");
    }
}
