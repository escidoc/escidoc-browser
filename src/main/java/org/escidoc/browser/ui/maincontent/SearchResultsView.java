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

import java.util.List;

import org.escidoc.browser.model.CurrentUser;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.repository.internal.SearchRepositoryImpl;
import org.escidoc.browser.ui.MainSite;

import com.google.common.base.Preconditions;
import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.om.container.Container;
import de.escidoc.core.resources.om.context.Context;
import de.escidoc.core.resources.sb.search.SearchResult;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;

@SuppressWarnings("serial")
public class SearchResultsView extends VerticalLayout {

    private static final String DATE_CREATED = "Date Created";

    private Repositories repositories;

    private PagedTable tblPagedResults;

    private Button btnAdvancedSearch;

    private final MainSite mainSite;

    private final EscidocServiceLocation serviceLocation;

    private final SearchRetrieveResponse results;

    private CurrentUser currentUser;

    private ResourceModelFactory resourceModelFactory;

    /**
     * The constructor should first build the main layout, set the composition root and then do any custom
     * initialization. The constructor will not be automatically regenerated by the visual editor.
     * 
     * @param mainSite
     * @param serviceLocation
     * @param repositories
     * @param currentUser
     */
    public SearchResultsView(final MainSite mainSite, final String searchString,
        final EscidocServiceLocation serviceLocation, final Repositories repositories, final CurrentUser currentUser) {
        Preconditions.checkNotNull(mainSite, "mainSite is null: %s", mainSite);
        Preconditions.checkNotNull(searchString, "searchString is null: %s", searchString);
        Preconditions.checkNotNull(serviceLocation, "serviceLocation is null: %s", serviceLocation);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        Preconditions.checkNotNull(currentUser, "currentUser is null: %s", currentUser);

        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
        this.repositories = repositories;
        this.currentUser = currentUser;

        resourceModelFactory = new ResourceModelFactory(repositories);
        final CssLayout cssLayout = configureLayout();
        final SearchRepositoryImpl srRep = new SearchRepositoryImpl(serviceLocation);
        results = srRep.simpleSearch(searchString.trim());
        try {
            createPaginationTblResults(cssLayout);
        }
        catch (final EscidocClientException e) {
            mainSite.getApplication().getMainWindow().showNotification(e.getMessage());
        }
        addComponent(cssLayout);
    }

    public SearchResultsView(final MainSite mainSite, final EscidocServiceLocation serviceLocation,
        final String titleTxt, final String creatorTxt, final String descriptionTxt, final String creationDateTxt,
        final String mimesTxt, final String resourceTxt, final String fulltxtTxt) {
        this.mainSite = mainSite;
        this.serviceLocation = serviceLocation;
        final CssLayout cssLayout = configureLayout();
        final SearchRepositoryImpl srRep = new SearchRepositoryImpl(serviceLocation);
        results =
            srRep.advancedSearch(titleTxt, creatorTxt, descriptionTxt, creationDateTxt, mimesTxt, resourceTxt,
                fulltxtTxt);
        try {
            createPaginationTblResults(cssLayout);
        }
        catch (final EscidocClientException e) {
            mainSite.getApplication().getMainWindow().showNotification(e.getMessage());
        }
        addComponent(cssLayout);
    }

    private CssLayout configureLayout() {
        setWidth("100.0%");
        setHeight("92%");
        setMargin(true);
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        addAdvancedSearchBtn(cssLayout);
        return cssLayout;
    }

    private void createPaginationTblResults(final CssLayout cssLayout) throws EscidocClientException {

        // The ResourceModelFactory will not be used once Michael corrects the search indexes to index XLinkTitle
        // ResourceModelFactory rmf =
        // new ResourceModelFactory(new ItemRepository(serviceLocation), new ContainerRepository(serviceLocation),
        // new ContextRepository(serviceLocation));
        ResourceProxy resourceProxy = null;

        tblPagedResults = createPagedTable(cssLayout);

        final List<SearchResultRecord> records = results.getRecords();

        final IndexedContainer container = createPagedTableItemContainer();

        // Adding items in the container
        for (final SearchResultRecord record : records) {
            final SearchResult s = record.getRecordData();
            String strResourceType = "";
            if (s.getContent() instanceof Container) {
                resourceProxy = new ContainerProxyImpl((Container) s.getContent());
                strResourceType = "Container";
            }
            else if (s.getContent() instanceof de.escidoc.core.resources.om.item.Item) {
                final Resource item = (de.escidoc.core.resources.om.item.Item) s.getContent();
                resourceProxy = new ItemProxyImpl((de.escidoc.core.resources.om.item.Item) item);
                strResourceType = "Item";
            }
            else if (s.getContent() instanceof Context) {
                final Resource resource = (Context) s.getContent();
                resourceProxy = new ContextProxyImpl((Context) resource);
                strResourceType = "Context";
            }

            final Object[] variablesForTheTab = { strResourceType, resourceProxy.getId(), resourceProxy.getId() };

            final Item item = container.addItem(variablesForTheTab);
            item.getItemProperty("Type").setValue(
                new Label("<img src= \"/browser/VAADIN/themes/myTheme/images/" + "Container.png\" />",
                    Label.CONTENT_RAW));
            item.getItemProperty("Name").setValue(resourceProxy.getName());

            item.getItemProperty(DATE_CREATED).setValue(resourceProxy.getCreatedOn());
        }

        // Populate the table with results
        tblPagedResults.setContainerDataSource(container);
        final Label lblResults =
            new Label("We found " + results.getNumberOfMatchingRecords() + " results for your search.");
        cssLayout.addComponent(lblResults);
        cssLayout.addComponent(tblPagedResults);
        cssLayout.addComponent(tblPagedResults.createControls());
    }

    /**
     * Create a Vaadin Container to contain elements of the PagedTable Used at createPaginationTblResults
     * 
     * @return IndexedContainer
     */
    private IndexedContainer createPagedTableItemContainer() {
        // Creating a container for the item
        final IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("Type", Label.class, null);
        container.addContainerProperty("Name", String.class, null);
        container.addContainerProperty(DATE_CREATED, String.class, null);
        return container;
    }

    private PagedTable createPagedTable(final CssLayout cssLayout) {
        tblPagedResults = new PagedTable();
        tblPagedResults.setImmediate(true);
        tblPagedResults.setWidth("100.0%");
        tblPagedResults.setPageLength(15);
        tblPagedResults.setImmediate(true);
        tblPagedResults.setSelectable(true);

        tblPagedResults.addContainerProperty("Type", Label.class, null);
        tblPagedResults.addContainerProperty("Name", String.class, null);
        tblPagedResults.addContainerProperty(DATE_CREATED, String.class, null);
        tblPagedResults.setColumnWidth("Type", 30);
        tblPagedResults.setColumnWidth(DATE_CREATED, 90);
        /**
         * Create new Tab based on the click The event registers a View (see ItemView for example) and a Name for the
         * new TAB
         */

        tblPagedResults.addListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(final ValueChangeEvent event) {
                final Object[] variablesForTheTab = (Object[]) tblPagedResults.getValue();
                if (variablesForTheTab == null) {
                    return;
                }
                openSelectedSearchResult(variablesForTheTab);
            }

            private void openSelectedSearchResult(final Object[] variablesForTheTab) {
                mainSite.openTab(buildResourceView(variablesForTheTab), getTabName(variablesForTheTab));
            }

            private String getTabName(final Object[] variablesForTheTab) {
                return (String) variablesForTheTab[2];
            }

            private Component buildResourceView(final Object[] variablesForTheTab) {
                if (variablesForTheTab[0].equals(ResourceType.CONTAINER.asLabel())) {
                    try {
                        return new ContainerView(serviceLocation, mainSite, find(variablesForTheTab,
                            ResourceType.CONTAINER), mainSite.getWindow(), mainSite.getCurrentUser(), repositories);
                    }
                    catch (final EscidocClientException e) {
                        showerror();
                    }
                }
                else if (variablesForTheTab[0].equals(ResourceType.ITEM.asLabel())) {
                    try {
                        return new ItemView(serviceLocation, repositories, mainSite, find(variablesForTheTab,
                            ResourceType.ITEM), mainSite.getWindow(), currentUser);
                    }
                    catch (final EscidocClientException e) {
                        showerror();
                    }
                }
                else if (variablesForTheTab[0].equals(ResourceType.CONTEXT.asLabel())) {
                    try {
                        return new ContextView(serviceLocation, mainSite,
                            find(variablesForTheTab, ResourceType.CONTEXT), mainSite.getWindow(), mainSite
                                .getCurrentUser(), repositories);
                    }
                    catch (final EscidocClientException e) {
                        showerror();
                    }
                }
                return new VerticalLayout();
            }

            private ResourceProxy find(final Object[] variablesForTheTab, final ResourceType resourceType)
                throws EscidocClientException {
                return (ResourceProxy) resourceModelFactory.find((String) variablesForTheTab[1], resourceType);
            }
        });
        return tblPagedResults;
    }

    private void addAdvancedSearchBtn(final CssLayout cssLayout) {
        // here comes the Advanced search label
        // Login
        btnAdvancedSearch = new Button("Advanced Search", this, "onClick");
        btnAdvancedSearch.setStyleName(BaseTheme.BUTTON_LINK);
        btnAdvancedSearch.setImmediate(true);
        btnAdvancedSearch.setStyleName("v-button-link floatright");
        cssLayout.addComponent(btnAdvancedSearch);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(final Button.ClickEvent event) {
        final SearchAdvancedView srch = new SearchAdvancedView(mainSite, serviceLocation);
        mainSite.openTab(srch, "Advanced Search");
    }

    public void showerror() {
        getWindow().showNotification("There was an error rendering the form");
    }

}
