package com.example.synthesizer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.*;

public class Main {

    public static void main(String[] args) throws LineUnavailableException {

        // sinewave sw = new sinewave()
        // volumeadjuster va = new volumeadjuster()
        // va.setvol(1.0)
        // va.connectinput(sw)
        // audioclip ac = va.getclip()

        // mixer will have AudioComponent[] connections. takes in volume component and vol

// Get properties from the system about samples rates, etc.
// AudioSystem is a class from the Java standard library.
        Clip c = AudioSystem.getClip(); // Note, this is different from our AudioClip class.

// This is the format that we're following, 44.1 KHz mono audio, 16 bits per sample.
        AudioFormat format16 = new AudioFormat( 44100, 16, 1, true, false );

        AudioComponent audioComponent1 = new SineWave(440); // Your code
        AudioComponent audioComponent2 = new SineWave(880);

        VolumeAdjuster volumeAdjuster = new VolumeAdjuster(1600);

        volumeAdjuster.connectInput(audioComponent1, 0);

        AudioClip audioClip = volumeAdjuster.getClip();         // Your code

        c.open( format16, audioClip.getData(), 0, audioClip.getData().length ); // Reads data from our byte array to play it.

        System.out.println( "About to play..." );
        c.start(); // Plays it.
        c.loop( 0 ); // Plays it 2 more times if desired, so 6 seconds total

// Makes sure the program doesn't quit before the sound plays.
        while( c.getFramePosition() < AudioClip.TOTAL_SAMPLES || c.isActive() || c.isRunning() ){
            // Do nothing while we wait for the note to play.
        }

        System.out.println( "Done." );
        c.close();
    }
}
