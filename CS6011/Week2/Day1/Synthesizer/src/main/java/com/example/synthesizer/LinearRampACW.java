package com.example.synthesizer;

import javafx.scene.layout.AnchorPane;

public class LinearRampACW extends AudioComponentWidget {

    LinearRampACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);
        baseLayout.setLayoutX(0);
        baseLayout.setLayoutY(180);
    }
}
