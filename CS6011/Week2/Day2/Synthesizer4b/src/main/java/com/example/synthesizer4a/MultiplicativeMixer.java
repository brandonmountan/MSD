package com.example.synthesizer4a;

import java.util.ArrayList;
import java.util.List;

public class MultiplicativeMixer implements AudioComponent {
    private final List<AudioComponent> inputs = new ArrayList<>();

    @Override
    public AudioClip getClip() {
        AudioClip result = new AudioClip();

        for (AudioComponent input : inputs) {
            AudioClip inputClip = input.getClip();
            for (int i = 0; i < AudioClip.TOTAL_SAMPLES; i++) {
                int combinedSample = result.getSample(i) * inputClip.getSample(i);
                result.setSample(i, Math.max(Short.MIN_VALUE, Math.min(Short.MAX_VALUE, combinedSample)));
            }
        }

        return result;
    }

    @Override
    public boolean hasInput() {
        return true; // Can accept inputs
    }

    @Override
    public void connectInput(AudioComponent input) {
        inputs.add(input);
    }
}
