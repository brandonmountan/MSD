package com.example.synthesizer;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;


public class MixerACW extends AudioComponentWidget {

    MixerACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);

        inputJack.setFill(Color.PINK);
        inputJack.setRadius(10);

        outputJack.setFill(Color.STEELBLUE);
        outputJack.setRadius(10);

        baseLayout.getChildren().add(outputJack);
        baseLayout.getChildren().add(inputJack);

        baseLayout.setLayoutX(300);
        baseLayout.setLayoutY(200);
    }
}