package org.escidoc.browser.elabsmodul.views;

import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class YesNoDialog extends Window implements Button.ClickListener {

    private static final long serialVersionUID = 2444046110793999051L;

    private Callback callback;

    private Button yes = new Button("Yes", this);

    private Button no = new Button("No", this);

    public YesNoDialog(String caption, String question, Callback callback) {
        super(caption);

        setModal(true);

        this.callback = callback;

        if (question != null) {
            addComponent(new Label(question));
        }

        HorizontalLayout layout = new HorizontalLayout();
        layout.setMargin(true);
        layout.addComponent(yes);
        layout.addComponent(no);
        addComponent(layout);
    }

    @Override
    public void buttonClick(final ClickEvent event) {
        if (getParent() != null) {
            getParent().removeWindow(this);
        }
        callback.onDialogResult(event.getSource() == yes);
    }

    public interface Callback {
        public void onDialogResult(boolean resultIsYes);
    }
}
