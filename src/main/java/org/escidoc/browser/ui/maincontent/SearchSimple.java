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
