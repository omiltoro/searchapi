package com.mclinic.search.activities;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.mclinic.search.adapter.EncounterAdapter;
import com.mclinic.search.api.filter.Filter;
import com.mclinic.search.api.filter.FilterFactory;
import com.mclinic.search.api.service.RestAssuredService;
import com.mclinic.search.module.Context;
import com.mclinic.search.module.ContextFactory;
import com.mclinic.search.sample.domain.Observation;
import com.mclinic.search.sample.domain.Patient;
import com.mclinic.search.util.Constants;
import com.mclinic.search.util.FileUtils;
import com.nribeka.search.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObservationTimelineActivity extends ListActivity {

    private Patient patient;

    private String fieldUuid;

    private ArrayList<Observation> observations = new ArrayList<Observation>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observation_timeline);

        if (!FileUtils.storageReady()) {
            showCustomToast(getString(R.string.error, R.string.storage_error));
            finish();
        }

        String patientUuid = getIntent().getStringExtra(Constants.KEY_PATIENT_ID);
        patient = getPatient(patientUuid);

        fieldUuid = getIntent().getStringExtra(Constants.KEY_OBSERVATION_FIELD_ID);
        String fieldName = getIntent().getStringExtra(Constants.KEY_OBSERVATION_FIELD_NAME);

        setTitle(getString(R.string.app_name) + " > " + getString(R.string.view_patient_detail));

        TextView textView = (TextView) findViewById(R.id.title_text);
        textView.setText(fieldName);
    }

    private Patient getPatient(final String uuid) {
        Patient patient = null;
        try {
            Context context = ContextFactory.createContext(getApplicationContext().getResources());
            RestAssuredService service = context.getInstance(RestAssuredService.class);
            patient = service.getObject(uuid, Patient.class);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Exception when trying to load patient", e);
        }
        return patient;
    }

    private void getObservations(final String patientUuid, final String conceptUuid) {
        List<Observation> observations = new ArrayList<Observation>();
        try {
            Context context = ContextFactory.createContext(getApplicationContext().getResources());
            RestAssuredService service = context.getInstance(RestAssuredService.class);
            Filter patientFilter = FilterFactory.createFilter("patientUuid", patientUuid);
            Filter conceptFilter = FilterFactory.createFilter("conceptUuid", conceptUuid);
            observations = service.getObjects(
                    Arrays.asList(patientFilter, conceptFilter), Observation.class);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Exception when trying to load patient", e);
        }

        observations.addAll(observations);
        ArrayAdapter<Observation> observationAdapter = new EncounterAdapter(this, R.layout.encounter_list_item, observations);
        setListAdapter(observationAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (patient != null && fieldUuid != null) {
            getObservations(patient.getUuid(), fieldUuid);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void showCustomToast(String message) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(android.content.Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast_view, null);

        // set the text in the view
        TextView tv = (TextView) view.findViewById(R.id.message);
        tv.setText(message);

        Toast t = new Toast(this);
        t.setView(view);
        t.setDuration(Toast.LENGTH_LONG);
        t.setGravity(Gravity.CENTER, 0, 0);
        t.show();
    }
}
