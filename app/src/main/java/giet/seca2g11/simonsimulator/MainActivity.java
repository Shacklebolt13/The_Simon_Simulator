package giet.seca2g11.simonsimulator;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    View leftTop;
    View leftBottom;
    View rightTop;
    View rightBottom;
    TextView textView;
    View game,creds;
    boolean truth=false;
    final int MAX_LENGTH = 1000;
    int[] array_of_moves = new int[MAX_LENGTH];
    public int numberOfElmentsInMovesArray = 0, k = 0, numberOfClicksEachStage = 0, x, sadMusic, highScore = 0, hardness;
    public SoundPool sp = new SoundPool(10, AudioManager.STREAM_MUSIC, 0);
    Random r = new Random();
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splashscreen);
        TextView oins = findViewById(R.id.OIns);
        TextView hlp = findViewById(R.id.htp);
        Spinner diff = findViewById(R.id.difficulty);
        ImageView play = findViewById(R.id.imageView);
        String a[] = {"Easy", "Medium", "Hard", "Geek"};
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getApplicationContext(), R.array.difficulty_setting, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        diff.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                hardness = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        diff.setAdapter(adapter);
        diff.setSelection(1);
        hlp.setText("HOW TO PLAY:\nLook closely at the pattern and follow accordingly, each correct sequence will award you with scores.Press the image to play");
        oins.setText("This game was made by group 11, SEC-A\n\nGagandeep Singh Bhambrah\nAbhishek Singh\n S. Bharat Kumar");
        play.setOnClickListener(v -> {
                startNow();
        });
    }



    public void startNow(){
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "Welcome to Simon,hardness: " + hardness+"\n Kindly turn off dark/night mode in case of discoloration", Toast.LENGTH_LONG).show();
        game= findViewById(R.id.gameLayout);
        creds= findViewById(R.id.credsLayout);
            creds.setVisibility(View.GONE);
            game.setVisibility(View.VISIBLE);

        sadMusic = sp.load(this, R.raw.sad, 1);
        leftTop = findViewById(R.id.buttonR);
        leftBottom = findViewById(R.id.buttonY);
        rightTop = findViewById(R.id.buttonB);
        rightBottom = findViewById(R.id.buttonG);
        textView = findViewById(R.id.textView);

        leftTop.setOnTouchListener(onTouch);
        leftBottom.setOnTouchListener(onTouch);
        rightBottom.setOnTouchListener(onTouch);
        rightTop.setOnTouchListener(onTouch);
        textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
        final Runnable r = this::playGame;
        handler.postDelayed(r, 3000);

}

    View.OnTouchListener onTouch = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                switch (v.getId()) {
                    case R.id.buttonR:
                        x = 1;
                        break;
                    case R.id.buttonB:
                        x = 2;
                        break;
                    case R.id.buttonG:
                        x = 4;
                        break;
                    case R.id.buttonY:
                        x = 3;
                        break;
                }
                if (array_of_moves[numberOfClicksEachStage] != x) { // on wrong click
                    sp.play(sadMusic, 1, 1, 1, 0, 1f);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                    alertDialogBuilder.setMessage("Game over, you reached level: " + (numberOfElmentsInMovesArray - 1) + ", Do you want to play agian?");
                    alertDialogBuilder.setPositiveButton("Yes",
                            (arg0, arg1) -> {
                                //Start over
                                clear();
                                textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
                                playGame();
                            });

                    alertDialogBuilder.setNegativeButton("No", (dialog, which) -> finish());

                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();

                    return true;
                }
                //on success
                playSound(v.getId());
                xorMyColor(v);
                numberOfClicksEachStage++;
                if (numberOfElmentsInMovesArray == numberOfClicksEachStage) { //if 4 boxes shown, then activate  function
                    //playGame only after 4 clicks have been made by the user

                    numberOfClicksEachStage = 0;
                    if (numberOfElmentsInMovesArray > highScore) {
                        highScore = numberOfElmentsInMovesArray;
                    }
                    textView.setText("Currect score: " + numberOfElmentsInMovesArray + "           High score: " + highScore);
                    final Runnable r = () -> playGame();
                    handler.postDelayed(r, 2000 - 500 * hardness);
                }
            }
            return true;
        }
    };

    private void playSound(int id) {
        //function that play sound according to sound ID
        int audioRes = 0;
        switch (id) {
            case R.id.buttonR:
                audioRes = R.raw.doo;
                break;
            case R.id.buttonB:
                audioRes = R.raw.re;
                break;
            case R.id.buttonG:
                audioRes = R.raw.mi;
                break;
            case R.id.buttonY:
                audioRes = R.raw.fa;
                break;
        }
        MediaPlayer p = MediaPlayer.create(this, audioRes);
        p.setOnCompletionListener(mp -> mp.release());
        p.start();
    }

    private void xorMyColor(final View v) {
        //function that changes the background color and get it back after 500 milliseconds
        v.getBackground().setAlpha(51);
        final Runnable r = () -> v.getBackground().setAlpha(255);
        handler.postDelayed(r, 300);
    }

    public void playGame() {
        appendValueToArray();
        numberOfElmentsInMovesArray++;
        for (k = 0; k < numberOfElmentsInMovesArray; k++) {
            click(k);
        }
    }

    public void click(final int click_index) {
        //Function that clicks one place randomally on the view
        final Runnable r = () -> {
            if (array_of_moves[click_index] == 1) {
                playSound(R.id.buttonR);
                xorMyColor(leftTop);
            } else if (array_of_moves[click_index] == 2) {
                playSound(R.id.buttonB);
                xorMyColor(rightTop);
            } else if (array_of_moves[click_index] == 3) {
                playSound(R.id.buttonY);
                xorMyColor(leftBottom);
            } else {
                playSound(R.id.buttonG);
                xorMyColor(rightBottom);
            }
        };

        handler.postDelayed(r, (2000 - 500 * hardness) * click_index);
    }


    private int generateRandomNumber() {
        return r.nextInt(4) + 1; // generate random number between 1 and 4
    }

    private void appendValueToArray() {  // add random number to the first free position in the array
        for (int i = 0; i < MAX_LENGTH; i++) {
            if (array_of_moves[i] == 0) {
                array_of_moves[i] = generateRandomNumber();
                break;
            }
        }
    }

    private void clear() {//reset the game to initial state
        for (int i = 0; i < MAX_LENGTH; i++) {
            array_of_moves[i] = 0;
        }
        numberOfClicksEachStage = 0;
        numberOfElmentsInMovesArray = 0;
    }

    public void closeCreds(View view) {
        creds.setVisibility(View.GONE);
        game.setVisibility(View.VISIBLE);
    }

    public void showCreds(View view) {
        game.setVisibility(View.GONE);
        creds.setVisibility(View.VISIBLE);
    }
}

