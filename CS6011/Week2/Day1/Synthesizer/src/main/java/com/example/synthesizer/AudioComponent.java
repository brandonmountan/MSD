package com.example.synthesizer;

public interface AudioComponent {
    //    AudioClip getClip() - return the current sound produced by this component
    AudioClip getClip();
// can you connect something to this as an input?
    Boolean hasInputs();

    void connectInput(AudioComponent ac, int index);
}

//    AudioComponent note = new SineWave( 440 );
//    AudioComponent vol = new VolumeAdjuster();
//    vol.connectInput( note );



