package com.example.synthesizer4a;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SynthesizeApplication extends Application {

    @Override
    public void start(Stage primaryStage) {
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 400, 300);

        Slider frequencySlider = new Slider(50, 10000, 440); // Min, Max, Initial Value
        frequencySlider.setShowTickLabels(true);
        frequencySlider.setShowTickMarks(true);

        Button playButton = new Button("Play");

        root.setTop(frequencySlider);
        root.setCenter(playButton);

        playButton.setOnAction(event -> playSound(frequencySlider.getValue()));

        primaryStage.setTitle("Synthesizer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void playSound(double frequency) {
        AudioComponent ramp = new LinearRamp(50, (int) frequency);
        VFSineWave sineWave = new VFSineWave();
        sineWave.connectInput(ramp);

        AudioClip clip = sineWave.getClip();
        playClip(clip);
    }

    private void playClip(AudioClip clip) {
        try {
            Clip audioClip = AudioSystem.getClip();
            audioClip.open(new AudioFormat(44100, 16, 1, true, false), clip.getData(), 0, clip.getData().length);
            audioClip.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }


}
