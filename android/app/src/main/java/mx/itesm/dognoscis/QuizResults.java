package mx.itesm.dognoscis;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42BadParameterException;
import com.shephertz.app42.paas.sdk.android.App42NotFoundException;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.GameService;
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.User.Profile;
import com.shephertz.app42.paas.sdk.android.user.User.UserGender;
import com.shephertz.app42.paas.sdk.android.user.UserService;

import java.math.BigDecimal;

public class QuizResults extends AppCompatActivity {

    private TextView totalPointsText;
    private Button submit;
    private Button leaderboard;
    private int score;
    public static String user = "martell";
    public static ScoreBoardService scoreBoardService;
    private Dialog Signin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        Intent intent = getIntent();
        totalPointsText = findViewById(R.id.totalPoints);
        submit = findViewById(R.id.submit);
        leaderboard = findViewById(R.id.leaderbutton);

        App42API.initialize(this,"29c9b3af384f01cde16de3848b06d1ea596a1f70bb2680b6b2b709d0649d8e37","d7f4242dcf94899aa9bc46c1d942d61c4518052e06d88fb121746a95636fed9c");
        //GameService gameService = App42API.buildGameService();
        scoreBoardService = App42API.buildScoreBoardService();

        score = Integer.parseInt(intent.getStringExtra("points"));

        totalPointsText.setText(Integer.toString(score));
    }

    public void leaderboardClick(View v){
        Intent intent = new Intent(this, Leaderboard.class);
        startActivity(intent);
    }

    public void SubmitScore(View v){
        SharedPreferences settings = getSharedPreferences("UserInfo", 0);
        if(settings.contains("Username")){
            user = settings.getString("Username", "").toString();
        } else {
            //callLoginDialog();
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("Username",user);
            editor.commit();
            Log.d("PREFERENCES: ","new user "+user+" added");
        }

        //save score
        BigDecimal gameScore = new BigDecimal(score);

        QuizResults.scoreBoardService.saveUserScore("Quiz", QuizResults.user, gameScore,new App42CallBack() {
            public void onSuccess(Object response) {
                Log.d("App42API:","score SAVED");
                //Toast.makeText(QuizResults.this, "Score saved.", Toast.LENGTH_SHORT).show();
                Log.wtf("LEADER", "score saved");
            }
            public void onException(Exception ex) {
                Log.d("App42API:", "score FAILED to save");
            }
        });

    }

    public void callLoginDialog()
    {
        Signin = new Dialog(this);
        Signin.setContentView(R.layout.signin);
        Signin.setCancelable(false);
        Button login = (Button) Signin.findViewById(R.id.submitUser);

        EditText usrname = (EditText) Signin.findViewById(R.id.user);
        EditText emailaddr = (EditText) Signin.findViewById(R.id.emailaddr);
        EditText password = (EditText) Signin.findViewById(R.id.password);
        Signin.show();

        login.setOnClickListener(new View.OnClickListener()
        {

            @Override
            public void onClick(View v)
            {
                String userName = usrname.getText().toString();
                String pwd = password.getText().toString();
                String emailId = emailaddr.getText().toString();
                UserService userService = App42API.buildUserService();
                userService.createUser( userName, pwd, emailId, new App42CallBack() {
                    public void onSuccess(Object response) {
                        User user = (User)response;
                        Log.d("App42API", "user: "+user.getUserName()+" CREATED");
                        QuizResults.user = user.getUserName().toString();
                        Toast.makeText(QuizResults.this, "Sign in successful.", Toast.LENGTH_SHORT).show();
                    }  public void onException(Exception ex) {
                        Toast.makeText(QuizResults.this, "Sign in failed, try again.", Toast.LENGTH_SHORT).show();
                    }
                });
                Signin.dismiss();
            }
        });
    }
}
