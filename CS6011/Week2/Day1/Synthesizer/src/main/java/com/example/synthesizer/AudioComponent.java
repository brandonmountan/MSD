package com.example.synthesizer;

public interface AudioComponent {
    AudioClip getClip();

    default boolean hasInputs() { return false; }

    void connectInput( AudioComponent input );

    default int availableInputPorts() { return 0; }

    default void removeInput( AudioComponent input ) { assert( false ); }
}



