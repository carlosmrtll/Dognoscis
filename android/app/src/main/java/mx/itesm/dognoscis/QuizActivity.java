package mx.itesm.dognoscis;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ImageView dogImageView;
    private String correctBreed;
    List<String> breeds = new ArrayList<>();
    private Button opcion1, opcion2, opcion3, opcion4;
    private Random random;
    private My1SecondCountDownTimer timer1Second = new My1SecondCountDownTimer();
    private My10SecondsCountDownTimer timer10Seconds = new My10SecondsCountDownTimer();
    private ProgressBar timeLeft;
    private int points;
    private TextView pointsText;
    private int question;
    private GradientDrawable gradientDefault = new GradientDrawable();
    private GradientDrawable gradientChosen = new GradientDrawable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();

        dogImageView = findViewById(R.id.dogImageView);
        breeds.add("Husky");  breeds.add("Dalmata");  breeds.add("San_Bernardo");  breeds.add("Chihuahua");
        breeds.add("Gran_Danes");  breeds.add("Pastor_Aleman");  breeds.add("Yorkshire");  breeds.add("Shih_Tzu");
        breeds.add("Beagle");
        opcion1 = findViewById(R.id.opcion1);
        opcion2 = findViewById(R.id.opcion2);
        opcion3 = findViewById(R.id.opcion3);
        opcion4 = findViewById(R.id.opcion4);
        timeLeft = findViewById(R.id.progressBar);
        pointsText = findViewById(R.id.pointsText);
        timeLeft.setMax(100);
        timeLeft.setScaleY(3f);
        points = 0;
        question = 0;

        gradientDefault.setColor(Color.parseColor("#0984e3"));
        gradientDefault.setCornerRadius(7);
        gradientDefault.setStroke(1, Color.parseColor("#0984e3"));

        gradientChosen.setColor(Color.parseColor("#74b9ff"));
        gradientChosen.setCornerRadius(7);
        gradientChosen.setStroke(1, Color.parseColor("#74b9ff"));
        randomPhoto();
    }

    private class My1SecondCountDownTimer extends CountDownTimer {
        public My1SecondCountDownTimer() {
            super(1000, 1000);
        }

        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            Log.wtf("QUIZ_", "terminó 1 segundo");
            timer10Seconds = new My10SecondsCountDownTimer();
            timer10Seconds.start();
        }
    }

    private class My10SecondsCountDownTimer extends CountDownTimer {
        public My10SecondsCountDownTimer() {
            super(10000, 100);
        }

        @Override
        public void onTick(long l) {
            timeLeft.setProgress(timeLeft.getProgress()-1);
        }

        @Override
        public void onFinish() {
            Log.wtf("QUIZ_", "terminaron 10 segundos");
        }
    }

    public void checkChoice(View v){
        Button b = (Button) v;
        String breedChosen = b.getText().toString().replaceAll(" ", "_");
        Log.wtf("QUIZ", "chosen:"+breedChosen+" cB:"+correctBreed);
        b.setClickable(false);
        b.setBackground(gradientChosen);
        if(breedChosen.equals(breeds.get(0)) && correctBreed.equals(breeds.get(0))){
            Log.wtf("QUIZ", "correct "+breeds.get(0));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(1)) && correctBreed.equals(breeds.get(1))){
            Log.wtf("QUIZ", "correct "+breeds.get(1));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(2)) && correctBreed.equals(breeds.get(2))){
            Log.wtf("QUIZ", "correct "+breeds.get(2));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(3)) && correctBreed.equals(breeds.get(3))){
            Log.wtf("QUIZ", "correct "+breeds.get(3));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(4)) && correctBreed.equals(breeds.get(4))){
            Log.wtf("QUIZ", "correct "+breeds.get(4));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(5)) && correctBreed.equals(breeds.get(5))){
            Log.wtf("QUIZ", "correct "+breeds.get(5));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(6)) && correctBreed.equals(breeds.get(6))){
            Log.wtf("QUIZ", "correct "+breeds.get(6));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(7)) && correctBreed.equals(breeds.get(7))){
            Log.wtf("QUIZ", "correct "+breeds.get(7));
            randomPhoto();
        }
        else if(breedChosen.equals(breeds.get(8)) && correctBreed.equals(breeds.get(8))){
            Log.wtf("QUIZ", "correct "+breeds.get(8));
            randomPhoto();
        }
        else {
            points -= 50;
            if(points<0) points=0;
        }
    }

    private void randomPhoto(){
        timer1Second.cancel();
        timer10Seconds.cancel();
        Log.wtf("QUIZ_","points:"+String.valueOf(timeLeft.getProgress()));
        points += timeLeft.getProgress();
        if(question == 10){
            question = 1;
            Intent intent = new Intent(this, QuizResults.class);
            intent.putExtra("points", String.valueOf(points));
            points = 0;
            startActivity(intent);
        } else {
            question++;
        }
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        random = new Random(System.currentTimeMillis());
        Collections.shuffle(breeds);
        correctBreed = breeds.get(random.nextInt(4));

        opcion1.setText(breeds.get(0).replaceAll("_", " "));
        opcion1.setClickable(true);
        opcion1.setBackground(gradientDefault);
        opcion2.setText(breeds.get(1).replaceAll("_", " "));
        opcion2.setClickable(true);
        opcion2.setBackground(gradientDefault);
        opcion3.setText(breeds.get(2).replaceAll("_", " "));
        opcion3.setClickable(true);
        opcion3.setBackground(gradientDefault);
        opcion4.setText(breeds.get(3).replaceAll("_", " "));
        opcion4.setClickable(true);
        opcion4.setBackground(gradientDefault);
        Log.wtf("QUIZ", "correctBreed:"+correctBreed);
        pointsText.setText(String.valueOf(points));
        ImageRequest imageRequest = new ImageRequest(
            ValuesSecret.RANDOM_PHOTO_API_URL+correctBreed,
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    dogImageView.setImageBitmap(response);
                    timer1Second = new My1SecondCountDownTimer();
                    timer1Second.start();
                    timeLeft.setProgress(100);
                }
            },
            0,
            0,
            ImageView.ScaleType.CENTER_CROP,
            Bitmap.Config.RGB_565,
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    error.printStackTrace();
                    Toast.makeText(QuizActivity.this, "Error, please try again", Toast.LENGTH_SHORT).show();
                }
            }
        );
        // Add ImageRequest to the RequestQueue
        imageRequest.setShouldCache(false);
        requestQueue.add(imageRequest);
    }
}
