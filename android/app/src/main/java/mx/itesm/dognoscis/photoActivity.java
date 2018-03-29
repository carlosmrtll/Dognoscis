package mx.itesm.dognoscis;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.VoiceInteractor;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

import static com.loopj.android.http.AsyncHttpClient.log;

public class photoActivity extends AppCompatActivity {
    static final int REQUEST_TAKE_PHOTO = 1;
    String mCurrentPhotoPath;
    File photoFile = null;
    int quantity;
    Uri photoURI;
    ImageView image;
    TextView percentages, top;
    Button reportbutton;
    String breed = null;
    HttpURLConnection urlConnection;
    private Bitmap bitmap;
    Properties properties;
    public static final String PROPERTIES_FILE = "properties.xml";
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        image = findViewById(R.id.imageView);
        percentages = findViewById(R.id.percentages);
        top = findViewById(R.id.top);
        reportbutton = findViewById(R.id.button4);
        intent = new Intent();
        //sss

        dispatchTakePictureIntent();

        properties = new Properties();
    }


    private void dispatchTakePictureIntent() {
        android.content.Intent takePictureIntent = new android.content.Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);




            }
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    ProgressDialog loading;
    MyCountDownTimer timer;
    public class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) { }

        @Override
        public void onFinish() {
            Toast.makeText(photoActivity.this,"Response is taking too long, please wait.",  Toast.LENGTH_LONG).show();
            loading.dismiss();
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, android.content.Intent data) {
        image.setImageURI(photoURI);
        loading = ProgressDialog.show(this,"Recognizing...","Please wait...",false,false);
        timer = new MyCountDownTimer(20000, 1000);
        timer.start();

        AsyncHttpClient client = new AsyncHttpClient();


        RequestParams params = new RequestParams();
        params.put("enctype", "multipart/form-data");
        try {
            params.put("fileupload", photoFile);
        } catch(FileNotFoundException e) {}


        class DogInfo{
            String name;
            double certainty;
            public DogInfo(String name, double certainty){
                this.name = name; this.certainty = certainty;
            }
        };
        class PQsort implements Comparator<DogInfo> {
            public int compare(DogInfo one, DogInfo two) {
                return (int)two.certainty - (int)one.certainty;
            }
        };

        client.post(ValuesSecret.IMAGE_RECOGNIZER_API_URL, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response){
                Log.d("mens", "success!");
                timer.cancel();
                try{
                    PQsort pqs = new PQsort();
                    PriorityQueue<DogInfo> queue=new PriorityQueue<DogInfo>(4,pqs);
                    JSONObject JSONhusky = response.getJSONObject("husky");
                    JSONObject JSONdalmata = response.getJSONObject("dalmata");
                    JSONObject JSONchihuahua = response.getJSONObject("chihuahua");
                    JSONObject JSONsanBernardo = response.getJSONObject("san bernardo");
                    JSONObject JSONgranDanes = response.getJSONObject("gran danes");
                    JSONObject JSONpastorAleman = response.getJSONObject("pastor aleman");
                    JSONObject JSONYorkshire = response.getJSONObject("yorkshire");
                    JSONObject JSONshihTzu = response.getJSONObject("shih tzu");
                    queue.add(new DogInfo("Husky", JSONhusky.getDouble("value")*100.0));
                    queue.add(new DogInfo("Dalmata", JSONdalmata.getDouble("value")*100.0));
                    queue.add(new DogInfo("Chihuahua", JSONchihuahua.getDouble("value")*100.0));
                    queue.add(new DogInfo("San Bernardo", JSONsanBernardo.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Gran Danes", JSONgranDanes.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Pastor Aleman", JSONpastorAleman.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Yorkshire", JSONYorkshire.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Shih Tzu", JSONshihTzu.getDouble("value") * 100.0));
                    DogInfo first = queue.poll();
                    DogInfo second = queue.poll();
                    DogInfo third = queue.poll();
                    DogInfo fourth = queue.poll();
                    DogInfo fifth = queue.poll();
                    DogInfo sixth = queue.poll();
                    DogInfo seventh = queue.poll();
                    DogInfo eighth = queue.poll();
                    Log.d("response: ", "first: " + first.name + " - " + first.certainty);
                    Log.d("response: ", "second: " + second.name + " - " + second.certainty);
                    Log.d("response: ", "third: " + third.name + " - " + third.certainty);

                    top.setText("That's a\n" + first.name + "!");
                    percentages.setText(String.format("\n %s - %d%c \n %s - %d%c \n %s - %d%c \n %s - %d%c" +
                                    "\n %s - %d%c \n %s - %d%c \n %s - %d%c \n %s - %d%c",
                            first.name, (int)first.certainty, '%',
                            second.name, (int)second.certainty, '%',
                            third.name, (int)third.certainty, '%',
                            fourth.name, (int)fourth.certainty, '%',
                            fifth.name, (int)fifth.certainty, '%',
                            sixth.name, (int)sixth.certainty, '%',
                            seventh.name, (int)seventh.certainty, '%',
                            eighth.name, (int)eighth.certainty, '%'
                    ));
                    loading.dismiss();
                    /*if(first.certainty > 70){
                        //textView1.setText(first.name+" -  calorias: "+first.calories+"  protein: "+first.protein+"\n  fat: "+first.fat+"  carbohidrates: "+first.carbohidrates );
                    } else {
                        percentages.setText("irreconocible");
                    }*/

                    try{
                        FileInputStream fis = openFileInput(PROPERTIES_FILE);
                        properties.loadFromXML(fis);
                        fis.close();

                        quantity = Integer.parseInt(properties.getProperty("quantity"));
                        quantity++;

                        properties.put("quantity", String.valueOf(quantity));
                        properties.put(String.valueOf(quantity),percentages.getText());
                        properties.put(String.valueOf(quantity)+"uri",photoURI.toString());
                        saveProperties();

                        File myFile = new File("/path/to/file.png");
                    }catch (IOException ioe){
                        ioe.printStackTrace();
                    }
                    breed = first.name;
                    intent.putExtra("breed", breed);
                    intent.putExtra("clicked", true);
                    setResult(Activity.RESULT_OK, intent);
                } catch(JSONException e){
                    Log.d("response","EXCEPTION: " + e.getMessage());
                }
            }
        });

    }

    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this, "Uploading...", "Please wait...", false, false);
        /*new CountDownTimer(15000, 1000) {

            public void onTick(long millisUntilFinished) {
                //mTextField.setText("seconds remaining: " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                loading.dismiss();
            }
        }.start();*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ValuesSecret.IMAGE_RECOGNIZER_API_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(photoActivity.this, s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(photoActivity.this, volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = "bitmapString";

                //Getting Image Name
                String name = "name";

                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("fileupload", "photoFile");
                //params.put("name", name);

                //returning parameters
                return params;

            }
        };
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);





        try {
            FileOutputStream outputStream = getApplicationContext().openFileOutput("imagen.gif", Context.MODE_PRIVATE);
            outputStream.write(imageBytes);
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        return encodedImage;
    }

    private void saveProperties() throws IOException{

        FileOutputStream fos = openFileOutput(PROPERTIES_FILE, Context.MODE_PRIVATE);
        properties.storeToXML(fos, null);
        fos.close();
        Toast.makeText(this, "FILE SAVED", Toast.LENGTH_SHORT).show();
    }


    public void reportClick(View v) {
        intent.putExtra("breed", breed);
        intent.putExtra("clicked", false);
        setResult(Activity.RESULT_OK, intent);
        Toast.makeText(photoActivity.this,"Report sent",  Toast.LENGTH_LONG).show();
    }
}
