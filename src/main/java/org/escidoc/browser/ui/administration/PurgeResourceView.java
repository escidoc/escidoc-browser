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
package org.escidoc.browser.ui.administration;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.AppConstants;
import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.repository.Repository;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.ViewConstants;
import org.escidoc.browser.ui.administration.Style.H2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.AbstractSelect;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Reindeer;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.resources.ResourceType;

@SuppressWarnings("serial")
public class PurgeResourceView extends VerticalLayout {

    private final class FilterButtonListener implements ClickListener {

        private final Logger LOG = LoggerFactory.getLogger(PurgeResourceView.FilterButtonListener.class);

        private VerticalLayout resultLayout = new VerticalLayout();

        @Override
        public void buttonClick(ClickEvent event) {
            try {
                showResult(getResult());
            }
            catch (EscidocClientException e) {
                // TODO show error
            }
        }

        private void showResult(List<ResourceModel> result) {
            LOG.debug("Found: " + result.size());
            emptyPreviousResult();
            if (isEmpty(result)) {
                showNoResult();
            }
            else {
                Table table = createFilterResultView(result);
                showFilterResultView(table);
            }
        }

        private void emptyPreviousResult() {
            resultLayout.removeAllComponents();
            resultLayout.addComponent(new Style.Ruler());
            resultLayout.setHeight("100%");
        }

        private void showFilterResultView(Table table) {
            resultLayout.addComponent(table);
            addComponent(resultLayout);
        }

        private Table createFilterResultView(List<ResourceModel> result) {
            LOG.debug("found" + result.size());
            BeanItemContainer<ResourceModel> filteredResourcesContainer =
                new BeanItemContainer<ResourceModel>(ResourceModel.class, result);

            Table filterResultTable = new Table("Filtered Resources", filteredResourcesContainer);
            filterResultTable.setHeight("100px");
            filterResultTable.setWidth("100%");
            // filterResultTable.setColumnWidth(PropertyId.OBJECT_ID, 70);

            // filterResultTable.setVisibleColumns(new Object[] { PropertyId.OBJECT_ID, PropertyId.XLINK_TITLE });
            // filterResultTable.setColumnHeader(PropertyId.OBJECT_ID, ViewConstants.OBJECT_ID_LABEL);
            // filterResultTable.setColumnHeader(PropertyId.XLINK_TITLE, ViewConstants.TITLE_LABEL);

            filterResultTable.setSelectable(true);
            filterResultTable.setMultiSelect(true);
            return filterResultTable;
        }

        private void showNoResult() {
            resultLayout.addComponent(new Label(ViewConstants.NO_RESULT));
            addComponent(resultLayout);
        }

        private boolean isEmpty(List<ResourceModel> result) {
            return result.isEmpty();

        }

        private List<ResourceModel> getResult() throws EscidocClientException {
            if (isInputEmpty()) {
                return getRepository().findAll();
            }
            return getRepository().filterUsingInput(getRawFilter());
        }

        private Repository getRepository() {
            ResourceType selectedType = getSelectedType();
            switch (selectedType) {
                case ITEM:
                    return repositories.item();
                case CONTAINER:
                    return repositories.container();
                case CONTEXT:
                    return repositories.context();
                default:
                    router.getMainWindow().showNotification(selectedType + " not yet supported",
                        Window.Notification.TYPE_WARNING_MESSAGE);
            }

            return null;
        }

        private boolean isInputEmpty() {
            return getRawFilter().isEmpty();

        }

        private ResourceType getSelectedType() {
            final Object value = resourceOption.getValue();
            if (value instanceof ResourceType) {
                return (ResourceType) value;
            }
            return ResourceType.ITEM;
        }

        private String getRawFilter() {
            final Object value = textField.getValue();
            if (value instanceof String) {
                return ((String) value).trim();
            }
            return AppConstants.EMPTY_STRING;
        }
    }

    private final AbstractSelect resourceOption = new NativeSelect();

    private TextField textField = new TextField();

    private HorizontalLayout filterLayout;

    private Router router;

    private Repositories repositories;

    public PurgeResourceView(Router router, Repositories repositories) {
        Preconditions.checkNotNull(router, "router is null: %s", router);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.router = router;
        this.repositories = repositories;
    }

    public void init() {

        setMargin(true);
        addHeader();
        addRuler();
        addDescription();
        addRuler();
        addContent();
    }

    private void addContent() {
        filterLayout = new HorizontalLayout();
        filterLayout.setMargin(true);
        filterLayout.setSpacing(true);
        addMockUp();
    }

    private void addMockUp() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.setWidth("100%");
        horizontalLayout.setSpacing(true);

        textField.setWidth("100%");

        final List<ResourceType> list = new ArrayList<ResourceType>();
        list.add(ResourceType.CONTEXT);
        list.add(ResourceType.CONTAINER);
        list.add(ResourceType.ITEM);
        resourceOption.setContainerDataSource(new BeanItemContainer<ResourceType>(ResourceType.class, list));
        resourceOption.setItemCaptionPropertyId(PropertyId.LABEL);
        resourceOption.setNewItemsAllowed(false);
        resourceOption.setNullSelectionAllowed(false);
        resourceOption.select(ResourceType.ITEM);

        final Label popUpContent = new Label(ViewConstants.FILTER_EXAMPLE_TOOLTIP_TEXT, Label.CONTENT_XHTML);
        popUpContent.setWidth(400, UNITS_PIXELS);
        final PopupView popup = new PopupView(ViewConstants.TIP, popUpContent);

        Button filterButton = new Button("Filter");
        filterButton.setStyleName(Reindeer.BUTTON_SMALL);
        filterButton.addListener(new FilterButtonListener());

        horizontalLayout.addComponent(resourceOption);
        horizontalLayout.addComponent(popup);
        horizontalLayout.addComponent(textField);
        horizontalLayout.addComponent(filterButton);
        horizontalLayout.setExpandRatio(textField, 1.0f);

        addComponent(horizontalLayout);
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
}
