package com.example.synthesizer;

import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class AudioComponentWidget extends Pane {

    public AudioComponent audioComponent_;
    private AnchorPane parent_;
    private String name_;

    public HBox baseLayout;
    public Circle inputJack = new Circle();
    public Circle outputJack = new Circle();

    AudioComponentWidget(AudioComponent ac, AnchorPane parent, String name) {

        audioComponent_ = ac;
        parent_= parent;
        name_ = name;

        baseLayout = new HBox();
        baseLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3");
        this.getChildren().add(baseLayout);

        Label title = new Label(name);
        baseLayout.getChildren().add(title);

        VBox leftSide = new VBox();
        leftSide.setAlignment(Pos.CENTER);
        leftSide.setPadding(new Insets(3));
        leftSide.setSpacing(5);

        Button close = new Button("Close");
        close.setOnAction(e -> destroyWidget());
        leftSide.getChildren().add(close);

        baseLayout.getChildren().add(leftSide);

        makeDraggable();

    };

    private void makeDraggable() {
        this.setOnMousePressed(event -> {
            this.setUserData(new Point2D(event.getSceneX(), event.getSceneY()));
        });

        this.setOnMouseDragged(event -> {
            Point2D initialPos = (Point2D) this.getUserData();
            double deltaX = event.getSceneX() - initialPos.getX();
            double deltaY = event.getSceneY() - initialPos.getY();

            this.setLayoutX(this.getLayoutX() + deltaX);
            this.setLayoutY(this.getLayoutY() + deltaY);

            this.setUserData(new Point2D(event.getSceneX(), event.getSceneY()));
        });
    }

    private void destroyWidget() {
        parent_.getChildren().remove(this);
        SynthesizeApplication.removeWidget(this);
    };

    public AudioComponent getAudioComponent() {
        return audioComponent_;
    }
}