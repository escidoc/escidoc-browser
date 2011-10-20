package org.escidoc.browser.elabsmodul.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.elabsmodul.views.helper.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.POJOItem;
import com.vaadin.event.LayoutEvents.LayoutClickListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class InvestigationView extends Panel implements ILabsPanel, ILabsAction {

    private static final long serialVersionUID = -5284506653803233585L;

    private static Logger LOG = LoggerFactory.getLogger(InvestigationView.class);

    private InvestigationBean investigationBean;

    private ISaveAction saveComponent;

    private List<ResourceModel> breadCrumbModel;

    private ContainerProxy containerProxy;

    private List<String> eSyncDaemonUrls;

    private VerticalLayout mainLayout, dynamicLayout;

    private POJOItem<InvestigationBean> pojoItem;

    private List<HorizontalLayout> registeredComponents;

    private final int COMPONENT_COUNT = 6;

    private LayoutClickListener clientViewEventHandler;

    private ClickListener mouseClickListener;

    private HorizontalLayout buttonLayout;

    private HorizontalLayout modifiedComponent;

    public InvestigationView(InvestigationBean sourceBean, ISaveAction saveComponent,
        List<ResourceModel> breadCrumbModel, ContainerProxy containerProxy, List<String> depositEndPointUrls) {

        this.investigationBean = (sourceBean != null) ? sourceBean : new InvestigationBean();
        this.saveComponent = saveComponent;
        this.breadCrumbModel = breadCrumbModel;
        this.containerProxy = containerProxy;
        this.eSyncDaemonUrls = depositEndPointUrls;

        initialisePanelComponents();
        buildPropertiesGUI();
        buildPanelGUI();
        createPanelListener();
        createClickListener();

    }

    private void initialisePanelComponents() {

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        this.dynamicLayout = new VerticalLayout();
        this.dynamicLayout.setSpacing(true);
        this.dynamicLayout.setMargin(true);

        this.pojoItem =
            new POJOItem<InvestigationBean>(this.investigationBean, ELabsViewContants.INVESTIGATION_PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        setContent(mainLayout);
        setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        addComponent(new ResourcePropertiesViewHelper(this.containerProxy, this.breadCrumbModel)
            .generatePropertiesView());
    }

    private void createPanelListener() {
        this.clientViewEventHandler =
            new LabsClientViewEventHandler(this.registeredComponents, this.dynamicLayout, this, this);
        this.dynamicLayout.addListener(clientViewEventHandler);
    }

    private void createClickListener() {
        this.mouseClickListener = new Button.ClickListener() {

            private static final long serialVersionUID = 3427496817637644626L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    saveComponent.saveAction(investigationBean);
                    // FIXME why do we these methods?
                    InvestigationView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();

                }
            }
        };

        try {
            ((Button) buttonLayout.getComponent(1)).addListener(mouseClickListener);
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    // FIXME needed?
    protected void resetLayout() {
        Preconditions.checkNotNull(dynamicLayout, "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (Iterator<Component> iterator = dynamicLayout.getComponentIterator(); iterator.hasNext();) {
            Component component = iterator.next();
            if (component instanceof HorizontalLayout) {
                tempParentLayout = (HorizontalLayout) component;
            }
            else {
                LOG.error("DynamicLayout can contain only HorizontalLayouts as direct child element.");
                break;
            }
            if (LabsLayoutHelper.switchToLabelFromEditedField(tempParentLayout)) {
                setModifiedComponent(null);
            }
        }
    }

    /**
     * Build the specific editable layout of the eLabsElement.
     */
    private void buildPanelGUI() {
        this.dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INVESTIGATION_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INVESTIGATION_DESC,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INVESTIGATION_DEPOSIT_SERVICE,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_DEPOSIT_SERVICE));
        HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INVESTIGATION_INVESTIGATOR,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_INVESTIGATOR));
        HorizontalLayout h5 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_INVESTIGATION_DURATION,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_DURATION));
        HorizontalLayout h6 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_INVESTIGATION_RIG,
                pojoItem.getItemProperty(ELabsViewContants.P_INVESTIGATION_RIG));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);
        registeredComponents.add(h5);
        registeredComponents.add(h6);

        dynamicLayout.addComponent(h1, 0);
        dynamicLayout.addComponent(h2, 1);
        dynamicLayout.addComponent(h3, 2);
        dynamicLayout.addComponent(h4, 3);
        dynamicLayout.addComponent(h5, 4);
        dynamicLayout.addComponent(h6, 5);
        dynamicLayout.addComponent(new HorizontalLayout(), 6);

        mainLayout.addComponent(dynamicLayout);
        mainLayout.attach();
        mainLayout.requestRepaintAll();

    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (dynamicLayout != null && buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
            if (horizontalLayout != null) {
                horizontalLayout.removeAllComponents();
                horizontalLayout.addComponent(buttonLayout);
            }
        }
    }

    @Override
    public void hideButtonLayout() {
        if (dynamicLayout != null && dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
        }
    }

    @Override
    public Component getModifiedComponent() {
        return this.modifiedComponent;
    }

    @Override
    public void setModifiedComponent(Component modifiedComponent) {
        try {
            this.modifiedComponent = (HorizontalLayout) modifiedComponent;
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Panel getReference() {
        return this;
    }

}
