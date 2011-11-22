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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.ui.Router;

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

    private final Router router;

    TextField txtTitle;

    TextField txtCreator;

    TextField txtFullText;

    TextField txtDescription;

    PopupDateField creationDate;

    ComboBox mimes;

    ComboBox resource;

    private final EscidocServiceLocation serviceLocation;

    public SearchAdvancedView(final Router router, final EscidocServiceLocation serviceLocation) {
        this.router = router;
        this.serviceLocation = serviceLocation;
        setWidth("100.0%");
        setHeight("85%");
        setMargin(true);

        // CssLayout to hold the BreadCrumb
        final CssLayout cssLayout = new CssLayout();
        cssLayout.setWidth("60%");
        cssLayout.setCaption("Advanced Search");
        // Css Hack * Clear Div
        final Label lblClear = new Label();
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

        for (final String mimetype : mimetypes) {
            mimes.addItem(mimetype);
        }
        mimes.setInputPrompt("Mime Types");
        mimes.setFilteringMode(Filtering.FILTERINGMODE_STARTSWITH);
        mimes.setImmediate(true);

        // Dropdown for Resource Type
        final String[] resourcearr = new String[] { "Context", "Container", "Item" };
        resource = new ComboBox();
        for (final String element : resourcearr) {
            resource.addItem(element);
        }
        resource.setInputPrompt("Resource Type");
        resource.setFilteringMode(Filtering.FILTERINGMODE_OFF);
        resource.setImmediate(true);

        txtFullText = new TextField();
        txtFullText.setInputPrompt("FullText");
        txtFullText.setImmediate(false);

        final Button bSearch = new Button("Search", this, "onClick");
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
    public void onClick(final Button.ClickEvent event) {
        final String titleTxt = (String) txtTitle.getValue();
        final String creatorTxt = (String) txtCreator.getValue();
        final String descriptionTxt = (String) txtDescription.getValue();
        final Object creationDateTxt = creationDate.getValue();
        final String mimesTxt = (String) mimes.getValue();
        final String resourceTxt = (String) resource.getValue();
        final String fulltxtTxt = (String) txtFullText.getValue();

        if (validateInputs(titleTxt, creatorTxt, descriptionTxt, creationDateTxt, mimesTxt, resourceTxt, fulltxtTxt)) {
            final SearchResultsView srchRes =
                new SearchResultsView(router, serviceLocation, titleTxt, creatorTxt, descriptionTxt, creationDateTxt,
                    mimesTxt, resourceTxt, fulltxtTxt);
            router.openTab(srchRes, "Advanced Search " + titleTxt + " " + creationDateTxt + " " + mimesTxt);
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
        final String titleTxt, final String creatorTxt, final String descriptionTxt, final Object creationDateTxt,
        final String mimesTxt, final String resourceTxt, final String fulltxtTxt) {

        if ((!titleTxt.isEmpty() && validateValidInputs(titleTxt))
            || (!creatorTxt.isEmpty() && validateValidInputs(creatorTxt))
            || (!descriptionTxt.isEmpty() && validateValidInputs(descriptionTxt)) || creationDateTxt.toString() != null
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
    private boolean validateValidInputs(final String term) {
        final Pattern p = Pattern.compile("[A-Za-z0-9_.\\s\":#*]{3,}");
        final Matcher m = p.matcher(term);
        return m.matches();
    }

}
