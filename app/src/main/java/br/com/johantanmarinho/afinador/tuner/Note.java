package br.com.johantanmarinho.afinador.tuner;

/**
 * Created by johnatan on 26/06/18.
 */

public class Note {
    private double frequency;
    private double difference;
    private String noteName;
    private int octave;
    private double originalFreq;

    public double getOriginalFreq() {
        return originalFreq;
    }

    public Note() {
        this.frequency = 0;
        this.difference = 0;
        this.octave = 0;
        this.originalFreq = 0;
        this.noteName = "Waiting...";
    }


    public Note(double originalFreq, double frenquency, int octave, double difference, String noteName) {
        this.frequency = frenquency;
        this.difference = difference;
        this.noteName = noteName;
        this.octave = octave;
        this.originalFreq = originalFreq;
    }

    public int getOctave() {
        return octave;
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency(double frequency) {
        this.frequency = frequency;
    }

    public double getDifference() {
        return difference;
    }

    public void setDifference(double difference) {
        this.difference = difference;
    }

    public String getNoteName() {
        return noteName;
    }
    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

}
