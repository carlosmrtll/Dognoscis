package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class QuizResults extends AppCompatActivity {

    private TextView totalPointsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        Intent intent = getIntent();
        totalPointsText = findViewById(R.id.totalPoints);
        totalPointsText.setText(intent.getStringExtra("points"));
    }
}
