package mx.itesm.dognoscis;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class RankActivity extends AppCompatActivity {

    private String pname = "husky";
    private ListView listView;
    private String[] source = {"1 - Husky", "2 - Dalmata", "3 - Chihuahua", " 4 - San Bernardo"};
    private String[] perros = {"husky", "dalmata", "chihuahua", "sanbernardo"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rank);

        listView = (ListView)findViewById(R.id.ListView);

        // Adapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                source
        );

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String temp = ""+(listView.getItemAtPosition(position));

                for (int i = 0; i < source.length; i++){
                    if(source[i].equals(temp)){
                        pname = perros[i];
                        break;
                    } else {
                        continue;
                    }
                }
                click(view);
            }
        });
    }

    public void click(View v){
        Intent intent = new Intent(this, RankInfoActivity.class);
        intent.putExtra("perroname", pname);

        startActivity(intent);
    }

}

