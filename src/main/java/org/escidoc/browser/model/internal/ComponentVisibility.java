package org.escidoc.browser.model.internal;

public enum ComponentVisibility {
    PUBLIC("public"), PRIVATE("private"), AUDIENCE("audience");

    private String label;

    ComponentVisibility(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }
}
