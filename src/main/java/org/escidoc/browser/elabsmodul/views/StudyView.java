package org.escidoc.browser.elabsmodul.views;

import java.util.ArrayList;
import java.util.List;

import org.escidoc.browser.elabsmodul.constants.ELabsViewContants;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.StudyBean;
import org.escidoc.browser.elabsmodul.views.helpers.ResourcePropertiesViewHelper;
import org.escidoc.browser.model.ContainerProxy;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import com.vaadin.data.util.POJOItem;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

public class StudyView extends Panel {

    private POJOItem<StudyBean> pojoItem = null;

    private StudyBean studyBean = null, lastStateBean = null;

    private ISaveAction saveComponent;

    private VerticalLayout mainLayout = null, dynamicLayout = null;

    private List<ResourceModel> breadCrumbModel;

    private ContainerProxy containerProxy;

    private final String[] PROPERTIES = ELabsViewContants.INSTRUMENT_PROPERTIES;

    final String VIEWCAPTION = "Instument View";

    final String LAST_MODIFIED_BY = "Last modification by ";

    final String FLOAT_LEFT = "floatleft";

    final String FLOAT_RIGHT = "floatright";

    private final int COMPONENT_COUNT = 9;

    private List<HorizontalLayout> registeredComponents = null;

    public StudyView(StudyBean sourceBean, ISaveAction saveComponent, List<ResourceModel> breadCrumbModel,
        ResourceProxy resourceProxy) {
        this.studyBean = (sourceBean != null) ? sourceBean : new StudyBean();
        this.lastStateBean = studyBean;
        this.saveComponent = saveComponent;
        this.breadCrumbModel = breadCrumbModel;
        this.containerProxy = (ContainerProxy) resourceProxy;
        initialisePanelComponents();
        buildPropertiesGUI();
        // buildPanelGUI();
        // createPanelListener();
        // createClickListener();
    }

    private void initialisePanelComponents() {

        this.mainLayout = new VerticalLayout();
        this.mainLayout.setSpacing(true);
        this.mainLayout.setMargin(true);
        this.dynamicLayout = new VerticalLayout();
        this.dynamicLayout.setSpacing(true);
        this.dynamicLayout.setMargin(true);

        this.pojoItem = new POJOItem<StudyBean>(studyBean, PROPERTIES);
        this.registeredComponents = new ArrayList<HorizontalLayout>(COMPONENT_COUNT);

        this.setContent(this.mainLayout);
        this.setScrollable(true);
    }

    /**
     * Build the read-only layout of the eLabsElement
     */
    private void buildPropertiesGUI() {
        this.addComponent(new ResourcePropertiesViewHelper(containerProxy, breadCrumbModel).generatePropertiesView());
    }

}
