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

import com.jensjansson.pagedtable.PagedTable;
import com.vaadin.annotations.AutoGenerated;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.IndexedContainer;
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

    private PagedTable tblPagedResults;

    private static final Logger LOG = LoggerFactory.getLogger(BrowserApplication.class);

    private final int appHeight;

    private Button btnAdvancedSearch;

    private final MainSite mainSite;

    private String searchString = "";

    private final EscidocServiceLocation serviceLocation;

    SearchRetrieveResponse results;

    // this is used for the view (ItemView)
    private Component cmp;

    /**
     * The constructor should first build the main layout, set the composition root and then do any custom
     * initialization. The constructor will not be automatically regenerated by the visual editor.
     * 
     * @param mainSite
     * @param serviceLocation
     */
    public SearchResultsView(MainSite mainSite, String searchString, EscidocServiceLocation serviceLocation) {
        this.mainSite = mainSite;
        this.appHeight = mainSite.getApplicationHeight();
        this.searchString = searchString;
        this.serviceLocation = serviceLocation;
        final CssLayout cssLayout = configureLayout();
        SearchRepositoryImpl srRep = new SearchRepositoryImpl(serviceLocation);
        results = srRep.simpleSearch(searchString.trim());
        try {
            createPaginationTblResults(cssLayout);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addComponent(cssLayout);
    }

    public SearchResultsView(MainSite mainSite, EscidocServiceLocation serviceLocation, String titleTxt,
        String creatorTxt, String descriptionTxt, String creationDateTxt, String mimesTxt, String resourceTxt,
        String fulltxtTxt) {
        this.mainSite = mainSite;
        this.appHeight = mainSite.getApplicationHeight();
        this.serviceLocation = serviceLocation;
        final CssLayout cssLayout = configureLayout();
        SearchRepositoryImpl srRep = new SearchRepositoryImpl(serviceLocation);
        results =
            srRep.advancedSearch(titleTxt, creatorTxt, descriptionTxt, creationDateTxt, mimesTxt, resourceTxt,
                fulltxtTxt);
        try {
            createPaginationTblResults(cssLayout);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        addComponent(cssLayout);
    }

    /**
     * Build Layout
     * 
     * @return
     */
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

    /**
     * This is the pagination version of the table
     * 
     * @param cssLayout
     * @throws EscidocClientException
     */
    private void createPaginationTblResults(final CssLayout cssLayout) throws EscidocClientException {

        // The ResourceModelFactory will not be used once Michael corrects the search indexes to index XLinkTitle
        ResourceModelFactory rmf =
            new ResourceModelFactory(new ItemRepository(serviceLocation), new ContainerRepository(serviceLocation),
                new ContextRepository(serviceLocation));
        ResourceProxy resourceProxy = null;

        tblPagedResults = createPagedTable(cssLayout);

        Collection<Record<?>> records = results.getRecords();

        IndexedContainer container = createPagedTableItemContainer();

        // Adding items in the container
        int i = 0;
        for (Record<?> record : records) {
            SearchResultRecord s = ((SearchResultRecordRecord) record).getRecordData();
            if (s.getContent().getResourceType().toString().equals("Container")) {
                resourceProxy = (ContainerProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.CONTAINER);
                // Resource container = s.getContent();
                // resourceProxy = new ContainerProxyImpl((Container) s.getContent());
            }
            else if (s.getContent().getResourceType().toString().equals("Item")) {
                // Resource item = s.getContent();
                // resourceProxy = new ItemProxyImpl((de.escidoc.core.resources.om.item.Item) item);
                resourceProxy = (ItemProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.ITEM);
            }
            else if (s.getContent().getResourceType().toString().equals("Context")) {
                resourceProxy = (ContextProxyImpl) rmf.find(s.getContent().getObjid(), ResourceType.CONTEXT);
                // Resource resource = s.getContent();
                // resourceProxy = new ContextProxyImpl((Context) resource);
            }
            Object[] variablesForTheTab =
                { s.getContent().getResourceType().toString(), resourceProxy.getId(), resourceProxy.getName() };
            Item item = container.addItem(variablesForTheTab);
            item.getItemProperty("Type").setValue(
                new Label("<img src= \"/browser/VAADIN/themes/myTheme/images/resources/"
                    + s.getContent().getResourceType().toString() + ".png\" />", Label.CONTENT_RAW));
            item.getItemProperty("Name").setValue(resourceProxy.getName());
            item.getItemProperty("Belongs to Context").setValue(resourceProxy.getContext().getXLinkTitle());
            item.getItemProperty("Description").setValue(resourceProxy.getDescription());
            item.getItemProperty("Date Created").setValue(resourceProxy.getCreatedOn());
        }

        // Populate the table with results
        tblPagedResults.setContainerDataSource(container);
        Label lblResults = new Label("We found " + results.getNumberOfMatchingRecords() + " results for your search.");
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
        IndexedContainer container = new IndexedContainer();
        container.addContainerProperty("Type", Label.class, null);
        container.addContainerProperty("Name", String.class, null);
        container.addContainerProperty("Belongs to Context", String.class, null);
        container.addContainerProperty("Description", String.class, null);
        container.addContainerProperty("Date Created", String.class, null);
        return container;
    }

    private PagedTable createPagedTable(CssLayout cssLayout) {
        tblPagedResults = new PagedTable();
        tblPagedResults.setImmediate(true);
        tblPagedResults.setWidth("100.0%");
        tblPagedResults.setPageLength(15);
        tblPagedResults.setImmediate(true);
        tblPagedResults.setSelectable(true);

        tblPagedResults.addContainerProperty("Type", Label.class, null);
        tblPagedResults.addContainerProperty("Name", String.class, null);
        tblPagedResults.addContainerProperty("Belongs to Context", String.class, null);
        tblPagedResults.addContainerProperty("Description", String.class, null);
        tblPagedResults.addContainerProperty("Date Created", String.class, null);
        tblPagedResults.setColumnWidth("Type", 30);
        tblPagedResults.setColumnWidth("Belongs to Context", 130);
        tblPagedResults.setColumnWidth("Date Created", 90);
        /**
         * Create new Tab based on the click The event registers a View (see ItemView for example) and a Name for the
         * new TAB
         */

        tblPagedResults.addListener(new Property.ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                ResourceModelFactory rmf =
                    new ResourceModelFactory(new ItemRepository(serviceLocation), new ContainerRepository(
                        serviceLocation), new ContextRepository(serviceLocation));
                ResourceProxy resourceProxy = null;
                Object[] variablesForTheTab = (Object[]) tblPagedResults.getValue();

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
        return tblPagedResults;
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
        SearchAdvancedView srch = new SearchAdvancedView(mainSite, serviceLocation);
        this.mainSite.openTab(srch, "Advanced Search");
    }

    public void showerror() {
        getWindow().showNotification("There was an error rendering the form");
    }

}
