package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class VolumeAdjusterACW extends AudioComponentWidget {
    VolumeAdjusterACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);

        VBox rightSide = new VBox();

        Slider slider = new Slider(0.0, 10.0, 1.0);
        slider.valueChangingProperty().addListener(e -> handleSlider(slider));
        rightSide.getChildren().add(slider);

        baseLayout.getChildren().add(rightSide);

        VBox farRightSide = new VBox();

        outputJack.setFill(Color.STEELBLUE);
        outputJack.setRadius(10);

        inputJack.setFill(Color.PINK);
        inputJack.setRadius(10);

        farRightSide.getChildren().add(outputJack);
        farRightSide.getChildren().add(inputJack);

        baseLayout.getChildren().add(farRightSide);

        super.setLayoutX(0);
        super.setLayoutY(60);
    }

    private void handleSlider(Slider slider) {
        if (!slider.isValueChanging()) {
            ((VolumeAdjuster)audioComponent_).scale = (int)slider.getValue();
        }
    }
}
