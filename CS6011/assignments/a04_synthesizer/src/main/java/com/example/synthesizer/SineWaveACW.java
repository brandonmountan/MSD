package com.example.synthesizer;

import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class SineWaveACW extends AudioComponentWidget {

    SineWaveACW(AudioComponent ac, AnchorPane parent, String name) {

        super(ac, parent, name);

        VBox rightSide = new VBox();

        Slider slider = new Slider(0, 1000, 440);
        slider.setShowTickLabels(true);
        slider.valueChangingProperty().addListener(e -> handleSlider(slider));

        rightSide.getChildren().add(slider);

        baseLayout.getChildren().add(rightSide);
        outputJack.setFill(Color.STEELBLUE);
        outputJack.setRadius(10);
        baseLayout.getChildren().add(outputJack);

        inputJack = null;
    }

    private void handleSlider(Slider slider) {
        if (!slider.isValueChanging()) {
            ((SineWave)audioComponent_).frequency = (int)slider.getValue();
        }
    }
}