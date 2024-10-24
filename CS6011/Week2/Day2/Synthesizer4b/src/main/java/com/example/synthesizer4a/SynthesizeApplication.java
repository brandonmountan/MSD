package com.example.synthesizer4a;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javax.sound.sampled.*;

public class SynthesizeApplication extends Application {
    private int frequency = 440; // Default frequency

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Synthesizer");

        BorderPane root = new BorderPane();
        Label frequencyLabel = new Label("Frequency: " + frequency);
        Slider frequencySlider = new Slider(50, 10000, frequency);
        frequencySlider.valueProperty().addListener((obs, oldValue, newValue) -> {
            frequency = newValue.intValue();
            frequencyLabel.setText("Frequency: " + frequency);
        });

        Button playButton = new Button("Play");
        playButton.setOnAction(e -> playSound());

        root.setTop(frequencyLabel);
        root.setCenter(frequencySlider);
        root.setBottom(playButton);

        Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playSound() {
        try {
            Clip c = AudioSystem.getClip();
            AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

            LinearRamp ramp = new LinearRamp(50, 2000);
            VFSineWave sineWave = new VFSineWave();
            ramp.connectInput(sineWave);
            sineWave.connectInput(ramp);

            c.open(format16, ramp.getClip().getData(), 0, ramp.getClip().getData().length);
            c.start();
            c.loop(0);

            // Close the clip on stop
            c.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP) {
                    c.close();
                }
            });
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
