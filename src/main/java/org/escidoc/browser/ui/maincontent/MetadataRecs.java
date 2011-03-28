package org.escidoc.browser.ui.maincontent;

import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;

public class MetadataRecs {
	private int height;

	public MetadataRecs(int innerelementsHeight) {
		this.height=innerelementsHeight;
	}

	public Accordion asAccord(){
		//Metadata Records
		Accordion metadataRecs = new Accordion();
		metadataRecs.setSizeFull();
		//create some content fo
		Label l1 = new Label("escidoc");

		l1.setHeight(height+"px");
		Label l2 = new Label("Relations");
		l2.setHeight(height+"px");
		Label l3 = new Label("Additional Resources");
		l3.setHeight(height+"px");
		// Add the components as tabs in the Accordion.
		metadataRecs.addTab(l1, "Metadata Records", null);
		metadataRecs.addTab(l2, "Relations", null);
		metadataRecs.addTab(l3, "Additional Resources", null);
		return metadataRecs;
	}
}
