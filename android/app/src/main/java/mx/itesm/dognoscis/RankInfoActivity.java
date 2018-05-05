package mx.itesm.dognoscis;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RankInfoActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private String perro, readablePerro, info, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_info);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();

        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();

        perro = intent.getStringExtra("perroname");
        readablePerro = intent.getStringExtra("readablePerroname");
        Log.wtf("RANK", "perro:"+perro);
        Log.wtf("RANK", "readablePerro:"+readablePerro);
        info = "";
        temp = "";

        //Para conseguir el texto de app->src->main->assets
        AssetManager manager = getAssets();
        try {
            InputStream is = manager.open(perro+".txt");
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            info = br.readLine();
            while ((temp = br.readLine()) != null){
                info += "\n" + temp;
            }
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        textView.setText(info);

        //para conseguir las imagenes
        try
        {
            // get input stream
            InputStream ims = manager.open(perro+".png");
            // load image as Drawable
            Drawable d = Drawable.createFromStream(ims, null);
            // set image to ImageView
            imageView.setImageDrawable(d);
            ims.close();
        }
        catch(IOException ex)
        {
            ex.printStackTrace();
        }

    }

    public void buttonClick(View v){
        finish();
    }

    public void openMap(View v){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RankInfoActivity.this, new String[] {android.Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        } else{
            Log.wtf("PERMISOS", "ALREADY AUTHORIZED");
            Intent intent = new Intent(this, MapBreeds.class);
            intent.putExtra("breeds", readablePerro);
            startActivity(intent);
        }
    }

    public void onRequestPermissionsResult(int requestCode, String[] p, int[] r){
        if(requestCode == 0 && r[0] == PackageManager.PERMISSION_GRANTED){
            Log.wtf("PERMISOS", "SI AUTORIZO");
            Intent intent = new Intent(this, MapBreeds.class);
            intent.putExtra("breeds", readablePerro);
            startActivity(intent);
        } else {
            Log.wtf("PERMISOS", "NO HAY AUTORIZACION");
        }
    }

}
