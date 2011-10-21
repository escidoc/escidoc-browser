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

    private static final String RIGHT_PANEL = null;

    private POJOItem<StudyBean> pojoItem = null;

    private StudyBean studyBean = null, lastStateBean = null;

    private ISaveAction saveComponent;

    private VerticalLayout mainLayout = null, dynamicLayout = null;

    private List<ResourceModel> breadCrumbModel;

    private ContainerProxy containerProxy;

    private final String[] PROPERTIES = ELabsViewContants.STUDY_PROPERTIES;

    final String VIEWCAPTION = "Instument View";

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 4;

    private HorizontalLayout buttonLayout = null;

    private List<HorizontalLayout> registeredComponents = null;

    private LabsClientViewEventHandler clientViewEventHandler;

    private ClickListener mouseClickListener = null;

    private static Logger LOG = LoggerFactory.getLogger(StudyView.class);

    private HorizontalLayout modifiedComponent = null;

    private final CssLayout cssLayout = new CssLayout();

    private Router router;

    public StudyView(StudyBean sourceBean, ISaveAction saveComponent,
        List<ResourceModel> breadCrumbModel, ResourceProxy resourceProxy,
        Router router) {
        this.studyBean = (sourceBean != null) ? sourceBean : new StudyBean();
        this.lastStateBean = studyBean;
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

    private void createClickListener() {
        mouseClickListener = new Button.ClickListener() {
            private static final long serialVersionUID = -8330004043242560612L;

            @Override
            public void buttonClick(com.vaadin.ui.Button.ClickEvent event) {
                if (event.getButton().getCaption().equals("Save")) {
                    saveComponent.saveAction(studyBean);
                    StudyView.this.resetLayout();
                    dynamicLayout.requestRepaintAll();

                }
            }
        };

        try {
            ((Button) buttonLayout.getComponent(1))
                .addListener(mouseClickListener);
        }
        catch (ClassCastException e) {
            LOG.error(e.getMessage());
        }

    }

    protected void resetLayout() {
        Preconditions.checkNotNull(dynamicLayout,
            "View's dynamiclayout is null.");

        HorizontalLayout tempParentLayout = null;
        for (Iterator<Component> iterator =
            dynamicLayout.getComponentIterator(); iterator.hasNext();) {
            Component component = iterator.next();
            if (component instanceof HorizontalLayout) {
                tempParentLayout = (HorizontalLayout) component;
            }
            else {
                LOG
                    .error("DynamicLayout can contain only HorizontalLayouts as direct child element.");
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
        this.registeredComponents =
            new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setContent(mainLayout);

        this.setScrollable(true);
    }

    private void buildPanelGUI() {
        dynamicLayout.setStyleName(ELabsViewContants.STYLE_ELABS_FORM);

        this.buttonLayout = LabsLayoutHelper.createButtonLayout();
        HorizontalLayout h1 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_STUDY_TITLE,
                pojoItem.getItemProperty(ELabsViewContants.P_STUDY_TITLE));
        HorizontalLayout h2 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_STUDY_DESC,
                getPojoItem().getItemProperty(ELabsViewContants.P_STUDY_DESC));
        HorizontalLayout h3 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_STUDY_MOT_PUB, getPojoItem()
                    .getItemProperty(ELabsViewContants.P_STUDY_MOT_PUB));
        HorizontalLayout h4 =
            LabsLayoutHelper.createHorizontalLayoutWithELabsLabelAndLabelData(
                ELabsViewContants.L_STUDY_RES_PUB, getPojoItem()
                    .getItemProperty(ELabsViewContants.P_STUDY_RES_PUB));

        registeredComponents.add(h1);
        registeredComponents.add(h2);
        registeredComponents.add(h3);
        registeredComponents.add(h4);

        dynamicLayout.addComponent(h1, 0);
        dynamicLayout.addComponent(h2, 1);
        dynamicLayout.addComponent(h3, 2);
        dynamicLayout.addComponent(h4, 3);

        dynamicLayout.addComponent(new HorizontalLayout(), 4);

        // this.mainLayout.addComponent(this.dynamicLayout);
        rightCell(dynamicLayout);
        mainLayout.addComponent(cssLayout);
        mainLayout.setExpandRatio(cssLayout, 1.0f);
        this.mainLayout.attach();
        this.mainLayout.requestRepaintAll();
    }

    private void createPanelListener() {
        this.clientViewEventHandler =
            new LabsClientViewEventHandler(registeredComponents, dynamicLayout,
                this, this);
        dynamicLayout.addListener(this.clientViewEventHandler);

    }

    public POJOItem<StudyBean> getPojoItem() {
        return pojoItem;
    }

    private void buildContainerGUI() {
        cssLayout.setWidth("100%");
        cssLayout.setHeight("100%");
        try {
            leftCell();
        }
        catch (EscidocClientException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * This is the inner Right Cell within a Context By default a set of
     * Organizational Unit / Admin Description / RelatedItem / Resources are
     * bound
     * 
     * @param comptoBind
     */
    @SuppressWarnings("deprecation")
    private void rightCell(final Component comptoBind) {
        final Panel rightpnl = new Panel();
        rightpnl.setDescription(RIGHT_PANEL);
        rightpnl.setStyleName("floatright");
        rightpnl.setWidth("70%");
        rightpnl.setHeight("82%");
        rightpnl.getLayout().setMargin(false);
        final Label nameofPanel =
            new Label("<strong>" + ELabsViewContants.BWELABS_STUDY
                + "</string>", Label.CONTENT_RAW);
        nameofPanel.setStyleName("grey-label");
        rightpnl.addComponent(nameofPanel);
        rightpnl.addComponent(comptoBind);
        cssLayout.addComponent(rightpnl);
    }

    @SuppressWarnings("deprecation")
    private void leftCell() throws EscidocClientException {
        final Panel leftPanel = new Panel();
        leftPanel.setStyleName("directmembers floatleft");
        leftPanel.setScrollable(false);
        leftPanel.getLayout().setMargin(false);
        leftPanel.setWidth("30%");
        leftPanel.setHeight("82%");

        new DirectMember(router.getServiceLocation(), router,
            containerProxy.getId(), router.getMainWindow(),
            router.getCurrentUser(), router.getRepositories(), leftPanel)
            .containerAsTree();
        cssLayout.addComponent(leftPanel);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        this.addComponent(new ResourcePropertiesViewHelper(containerProxy,
            breadCrumbModel, "Study").generatePropertiesView());
    }

    @Override
    public void showButtonLayout() {
        HorizontalLayout horizontalLayout = null;
        if (dynamicLayout != null && buttonLayout != null) {
            try {
                horizontalLayout =
                    (HorizontalLayout) dynamicLayout
                        .getComponent(COMPONENT_COUNT);
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
    public Component getModifiedComponent() {
        return modifiedComponent;
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

    @Override
    public void hideButtonLayout() {
        if (dynamicLayout != null
            && dynamicLayout.getComponent(COMPONENT_COUNT) != null) {
            try {
                ((HorizontalLayout) dynamicLayout.getComponent(COMPONENT_COUNT))
                    .removeAllComponents();
            }
            catch (ClassCastException e) {
                LOG.error(e.getMessage());
            }
        }
    }
}
