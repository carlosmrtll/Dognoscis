package mx.itesm.dognoscis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by MARTELL on 06-Apr-18.
 */

public class RankAdapter extends BaseAdapter {

    Context context;
    ArrayList<Rank> data;
    private static LayoutInflater inflater = null;

    public RankAdapter(Context context, ArrayList<Rank> data){
        this.context = context;
        this.data = data;
        this.inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View v = view;
        if (v == null)
            v = inflater.inflate(R.layout.activity_ranking_row, null);

        TextView name = (TextView) v.findViewById(R.id.breedName);
        name.setText(data.get(i).breedName);

        TextView count = (TextView) v.findViewById(R.id.breedCount);
        count.setText(String.valueOf(data.get(i).breedCount));

        return v;
    }
}
