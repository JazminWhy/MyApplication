package com.example.jasmin.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    View v;
    float b;
    int value = 0;
    int goal = 0;

    int punktzahl;
    TextView aufgabe, levelText, wert, punkte;
    SharedPreferences prefs;
    SensorManager manager;
    Sensor accelerator;
    Button game;
    int axis;
    int level; //wäre auch mit Byte gegangen, aber wegen Preferences..
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        aufgabe = findViewById(R.id.aufgabe);
        wert = findViewById(R.id.wert);
        punkte = findViewById(R.id.punkte);
       levelText = findViewById(R.id.level);
        game = findViewById(R.id.game);

        //SensorManager aufsetzen
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        //Sensor auswählen
        accelerator = manager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        //Listener registrieren
        manager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL); //normale ABrufhäufigkeit

        prefs = getSharedPreferences("Punktzahl",MODE_PRIVATE);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Start a new profile?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                punktzahl = 0;
                level = 0;
                punkte.setText("Deine Punktzahl: " + punktzahl);
                levelText.setText("Dein Level: " + level);
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                punktzahl = prefs.getInt("Points", 0);
                level = prefs.getInt("Level", 0);
                System.out.println("" + punktzahl + ""+ level);
                punkte.setText("Deine Punktzahl: " + punktzahl);
                levelText.setText("Dein Level: " + level);
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
        System.out.println(level + "");
        System.out.println(punktzahl + "");
        aufgabe.setText("Deine Aufgabe: ");
        Spiel();
    }

    public void Spiel(){
        manager.registerListener(this, accelerator, SensorManager.SENSOR_DELAY_NORMAL); //normale ABrufhäufigkeit
        wert.setTextColor(Color.BLACK);
        game.setVisibility(View.INVISIBLE);
        String aufgabenText;
String achse = "";
axis = ThreadLocalRandom.current().nextInt(0, 3);
int rangeTopAcc = 0, rangeBottomAcc = 0,rangeTopPoints = 0, rangeBottomPoints= 0;


System.out.println("axis" + axis);

switch(axis){
    case 0:
    achse = "X";
    break;
    case 1:
        achse = "Y";
        break;
    case 2:
        achse = "Z";
        break;
}

        switch(level){
            case 0:
               rangeTopAcc = 20;
               rangeBottomAcc = 15;
               rangeTopPoints = 30;
               rangeBottomPoints = 10;
                break;
            case 1:
                rangeTopAcc = 25;
                rangeBottomAcc = 20;
                rangeTopPoints = 40;
                rangeBottomPoints = 20;
                break;

            case 2:
                rangeTopAcc = 28;
                rangeBottomAcc = 25;
                rangeTopPoints = 45;
                rangeBottomPoints = 30;
                break;
            case 3:
                rangeTopAcc = 30;
                rangeBottomAcc = 28;
                rangeTopPoints = 70;
                rangeBottomPoints = 45;
                break;
            case 4:
                rangeTopAcc = 33;
                rangeBottomAcc = 30;
                rangeTopPoints = 85;
                rangeBottomPoints = 65;
                break;
            case 5:
                rangeTopAcc = 39;
                rangeBottomAcc = 33;
                rangeTopPoints = 120;
                rangeBottomPoints = 80;
                break;

        }

        System.out.println("" + rangeBottomAcc + "" + rangeTopAcc + "" + rangeBottomPoints + "" +  rangeTopPoints);
        goal = ThreadLocalRandom.current().nextInt(rangeBottomAcc, rangeTopAcc + 1);
        value = ThreadLocalRandom.current().nextInt(rangeBottomPoints, rangeTopPoints + 1);
        System.out.println("Goal" + goal);
        System.out.println("Punkte" + value);
        System.out.println("Level" + level);
        aufgabenText = "Beschleunige dein Handy entlang der " + achse + "-Achse bis auf " + goal + " um " + value + " Punkte zu erhalten";
        aufgabe.setText("Deine Aufgabe: " +aufgabenText);

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
    b = sensorEvent.values[axis];
    wert.setText("" + b);
        if(b>=goal){
       onAchieved();
        game.setVisibility(View.VISIBLE);
        }
    }

    public void onClickGame(View aView){
        Spiel();

    }

    public void onAchieved(){
        System.out.println(accelerator.getMaximumRange() + "");
        wert.setTextColor(Color.MAGENTA);
        if (level<5){
            level++;
        }
        else{
            level = 5;
        }
        manager.unregisterListener(this);
        wert.setText("" + b);
        punktzahl = punktzahl + value;

        SharedPreferences.Editor e = prefs.edit();
        e.putInt("Points", punktzahl);
        e.putInt("Level", level);
        e.commit();
        punkte.setText("Deine Punktzahl: " + punktzahl);
        levelText.setText("Dein Level: " + level);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
//hier nicht so wichtig
    }
}
