package org.escidoc.genclient.ui.maincontent;

import com.vaadin.ui.ListSelect;
import com.vaadin.ui.Tree;

public class DirectMember {

    /**
     * Show the members as ListSelect
     * 
     * @return
     */
    public ListSelect asList() {
        // Create the selection component
        final ListSelect select = new ListSelect("Direct Member");
        select.setSizeFull();
        // Add some items
        select.addItem("Tübingen");
        select.addItem("Karlsruhe");
        select.addItem("DSC_0107.jpg");
        select.setNullSelectionAllowed(false);
        return select;
    }

    public Tree asTree() {
        final Tree tree = new Tree();
        tree.setSizeFull();
        tree.addItem("Tübingen");
        tree.addItem("Karlsruhe");
        tree.addItem("DSC_0107.jpg");
        return tree;

    }

}
