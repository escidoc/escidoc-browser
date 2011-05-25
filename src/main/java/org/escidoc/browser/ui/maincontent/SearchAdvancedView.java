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

public class SearchAdvancedView extends VerticalLayout {

    private final MainSite mainSite;

    private final int appHeight;

    public SearchAdvancedView(MainSite mainSite, final int appHeight) {
        this.mainSite = mainSite;
        this.appHeight = appHeight;
        setWidth("100.0%");
        setHeight("85%");
        setMargin(true);

        // CssLayout to hold the BreadCrumb
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("60%");
        // cssLayout.setHeight("100%");
        cssLayout.setStyleName("aligncenter");

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
            new String[] { "application/octet-stream", "text/html", "audio/aiff", "video/avi", "image/bmp",
                "application/book", "text/plain", "image/gif", "image/jpeg", "audio/midi", "video/quicktime",
                "audio/mpeg", "application/xml", "text/xml" };
        ComboBox mimes = new ComboBox();

        for (int i = 0; i < mimetypes.length; i++) {
            mimes.addItem(mimetypes[i]);
        }
        mimes.setInputPrompt("Mime Types");
        mimes.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
        mimes.setImmediate(true);

        // Dropdown for Resource Type
        final String[] resourcearr = new String[] { "Context", "Container", "Item" };
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
        txtTitle.setStyleName("floatleft paddingtop20 ");
        cssLayout.addComponent(txtTitle);

        txtDescription.setWidth("50%");
        txtDescription.setStyleName("floatright paddingtop20 ");
        cssLayout.addComponent(txtDescription);

        txtCreator.setWidth("50%");
        txtCreator.setStyleName("floatleft paddingtop20");
        cssLayout.addComponent(txtCreator);

        creationDate.setWidth("50%");
        creationDate.setStyleName("floatright");
        cssLayout.addComponent(creationDate);

        // Clean Divs
        cssLayout.addComponent(lblClear);

        mimes.setWidth("45%");
        mimes.setStyleName("floatleft");
        cssLayout.addComponent(mimes);

        resource.setWidth("45%");
        resource.setStyleName("floatright");
        cssLayout.addComponent(resource);

        txtFullText.setWidth("70%");
        txtFullText.setStyleName("floatleft");
        cssLayout.addComponent(txtFullText);

        bSearch.setStyleName("floatright");
        cssLayout.addComponent(bSearch);

        addComponent(cssLayout);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(Button.ClickEvent event) {
        SearchResultsView srchResults = new SearchResultsView(mainSite, appHeight, "null", null);
        this.mainSite.openTab(srchResults, "Search?");

    }
}
