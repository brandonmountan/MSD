package com.example.synthesizer;

import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;


public class MixerACW extends AudioComponentWidget {

    MixerACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);

        Label title = new Label(name);
        baseLayout.getChildren().add(title);

        inputJack.setFill(Color.PINK);
        inputJack.setRadius(10);

        baseLayout.getChildren().add(outputJack);
        baseLayout.getChildren().add(inputJack);

        baseLayout.setLayoutX(300);
        baseLayout.setLayoutY(200);
    }
}
