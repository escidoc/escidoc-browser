package org.escidoc.browser.ui.maincontent;

import java.util.Collection;

import org.escidoc.browser.BrowserApplication;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceModelFactory;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.model.internal.ContextProxyImpl;
import org.escidoc.browser.repository.ContainerRepository;
import org.escidoc.browser.repository.ContextRepository;
import org.escidoc.browser.repository.ItemRepository;
import org.escidoc.browser.repository.internal.ContainerProxyImpl;
import org.escidoc.browser.repository.internal.ItemProxyImpl;
import org.escidoc.browser.repository.internal.SearchRepositoryImpl;
import org.escidoc.browser.ui.MainSite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.ui.AbsoluteLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.BaseTheme;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.sb.Record;
import de.escidoc.core.resources.sb.search.SearchResultRecord;
import de.escidoc.core.resources.sb.search.SearchRetrieveResponse;
import de.escidoc.core.resources.sb.search.records.SearchResultRecordRecord;

public class SearchResultsView extends VerticalLayout {

    @AutoGenerated
    private AbsoluteLayout mainLayout;

    @AutoGenerated
    private Table tblResults;

    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final int appHeight;

    private Button btnAdvancedSearch;

    private final MainSite mainSite;

    private final String searchString;

    private final EscidocServiceLocation serviceLocation;

    // this is used for the view (ItemView)
    private Component cmp;

    /**
     * The constructor should first build the main layout, set the composition root and then do any custom
     * initialization.
     * 
     * The constructor will not be automatically regenerated by the visual editor.
     * 
     * @param mainSite
     * @param serviceLocation
     */
    public SearchResultsView(MainSite mainSite, final int appHeight, String searchString,
        EscidocServiceLocation serviceLocation) {
        this.mainSite = mainSite;
        this.appHeight = appHeight;
        this.searchString = searchString;
        this.serviceLocation = serviceLocation;
        setWidth("100.0%");
        setHeight(appHeight + "px");
        setMargin(true);
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // Here comes the breadcrumb menu
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "search");

        addAdvancedSearchBtn(cssLayout);
        try {
            createTblResults(cssLayout);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        addComponent(cssLayout);
    }

    /**
     * Create table with the results We retrieve a SearchResultRecordRecord from the result Based on the Resource we
     * transform it to Context, Container or Item Create a Component that can be used in the view (ContainerView etc)
     * (just to avoid duplicate if cases on another method) Pass the arguments to the listener to open NewTab on the
     * mainSite
     * 
     * @param cssLayout
     * @throws EscidocClientException
     */
    private void createTblResults(final CssLayout cssLayout) throws EscidocClientException {
        Label lblResults = new Label("Search Results for: " + searchString);
        ResourceModelFactory rmf =
            new ResourceModelFactory(new ItemRepository(serviceLocation), new ContainerRepository(serviceLocation),
                new ContextRepository(serviceLocation));
        ResourceProxy resourceProxy = null;

        Table tbl = createTable(cssLayout);

        SearchRepositoryImpl srRep = new SearchRepositoryImpl(serviceLocation);
        SearchRetrieveResponse results = srRep.search("\"escidoc.any-title\"=" + searchString + "");
        Collection<Record<?>> records = results.getRecords();

        int i = 1;
        for (Record<?> record : records) {
            SearchResultRecord s = ((SearchResultRecordRecord) record).getRecordData();
            if (s.getContent().getResourceType().toString().equals("Container")) {
                resourceProxy = (ContainerProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.CONTAINER);
            }
            else if (s.getContent().getResourceType().toString().equals("Item")) {
                resourceProxy = (ItemProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.ITEM);
            }
            else if (s.getContent().getResourceType().toString().equals("Context")) {
                resourceProxy = (ContextProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.CONTEXT);
            }
            Object[] variablesForTheTab =
                { s.getContent().getResourceType().toString(), resourceProxy.getId(), resourceProxy.getName() };
            tbl.addItem(
                new Object[] {
                    new Label("<img src= \"/browser/VAADIN/themes/myTheme/images/resources/"
                        + s.getContent().getResourceType().toString() + ".png\" />", Label.CONTENT_RAW),
                    resourceProxy.getName(), resourceProxy.getDescription(), resourceProxy.getCreatedOn() },
                variablesForTheTab);
        }

        cssLayout.addComponent(lblResults);
        cssLayout.addComponent(tblResults);

    }

    /**
     * Create Table Layout and basic events
     * 
     * @param cssLayout
     * @return Table
     */
    private Table createTable(final CssLayout cssLayout) {
        tblResults = new Table();
        tblResults.setImmediate(true);
        tblResults.setWidth("100.0%");
        tblResults.setHeight("86%");
        tblResults.setImmediate(true);
        tblResults.setSelectable(true);

        tblResults.addContainerProperty("Type", Label.class, null);
        tblResults.addContainerProperty("Name", String.class, null);
        tblResults.addContainerProperty("Description", String.class, null);
        tblResults.addContainerProperty("Date Created", String.class, null);
        tblResults.setColumnWidth("Type", 30);
        tblResults.setColumnWidth("Date Created", 90);

        /**
         * Create new Tab based on the click The event registers a View (see ItemView for example) and a Name for the
         * new TAB
         */
        tblResults.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                ResourceModelFactory rmf =
                    new ResourceModelFactory(new ItemRepository(serviceLocation), new ContainerRepository(
                        serviceLocation), new ContextRepository(serviceLocation));
                ResourceProxy resourceProxy = null;
                Object[] variablesForTheTab = (Object[]) tblResults.getValue();

                if (variablesForTheTab[0].equals("Container")) {
                    try {
                        resourceProxy =
                            (ContainerProxyImpl) rmf.find((String) variablesForTheTab[1], ResourceType.CONTAINER);
                        cmp =
                            new ContainerView(serviceLocation, mainSite, resourceProxy, mainSite.getWindow(), mainSite
                                .getUser());
                    }
                    catch (EscidocClientException e) {
                        showerror();
                        // e.printStackTrace();
                    }
                }
                else if (variablesForTheTab[0].equals("Item")) {
                    try {
                        resourceProxy = (ItemProxyImpl) rmf.find((String) variablesForTheTab[1], ResourceType.ITEM);
                        cmp = new ItemView(serviceLocation, mainSite, resourceProxy, mainSite.getWindow());
                    }
                    catch (EscidocClientException e) {
                        showerror();
                        // e.printStackTrace();
                    }

                }
                else if (variablesForTheTab[0].equals("Context")) {
                    try {
                        resourceProxy =
                            (ContextProxyImpl) rmf.find((String) variablesForTheTab[1], ResourceType.CONTEXT);
                        cmp =
                            new ContextView(serviceLocation, mainSite, resourceProxy, mainSite.getWindow(), mainSite
                                .getUser());
                    }
                    catch (EscidocClientException e) {
                        showerror();
                        // e.printStackTrace();
                    }
                }
                mainSite.openTab(cmp, (String) variablesForTheTab[2]);
            }
        });
        return tblResults;

    }

    private void addAdvancedSearchBtn(final CssLayout cssLayout) {
        // here comes the Advanced search label
        // Login
        this.btnAdvancedSearch = new Button("Advanced Search", this, "onClick");
        this.btnAdvancedSearch.setStyleName(BaseTheme.BUTTON_LINK);
        this.btnAdvancedSearch.setImmediate(true);
        this.btnAdvancedSearch.setStyleName("v-button-link floatright");
        cssLayout.addComponent(this.btnAdvancedSearch);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(Button.ClickEvent event) {
        SearchAdvanced srch = new SearchAdvanced(mainSite, appHeight);
        this.mainSite.openTab(srch, "Advanced Search");
    }

    public void showerror() {
        getWindow().showNotification("There was an error rendering the form");
    }
}
