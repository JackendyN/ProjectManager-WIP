package com.project;

import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public abstract class OptionScreen {

    public Label hoverLabel;

    public void LabelHover(MouseEvent e) {
        hoverLabel = (Label)e.getTarget();
        hoverLabel.setUnderline(true);
    }

    public void LabelUnhover(MouseEvent e) {
        hoverLabel.setUnderline(false);
        hoverLabel = null;
    }

}
