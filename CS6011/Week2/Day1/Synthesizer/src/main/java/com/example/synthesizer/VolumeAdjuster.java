package com.example.synthesizer;

public class VolumeAdjuster implements AudioComponent {

    @Override
    public AudioClip getClip() {
        AudioClip adjustedClip = new AudioClip();
        adjustedClip = input.getClip();
        for (int i = 0; i < 88200; i++) {
            if ((scale * adjustedClip.getSample(i)) > 32000) {
                adjustedClip.setSample(i, 32000);
            } else if ((scale * adjustedClip.getSample(i)) < -32000) {
                adjustedClip.setSample(i, -32000);
            } else {
                adjustedClip.setSample(i, (scale * adjustedClip.getSample(i)));
            }
        }
        return adjustedClip;
    };

//    boolean hasInput() - can you connect something to this as an input?
    @Override
    public Boolean hasInputs() {
        return true;
    }

    @Override
    public void connectInput( AudioComponent input, int index ) {
        this.input = input;
    };

    VolumeAdjuster(int scale) {
        this.scale = scale;
    }

    private AudioComponent input; // setInput()
    private int scale;
}
