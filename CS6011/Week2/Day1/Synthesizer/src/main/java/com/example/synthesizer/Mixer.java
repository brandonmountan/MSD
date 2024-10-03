package com.example.synthesizer;

import java.util.ArrayList;

public class Mixer implements AudioComponent {


    @Override
    public AudioClip getClip() {
//        A mixer is very similar to a filter except it can take multiple
//        inputs. To use it, you'll the connectInput() method once for each
//        input you want to add up. You'll need to update the connectInput()
//        method of your AudioComponent interface to allow specifying which
//
//        Create a mixer that creates an audio clip that adds all the input
//        signals together to produce the output.
//
//        Test that you create 2 sine waves of different frequencies
//        (pitches), make them softer using your volume filter, then
//        add them together and play the output. Note: if you don't scale
//        volumes or implement clamping, you'll get really weird sounding
//        output!

        AudioClip mixedClip = new AudioClip();
        for (int i = 0; i < inputs.size(); i++) {
            AudioClip loopClip = inputs.get(i).getClip();
            for (int j = 0; j < 88200; i++){
                mixedClip.setSample(j, (mixedClip.getSample(j) + loopClip.getSample(j)));
            }
        }
        return mixedClip;
    }

    @Override
    public Boolean hasInputs() {
        return true;
    }

    @Override
    public void connectInput(AudioComponent ac, int index) {
        inputs.add(ac);
    }

    ArrayList<AudioComponent> inputs = new ArrayList<>();
}
