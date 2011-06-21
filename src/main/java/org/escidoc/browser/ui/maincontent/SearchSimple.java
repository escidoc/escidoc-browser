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
 * Copyright 2011 Fachinformationszentrum Karlsruhe Gesellschaft
 * fuer wissenschaftlich-technische Information mbH and Max-Planck-
 * Gesellschaft zur Foerderung der Wissenschaft e.V.
 * All rights reserved.  Use is subject to license terms.
 */
package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

/**
 * NOT READY YET
 * 
 * @author ARB
 * 
 */
public class SearchSimple extends VerticalLayout {

    private TextField searchfld = new TextField();

    private final Button searchbtn;

    private final Button btnAdvanced;

    private final MainSite mainSite;

    private final int appHeight;

    private final EscidocServiceLocation serviceLocation;

    public SearchSimple(MainSite mainSite, EscidocServiceLocation serviceLocation) {
        this.mainSite = mainSite;
        this.appHeight = mainSite.getApplicationHeight();
        this.serviceLocation = serviceLocation;

        final CustomLayout custom = new CustomLayout("simplesearch");
        addComponent(custom);
        // top-level component properties
        setWidth("100.0%");
        setHeight("100.0%");

        // textField_1
        searchfld = new TextField();
        searchfld.setWidth("268px");
        searchfld.setHeight("-1px");
        searchfld.setImmediate(false);

        // button_1
        searchbtn = new Button("Search", this, "onClick");
        searchbtn.setImmediate(true);

        // Advanced
        btnAdvanced = new Button("Advanced Search", this, "onClickAdvSearch");
        btnAdvanced.setStyleName(BaseTheme.BUTTON_LINK);
        btnAdvanced.setImmediate(true);

        custom.addComponent(btnAdvanced, "btnAdvanced");
        custom.addComponent(searchfld, "searchfld");
        custom.addComponent(searchbtn, "searchbtn");

    }

    /**
     * Handle the Search Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(Button.ClickEvent event) {
        SearchResultsView smpSearch = new SearchResultsView(mainSite, "null", null);
        this.mainSite.openTab(smpSearch, "Search Results");

    }

    /**
     * Handle the Advanced Search Event! At the moment a new window is opened to escidev6 for login TODO consider
     * including the window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClickAdvSearch(Button.ClickEvent event) {
        SearchAdvancedView advSearch = new SearchAdvancedView(mainSite, serviceLocation);
        this.mainSite.openTab(advSearch, "Search Results");

    }
}
