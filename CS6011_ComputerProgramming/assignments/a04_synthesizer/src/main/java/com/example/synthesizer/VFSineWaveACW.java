package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VFSineWaveACW extends AudioComponentWidget {

    VFSineWaveACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);

        outputJack.setFill(Color.STEELBLUE);
        outputJack.setRadius(10);

        inputJack.setFill(Color.PINK);
        inputJack.setRadius(10);

        baseLayout.getChildren().add(outputJack);
        baseLayout.getChildren().add(inputJack);

        baseLayout.setLayoutX(0);
        baseLayout.setLayoutY(200);
    }
}