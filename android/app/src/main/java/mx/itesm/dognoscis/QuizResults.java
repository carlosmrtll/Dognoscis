package mx.itesm.dognoscis;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.LeaderboardsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class QuizResults extends AppCompatActivity implements View.OnClickListener{

    private TextView totalPointsText;
    private Button leaderboard;
    private int score;
    private final String TAG = "QUIZR";

    private GoogleSignInClient googleSignInClient;
    private LeaderboardsClient leaderboardsClient;
    private SignInButton signInButton;
    GoogleSignInAccount account;

    // request codes we use when invoking an external activity
    private static final int RC_UNUSED = 5001;
    private static final int RC_SIGN_IN = 9001;
    private static final int RC_LEADERBOARD_UI = 9004;

    // To check which achievement to give
    private static final String PREFS_NAME = "BreedCount";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);
        Intent intent = getIntent();
        totalPointsText = findViewById(R.id.totalPoints);
        leaderboard = findViewById(R.id.leaderboardButton);
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        score = Integer.parseInt(intent.getStringExtra("points"));

        totalPointsText.setText(Integer.toString(score));

        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Games.SCOPE_GAMES_LITE)
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        /*if(isSignedIn()){
            GoogleSignInOptions googleSignInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN;
            GoogleSignInClient client = GoogleSignIn.getClient(this, googleSignInOptions);
        }*/
        //leaderboardsClient = Games.getLeaderboardsClient(this, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isSignedIn()){
            Log.wtf(TAG, "NOT signed in");
            signInButton.setVisibility(View.VISIBLE);
            leaderboard.setVisibility(View.GONE);
            signInSilently();
        } else {
            Log.wtf(TAG, "SIGNED in");
            signInButton.setVisibility(View.GONE);
            leaderboard.setVisibility(View.VISIBLE);
            submitScore();
            checkAchievements();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.leaderboardButton:
                Log.wtf(TAG, "GAMES LITE permission: "+GoogleSignIn.hasPermissions(account, Games.SCOPE_GAMES_LITE));
                Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .getLeaderboardIntent(getString(R.string.leaderboard_quiz_scores))
                        .addOnSuccessListener(new OnSuccessListener<Intent>() {
                            @Override
                            public void onSuccess(Intent intent) {
                                startActivityForResult(intent, RC_LEADERBOARD_UI);
                            }
                        });
                break;
        }
    }

    public void submitScore(){
        Log.wtf(TAG, "submitting score: "+score);
        Games.getLeaderboardsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .submitScore(getString(R.string.leaderboard_quiz_scores), score);
    }

    public void checkAchievements(){
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int chihuahuaNum = prefs.getInt("Chihuahua", 0);
        if (chihuahuaNum != 0 && chihuahuaNum >= 10) {
            Log.wtf(TAG, "CHIHUAHUA ACHIEVEMENT UNLOCKED");
            Toast.makeText(this, "You're now a Chihuahua expert", Toast.LENGTH_SHORT).show();
            Games.getAchievementsClient(this, GoogleSignIn.getLastSignedInAccount(this))
                    .unlock(getString(R.string.achievement_chihuahua_expert));
        }
    }

    private void signInSilently() {
        Log.wtf(TAG, "signInSilently()");
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.silentSignIn().addOnCompleteListener(this,
                new OnCompleteListener<GoogleSignInAccount>() {
                    @Override
                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
                        if (task.isSuccessful()) {
                            // The signed in account is stored in the task's result.
                            account = task.getResult();
                        } else {
                            // Player will need to sign-in explicitly using via UI
                        }
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void startSignInIntent() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }

    private void signOut() {
        GoogleSignInClient signInClient = GoogleSignIn.getClient(this,
                GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN);
        signInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        // at this point, the user is signed out.
                    }
                });
    }

    private boolean isSignedIn() {
        return GoogleSignIn.getLastSignedInAccount(this) != null;
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // The signed in account is stored in the result.
                GoogleSignInAccount signedInAccount = result.getSignInAccount();
            } else {
                String message = result.getStatus().getStatusMessage();
                if (message == null || message.isEmpty()) {
                    message = "sign in other error.";
                }
                new AlertDialog.Builder(this).setMessage(message)
                        .setNeutralButton(android.R.string.ok, null).show();
            }
        }
    }*/
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Log.wtf(TAG, "Login successfull!");
            submitScore();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Log.w(TAG, "Login NOT successfull :(");
        }
    }
    
    public void backMenu(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
