package br.com.johantanmarinho.afinador;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.NonNull;

import java.text.DecimalFormat;

import br.com.johantanmarinho.afinador.tuner.Note;
import br.com.johantanmarinho.afinador.tuner.NoteMap;

/**
 * Created by johnatan on 07/02/20.
 */

public class DrawNote {


    private Path freqCircle;
    private Path outCircle;
    private Paint paint;
    private DecimalFormat df;
    private float width;
    private float height;
    private float r;
    private float circumference;
    private float arcNumber;
    private float noteAngle;
    private Path agulha;
    private Paint campoNota;

    public DrawNote(){
        setup();
    }

    public void draw(Canvas canvas, Note note){

        paint.setColor(Color.rgb(144,164,174));
        LinearGradient l = new LinearGradient(0,0,width,height, Color.WHITE, Color.rgb(144,164,174), Shader.TileMode.CLAMP);
        paint.setShader(l);
        canvas.drawRect(0,0, width, height, paint);
        paint.reset();






        paint.setColor( Color.rgb(144,164,174));
        paint.setShadowLayer(10,0,0,Color.argb(70, 0,0,0));
        canvas.drawPath(outCircle, paint);
        paint.reset();

        float offset = 0;           //distancia entre notas
        float offsetchoose = 0;     // nota escolhida
        String freq = "0hz";

        float aux = -noteAngle/2;
        paint.setTextSize(28);
        for (int i = 0; i < NoteMap.NOTE_NAMES.length; i++){
            paint.setColor(Color.WHITE);
            paint.setFakeBoldText(true);
            if(note.getNoteName() == NoteMap.NOTE_NAMES[i]){
                offsetchoose = offset;

                campoNota.setStyle(Paint.Style.FILL);

                freq = df.format(note.getFrequency()) + "hz / " + df.format(note.getOriginalFreq()) + "hz";

                if(Math.abs(note.getFrequency() - note.getOriginalFreq()) < 0.5f)
                    paint.setColor(Color.rgb(31,170,0));
                else
                    paint.setColor(Color.GRAY);

                campoNota.setShader(l);
            }else{
                campoNota.setStyle(Paint.Style.STROKE);
            }
            //desenha campo da nota
            float x =(float) Math.cos(noteAngle + aux) * r + width/2 ;
            float y =(float) Math.sin(noteAngle + aux) * r + height/2 ;


            campoNota.setColor( Color.rgb(176,190,197));
            canvas.drawLine(width/2, height/2, x, y, campoNota);

            RectF bounds = new RectF();
            outCircle.computeBounds(bounds, true);
            canvas.drawArc(bounds,(float) Math.toDegrees(aux),(float)Math.toDegrees(noteAngle), true, campoNota);
            campoNota.reset();
            canvas.drawTextOnPath(NoteMap.NOTE_NAMES[i], outCircle, offset - paint.measureText(NoteMap.NOTE_NAMES[i])/2, 60f, paint);

            paint.setColor( Color.rgb(176,190,197));

            aux += noteAngle;
            offset += arcNumber;
        }

        paint.reset();
        double error =  note.getFrequency() -note.getOriginalFreq();

        error = (error > NoteMap.ALLOWABLE_ERROR ) ? NoteMap.ALLOWABLE_ERROR : error;
        error = (error < -NoteMap.ALLOWABLE_ERROR ) ? -NoteMap.ALLOWABLE_ERROR : error;


        offsetchoose = (float)(offsetchoose + (((arcNumber/2 ) *  error)/ NoteMap.ALLOWABLE_ERROR )) * 360 / circumference;


        canvas.rotate(offsetchoose , width/2, height/2);
        paint.setColor(Color.rgb(245,127,23));
        canvas.drawPath(agulha, paint);
        canvas.rotate(-(offsetchoose), width/2, height/2);


        String octave = note.getOctave() + "th";
        Path centCircle = getOctaveContainer();

        paint.setColor(Color.rgb(60,90,100));
        paint.setTextSize(38);
        canvas.drawPath(centCircle, paint);


        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.rgb(245,127,23));
        paint.setStrokeWidth(10);
        paint.setShadowLayer(5.0f, 0.0f, 5.0f, Color.argb(70, 0,0,0));
        canvas.drawPath(centCircle, paint);
        paint.setShadowLayer(0,0,0,02);
        paint.setStrokeWidth(1);
        paint.setStyle(Paint.Style.FILL);

        paint.setColor(Color.WHITE);
        canvas.drawText(octave,width/2 - paint.measureText(octave)/2, height/2 - 10, paint);
        String octave2 = "octave";
        paint.setTextSize(18);
        canvas.drawText(octave2, width/2 - paint.measureText(octave2)/2, height/2 + 10, paint);

        canvas.drawText(freq, width/2 - paint.measureText(freq)/2, height/2 + 30, paint);


    }


    private Path getOctaveContainer() {
        Path centCircle = new Path();
        centCircle.addCircle(width /2, height/2, r/3, Path.Direction.CW );
        return centCircle;
    }

    private void setup() {
        width = TunerActivity.WIDTH;
        height = TunerActivity.HEIGHT;
        r = Math.min(width /2 - 50, height /2 - 50);
        circumference = (float) (2 * Math.PI * r);
        arcNumber = circumference / NoteMap.NOTE_NAMES.length;

        noteAngle = (float) ( Math.PI * 2 ) / NoteMap.NOTE_NAMES.length;

        agulha = agulha();
        df = new DecimalFormat("00.00");
        paint = new Paint();
        paint.setAntiAlias(true);

        freqCircle = new Path();
        freqCircle.addCircle(width /2, 250, 150, Path.Direction.CW);
        outCircle = new Path();
        outCircle.addCircle(width /2, height /2, r, Path.Direction.CW );


        campoNota = new Paint();
    }


    private Path agulha(){
        Path agulha = new Path();

        agulha.lineTo(0,-32);
        agulha.lineTo(r - 60, 0);
        agulha.lineTo(0,32);
        agulha.close();


        Matrix m = new Matrix();
        m.setTranslate(width/2,height/2);
        agulha.transform(m);

        return agulha;
    }
    private Path teste(){
        Path p = new Path();

        float x = width/2 + r;
        float y = height/2;
        float angle = arcNumber * 360/ circumference;


        return p;
    }
}

