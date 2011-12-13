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
package org.escidoc.browser.ui.tools;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.tools.Style.H2;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.Window.Notification;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.resources.adm.MessagesStatus;

@SuppressWarnings("serial")
public class BulkTasksView extends VerticalLayout {

    final AbstractSelect resourceOption = new NativeSelect();

    final TextField textField = new TextField();

    final Router router;

    final Repositories repositories;

    public BulkTasksView(final Router router, final Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);

        this.router = router;
        this.repositories = repositories;
    }

    public void init() {
        setMargin(true);
        addImportView();
        addHeader();
        addRuler();
        addDescription();
        addRuler();
        addContent();
    }

    private void addImportView() {
        addComponent(new ImportView(router, repositories.ingest()));
    }

    private void addContent() {
        final HorizontalLayout filterLayout = new HorizontalLayout();
        filterLayout.setMargin(true);
        filterLayout.setSpacing(true);
        addResult();
    }

    private void addResult() {
        final HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        textField.setWidth("100%");

        createResourceOptions();

        final Button filterButton = new Button(ViewConstants.FILTER);
        filterButton.setStyleName(Reindeer.BUTTON_SMALL);
        filterButton.addListener(new FilterButtonListener(this, router.getMainWindow(), repositories));

        horizontalLayout.addComponent(resourceOption);
        horizontalLayout.addComponent(createHelpView());
        horizontalLayout.addComponent(textField);
        horizontalLayout.addComponent(filterButton);
        horizontalLayout.setExpandRatio(textField, 1.0f);

        addComponent(horizontalLayout);
    }

    private static PopupView createHelpView() {
        final Label popUpContent = new Label(ViewConstants.FILTER_EXAMPLE_TOOLTIP_TEXT, Label.CONTENT_XHTML);
        popUpContent.setWidth(400, UNITS_PIXELS);
        return new PopupView(ViewConstants.TIP, popUpContent);
    }

    private void createResourceOptions() {
        final BeanItemContainer<ResourceType> dataSource =
            new BeanItemContainer<ResourceType>(ResourceType.class, createResourceTypeList());
        dataSource.addNestedContainerProperty("label");
        resourceOption.setContainerDataSource(dataSource);
        resourceOption.setNewItemsAllowed(false);
        resourceOption.setNullSelectionAllowed(false);
        resourceOption.select(ResourceType.ITEM);
        resourceOption.setItemCaptionPropertyId("label");
    }

    private static List<ResourceType> createResourceTypeList() {
        final List<ResourceType> list = new ArrayList<ResourceType>();
        list.add(ResourceType.CONTEXT);
        list.add(ResourceType.CONTAINER);
        list.add(ResourceType.ITEM);
        list.add(ResourceType.CONTENT_MODEL);
        return list;
    }

    private void addDescription() {
        addComponent(new Label("<p>" + ViewConstants.FILTER_DESCRIPTION_TEXT + "</p>", Label.CONTENT_XHTML));
    }

    private void addRuler() {
        addComponent(new Style.Ruler());
    }

    private void addHeader() {
        final Label text = new H2(ViewConstants.FILTERING_RESOURCES_TITLE);
        text.setContentMode(Label.CONTENT_XHTML);
        addComponent(text);
    }

    void showErrorMessage(final Exception e) {
        router
            .getApp().getMainWindow()
            .showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
    }

    void showErrorMessage(final MessagesStatus status) {
        router.getMainWindow().showNotification(
            new Notification("Error", status.getStatusMessage(), Notification.TYPE_ERROR_MESSAGE));
    }

    void showWarningMessage(final String message) {
        router
            .getMainWindow().showNotification(new Notification("Warning", message, Notification.TYPE_WARNING_MESSAGE));
    }
}
