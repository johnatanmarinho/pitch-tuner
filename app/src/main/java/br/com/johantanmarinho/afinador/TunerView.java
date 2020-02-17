package br.com.johantanmarinho.afinador;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import br.com.johantanmarinho.afinador.core.MPM;
import br.com.johantanmarinho.afinador.tuner.Note;
import br.com.johantanmarinho.afinador.tuner.NoteMap;

/**
 * Created by johnatan on 05/02/20.
 */

public class TunerView extends SurfaceView implements Runnable, SurfaceHolder.Callback {

    private boolean isRunning = false;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Thread tunerThread;
    private AudioRecord audioRecord;
    private MPM mpm;
    private short[] data;
    private double pitch = 0;
    private Note detectedNote;
    private int frequence = 440;
    private DrawNote drawNote;
    private Bitmap teste;
    private Canvas canvas;
    private Matrix identityMatrix;

    public TunerView(Context context) {
        super(context);
        initialize();

    }


    private void initialize(){

        drawNote = new DrawNote();

        int bufferSize = AudioRecord.getMinBufferSize(44000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, 44000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize); // esse ai tava bufferElements * bufferSize
        data = new short[bufferSize];
        mpm = new MPM(audioRecord.getSampleRate(), bufferSize);
        detectedNote = new Note();
    }


    private double getDB(short[] buffer) {
        double average = 0;
        int bufferSize = buffer.length;
        for(short data : buffer){
            if(data > 0){
                average += Math.abs(data);
            }else{
                bufferSize--;
            }
        }

        double x = average/bufferSize;

        return (20 * Math.log10( (x / 51805.5336 ) / 0.00002 ) );

    }



    public void draw(){



        drawNote.draw(this.canvas, detectedNote);

        if(!getHolder().getSurface().isValid())
            return;

        Canvas canvas = getHolder().lockCanvas();

        canvas.drawBitmap(teste, identityMatrix, paint);

        if(getDB(data) > 40 && pitch != 0)
            paint.setColor(Color.rgb(245,127,23));
        else
            paint.setColor(Color.DKGRAY);

        float w = TunerActivity.WIDTH/2;
        float h = TunerActivity.HEIGHT;

        float size = (float)(-1 *(getDB(data) ) < -42 ? -1 *getDB(data)  : -42);
        size = size < -105 ? -105: size;



        canvas.translate(0, -100);
        canvas.drawRoundRect(w-10, h + size, w + 10, h-42, 15, 15, paint);
        paint.setColor(Color.GRAY);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(5);
        canvas.drawRoundRect(w-10, h -105, w + 10, h-42, 15, 15, paint);
        canvas.drawLine(w, h -10, w, h - 35, paint);
        canvas.drawLine(w-10, h-10, w + 10, h-10, paint);
        canvas.drawArc(w-18, h- 80, w + 18, h -35, 0, 180, false, paint);
        paint.reset();
        canvas.translate(0, 100);
        getHolder().unlockCanvasAndPost(canvas);
    }

    @Override
    public void run() {
        while (isRunning){
            audioRecord.read(data, 0, data.length);
            System.out.println(getDB(data));
            pitch = mpm.getPitchFromShort(data);

            detectedNote();

            draw();

            try {
                Thread.sleep(1000/30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void detectedNote(){
        Note detectedNote = NoteMap.getNoteFromPitch(pitch, frequence);
        if(detectedNote.getNoteName() != new Note().getNoteName())
            this.detectedNote = detectedNote;
    }
    public void pause(){
        isRunning = false;

        boolean retry = true;

        while(retry){
            try {
                tunerThread.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    public void updateFrequence(int frequence){
        this.frequence = frequence;
    }

    public void resume(){

        getHolder().addCallback(this);
        isRunning = true;
        audioRecord.startRecording();
        tunerThread = new Thread(this);
        tunerThread.start();


    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        int w = getWidth();
        int h = getHeight();
        teste = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        canvas = new Canvas();

        canvas.setBitmap(teste);

        identityMatrix = new Matrix();

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
