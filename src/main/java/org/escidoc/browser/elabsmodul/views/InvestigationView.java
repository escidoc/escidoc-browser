package org.escidoc.browser.elabsmodul.views;

import java.util.List;

import org.escidoc.browser.elabsmodul.interfaces.ILabsAction;
import org.escidoc.browser.elabsmodul.interfaces.ILabsPanel;
import org.escidoc.browser.elabsmodul.interfaces.ISaveAction;
import org.escidoc.browser.elabsmodul.model.InvestigationBean;
import org.escidoc.browser.model.ResourceModel;
import org.escidoc.browser.model.ResourceProxy;

import com.vaadin.ui.Component;
import com.vaadin.ui.Panel;

public class InvestigationView extends Panel implements ILabsPanel, ILabsAction {

    public InvestigationView(InvestigationBean sourceBean, ISaveAction saveComponent, List<ResourceModel> breadCrumbModel,
        ResourceProxy resourceProxy, List<String> eSyncDaemonUrls) {
        
    }

    @Override
    public void showButtonLayout() {
        // TODO Auto-generated method stub

    }

    @Override
    public void hideButtonLayout() {
        // TODO Auto-generated method stub

    }

    @Override
    public Component getModifiedComponent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setModifiedComponent(Component modifiedComponent) {
        // TODO Auto-generated method stub

    }

    @Override
    public Panel getReference() {
        // TODO Auto-generated method stub
        return null;
    }

}
