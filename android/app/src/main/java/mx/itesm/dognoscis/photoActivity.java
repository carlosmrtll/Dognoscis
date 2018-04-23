package mx.itesm.dognoscis;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.location.Location;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.loopj.android.http.*;
import cz.msebera.android.httpclient.Header;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Properties;

public class photoActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
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
    PieChart pieChart;
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("map");

    private GoogleApiClient client;
    private Location lastLocation;
    private LocationRequest locationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        Window window = getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this,R.color.light_bg));
        getSupportActionBar().hide();

        image = findViewById(R.id.imageView);
        percentages = findViewById(R.id.percentages);
        top = findViewById(R.id.top);
        reportbutton = findViewById(R.id.reportProblemBtn);
        intent = new Intent();
        pieChart = (PieChart)findViewById(R.id.chart);

        dispatchTakePictureIntent();

        properties = new Properties();

        if(ContextCompat.checkSelfPermission(photoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // configuración de cliente de api de google
            if (client == null){
                client = new GoogleApiClient.Builder(this)
                        .addConnectionCallbacks(this)
                        .addOnConnectionFailedListener(this)
                        .addApi(LocationServices.API)
                        .build();
            }
            locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
            locationRequest.setInterval(1000 * 5);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(ContextCompat.checkSelfPermission(photoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            client.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(ContextCompat.checkSelfPermission(photoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            client.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.wtf("PERMISOS", "onConnected()");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){
            lastLocation = LocationServices.FusedLocationApi.getLastLocation(client);
            if(lastLocation != null){
                Log.d("PERMISOS", lastLocation.getLatitude() + ", " +
                        lastLocation.getLongitude());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.wtf("PERMISOS", "connection suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.wtf("PERMISOS", "connection failed");
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
    private MyCountDownTimer timer;

    private class MyCountDownTimer extends CountDownTimer {

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
                    JSONObject JSONbeagle = response.getJSONObject("beagle");
                    queue.add(new DogInfo("Husky", JSONhusky.getDouble("value")*100.0));
                    queue.add(new DogInfo("Dalmata", JSONdalmata.getDouble("value")*100.0));
                    queue.add(new DogInfo("Chihuahua", JSONchihuahua.getDouble("value")*100.0));
                    queue.add(new DogInfo("San Bernardo", JSONsanBernardo.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Gran Danes", JSONgranDanes.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Pastor Aleman", JSONpastorAleman.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Yorkshire", JSONYorkshire.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Shih Tzu", JSONshihTzu.getDouble("value") * 100.0));
                    queue.add(new DogInfo("Beagle", JSONbeagle.getDouble("value") * 100.0));
                    DogInfo first = queue.poll();
                    DogInfo second = queue.poll();
                    DogInfo third = queue.poll();
                    DogInfo fourth = queue.poll();
                    DogInfo fifth = queue.poll();
                    DogInfo sixth = queue.poll();
                    DogInfo seventh = queue.poll();
                    DogInfo eighth = queue.poll();
                    DogInfo nineth = queue.poll();
                    Log.d("response: ", "first: " + first.name + " - " + first.certainty);
                    Log.d("response: ", "second: " + second.name + " - " + second.certainty);
                    Log.d("response: ", "third: " + third.name + " - " + third.certainty);
                    ArrayList<DogInfo> dogInfos = new ArrayList<>();
                    dogInfos.add(first); dogInfos.add(second); dogInfos.add(third); dogInfos.add(fourth);
                    dogInfos.add(fifth); dogInfos.add(sixth); dogInfos.add(seventh); dogInfos.add(eighth);
                    dogInfos.add(nineth);

                    try{
                        FileInputStream fis = openFileInput(PROPERTIES_FILE);
                        properties.loadFromXML(fis);
                        fis.close();

                        quantity = Integer.parseInt(properties.getProperty("quantity"));
                        quantity++;

                        Log.d("DEBUG",first.name+": "+first.certainty);
                        properties.put("quantity", String.valueOf(quantity));
                        properties.put(String.valueOf(quantity)+"percentages",first.name+": "+String.format("%.2f   ",first.certainty));
                        properties.put(String.valueOf(quantity)+"uri",photoURI.toString());
                        saveProperties();

                    }catch (IOException ioe){
                        ioe.printStackTrace();
                    }

                    if(first.certainty > 70){
                        top.setText("That's a\n" + first.name+"!");
                        if(ContextCompat.checkSelfPermission(photoActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED){
                            Log.wtf("PERMISOS", "ALREADY AUTHORIZED");
                            if(lastLocation != null){
                                Log.wtf("PERMISOS", "to database: " + lastLocation.getLatitude() + ", " +
                                        lastLocation.getLongitude());
                                BreedLocation newPhoto = new BreedLocation(first.name, lastLocation.getLatitude(), lastLocation. getLongitude(), first.certainty);
                                ref.push().setValue(newPhoto);
                            } else {
                                Log.wtf("PERMISOS", "lastLocation is NULL");
                            }
                        } else {
                            Log.wtf("PERMISOS", "no permission for location");
                        }
                    } else {
                        top.setText("It's hard to tell, are\nyou sure that's a dog?");
                    }
                    reportbutton.setText("That's not a " + first.name);
                    percentages.setText(String.format("\n %s - %d%c \n %s - %d%c \n %s - %d%c \n %s - %d%c" +
                                    "\n %s - %d%c \n %s - %d%c \n %s - %d%c \n %s - %d%c" +
                                    "\n %s - %d%c",
                            first.name, (int)first.certainty, '%',
                            second.name, (int)second.certainty, '%',
                            third.name, (int)third.certainty, '%',
                            fourth.name, (int)fourth.certainty, '%',
                            fifth.name, (int)fifth.certainty, '%',
                            sixth.name, (int)sixth.certainty, '%',
                            seventh.name, (int)seventh.certainty, '%',
                            eighth.name, (int)eighth.certainty, '%',
                            nineth.name, (int)nineth.certainty, '%'
                    ));
                    ArrayList<PieEntry> entries = new ArrayList<PieEntry>();

                    final int[] CUSTOM_COLORS = {
                            Color.rgb(106, 150, 31), // green
                            Color.rgb(245, 199, 0),  // yellow
                            Color.rgb(255, 102, 0),  // orange
                            Color.rgb(193, 37, 82),  // red
                            Color.rgb(179, 100, 53), // brown
                            Color.rgb(148, 0, 211),  // violet
                            Color.rgb(75, 0, 130),   // indigo
                            Color.rgb(0, 0, 255),    // blue
                            Color.rgb(18, 189, 185)  // cyan
                    };
                    Legend legend = pieChart.getLegend();
                    legend.setEnabled(true);
                    ArrayList<LegendEntry> labels = new ArrayList<>();
                    for(int i=0; i<9; i++){
                        entries.add(new PieEntry((float)(int)dogInfos.get(i).certainty, i));

                        LegendEntry legendEntry = new LegendEntry();
                        legendEntry.formColor = CUSTOM_COLORS[i];
                        legendEntry.label = dogInfos.get(i).name+" - "+(int)dogInfos.get(i).certainty+"%";
                        labels.add(legendEntry);
                    }
                    PieDataSet dataset = new PieDataSet(entries,"dataset label");
                    legend.setCustom(labels);
                    legend.setOrientation(Legend.LegendOrientation.VERTICAL);
                    dataset.setColors(CUSTOM_COLORS);
                    PieData data = new PieData(dataset);
                    pieChart.setData(data);
                    Description description = new Description();
                    description.setText("");
                    pieChart.setDescription(description);
                    pieChart.setExtraBottomOffset(30f);
                    pieChart.animateY(5000);
                    Highlight h = new Highlight(0, 0, 0);
                    pieChart.highlightValues(new Highlight[] { h });    // highlight value in pie chart with highest certainty
                    pieChart.invalidate();
                    loading.dismiss();

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
        Toast.makeText(photoActivity.this,"Report sent, our apologies",  Toast.LENGTH_LONG).show();
    }
}
