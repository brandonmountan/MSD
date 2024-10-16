package com.example.synthesizer;

import javafx.scene.layout.AnchorPane;

public class VFSineWaveACW extends AudioComponentWidget {

    VFSineWaveACW(AudioComponent ac, AnchorPane parent, String name) {
        super(ac, parent, name);
        baseLayout.setLayoutX(0);
        baseLayout.setLayoutY(240);
    }
}
