package mx.itesm.dognoscis;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class RankInfoActivity extends AppCompatActivity {

    private TextView textView;
    private ImageView imageView;
    private String perro, info, temp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank_info);

        textView = (TextView)findViewById(R.id.textView);
        imageView = (ImageView)findViewById(R.id.imageView);

        Intent intent = getIntent();

        perro = intent.getStringExtra("perroname");
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

}
