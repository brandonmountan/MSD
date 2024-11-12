package com.example.synthesizer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import javafx.scene.shape.Line;

import javax.sound.sampled.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import javafx.scene.paint.Color;
import javafx.scene.control.Label;

import javafx.event.ActionEvent;

import javafx.scene.input.MouseEvent;

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

        Button volumeBtn = new Button("Volume Adjuster");
        rightPane.getChildren().add(volumeBtn);
        volumeBtn.setOnAction(e -> createComponent("VolumeAdjuster"));

        Button linearRampBtn = new Button("Linear Ramp");
        rightPane.getChildren().add(linearRampBtn);
        linearRampBtn.setOnAction(e -> createComponent("LinearRamp"));

        Button vfSineWaveBtn = new Button("VF Sine Wave");
        rightPane.getChildren().add(vfSineWaveBtn);
        vfSineWaveBtn.setOnAction(e -> createComponent("VFSineWave"));

        mainCanvas_ = new AnchorPane();
        mainCanvas_.setStyle("-fx-background-color: darkgrey");

        Speaker speaker = new Speaker();
        mainCanvas_.getChildren().add(speaker);

        mainCanvas_.setOnMouseClicked(this::mouseClicked);
//        mainCanvas_.setOnMouseDragged(this::mouseDragged);
//        mainCanvas_.setOnMouseReleased(this::mouseReleased);

        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        Button playBtn = new Button("Play");
        playBtn.setOnAction(e -> play());
        bottomPane.getChildren().add(playBtn);

        Label title = new Label("Brandon Mountan's Synthesizer");

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
        if (Objects.equals(name, "SineWave")) {
            AudioComponent ac = new SineWave(440);
            SineWaveACW acw = new SineWaveACW(ac, mainCanvas_, "Sine Wave");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        } else if (Objects.equals(name, "VolumeAdjuster")) {
            AudioComponent ac = new VolumeAdjuster(1);
//            ac.connectInput(allWidgets_.getFirst().getAudioComponent());
            VolumeAdjusterACW acw = new VolumeAdjusterACW(ac, mainCanvas_, "Volume Adjuster");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        } else if (Objects.equals(name, "LinearRamp")) {
            AudioComponent ac = new LinearRamp(50, 2000);
            LinearRampACW acw = new LinearRampACW(ac, mainCanvas_, "Linear Ramp");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        } else if (Objects.equals(name, "VFSineWave")) {
            AudioComponent ac = new VFSineWave();
//            ac.connectInput(allWidgets_.getFirst().getAudioComponent());
            VFSineWaveACW acw = new VFSineWaveACW(ac, mainCanvas_, "VFSineWave");
            mainCanvas_.getChildren().add(acw);
            allWidgets_.add(acw);
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void removeWidget(AudioComponentWidget acw) {
        allWidgets_.remove(acw);
    }

    private void mouseClicked(MouseEvent e) {
        for (AudioComponentWidget acw : allWidgets_) {
            Point2D mouseLocalPoint = acw.outputJack.sceneToLocal(e.getSceneX(), e.getSceneY());
            boolean mousePointInJack = acw.outputJack.contains(mouseLocalPoint);
            if (mousePointInJack) {

                System.out.println("mouse clicked on output jack");
            } else {
                System.out.println("mouse not clicked on output jack");
            }
        }
    }

//    public void mouseDragged(MouseEvent e) {
//        //
//    }


//    public void mouseReleased(MouseEvent e) {
//        for (AudioComponentWidget acw : allWidgets_) {
//            // check if mouse is released within input jack
//            if (acw.inputJack != null) {
//                Point2D mouseLocalPoint = acw.inputJack.sceneToLocal(e.getSceneX(), e.getSceneY());
//                boolean mousePointInJack = acw.inputJack.contains(mouseLocalPoint);
//                if (mousePointInJack) {
//                    System.out.println("mouse released on input jack");
//                } else {
//                    System.out.println("mouse not released in input jack");
//                }
//            } else {
//                System.out.println("input jack is null");
//            }
//        }
//    }


    public static AnchorPane mainCanvas_;
    public static ArrayList<AudioComponentWidget> allWidgets_ = new ArrayList<>();

    //    public static ArrayList<Cable> connectedWidgets_ = new ArrayList<>();
}