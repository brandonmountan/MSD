package com.example.synthesizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.control.Button;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;

public class SynthesizeApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Synthesizer");

        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(10, 10, 10, 20));
        rightPane.setStyle("-fx-background-color: darkgrey;");
        Button sineWaveBtn = new Button("Sine Wave");
        rightPane.getChildren().add(sineWaveBtn);

        sineWaveBtn.setOnAction(e -> createComponent("SineWave"));

        mainCanvas_ = new AnchorPane();
        mainCanvas_.setStyle("-fx-background-color: darkgrey");

        speaker_ = new Circle(450, 200, 15);
        speaker_.setFill(Color.BLACK);
        mainCanvas_.getChildren().add(speaker_);

        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        Button playBtn = new Button("Play");
        playBtn.setOnAction(e -> play());
        bottomPane.getChildren().add(playBtn);

        Label title = new Label("Synthesizer");

        root.setRight(rightPane);
        root.setCenter(mainCanvas_);
        root.setBottom(bottomPane);
        root.setTop(title);

        stage.setScene(scene);
        stage.show();

    }

    private void play() {
// Get properties from the system about samples rates, etc.
// AudioSystem is a class from the Java standard library.
        try {
        Clip c = AudioSystem.getClip(); // Note, this is different from our AudioClip class.


        AudioListener listener = new AudioListener(c);
// This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
        AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );

        AudioComponent audioComponent1 = new SineWave(440);

        // TEST sinewave
//        SineWave sineWave = new SineWave(440);
//        AudioClip audioClip = sineWave.getClip();

        // TEST volume adjuster
//        VolumeAdjuster volumeAdjuster = new VolumeAdjuster(2);
//        volumeAdjuster.connectInput(audioComponent1, 0);
//        AudioClip audioClip = volumeAdjuster.getClip();

        // TEST mixer
        // try turning down volume before playing so using volume adjuster
//        AudioComponent audioComponent2 = new SineWave(220);
//        Mixer mixer = new Mixer();
//        mixer.connectInput(audioComponent1, 0);
//        mixer.connectInput(audioComponent2, 0);
//        AudioClip audioClip = mixer.getClip();

        // TEST linear ramp vfsine wave
//        LinearRamp linearRamp = new LinearRamp(50, 2000);
//        VFSineWave vfSineWave = new VFSineWave();
//        vfSineWave.connectInput(linearRamp, 0);
//        AudioClip audioClip = vfSineWave.getClip();

        // for play() method

        Mixer mixer = new Mixer();

        for(AudioComponentWidget acw : allWidgets_) {
            AudioComponent ac = acw.getAudioComponent();
            mixer.connectInput(ac, 0);
        }

        AudioClip aclip = mixer.getClip();
        byte[] data = aclip.getData();

        c.open( format16, data, 0, data.length ); // Reads data from our byte array to play it.

        System.out.println( "About to play..." );
        c.start(); // Plays it.
//        c.loop( 2 ); // Plays it 2 more times if desired, so 6 seconds total

//// Makes sure the program doesn't quit before the sound plays.
//        while( c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning() ){
//            // Do nothing while we wait for the note to play.
//        }
        c.addLineListener(listener);
        System.out.println( "Done." );
//        c.close();
    }
        catch (LineUnavailableException e) {
            System.out.println("error with play method");
        }
    }

    private void createComponent(String name) {
        AudioComponent ac = new SineWave(440);
        AudioComponentWidget acw = new AudioComponentWidget(ac, mainCanvas_, "Sine Wave");
        mainCanvas_.getChildren().add(acw);
        allWidgets_.add(acw);
    }

    public static void main(String[] args) {
        launch();
    }
    public static void removeWidget(AudioComponentWidget ac) {
        allWidgets_.remove(ac);
    }
    private AnchorPane mainCanvas_;

    public static Circle speaker_;

    public static ArrayList<AudioComponentWidget> widgetsConnectedToSpeaker_ = new ArrayList<>();
    private static ArrayList<AudioComponentWidget> allWidgets_ = new ArrayList<>();




}