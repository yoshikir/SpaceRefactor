package com.example.hectormediero.spaceinvadersdas.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hectormediero.spaceinvadersdas.Adapters.ScoreAdapter;
import com.example.hectormediero.spaceinvadersdas.Models.Score;
import com.example.hectormediero.spaceinvadersdas.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

public class ScoreActivity extends AppCompatActivity {
    private String mayor13;
    private String username;
    private Integer score;
    String mCurrentPhotoPath;
    ArrayList<Score> arrayPuntuaciones;
    ImageView cambia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        mCurrentPhotoPath=storageDir.getAbsolutePath();

        final Intent spaceGame = new Intent(getApplicationContext(), SpaceInvaderActivity.class);
        final Intent startGame = new Intent(getApplicationContext(), StartGameActivity.class);

        String result = getIntent().getExtras().getString("result");
        mayor13 = getIntent().getExtras().getString("mayor13");
        score = getIntent().getExtras().getInt("score");
        username = getIntent().getExtras().getString("username");

        cambia = findViewById(R.id.cambia);

        TextView resultTV = findViewById(R.id.GameResult);
        Button replayGame = findViewById(R.id.replayButton);
        arrayPuntuaciones = new ArrayList<>();
        try {
            BufferedReader fin =
                    new BufferedReader(
                            new InputStreamReader(
                                    openFileInput("nueva_puntuacioness2.txt")));
            String lineaActual;
            while ((lineaActual = fin.readLine()) != null) {
                System.out.println(lineaActual);
                String[] puntuacioneGuardadas = lineaActual.trim().split("#");
                for (int i = 0; i < puntuacioneGuardadas.length; i++) {
                    String[] datosPuntuacion = puntuacioneGuardadas[i].split("¬");
                    System.out.println(datosPuntuacion[0] + "-" + datosPuntuacion[1]);
                    arrayPuntuaciones.add(new Score(Integer.parseInt(datosPuntuacion[1]), datosPuntuacion[0], datosPuntuacion[2]));
                }
            }
            fin.close();

            Log.i("Ficheros", "Fichero leido!");
        } catch (FileNotFoundException e) {
            Log.i("Ficheros", "Fichero no leido!");
        } catch (IOException e) {
            Log.i("Ficheros", "ALGO PASA!");
        }

        if (arrayPuntuaciones.size() > 0) {
            Collections.sort(arrayPuntuaciones);
            System.out.println(arrayPuntuaciones.toString());
        }

        ScoreAdapter scoresAdapter = new ScoreAdapter(this, arrayPuntuaciones,mCurrentPhotoPath);

        ListView listView = findViewById(R.id.lista_puntuaciones);
        listView.setAdapter(scoresAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

            }
        });
        resultTV.setText(result);

        final AlertDialog.Builder rejugarDialog = new AlertDialog.Builder(this);
        rejugarDialog.setTitle("Aviso");
        rejugarDialog.setMessage("¿Desea volver a jugar?");
        rejugarDialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                spaceGame.putExtra("mayor13", mayor13);
                spaceGame.putExtra("username", username);
                startActivity(spaceGame);
                finish();
            }
        });
        rejugarDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogo1, int id) {
                startActivity(startGame);
                finish();
            }
        });
        if (score >= 500) {
            replayGame.setOnClickListener(new Button.OnClickListener() {
                public void onClick(View v) {
                    rejugarDialog.show();
                }
            });
        }

        String valor=findImage(username);
        if(!valor.equals("No")){
            int targetW = 200;
            int targetH = 200;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(valor, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(valor, bmOptions);
            cambia.setImageBitmap(bitmap);
        }
        cambia.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 4);
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        String valor=findImage(username);
        if(!valor.equals("No")){
            int targetW = 200;
            int targetH = 200;

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(valor, bmOptions);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(valor, bmOptions);
            cambia.setImageBitmap(bitmap);
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 4 && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cambia.setImageBitmap(imageBitmap);
        }
    }


    private String findImage(String username){
        String path = mCurrentPhotoPath +File.separator;
        System.out.println(path);
        File f = new File(path);
        //obtiene nombres de archivos dentro del directorio.
        File file[] = f.listFiles();
        for (int i=0; i < file.length; i++)
        {
            System.out.println("EEEEEEEEEEEEEEEE");
            System.out.println(file[i].getName());
            if(file[i].getName().contains(username)){
                return file[i].getAbsolutePath();
            }
        }
        return "No";
    }

    @Override
    public void onBackPressed() {
        //Code Smell...
    }
}
