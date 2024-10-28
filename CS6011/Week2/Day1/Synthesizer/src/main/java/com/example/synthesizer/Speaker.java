package com.example.synthesizer;

import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.awt.*;

public class Speaker extends Pane {

    public Circle input = new Circle();
    public HBox speakerLayout;

    Speaker() {
        speakerLayout = new HBox();
        speakerLayout.setStyle("-fx-background-color: white;");
        this.getChildren().add(speakerLayout);

        VBox vbox = new VBox();
        input.setFill(Color.BLACK);
        input.setRadius(30);
        vbox.getChildren().add(input);
        super.setLayoutX(200);
        super.setLayoutY(200);
    }
}
