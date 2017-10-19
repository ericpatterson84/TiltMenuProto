package com.example.epatterson.tiltmenuproto;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class TiltMenu extends AppCompatActivity implements TwoWayTiltListener{

    private TextView zAngle = null;
    private TextView percent = null;
    private TextView pathTarget = null;
    private TextView nodeALabel = null;
    private TextView nodeBLabel = null;
    private TwoWayTiltManager tiltManager = null;
    private ImageView menuBall = null;
    private float ballInitYPos = 0.0f;
//    private int ballRadius = 0;
    TwoWayMenuManager menuManager = null;
    private boolean menuSelected = false;
    private boolean currentPathDone = false;
    private DataCollectionManager dataCollectionManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_menu);
        zAngle = (TextView)findViewById(R.id.zAngle);
        percent = (TextView)findViewById(R.id.percent);
        pathTarget = (TextView)findViewById(R.id.delta);
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
                            tiltManager.startTiltRecord();

                            initMenuOptions();
                        }
                    }
                }
        );

        TwoWayMenuNode rootMenuNode = null;
        ArrayList<TwoWayMenuPath> menuPaths = null;

        try {
            InputStream jsonMenuStream = getResources().openRawResource(R.raw.two_way_menu);
            byte[] menuBytes = new byte[jsonMenuStream.available()];
            jsonMenuStream.read(menuBytes);
            jsonMenuStream.close();
            String menuJsonStr = new String(menuBytes);

            TwoWayMenuParser parser = new TwoWayMenuParser();
            rootMenuNode = parser.parseFromJson(menuJsonStr);
            nodeALabel.setText(rootMenuNode.getSubLabel(TwoWayMenuNode.SubNodeId.A));
            nodeBLabel.setText(rootMenuNode.getSubLabel(TwoWayMenuNode.SubNodeId.A));

        } catch (IOException e) {

        }

        try {
            InputStream jsonPathsStream = getResources().openRawResource(R.raw.two_way_paths_test);
            byte[] pathBytes = new byte[jsonPathsStream.available()];
            jsonPathsStream.read(pathBytes);
            jsonPathsStream.close();
            String menuPathStr = new String(pathBytes);

            TwoWayPathParser parser = new TwoWayPathParser();
            menuPaths = parser.parsePathsJson(menuPathStr);

        } catch (IOException e) {

        }

        if(rootMenuNode != null && menuPaths != null)
        {
            menuManager = new TwoWayMenuManager(menuPaths, rootMenuNode);
            dataCollectionManager = new DataCollectionManager();
        }

    }

    @Override
    public void onTiltAngleChanged(float angle) {
        //invert the direction of the angle to make the Y position math easier
        //angle *= -1.0f;

        //600 pixel diameter circle
        //35 degree tilt should get ball to the edge of range
        //translate rotation angle to y position within the circle
        //add ball radius to calculated y pos

        float rotationPercent = angle / 35.0f;
        float yPosDelta = rotationPercent * 300;
        float newBallYPos = ballInitYPos - yPosDelta;

        if(Math.abs(angle) <= 35.0f) {
            menuBall.setY(newBallYPos);

            if(menuSelected && Math.abs(angle) <= 30.0f)
            {
                if(currentPathDone)
                {
                    //check for correct path
                    //log ux data
                    //handle next path
                    if(!menuManager.determineNextMenuPath())
                    {
                        //completed all paths
                        pathTarget.setText("COMPLETE!!");
                        tiltManager.stopTiltRecord();
                        showSessionCompleteDialog();
                        return;
                    }
                    updatePathTarget();
                }
                updateMenuOptions();
            }
        } else if(angle >= 35.0f && !menuSelected) {
            selectMenuOption(TwoWayMenuNode.SubNodeId.A);
        } else if(angle <= -35.0f && !menuSelected) {
            selectMenuOption(TwoWayMenuNode.SubNodeId.B);
        }

//        String zAngleStr = Float.toString(angle);
//        zAngle.setText(zAngleStr);
//
//        String percentStr = Float.toString(rotationPercent);
//        percent.setText(percentStr);
//
//        String deltaStr = Float.toString(yPosDelta);
//        delta.setText(deltaStr);
    }

    private void initMenuOptions()
    {
        if(menuManager != null && menuManager.determineNextMenuPath()) {
            updateMenuOptions();
            updatePathTarget();
        }
    }

    private void updateMenuOptions()
    {
        String[] menuOptions = menuManager.getNextMenuOptions();
        nodeALabel.setText(menuOptions[0]);
        nodeBLabel.setText(menuOptions[1]);
        menuSelected = false;
        dataCollectionManager.startTimer();
    }

    private void selectMenuOption(TwoWayMenuNode.SubNodeId id)
    {
        menuSelected = true;
        menuManager.recordMenuPath(id);
        currentPathDone = !menuManager.traverseMenuTree(id);
        if(currentPathDone)
        {
            dataCollectionManager.stopTimer();
            dataCollectionManager.logMenuOptionData(
                    menuManager.getCurrentTarget(),
                    dataCollectionManager.getDuration(),
                    menuManager.isUserPathSuccess());
        }
    }

    private void updatePathTarget()
    {
        String target = menuManager.getCurrentTarget();
        pathTarget.setText(target);
        currentPathDone = false;
    }

    private DialogInterface.OnClickListener sessionCompleteDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    String sessionCsvStr = dataCollectionManager.loggedDataAsCsvString();
                    writeDataFileAndEmail(sessionCsvStr);
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        }
    };

    private void showSessionCompleteDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your sessions is complete. Please return device to the facilitator").setPositiveButton("Email session data", sessionCompleteDialogClickListener)
                .setNegativeButton("New session", sessionCompleteDialogClickListener).show();
    }

    private void writeDataFileAndEmail(String csvData)
    {
        File file   = null;
        File root   = Environment.getExternalStorageDirectory();
        if (root.canWrite()){
            File dir    =   new File (root.getAbsolutePath() + "/TiltMenuData");
            dir.mkdirs();
            file   =   new File(dir, "Data.csv");
            FileOutputStream out   =   null;
            try {
                out = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                out.write(csvData.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Uri u1 = Uri.fromFile(file);

        Intent sendIntent = new Intent(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "TiltMenu Session Data");
        sendIntent.putExtra(Intent.EXTRA_STREAM, u1);
        sendIntent.setType("text/html");
        startActivity(sendIntent);
    }
}
