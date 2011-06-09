package org.escidoc.browser.ui.listeners;

import java.net.MalformedURLException;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.escidoc.browser.model.ContainerModel;
import org.escidoc.browser.model.ContentModelService;
import org.escidoc.browser.model.EscidocServiceLocation;
import org.escidoc.browser.model.ResourceContainer;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.internal.ContainerBuilder;
import org.escidoc.browser.repository.ContainerRepository;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

import de.escidoc.core.client.exceptions.EscidocClientException;
import de.escidoc.core.client.exceptions.EscidocException;
import de.escidoc.core.client.exceptions.InternalClientException;
import de.escidoc.core.client.exceptions.TransportException;
import de.escidoc.core.resources.Resource;
import de.escidoc.core.resources.common.reference.ContentModelRef;
import de.escidoc.core.resources.common.reference.ContextRef;
import de.escidoc.core.resources.om.container.Container;

/**
 * @author ajb
 * 
 */
public class TreeCreateContainer {

    private final EscidocServiceLocation serviceLocation;

    private final Window mainWindow;

    private TextField editor;

    private Button change;

    private final Object target;

    private final ContainerRepository containerRepo;

    private NativeSelect listContentModels;

    private Window subwindow;

    private final ResourceContainer resourceContainer;

    private final String contextId;

    /**
     * @param target
     * @param contextId
     * @param serviceLocation
     * @param window
     * @param tree
     * @param containerRepo
     * @param resourceContainer
     */
    public TreeCreateContainer(Object target, String contextId, EscidocServiceLocation serviceLocation, Window window,
        ContainerRepository containerRepo, ResourceContainer resourceContainer) {

        this.target = target;
        this.serviceLocation = serviceLocation;
        this.mainWindow = window;
        this.containerRepo = containerRepo;
        this.resourceContainer = resourceContainer;
        this.contextId = contextId;
    }

    // TODO heavy refactoring here
    public void createContainer() {
        try {
            addContainerForm();
        }
        catch (EscidocException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (InternalClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (TransportException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void addContainerForm() throws EscidocException, MalformedURLException, InternalClientException,
        TransportException {
        HorizontalLayout editBar = new HorizontalLayout();
        editBar.setMargin(false, false, false, true);
        // textfield
        editor = new TextField("Container name");
        editor.setImmediate(true);
        editBar.addComponent(editor);
        // apply-button
        change = new Button("Apply", this, "clickButtonaddContainer");

        listContentModels = new NativeSelect("Please select Content Model");
        ContentModelService cMS = new ContentModelService(serviceLocation);

        Collection<? extends Resource> contentModels = cMS.filterUsingInput("");
        for (Resource resource : contentModels) {
            listContentModels.addItem(resource.getObjid());
        }
        editBar.addComponent(listContentModels);
        editBar.addComponent(change);
        editBar.setComponentAlignment(change, Alignment.BOTTOM_LEFT);
        subwindow = new Window("Create Container");
        subwindow.setWidth("600px");
        subwindow.setModal(true);
        subwindow.addComponent(editBar);
        mainWindow.addWindow(subwindow);
    }

    public void clickButtonaddContainer(ClickEvent event) {
        String containerName = editor.getValue().toString();
        String contentModelId = (String) listContentModels.getValue();

        // We really create the container here
        try {
            createNewContainer(containerName, contentModelId, contextId);
        }
        catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Creating a container Required: A Context Reference A Name A Content Model
     * 
     * @param contextId
     * @param contentModelId
     * @param containerName
     * @throws ParserConfigurationException
     * 
     */
    private void createNewContainer(String containerName, String contentModelId, String contextId)
        throws ParserConfigurationException {

        ContainerBuilder cntBuild =
            new ContainerBuilder(new ContextRef(contextId), new ContentModelRef(contentModelId));
        Container newContainer = cntBuild.build(containerName);

        try {
            Container create = containerRepo.create(newContainer);
            resourceContainer.addChild((ResourceModel) target, new ContainerModel(create));
            subwindow.getParent().removeWindow(subwindow);
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
