package com.mclinic.search.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.mclinic.search.sample.domain.Observation;
import com.nribeka.search.R;

import java.text.SimpleDateFormat;
import java.util.List;

public class EncounterAdapter extends ArrayAdapter<Observation> {

    private SimpleDateFormat mDateFormat = new SimpleDateFormat("dd/MMM/yyyy");

    public EncounterAdapter(Context context, int textViewResourceId, List<Observation> items) {
        super(context, textViewResourceId, items);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.encounter_list_item, null);
        }

        Observation obs = getItem(position);
        if (obs != null) {

            TextView textView = (TextView) v.findViewById(R.id.value_text);
            textView.setText(obs.getValue());

            textView = (TextView) v.findViewById(R.id.encounterdate_text);
            textView.setText(mDateFormat.format(obs.getObservationDate()));
        }
        return v;
    }
}
