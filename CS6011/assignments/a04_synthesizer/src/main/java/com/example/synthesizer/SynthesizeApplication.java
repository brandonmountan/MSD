package com.example.synthesizer;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.sound.sampled.*;
import java.util.ArrayList;
import java.util.Objects;

public class SynthesizeApplication extends Application {

    public static AnchorPane mainCanvas_; // Canvas for placing widgets
    public static ArrayList<AudioComponentWidget> allWidgets_ = new ArrayList<>(); // Track all widgets

    @Override
    public void start(Stage stage) {
        // Setup root layout and scene
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 800, 600);
        stage.setTitle("Synthesizer");

        // Set up panes
        VBox rightPane = createRightPane();
        mainCanvas_ = createMainCanvas();
        HBox bottomPane = createBottomPane();

        // Application title
        Label title = new Label("Synthesizer");
        title.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        title.setPadding(new Insets(10));

        // Add components to the root layout
        root.setRight(rightPane);
        root.setCenter(mainCanvas_);
        root.setBottom(bottomPane);
        root.setTop(title);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates the right-side pane with buttons for adding components.
     */
    private VBox createRightPane() {
        VBox rightPane = new VBox();
        rightPane.setPadding(new Insets(10));
        rightPane.setSpacing(10);
        rightPane.setStyle("-fx-background-color: #d3d3d3;");

        // Add buttons for each component
        addButtonToPane("Sine Wave", rightPane, e -> createComponent("SineWave"));
        addButtonToPane("Volume Adjuster", rightPane, e -> createComponent("VolumeAdjuster"));
        addButtonToPane("Linear Ramp", rightPane, e -> createComponent("LinearRamp"));
        addButtonToPane("VF Sine Wave", rightPane, e -> createComponent("VFSineWave"));

        return rightPane;
    }

    /**
     * Adds a button with a given label to the specified pane and sets its action.
     */
    private void addButtonToPane(String label, VBox pane, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button button = new Button(label);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setOnAction(action);
        pane.getChildren().add(button);
    }

    /**
     * Creates the main canvas for placing and interacting with widgets.
     */
    private AnchorPane createMainCanvas() {
        AnchorPane canvas = new AnchorPane();
        canvas.setStyle("-fx-background-color: #808080;");
        canvas.setOnMouseClicked(this::handleCanvasClick);
        return canvas;
    }

    /**
     * Creates the bottom pane with the Play button.
     */
    private HBox createBottomPane() {
        HBox bottomPane = new HBox();
        bottomPane.setAlignment(Pos.CENTER);
        bottomPane.setPadding(new Insets(10));

        Button playBtn = new Button("Play");
        playBtn.setOnAction(e -> playAudio());
        bottomPane.getChildren().add(playBtn);

        return bottomPane;
    }

    /**
     * Handles the Play button action.
     */
    private void playAudio() {
        try {
            Clip clip = AudioSystem.getClip();
            AudioListener listener = new AudioListener(clip);
            AudioFormat format16 = new AudioFormat(44100, 16, 1, true, false);

            Mixer mixer = new Mixer();
            for (AudioComponentWidget acw : allWidgets_) {
                mixer.connectInput(acw.getAudioComponent());
            }

            AudioClip audioClip = mixer.getClip();
            clip.open(format16, audioClip.getData(), 0, audioClip.getData().length);
            clip.start();
            clip.addLineListener(listener);

            System.out.println("Audio playing...");
        } catch (LineUnavailableException e) {
            System.err.println("Error playing audio: " + e.getMessage());
        }
    }

    /**
     * Creates and adds an AudioComponent widget to the main canvas.
     */
    private void createComponent(String name) {
        AudioComponentWidget widget = null;

        switch (name) {
            case "SineWave" -> widget = new SineWaveACW(new SineWave(440), mainCanvas_, "Sine Wave");
            case "VolumeAdjuster" -> widget = new VolumeAdjusterACW(new VolumeAdjuster(1), mainCanvas_, "Volume Adjuster");
            case "LinearRamp" -> widget = new LinearRampACW(new LinearRamp(50, 2000), mainCanvas_, "Linear Ramp");
            case "VFSineWave" -> widget = new VFSineWaveACW(new VFSineWave(), mainCanvas_, "VF Sine Wave");
        }

        if (widget != null) {
            mainCanvas_.getChildren().add(widget);
            allWidgets_.add(widget);
        }
    }

    /**
     * Handles clicks on the main canvas, such as for jack connections.
     */
    private void handleCanvasClick(MouseEvent e) {
        for (AudioComponentWidget widget : allWidgets_) {
            Point2D mouseLocalPoint = widget.outputJack.sceneToLocal(e.getSceneX(), e.getSceneY());
            if (widget.outputJack.contains(mouseLocalPoint)) {
                System.out.println("Output jack clicked on widget: " + widget.getClass().getSimpleName());
            }
        }
    }

    /**
     * Removes a widget from the main canvas and list.
     */
    public static void removeWidget(AudioComponentWidget acw) {
        allWidgets_.remove(acw);
    }

    public static void main(String[] args) {
        launch();
    }
}
