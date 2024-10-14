package com.example.synthesizer;

import javafx.geometry.Insets;
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

import javax.sound.sampled.Line;
import java.awt.*;

public class AudioComponentWidget extends Pane {

    AudioComponentWidget(AudioComponent ac, AnchorPane parent, String name) {
        audioComponent_ = ac;
        parent_= parent;
        name_ = name;

        baseLayout = new HBox();
        baseLayout.setStyle("-fx-background-color: white; -fx-border-color: black; -fx-border-width: 3");
        this.getChildren().add(baseLayout);

        VBox rightSide = new VBox();
        rightSide.setAlignment(Pos.CENTER);
        rightSide.setPadding(new Insets(3));
        rightSide.setSpacing(5);
        Button close = new Button("Close");
        close.setOnAction(e -> destroyWidget());
        Circle circle = new Circle(10);
        circle.setFill(Color.BLUE);

        rightSide.getChildren().add(close);
        rightSide.getChildren().add(circle);

        VBox left = new VBox();
        Label title = new Label(name);
        Slider slider = new Slider();
        left.getChildren().add(title);
        left.getChildren().add(slider);

        baseLayout.getChildren().add(left);
        baseLayout.getChildren().add(rightSide);

        this.setLayoutX(50);
        this.setLayoutY(100);
    };
    private void destroyWidget() {
        parent_.getChildren().remove(this);
        SynthesizeApplication.removeWidget(this);
    }
    public AudioComponent getAudioComponent() {return audioComponent_;}
    private AnchorPane parent_;
    private HBox baseLayout;
    private AudioComponent audioComponent_;
    private AudioComponentWidget widegetIamSendingOutputTo = null;
    private String name_;
    private Line line_;
    private Label nameLable_;
    double mouseStartDragX_, mouseStartDragY_, widgetStartDragX_, widgetStartDragY_;
}
