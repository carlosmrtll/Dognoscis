package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class QuizResults extends AppCompatActivity {

    private TextView totalPointsText;
    private Button submit;
    private Button leaderboard;
    private int score;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        Intent intent = getIntent();
        totalPointsText = findViewById(R.id.totalPoints);
        submit = findViewById(R.id.submit);
        leaderboard = findViewById(R.id.leaderbutton);

        score = Integer.parseInt(intent.getStringExtra("points"));

        totalPointsText.setText(Integer.toString(score));

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();
    }

    public void backMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
