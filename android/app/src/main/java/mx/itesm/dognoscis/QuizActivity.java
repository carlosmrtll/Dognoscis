package mx.itesm.dognoscis;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.Volley;

import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    private ImageView dogImageView;
    private String correctBreed;
    private String[] breeds = {"Husky", "Dalmata", "San_Bernardo", "Chihuahua"};
    private Random random;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        dogImageView = findViewById(R.id.dogImageView);
        randomPhoto();
    }

    public void checkChoice(View v){
        int id = v.getId();
        Log.wtf("QUIZ", "id:"+id+" cB:"+correctBreed);
        if(id==R.id.Husky && correctBreed.equals(breeds[0])){
            randomPhoto();
            Log.wtf("QUIZ", "correct Husky");
        }
        else if(id==R.id.Dalmata && correctBreed.equals(breeds[1])){
            randomPhoto();
            Log.wtf("QUIZ", "correct Dalmata");
        }
        else if(id==R.id.SanBernardo && correctBreed.equals(breeds[2])){
            randomPhoto();
            Log.wtf("QUIZ", "correct San Bernardo");
        }
        else if(id==R.id.Chihuahua && correctBreed.equals(breeds[3])){
            randomPhoto();
            Log.wtf("QUIZ", "correct Chihuahua");
        }
    }

    private void randomPhoto(){
        // Initialize a new RequestQueue instance
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        random = new Random(System.currentTimeMillis());
        correctBreed = breeds[random.nextInt(breeds.length)];
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
