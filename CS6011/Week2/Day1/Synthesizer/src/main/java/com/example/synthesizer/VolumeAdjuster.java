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

        // How do I get the sound? Where is the sound coming into this?
        // Volume Adjuster?
        // the input_
        // if (input == null) { throw error }
        // audioclip ac = input.getclip();
        // short samp = ac.getsample(i); samp[i] *= scale; ac.setSample(i, samp); return ac;
        // byte[] data = ac.getdata()
//        AudioClip result = // Some modification of the original clip.
//        return result;
//        AudioClip out = input_.getClip();
        // for each sample in out
        // sample *= scale

    };

//    boolean hasInput() - can you connect something to this as an input?
    @Override
    public Boolean hasInputs() {
        return true;
    }

//    void connectInput( AudioComponent input) - connect another device to this input.
//    For most classes implementing this interface, this method will just store a reference to the AudioComponent parameter.
//    If the component doesn't accept inputs, you can assert( false ) in here.
    @Override
    public void connectInput( AudioComponent input, int index ) {
        // talk to sinewave class
        this.input = input;
    };

    VolumeAdjuster(int scale) {
        this.scale = scale;
    }

    private AudioComponent input; // setInput()
    private int scale;
}
