package mx.itesm.dognoscis;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dogImageView = findViewById(R.id.dogImageView);
        breeds.add("Husky");  breeds.add("Dalmata");  breeds.add("San_Bernardo");  breeds.add("Chihuahua");
        breeds.add("Gran_Danes");  breeds.add("Pastor_Aleman");  breeds.add("Yorkshire");  breeds.add("Shih_Tzu");
        breeds.add("Beagle");
        opcion1 = findViewById(R.id.opcion1);
        opcion2 = findViewById(R.id.opcion2);
        opcion3 = findViewById(R.id.opcion3);
        opcion4 = findViewById(R.id.opcion4);
        randomPhoto();
    }

    public void checkChoice(View v){
        Button b = (Button) v;
        String breedChosen = b.getText().toString();
        Log.wtf("QUIZ", "chosen:"+breedChosen+" cB:"+correctBreed);
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
    }

    private void randomPhoto(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        random = new Random(System.currentTimeMillis());
        Collections.shuffle(breeds);
        correctBreed = breeds.get(random.nextInt(4));
        opcion1.setText(breeds.get(0));
        opcion2.setText(breeds.get(1));
        opcion3.setText(breeds.get(2));
        opcion4.setText(breeds.get(3));
        Log.wtf("QUIZ", "correctBreed:"+correctBreed);

        ImageRequest imageRequest = new ImageRequest(
            ValuesSecret.RANDOM_PHOTO_API_URL+correctBreed,
            new Response.Listener<Bitmap>() {
                @Override
                public void onResponse(Bitmap response) {
                    dogImageView.setImageBitmap(response);
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
