package org.escidoc.genclient.ui;

import com.vaadin.event.ItemClickEvent;
import com.vaadin.event.ItemClickEvent.ItemClickListener;
import com.vaadin.ui.Tree;

public class TreeMenu implements ItemClickListener {

    private TabContainer listenertab;

    public TreeMenu() {
        // this.listenertab = tab;
    }

    public Tree sampleTree() {
        final Object[][] planets =
            new Object[][] {
                new Object[] { "Mercury" },
                new Object[] { "Venus" },
                new Object[] { "Earth", "The Moon" },
                new Object[] { "Mars", "Phobos", "Deimos" },
                new Object[] { "Jupiter", "Io", "Europa", "Ganymedes",
                    "Callisto" },
                new Object[] { "Saturn", "Titan", "Tethys", "Dione", "Rhea",
                    "Iapetus" },
                new Object[] { "Uranus", "Miranda", "Ariel", "Umbriel",
                    "Titania", "Oberon" },
                new Object[] { "Neptune", "Triton", "Proteus", "Nereid",
                    "Larissa" },
                new Object[] { "Neptune2", "Triton1", "Proteus1", "Nereid1",
                    "Larissa" },
                new Object[] { "Neptune3", "Triton2", "Proteus2", "Nereid2",
                    "Larissa" },
                new Object[] { "Neptun4", "Triton3", "Proteus3", "Nereid3",
                    "Larissa" } };

        final Tree tree = new Tree("The Planets and Major Moons");
        /* Add planets as root items in the tree. */
        for (final Object[] planet2 : planets) {
            final String planet = (String) (planet2[0]);
            tree.addItem(planet);

            if (planet2.length == 1) {
                // The planet has no moons so make it a leaf.
                tree.setChildrenAllowed(planet, false);
            }
            else {
                // Add children (moons) under the planets.
                for (int j = 1; j < planet2.length; j++) {
                    final String moon = (String) planet2[j];

                    // Add the item as a regular item.
                    tree.addItem(moon);

                    // Set it to be a child.
                    tree.setParent(moon, planet);

                    // Make the moons look like leaves.
                    tree.setChildrenAllowed(moon, false);
                }
                // Expand the subtree.
                // tree.expandItemsRecursively(planet);
            }
        }
        tree.setSizeFull();
        return tree;

    }

    public void itemClick(final ItemClickEvent event) {
        if (event.isDoubleClick()) {
            listenertab
                .newdblclick("That was a double click" + event.getItem());
        }
        else {
            // Probably implement retrieval of sub-level items
            // Why bother for a single click?

        }

    }

}
