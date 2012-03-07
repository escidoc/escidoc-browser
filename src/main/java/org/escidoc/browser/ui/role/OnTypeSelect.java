package org.escidoc.browser.ui.role;

import com.google.common.base.Preconditions;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.ListSelect;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import org.escidoc.browser.model.PropertyId;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceType;
import org.escidoc.browser.repository.Repositories;
import org.escidoc.browser.ui.ViewConstants;

import java.util.Iterator;
import java.util.List;

import de.escidoc.core.client.exceptions.EscidocClientException;

@SuppressWarnings("serial")
public class OnTypeSelect implements ValueChangeListener {
    private VerticalLayout layout;

    private ListSelect select;

    private Repositories repositories;

    private Window mainWindow;

    protected OnTypeSelect(Window mainWindow, VerticalLayout layout, ListSelect select, Repositories repositories) {
        Preconditions.checkNotNull(mainWindow, "mw is null: %s", mainWindow);
        Preconditions.checkNotNull(layout, "layout is null: %s", layout);
        Preconditions.checkNotNull(select, "resourceSelect is null: %s", select);
        Preconditions.checkNotNull(repositories, "repositories is null: %s", repositories);
        this.mainWindow = mainWindow;
        this.layout = layout;
        this.select = select;
        this.repositories = repositories;
    }

    @Override
    public void valueChange(final ValueChangeEvent event) {
        try {
            onSelectedResourceType(event);
        }
        catch (UnsupportedOperationException e) {
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
        catch (EscidocClientException e) {
            mainWindow.showNotification(ViewConstants.ERROR, e.getMessage(), Window.Notification.TYPE_ERROR_MESSAGE);
        }
    }

    private void onSelectedResourceType(final ValueChangeEvent event) throws UnsupportedOperationException,
        EscidocClientException {
        final Object value = event.getProperty().getValue();
        if (value instanceof ResourceType) {
            Component newComponent = assignComponent();
            loadData((ResourceType) value);
            final Iterator<Component> it = layout.getComponentIterator();
            if (it.hasNext()) {
                layout.replaceComponent(it.next(), newComponent);
            }
            else {
                layout.addComponent(newComponent);
            }
        }
    }

    private void loadData(ResourceType type) throws UnsupportedOperationException, EscidocClientException {
        final BeanItemContainer<ResourceModel> dataSource = newContainer();
        for (final ResourceModel rm : findAll(type)) {
            dataSource.addItem(rm);
        }
        configureList(dataSource);
    }

    private void configureList(final BeanItemContainer<ResourceModel> container) {
        select.setContainerDataSource(container);
        select.setItemCaptionPropertyId(PropertyId.NAME);
    }

    private List<ResourceModel> findAll(ResourceType type) throws EscidocClientException {
        return repositories.findByType(type).findAll();
    }

    private static BeanItemContainer<ResourceModel> newContainer() {
        return new BeanItemContainer<ResourceModel>(ResourceModel.class);
    }

    private Component assignComponent() {
        Component newComponent = select;
        return newComponent;
    }
}