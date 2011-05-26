package org.escidoc.browser.ui.maincontent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.MainSite;

import com.vaadin.terminal.UserError;
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

    TextField txtTitle;

    TextField txtCreator;

    TextField txtFullText;

    TextField txtDescription;

    PopupDateField creationDate;

    ComboBox mimes;

    ComboBox resource;

    private final EscidocServiceLocation serviceLocation;

    public SearchAdvancedView(MainSite mainSite, final EscidocServiceLocation serviceLocation) {
        this.mainSite = mainSite;
        this.appHeight = mainSite.getApplicationHeight();
        this.serviceLocation = serviceLocation;
        setWidth("100.0%");
        setHeight("85%");
        setMargin(true);

        // CssLayout to hold the BreadCrumb
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("60%");
        cssLayout.setCaption("Advanced Search");
        // Css Hack * Clear Div
        Label lblClear = new Label();
        lblClear.setStyleName("clear");

        txtTitle = new TextField();
        txtTitle.setInputPrompt("Title");
        txtTitle.setImmediate(false);
        txtDescription = new TextField();
        txtDescription.setInputPrompt("Description");
        txtDescription.setImmediate(false);
        // Clean Divs
        cssLayout.addComponent(lblClear);

        txtCreator = new TextField();
        txtCreator.setInputPrompt("Creator");
        txtCreator.setImmediate(false);
        // DatePicker for CreationDate
        creationDate = new PopupDateField();
        creationDate.setInputPrompt("Creation date");
        creationDate.setResolution(PopupDateField.RESOLUTION_DAY);
        creationDate.setImmediate(false);

        // Dropdown for MimeType
        final String[] mimetypes =
            new String[] { "application/octet-stream", "text/html", "audio/aiff", "video/avi", "image/bmp",
                "application/book", "text/plain", "image/gif", "image/jpeg", "audio/midi", "video/quicktime",
                "audio/mpeg", "application/xml", "text/xml" };
        mimes = new ComboBox();

        for (int i = 0; i < mimetypes.length; i++) {
            mimes.addItem(mimetypes[i]);
        }
        mimes.setInputPrompt("Mime Types");
        mimes.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
        mimes.setImmediate(true);

        // Dropdown for Resource Type
        final String[] resourcearr = new String[] { "Context", "Container", "Item" };
        resource = new ComboBox();
        for (int i = 0; i < resourcearr.length; i++) {
            resource.addItem(resourcearr[i]);
        }
        resource.setInputPrompt("Resource Type");
        resource.setFilteringMode(Filtering.FILTERINGMODE_OFF);
        resource.setImmediate(true);

        txtFullText = new TextField();
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
        this.setComponentAlignment(cssLayout, VerticalLayout.ALIGNMENT_HORIZONTAL_CENTER,
            VerticalLayout.ALIGNMENT_VERTICAL_CENTER);
    }

    /**
     * Handle the Login Event! At the moment a new window is opened to escidev6 for login TODO consider including the
     * window of login from the remote server in a iframe within the MainContent Window
     * 
     * @param event
     */
    public void onClick(Button.ClickEvent event) {
        final String titleTxt = (String) txtTitle.getValue();
        final String creatorTxt = (String) txtCreator.getValue();
        final String descriptionTxt = (String) txtDescription.getValue();
        final String creationDateTxt = convertDateToTime((Date) creationDate.getValue());
        final String mimesTxt = (String) mimes.getValue();
        final String resourceTxt = (String) resource.getValue();
        final String fulltxtTxt = (String) txtFullText.getValue();

        if (validateInputs(titleTxt, creatorTxt, descriptionTxt, creationDateTxt, mimesTxt, resourceTxt, fulltxtTxt)) {
            final SearchResultsView srchRes =
                new SearchResultsView(mainSite, serviceLocation, titleTxt, creatorTxt, descriptionTxt, creationDateTxt,
                    mimesTxt, resourceTxt, fulltxtTxt);
            this.mainSite.openTab(srchRes, "Advanced Search " + titleTxt + " " + creationDateTxt + " " + mimesTxt);
        }
        else {
            txtTitle.setComponentError(new UserError(
                "Please fill in one of the fields by enterin 3 or more alphabet characters"));
        }
    }

    /**
     * Checking if at least one element is filled in the form and it has a sensful value
     * 
     * @param titleTxt
     * @param creatorTxt
     * @param descriptionTxt
     * @param creationDateTxt
     * @param mimesTxt
     * @param resourceTxt
     * @param fulltxtTxt
     * @return
     */
    public boolean validateInputs(
        String titleTxt, String creatorTxt, String descriptionTxt, String creationDateTxt, String mimesTxt,
        String resourceTxt, String fulltxtTxt) {

        if ((!titleTxt.isEmpty() && validateValidInputs(titleTxt))
            || (!creatorTxt.isEmpty() && validateValidInputs(creatorTxt))
            || (!descriptionTxt.isEmpty() && validateValidInputs(descriptionTxt)) || creationDateTxt != null
            || mimesTxt != null && ((resourceTxt != null) && validateValidInputs(creatorTxt))
            || (!fulltxtTxt.isEmpty() && validateValidInputs(fulltxtTxt))) {
            return true;
        }
        return false;
    }

    /**
     * Handle Search Query Validation Check string length Any possible injections
     * 
     * @param searchString
     * @return boolean
     */
    private boolean validateValidInputs(String term) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_.\\s\":#*]{3,}");
        final Matcher m = p.matcher(term);
        return m.matches();
    }

    private String convertDateToTime(Date date) {
        SimpleDateFormat dateformatYYYYMMDD = new SimpleDateFormat("yyyy-MM-dd");
        try {
            String nowYYYYMMDD = new String(dateformatYYYYMMDD.format(date));
            return nowYYYYMMDD;
        }
        catch (Exception e) {
            return null;
        }
    }
}
