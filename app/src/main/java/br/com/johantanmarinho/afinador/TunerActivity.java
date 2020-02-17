package br.com.johantanmarinho.afinador;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class TunerActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener{

    private TunerView tuner;

    public static float WIDTH;
    public static float HEIGHT;
    private int frequence = 440;
    public static int REQUEST_CAMERA;
    private Button btn;
    private RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);


        Point point = new Point();
        getWindowManager().getDefaultDisplay().getSize(point);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        WIDTH = metrics.widthPixels;
        HEIGHT = metrics.heightPixels;

        layout = new RelativeLayout(this);
        btn = new Button(this);
        btn.setText("LÁ: " + frequence + "hz");
        btn.setBackgroundColor(Color.TRANSPARENT);
        btn.setTextColor(Color.DKGRAY);

        btn.setWidth(300);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show();
            }
        });
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        btn.setPadding(0, 64, 0, 0);

        init();
        layout.addView(btn, params);



        setContentView(layout);
    }

    private void init(){
        if(checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED){
            startTuner();
        } else {
            if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Microphone permission is needed to start the tuner",
                        Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_CAMERA);
        }
    }

    private void startTuner(){
        tuner = new TunerView(this);
        layout.addView(tuner);
    }

    private void show(){
        final Dialog d = new Dialog(this);
        d.setTitle("Frequencia de Lá");
        d.setContentView(R.layout.dialog);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.numberPicker1);
        np.setMaxValue(446);
        np.setMinValue(431);
        np.setValue(frequence);
        np.setWrapSelectorWheel(false);
        np.setOnValueChangedListener(this);

        d.show();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if(tuner != null)
            tuner.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(tuner != null)
            tuner.resume();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CAMERA){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                startTuner();
            }else{
                Toast.makeText(this, "Permission was not granted", Toast.LENGTH_SHORT).show();
            }
        } else {
             super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        frequence = newVal;
        tuner.updateFrequence(frequence);
        btn.setText("LÁ: " + frequence + "hz");
    }
}
