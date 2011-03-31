package org.escidoc.browser.ui.maincontent;

import org.escidoc.browser.ui.MainSite;

import com.vaadin.ui.AbstractSelect.Filtering;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

public class SearchAdvanced extends VerticalLayout {

    private MainSite mainSite;

    private final int appHeight;

    public SearchAdvanced(MainSite mainSite, final int appHeight) {
        this.mainSite = mainSite;
        this.appHeight = appHeight;
        setWidth("100.0%");
        setHeight(appHeight + "px");
        setMargin(true);

        // CssLayout to hold the BreadCrumb
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");

        // Here comes the breadcrumb menu
        final BreadCrumbMenu bm = new BreadCrumbMenu(cssLayout, "search");

        // CssLayout to hold search elements
        CssLayout srchContainer = new CssLayout();
        srchContainer.setWidth("60%");
        srchContainer.setStyleName("paddingtop20 aligncenter");

        // Css Hack * Clear Div
        Label lblClear = new Label();
        lblClear.setStyleName("clear");

        TextField txtTitle = new TextField();
        txtTitle.setInputPrompt("Title");
        txtTitle.setImmediate(false);

        TextField txtDescription = new TextField();
        txtDescription.setInputPrompt("Description");
        txtDescription.setImmediate(false);

        // Clean Divs
        cssLayout.addComponent(lblClear);

        TextField txtCreator = new TextField();
        txtCreator.setInputPrompt("Creator");
        txtCreator.setImmediate(false);

        // DatePicker for CreationDate
        PopupDateField creationDate = new PopupDateField();
        creationDate.setInputPrompt("Start date");
        creationDate.setResolution(PopupDateField.RESOLUTION_DAY);
        creationDate.setImmediate(false);

        // Dropdown for MimeType
        final String[] mimetypes =
            new String[] { "Berlin", "Brussels", "Helsinki", "Madrid", "Oslo",
                "Paris", "Stockholm" };
        ComboBox mimes = new ComboBox();
        for (int i = 0; i < mimetypes.length; i++) {
            mimes.addItem(mimetypes[i]);
        }
        mimes.setInputPrompt("Mime Types");
        mimes.setFilteringMode(Filtering.FILTERINGMODE_OFF);
        mimes.setImmediate(true);

        // Dropdown for Resource Type
        final String[] resourcearr =
            new String[] { "Berlin", "Brussels", "Helsinki", "Madrid", "Oslo",
                "Paris", "Stockholm" };
        ComboBox resource = new ComboBox();
        for (int i = 0; i < resourcearr.length; i++) {
            resource.addItem(resourcearr[i]);
        }
        resource.setInputPrompt("Resource Type");
        resource.setFilteringMode(Filtering.FILTERINGMODE_OFF);
        resource.setImmediate(true);

        TextField txtFullText = new TextField();
        txtFullText.setInputPrompt("FullText");
        txtFullText.setImmediate(false);

        Button bSearch = new Button("Search", this, "onClick");
        bSearch.setDescription("Search Tooltip");

        // Placing the elements in the design:
        txtTitle.setWidth("50%");
        txtTitle.setStyleName("floatleft paddingtop20 aligncenter");
        srchContainer.addComponent(txtTitle);

        txtDescription.setWidth("50%");
        txtDescription.setStyleName("floatright paddingtop20 aligncenter");
        srchContainer.addComponent(txtDescription);

        txtCreator.setWidth("50%");
        txtCreator.setStyleName("floatleft paddingtop20");
        srchContainer.addComponent(txtCreator);

        creationDate.setWidth("50%");
        creationDate.setStyleName("floatright");
        srchContainer.addComponent(creationDate);

        // Clean Divs
        srchContainer.addComponent(lblClear);

        mimes.setWidth("45%");
        mimes.setStyleName("floatleft");
        srchContainer.addComponent(mimes);

        resource.setWidth("45%");
        resource.setStyleName("floatright");
        srchContainer.addComponent(resource);

        txtFullText.setWidth("70%");
        txtFullText.setStyleName("floatleft");
        srchContainer.addComponent(txtFullText);

        bSearch.setStyleName("floatright");
        srchContainer.addComponent(bSearch);

        addComponent(cssLayout);
        addComponent(srchContainer);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6
     * for login TODO consider including the window of login from the remote
     * server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(Button.ClickEvent event) {
        SearchResults srchResults = new SearchResults(mainSite, appHeight);
        this.mainSite.openTab(srchResults, "Search?");

    }
}
