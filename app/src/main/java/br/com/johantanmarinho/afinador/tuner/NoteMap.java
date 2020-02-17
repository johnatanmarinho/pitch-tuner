package br.com.johantanmarinho.afinador.tuner;

/**
 * Created by johnatan on 26/06/18.
 */

public class NoteMap {
    private static final double PITCH_LOW_LIMIT = 25.0f;
    private static final double PITCH_HIGH_LIMIT = 4200.0f;
    private static final double FACTOR = 1.059463;

    public static String[] NOTE_NAMES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};


    public static final double ALLOWABLE_ERROR = 4.5; //hz

    public static Note getNoteFromPitch(double pitch, int frequency) {
        Note emptyNote = new Note();

        double percentCloseness = 0.0f;

        double[][] octaves = getNoteMap(frequency);

        double[] comparisonOctave;
        double[] octave = null;
        int bestFitOctave = 0;

        if ((pitch < PITCH_LOW_LIMIT) || (pitch > PITCH_HIGH_LIMIT)) {
            return emptyNote;
        } else {
            for (int i = 0; i < octaves.length; i++) {
                comparisonOctave = octaves[i];
                if ((pitch > (comparisonOctave[0] - ALLOWABLE_ERROR)) &&
                        (pitch < (comparisonOctave[octaves[i].length - 1] + ALLOWABLE_ERROR))) {
                    octave = comparisonOctave;
                    bestFitOctave = i;
                    break;
                }
            }
        }

        if (octave == null) return emptyNote;

        double bestDifference = Double.MAX_VALUE;
        int bestFitNoteIndex = -1;

        for (int i = 0; i < octave.length; i++) {
            double diff = Math.abs(pitch - octave[i]);
            if (diff < bestDifference) {
                bestFitNoteIndex = i;
                bestDifference = diff;
            }
        }
        percentCloseness = (pitch / octave[bestFitNoteIndex]) * 100;
        //System.out.println(NOTE_NAMES[bestFitNoteIndex] + Integer.toString(bestFitOctave) + " hz:" + pitch);
        return new Note(octave[bestFitNoteIndex] ,pitch, bestFitOctave , percentCloseness, NOTE_NAMES[bestFitNoteIndex] );

    }

    public static double[][] getNoteMap(int frequency){

        double[][] octaves = new double[9][12];

        //find all chords from the same octave as A4
        for(int note = 0; note < octaves[4].length; note++){
            if(note < 9)
                octaves[4][note] = frequency / Math.pow(FACTOR, 9 - note) ;
            if(note > 9)
                octaves[4][note] = frequency * Math.pow(FACTOR, note- 9);
            if(note == 9)
                octaves[4][note] = frequency;
        }


        for(int i = 3; i >= 0; i--){
            for(int note = 0; note < octaves[0].length; note++){
                octaves[i][note] = octaves[i + 1][note] / 2;
                octaves[8 - i][note] = octaves[7 - i][note] * 2;
            }
        }

        return octaves;
    }
}
