package com.mclinic.search.activities;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
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
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObservationChartActivity extends Activity {

    private Patient patient;

    private String observationFieldName;

    private String observationFieldUuid;

    private XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();

    private XYMultipleSeriesRenderer renderer = new XYMultipleSeriesRenderer();

    private GraphicalView graphicalView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.observation_chart);

        if (!FileUtils.storageReady()) {
            showCustomToast(getString(R.string.error, R.string.storage_error));
            finish();
        }

        String patientUuid = getIntent().getStringExtra(Constants.KEY_PATIENT_ID);
        patient = getPatient(patientUuid);

        observationFieldUuid = getIntent().getStringExtra(Constants.KEY_OBSERVATION_FIELD_ID);
        observationFieldName = getIntent().getStringExtra(Constants.KEY_OBSERVATION_FIELD_NAME);

        setTitle(getString(R.string.app_name) + " > " + getString(R.string.view_patient_detail));

        TextView textView = (TextView) findViewById(R.id.title_text);
        textView.setText(observationFieldName);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setLineWidth(3.0f);
        r.setColor(getResources().getColor(R.color.chart_red));
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);

        renderer.addSeriesRenderer(r);
        renderer.setShowLegend(false);
        renderer.setLabelsTextSize(11.0f);
        renderer.setShowGrid(true);
        renderer.setLabelsColor(getResources().getColor(android.R.color.black));
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

    private void getObservations(final String patientUuid, final String fieldName, final String conceptUuid) {
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

        XYSeries series;
        if (dataset.getSeriesCount() > 0) {
            series = dataset.getSeriesAt(0);
            series.clear();
        } else {
            series = new XYSeries(fieldName);
            dataset.addSeries(series);
        }

        for (Observation observation : observations) {
            double d = Double.parseDouble(observation.getValue());
            series.add(observation.getObservationDate().getTime(), d);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (patient != null && observationFieldName != null) {
            getObservations(patient.getUuid(), observationFieldName, observationFieldUuid);
        }

        if (graphicalView == null) {
            LinearLayout layout = (LinearLayout) findViewById(R.id.chart);
            graphicalView = ChartFactory.getTimeChartView(this, dataset, renderer, null);
            layout.addView(graphicalView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
        } else {
            graphicalView.repaint();
        }
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
