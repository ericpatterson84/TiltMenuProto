package com.example.epatterson.tiltmenuproto;

import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class TiltMenu extends AppCompatActivity implements TwoWayTiltListener{

    private TextView pathTarget = null;
    private TextView nodeALabel = null;
    private TextView nodeBLabel = null;
    private TwoWayTiltManager tiltManager = null;
    private ImageView menuBall = null;
    private ImageView selectionCircle = null;
    private int selectionCircleRadius = 0;
    private int selectionCircleTop = 0;
    private int selectionCircleBottom = 0;
    private int selectionCircleTopEdge = 0;
    private int selectionCircleBottomEdge = 0;
    private float ballInitYPos = 0.0f;
    private int ballHeight = 0;
    TwoWayMenuManager menuManager = null;
    private boolean menuSelected = false;
    private boolean currentPathDone = false;
    private DataCollectionManager dataCollectionManager = null;
    private Tracker mTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tilt_menu);
        pathTarget = (TextView)findViewById(R.id.menuTarget);
        menuBall = (ImageView)findViewById(R.id.menuBall);
        nodeALabel = (TextView)findViewById(R.id.textTop);
        nodeBLabel = (TextView)findViewById(R.id.textBottom);
        selectionCircle = (ImageView)findViewById(R.id.selectionCircle);

        menuBall.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if(top > 0)
                        {
                            ballInitYPos = top;
                            ballHeight = (bottom - top);
                            v.removeOnLayoutChangeListener(this);

                            SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
                            tiltManager = new TwoWayTiltManager(sensorManager, (TwoWayTiltListener)v.getContext());
                            tiltManager.startTiltRecord();

                            if(selectionCircleTop != 0) {
                                initMenuOptions();
                            }
                        }
                    }
                }
        );

        selectionCircle.addOnLayoutChangeListener(
                new View.OnLayoutChangeListener() {
                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        if(top > 0)
                        {
                            selectionCircleTop = top;
                            selectionCircleBottom = bottom;
                            selectionCircleRadius = (bottom - top) / 2;
                            v.removeOnLayoutChangeListener(this);

                            if(ballInitYPos != 0) {
                                initMenuOptions();
                            }
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
            InputStream jsonPathsStream = getResources().openRawResource(R.raw.two_way_paths);
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

        // Obtain the shared Tracker instance.
        AnalyticsApplication application = (AnalyticsApplication) getApplication();
        mTracker = application.getDefaultTracker();
        dataCollectionManager.setTracker(mTracker);
    }

    @Override
    public void onTiltAngleChanged(float angle) {
        //35 degree tilt should get ball to the edge of circle
        //translate rotation angle to y position within the circle

        float rotationPercent = angle / 35.0f;
        float yPosDelta = rotationPercent * selectionCircleRadius;
        float newBallYPos = ballInitYPos - yPosDelta;

        if( (newBallYPos >= selectionCircleTop) && (newBallYPos <= selectionCircleBottomEdge)) {
            menuBall.setY(newBallYPos);
        } else if(newBallYPos < selectionCircleTop) {
            menuBall.setY(selectionCircleTop);
        } else if(newBallYPos > selectionCircleBottomEdge) {
            menuBall.setY(selectionCircleBottomEdge);
        }

        if(Math.abs(angle) <= 35.0f) {
            if(menuSelected && Math.abs(angle) <= 15.0f)
            {
                if (currentPathDone) {
                    if (!menuManager.determineNextMenuPath()) {
                        //completed all paths
                        tiltManager.stopTiltRecord();
                        showSessionCompleteDialog();
                        return;
                    }
                    updatePathTarget();
                }
                updateMenuOptions();
            }

            if (!menuSelected && (newBallYPos <= selectionCircleTopEdge)) {
                selectMenuOption(TwoWayMenuNode.SubNodeId.A);
            } else if (!menuSelected && (newBallYPos >= selectionCircleBottomEdge - ballHeight)) {
                selectMenuOption(TwoWayMenuNode.SubNodeId.B);
            }
        }
    }

    private void initMenuOptions()
    {
        selectionCircleTopEdge = selectionCircleTop + ballHeight;
        selectionCircleBottomEdge = selectionCircleBottom - ballHeight;
        if(menuManager != null && menuManager.determineNextMenuPath()) {
            updateMenuOptions();
            updatePathTarget();
        }
    }

    private void restartSession()
    {
        menuManager.randomizePaths();
        if(menuManager.determineNextMenuPath())
        {
            updateMenuOptions();
            updatePathTarget();
            tiltManager.startTiltRecord();
        }
    }

    private void updateMenuOptions()
    {
        String[] menuOptions = menuManager.getNextMenuOptions();
        nodeALabel.setText(menuOptions[0]);
        nodeBLabel.setText(menuOptions[1]);
        menuSelected = false;
        selectionCircle.setImageResource(R.drawable.circle_unselected);
    }

    private void selectMenuOption(TwoWayMenuNode.SubNodeId id)
    {
        menuSelected = true;
        if( id == TwoWayMenuNode.SubNodeId.A) {
            selectionCircle.setImageResource(R.drawable.circle_a_selected);
        } else  {
            selectionCircle.setImageResource(R.drawable.circle_b_selected);
        }
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
        pathTarget.setText("Navigate to: " + target);
        currentPathDone = false;
        dataCollectionManager.startTimer();
        Toast.makeText(this,"Navigation target updated", Toast.LENGTH_SHORT).show();
    }

    private DialogInterface.OnClickListener sessionCompleteDialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which){
                case DialogInterface.BUTTON_POSITIVE:
                    restartSession();
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
        builder.setMessage("Your sessions is complete. Please return device to the facilitator")
                .setPositiveButton("New session", sessionCompleteDialogClickListener).show();
    }
}
