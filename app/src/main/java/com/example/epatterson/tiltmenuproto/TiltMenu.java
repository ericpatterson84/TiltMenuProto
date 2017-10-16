package com.example.epatterson.tiltmenuproto;

import android.graphics.Rect;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.Image;
import android.os.health.PackageHealthStats;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class TiltMenu extends AppCompatActivity implements TwoWayTiltListener{

    private TextView zAngle = null;
    private TextView percent = null;
    private TextView delta = null;
    private TextView nodeALabel = null;
    private TextView nodeBLabel = null;
    private TwoWayTiltManager tiltManager = null;
    private ImageView menuBall = null;
    private float ballInitYPos = 0.0f;
//    private int ballRadius = 0;
    TwoWayMenuNode rootMenuNode = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_menu);
        zAngle = (TextView)findViewById(R.id.zAngle);
        percent = (TextView)findViewById(R.id.percent);
        delta = (TextView)findViewById(R.id.delta);
        menuBall = (ImageView)findViewById(R.id.menuBall);
        nodeALabel = (TextView)findViewById(R.id.textTop);
        nodeBLabel = (TextView)findViewById(R.id.textBottom);

//        ballRadius = menuBall.getHeight()/2;

        menuBall.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if(top > 0)
                        {
                            ballInitYPos = top;
                            v.removeOnLayoutChangeListener(this);

                            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                            tiltManager = new TwoWayTiltManager(sensorManager, (TwoWayTiltListener)v.getContext());
                        }
                    }
                }
        );

        try {
            InputStream jsonMenuStream = getResources().openRawResource(R.raw.two_way_menu);
            byte[] menuBytes = new byte[jsonMenuStream.available()];
            jsonMenuStream.read(menuBytes);
            jsonMenuStream.close();
            String menuJsonStr = new String(menuBytes);

            TwoWayMenuParser parser = new TwoWayMenuParser();
            rootMenuNode = parser.parseFromJson(menuJsonStr);
            nodeALabel.setText(rootMenuNode.getSubALabel());
            nodeBLabel.setText(rootMenuNode.getSubBLabel());

        } catch (IOException e) {

        }

    }

    @Override
    public void onTiltAngleChanged(float angle) {
        //invert the direction of the angle to make the Y position math easier
        angle *= -1.0f;

        //500 pixel diameter circle
        //45 degree tilt should get ball to the edge of range
        //translate rotation angle to y position within the circle
        //add ball radius to calculated y pos

        float rotationPercent = angle / 35.0f;
        float yPosDelta = rotationPercent * 300;
        float newBallYPos = ballInitYPos + yPosDelta;

        if(Math.abs(angle) <= 35.0f) {
            menuBall.setY(newBallYPos);
        }

        String zAngleStr = Float.toString(angle);
        zAngle.setText(zAngleStr);

        String percentStr = Float.toString(rotationPercent);
        percent.setText(percentStr);

        String deltaStr = Float.toString(yPosDelta);
        delta.setText(deltaStr);
    }
}