package com.project;

import java.io.Serializable;

import javafx.scene.paint.Color;

@SuppressWarnings("serial")
public class SerializableColor implements Serializable {

	String colorHex;

    public SerializableColor(Color color) {
        this.colorHex = toHexString(color);
    }

    public Color getColor() {
        return Color.web(colorHex);
    }

    String toHexString(Color color) {
        return String.format("#%02X%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255),
                (int) (color.getOpacity() * 255));
    }
    
}

	

