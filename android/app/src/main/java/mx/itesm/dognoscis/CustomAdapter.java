package mx.itesm.dognoscis;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Debug;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class CustomAdapter extends BaseAdapter {
    Context context;
    LayoutInflater inflater;
    ImageView image;
    TextView text;
    Properties properties;
    int quantity,startingNum;
    public static final String PROPERTIES_FILE = "properties.xml";

    public CustomAdapter(Context applicationContext,Properties properties,int startingNum) {
        this.context = applicationContext;
        this.properties= properties;
        this.startingNum=startingNum;
        if(properties.getProperty("quantity")==null){
            this.quantity=0;
        }else {
            this.quantity = Integer.parseInt(properties.getProperty("quantity"));
        }
        inflater = (LayoutInflater.from(applicationContext));


    }

    @Override
    public int getCount() {
        return quantity;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

            view = inflater.inflate(R.layout.activity_listview, null);
            image = view.findViewById(R.id.imageViewH);
            text = view.findViewById(R.id.percentTextH);

            text.setText(properties.getProperty(String.valueOf(i+1)+"percentages"));

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.context.getContentResolver(), Uri.parse(properties.getProperty(String.valueOf(i + 1) + "uri")));
                Bitmap scaledBitmap = scaleDown(bitmap, 100, true);
                image.setImageBitmap(scaledBitmap);
            }catch (java.io.IOException exp){
                Log.d("bitmap","bmp");
            }

            //image.setImageURI(Uri.parse(properties.getProperty(String.valueOf(i+1)+"uri")));



        return view;
    }

    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize,
                                   boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }
}