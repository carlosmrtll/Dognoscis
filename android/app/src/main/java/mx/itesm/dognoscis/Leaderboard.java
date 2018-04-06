package mx.itesm.dognoscis;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42CallBack;
import com.shephertz.app42.paas.sdk.android.game.Game;
import com.shephertz.app42.paas.sdk.android.game.GameService;
import com.shephertz.app42.paas.sdk.android.game.ScoreBoardService;
import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.App42Response;
import com.shephertz.app42.paas.sdk.android.App42Exception;
import com.shephertz.app42.paas.sdk.android.App42BadParameterException;
import com.shephertz.app42.paas.sdk.android.App42NotFoundException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class Leaderboard extends AppCompatActivity {

    private ListView leaderboard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        leaderboard = findViewById(R.id.leaderboard);

        //Inicializar API
        App42API.initialize(this,"29c9b3af384f01cde16de3848b06d1ea596a1f70bb2680b6b2b709d0649d8e37","d7f4242dcf94899aa9bc46c1d942d61c4518052e06d88fb121746a95636fed9c");

        String gameName = "Quiz";

        int max = 10;

        ArrayList<String> topPlayers = new ArrayList<>();

        ScoreBoardService scoreBoardService = App42API.buildScoreBoardService();

        scoreBoardService.getTopNRankers(gameName, max, new App42CallBack(){
            public void onSuccess(Object response) {
                Game game = (Game)response;
                Log.d("App42API:","gameName is " + game.getName());
                for(int i = 0;i<game.getScoreList().size();i++) {
                    topPlayers.add(i,game.getScoreList().get(i).getUserName()+" - "+game.getScoreList().get(i).getValue());
                    Log.d("App42API:",game.getScoreList().get(i).getUserName()+" - "+game.getScoreList().get(i).getValue());
                }
            }

            public void onException(Exception ex)
            {
                Log.d("App42API","Exception Message"+ex.getMessage());
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ArrayAdapter<String> leaderboardAdapter = new ArrayAdapter<String>(Leaderboard.this, android.R.layout.simple_list_item_1, topPlayers);
                leaderboard.setAdapter(leaderboardAdapter);
            }
        }, 2000);   //5 seconds
    }
}
