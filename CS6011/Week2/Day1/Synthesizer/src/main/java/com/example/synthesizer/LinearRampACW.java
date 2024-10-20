package com.example.synthesizer;

import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class LinearRampACW extends AudioComponentWidget {

    LinearRampACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);

        VBox rightSide = new VBox();

        Label title = new Label(name);
        rightSide.getChildren().add(title);

        Slider startSlider = new Slider(0, 2000, 50);
        startSlider.valueChangingProperty().addListener(e -> handleSliderStart(startSlider));
        Slider stopSlider = new Slider(0, 2000, 2000);
        stopSlider.valueChangingProperty().addListener(e -> handleSliderStop(stopSlider));
        rightSide.getChildren().add(startSlider);
        rightSide.getChildren().add(stopSlider);

        baseLayout.getChildren().add(rightSide);
        outputJack.setFill(Color.STEELBLUE);
        outputJack.setRadius(10);
        baseLayout.getChildren().add(outputJack);

        super.setLayoutX(0);
        super.setLayoutY(120);
    }

    private void handleSliderStart(Slider slider) {
        if (!slider.isValueChanging()) {
            ((LinearRamp)audioComponent_).start = (int)slider.getValue();
        }
    }

    private void handleSliderStop(Slider slider) {
        if (!slider.isValueChanging()) {
            ((LinearRamp)audioComponent_).stop = (int)slider.getValue();
        }
    }

//    public Circle outputJack = new Circle();

}
