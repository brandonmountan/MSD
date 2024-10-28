package com.example.synthesizer;

import java.util.ArrayList;

public class Mixer implements AudioComponent {

    public ArrayList<AudioComponent> inputs = new ArrayList<>();

    @Override
    public AudioClip getClip() {
        AudioClip mixedClip = new AudioClip();
        for (AudioComponent input : inputs) {
            AudioClip clip = input.getClip();
            for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
                int mixedSample = mixedClip.getSample(i) + clip.getSample(i);
                if (mixedSample > 32000) {
                    mixedSample = 32000;
                } else if (mixedSample > -32000) {
                    mixedSample = -32000;
                }
                mixedClip.setSample(i, mixedSample);
            }
        }
        return mixedClip;
    }

    @Override
    public boolean hasInput() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent input) {
        inputs.add(input);
    }

}
