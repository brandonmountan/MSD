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
import java.util.Objects;

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

        Button linearRampBtn = new Button("Linear Ramp");
        rightPane.getChildren().add(linearRampBtn);
        linearRampBtn.setOnAction(e -> createComponent("LinearRamp"));


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

        Label title = new Label("Brandon Mountan's Synthesizer");
        title.setAlignment(Pos.CENTER);

        root.setRight(rightPane);
        root.setCenter(mainCanvas_);
        root.setBottom(bottomPane);
        root.setTop(title);

        stage.setScene(scene);
        stage.show();

    }

    private void play() {
    try {
        Clip c = AudioSystem.getClip();

        AudioListener listener = new AudioListener(c);

        AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );

        Mixer mixer = new Mixer();

        for(AudioComponentWidget acw : allWidgets_) {
            AudioComponent ac = acw.getAudioComponent();
            mixer.connectInput(ac);
        }

        AudioClip audioClip = mixer.getClip();
        byte[] data = audioClip.getData();

        c.open( format16, data, 0, data.length ); // Reads data from our byte array to play it.

        System.out.println( "About to play..." );
        c.start();

        c.addLineListener(listener);
        System.out.println( "Done." );
    }
        catch (LineUnavailableException e) {
            System.out.println("error with play method");
        }
    }

    private void createComponent(String name) {
        if(Objects.equals(name, "SineWave")) {
            AudioComponent ac = new SineWave(440);
            AudioComponentWidget acw = new AudioComponentWidget(ac, mainCanvas_, "Sine Wave");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        } else if(Objects.equals(name, "LinearRamp")) {
            AudioComponent ac = new LinearRamp(50, 2000);
            VFSineWave vfSineWave = new VFSineWave();
            vfSineWave.connectInput(ac);
            AudioComponentWidget acw = new AudioComponentWidget(ac, mainCanvas_, "Linear Ramp");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        }
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
    // spaghetti hack making it public.
    public static ArrayList<AudioComponentWidget> allWidgets_ = new ArrayList<>();

}