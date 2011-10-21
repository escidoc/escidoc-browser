package org.escidoc.browser.elabsmodul.views;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.helpers.LabsLayoutHelper;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.elabsmodul.views.listeners.LabsClientViewEventHandler;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;
import org.escidoc.browser.ui.Router;
import org.escidoc.browser.ui.view.helpers.DirectMember;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.vaadin.data.util.POJOItem;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import de.escidoc.core.client.exceptions.EscidocClientException;

public class StudyView extends Panel implements ILabsPanel, ILabsAction {

    private static Logger LOG = LoggerFactory.getLogger(StudyView.class);

    private static final String RIGHT_PANEL = "";

    private static final String[] PROPERTIES = ELabsViewContants.STUDY_PROPERTIES;

    private final StudyBean studyBean;

    private final ISaveAction saveComponent;

    private final List<ResourceModel> breadCrumbModel;

    private final ContainerProxy containerProxy;

    private final int COMPONENT_COUNT = 4;

    private final CssLayout cssLayout = new CssLayout();

    private final Router router;

    private POJOItem<StudyBean> pojoItem;

    private VerticalLayout mainLayout, dynamicLayout;

    private HorizontalLayout buttonLayout;

    private List<HorizontalLayout> registeredComponents;

    private LabsClientViewEventHandler clientViewEventHandler;

    private ClickListener mouseClickListener;

    private HorizontalLayout modifiedComponent;

    public StudyView(final StudyBean sourceBean, final ISaveAction saveComponent,
        final List<ResourceModel> breadCrumbModel, final ResourceProxy resourceProxy, final Router router) {
        Preconditions.checkNotNull(sourceBean, "sourceBean is null: %s", sourceBean);
        Preconditions.checkNotNull(saveComponent, "saveComponent is null: %s", saveComponent);
        Preconditions.checkNotNull(breadCrumbModel, "breadCrumbModel is null: %s", breadCrumbModel);
        Preconditions.checkNotNull(resourceProxy, "resourceProxy is null: %s", resourceProxy);
        Preconditions.checkNotNull(router, "router is null: %s", router);

        this.studyBean = (sourceBean != null) ? sourceBean : new StudyBean();
        this.saveComponent = saveComponent;
        this.breadCrumbModel = breadCrumbModel;
        this.containerProxy = (ContainerProxy) resourceProxy;
        this.router = router;

        initialisePanelComponents();
        buildContainerGUI();
        buildPropertiesGUI();
        buildPanelGUI();
        createPanelListener();
        createClickListener();
    }

    @SuppressWarnings("serial")
    private void createClickListener() {
        mouseClickListener = new Button.ClickListener() {

            @Override
            public void buttonClick(final com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    saveComponent.saveAction(studyBean);

                    // TODO Why do we need to call these two methods?
                    StudyView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();
                }
            }
        };

        try {
            ((Button) buttonLayout.getComponent(1)).addListener(mouseClickListener);
        }
        catch (final ClassCastException e) {
            // TODO report error to user?
            LOG.error(e.getMessage());
        }

    }

    protected void resetLayout() {
        Preconditions.checkNotNull(dynamicLayout, "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (final Iterator<Component> iterator = dynamicLayout.getComponentIterator(); iterator.hasNext();) {
            final Component component = iterator.next();
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

    private void initialisePanelComponents() {
        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        this.mainLayout.setHeight(router.getApplicationHeight() - 30 + "px");
        this.mainLayout.setStyleName("red");
        this.dynamicLayout = new VerticalLayout();
        dynamicLayout.setSpacing(true);
        dynamicLayout.setMargin(true);

        this.pojoItem = new POJOItem<StudyBean>(studyBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setContent(mainLayout);

        this.setScrollable(true);
    }

    private void buildPanelGUI() {
        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        final HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_STUDY_TITLE));
        final HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_DESC,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_DESC));
        final HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_MOT_PUB,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_MOT_PUB));
        final HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(ELabsViewContants.L_STUDY_RES_PUB,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_RES_PUB));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);

        dynamicLayout.addComponent(h1, 0);
        dynamicLayout.addComponent(h2, 1);
        dynamicLayout.addComponent(h3, 2);
        dynamicLayout.addComponent(h4, 3);

        dynamicLayout.addComponent(new HorizontalLayout(), 4);

        rightCell(dynamicLayout);
        mainLayout.addComponent(cssLayout);
        mainLayout.setExpandRatio(cssLayout, 1.0f);
        mainLayout.attach();
        mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler = new LabsClientViewEventHandler(registeredComponents, dynamicLayout, this, this);
        dynamicLayout.addListener(this.clientViewEventHandler);

    }

    public POJOItem<StudyBean> getPojoItem() {
        return pojoItem;
    }

    /**
     * Builds a Container for the DM and the ElabPanel
     */
    private void buildContainerGUI() {
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        try {
            leftCell();
        }
        catch (final EscidocClientException e) {
            router.getMainWindow().showNotification(
                "Could not load the Direct Members Helper in the View" + e.getLocalizedMessage());
        }
    }

    /**
     * This is the inner Right Cell within a Context By default a set of Organizational Unit / Admin Description /
     * RelatedItem / Resources are bound
     * 
     * @param comptoBind
     */
    // TODO why deprecated?
    @SuppressWarnings("deprecation")
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setDescription(RIGHT_PANEL);
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.getLayout().setMargin(false);
        final Label nameofPanel =
            new Label("<strong>" + ELabsViewContants.BWELABS_STUDY + "</string>", Label.CONTENT_RAW);
        nameofPanel.setStyleName("grey-label");
        rightpnl.addComponent(nameofPanel);
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    // TODO why deprecated?
    @SuppressWarnings("deprecation")
    private void leftCell() throws EscidocClientException {
        final Panel leftPanel = new Panel();
        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.getLayout().setMargin(false);
        leftPanel.setWidth("30%");
        leftPanel.setHeight("82%");

        new DirectMember(router.getServiceLocation(), router, containerProxy.getId(), router.getMainWindow(),
            router.getCurrentUser(), router.getRepositories(), leftPanel).containerAsTree();
        cssLayout.addComponent(leftPanel);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        this.addComponent(new ResourcePropertiesViewHelper(containerProxy, breadCrumbModel, "Study")
            .generatePropertiesView());
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (dynamicLayout != null && buttonLayout != null) {
            try {
                horizontalLayout = (HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT);
            }
            catch (final ClassCastException e) {
                // TODO log exception and tell the user something going wrong
                LOG.error(e.getMessage());
            }
            if (horizontalLayout != null) {
                horizontalLayout.removeAllComponents();
                horizontalLayout.addComponent(buttonLayout);
            }
        }
    }

    @Override
    public Component getModifiedComponent() {
        return modifiedComponent;
    }

    @Override
    public void setModifiedComponent(final Component modifiedComponent) {
        try {
            this.modifiedComponent = (HorizontalLayout) modifiedComponent;
        }
        catch (final ClassCastException e) {
            // TODO tell the user something going wrong
            LOG.error(e.getMessage());
        }
    }

    @Override
    public Panel getReference() {
        return this;
    }

    @Override
    public void hideButtonLayout() {
        if (dynamicLayout != null && dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT)).removeAllComponents();
            }
            catch (final ClassCastException e) {
                // TODO tell the user something going wrong
                LOG.error(e.getMessage());
            }
        }
    }
}