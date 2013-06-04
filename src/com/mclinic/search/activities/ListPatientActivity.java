package com.mclinic.search.activities;

import android.app.ListActivity;
import android.content.Intent;
import android.app.Activity;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import com.mclinic.search.adapter.PatientAdapter;
import com.mclinic.search.api.service.RestAssuredService;
import com.mclinic.search.api.util.StringUtil;
import com.mclinic.search.module.Context;
import com.mclinic.search.module.ContextFactory;
import com.mclinic.search.sample.domain.Patient;
import com.mclinic.search.task.PatientLoaderTask;
import com.mclinic.search.util.Constants;
import com.mclinic.search.util.FileUtils;
import com.nribeka.search.R;

import java.util.ArrayList;
import java.util.List;

public class ListPatientActivity extends ListActivity {

    private static final int MENU_PREFERENCES = Menu.FIRST;

    private static final String DOWNLOAD_PATIENT_CANCELED_KEY = "downloadPatientCanceled";

    public static final int BARCODE_CAPTURE = 2;

    public static final int DOWNLOAD_PATIENT = 1;

    private EditText editText;

    private TextWatcher textWatcher;

    private ArrayList<Patient> patients = new ArrayList<Patient>();

    private ArrayAdapter<Patient> patientAdapter;

    private boolean downloadCanceled = false;
    
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_patient);
        
       // addListenerOnButton();
        
        setListAdapter(patientAdapter);

        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(DOWNLOAD_PATIENT_CANCELED_KEY)) {
                downloadCanceled = savedInstanceState.getBoolean(DOWNLOAD_PATIENT_CANCELED_KEY);
            }
        }

        setTitle(getString(R.string.app_name) + " > " + getString(R.string.find_patient));

        if (!FileUtils.storageReady()) {
            showCustomToast(getString(R.string.error, getString(R.string.storage_error)));
            finish();
        }

        textWatcher = new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                getPatients(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
        };

        editText = (EditText) findViewById(R.id.search_text);
        editText.addTextChangedListener(textWatcher);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progress_bar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        Button downloadButton = (Button) findViewById(R.id.download_patients);
        downloadButton.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String server = settings.getString(
                        PreferencesActivity.KEY_SERVER, getString(R.string.default_server));
                String username = settings.getString(
                        PreferencesActivity.KEY_USERNAME, getString(R.string.default_username));
                String password = settings.getString(
                        PreferencesActivity.KEY_PASSWORD, getString(R.string.default_password));
                PatientLoaderTask patientLoaderTask = new PatientLoaderTask(
                        progressBar, getApplicationContext().getResources());
                patientLoaderTask.execute(username, password, server);
            }
        });
    }

    @Override
    protected void onListItemClick(ListView listView, View view, int position, long id) {
        // Get selected patient
        Patient patient = (Patient) getListAdapter().getItem(position);
        String patientUuid = patient.getUuid();

        Intent ip = new Intent(getApplicationContext(), ViewPatientActivity.class);
        ip.putExtra(Constants.KEY_PATIENT_ID, patientUuid);
        startActivity(ip);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.layout.menu, menu);
              super.onCreateOptionsMenu(menu);
        menu.add(0, MENU_PREFERENCES, 0,
                getString(R.string.server_preferences)).setIcon(android.R.drawable.ic_menu_preferences);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        
        
            case MENU_PREFERENCES:
                Intent ip = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(ip);
           /* case R.id.menu_bookmark:
            	// Single menu item is selected do something
            	// Ex: launching new activity/screen or show alert message
                Toast.makeText(ListPatientActivity.this, "Bookmark is Selected", Toast.LENGTH_SHORT).show();
              
	 			                           return true;
            case R.id.menu_save:
            	Toast.makeText(ListPatientActivity.this, "Save is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_search:
            	Toast.makeText(ListPatientActivity.this, "Search is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_share:
            	Toast.makeText(ListPatientActivity.this, "Share is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_delete:
            	Toast.makeText(ListPatientActivity.this, "Delete is Selected", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menu_preferences:
            	Toast.makeText(ListPatientActivity.this, "Preferences is Selected", Toast.LENGTH_SHORT).show();
                return true; */
            default:
                return super.onOptionsItemSelected(item);
                
              
             
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {

        if (resultCode == RESULT_CANCELED) {
            if (requestCode == DOWNLOAD_PATIENT) {
                downloadCanceled = true;
            }
            return;
        }

        if (requestCode == BARCODE_CAPTURE && intent != null) {
            String sb = intent.getStringExtra("SCAN_RESULT");
            if (sb != null && sb.length() > 0) {
                editText.setText(sb);
            }
        }
        super.onActivityResult(requestCode, resultCode, intent);

    }

    private void getPatients() {
        getPatients(StringUtil.EMPTY);
    }

    private void getPatients(final String searchStr) {
        patients.clear();

        List<Patient> objects = new ArrayList<Patient>();
        try {
            Context context = ContextFactory.createContext(getApplicationContext().getResources());
            RestAssuredService service = context.getInstance(RestAssuredService.class);

            String param = StringUtil.EMPTY;
            if (!StringUtil.isEmpty(searchStr)) {
                param = "givenName:" + searchStr + "* OR middleName:" + searchStr + "* OR familyName:" + searchStr + "*";
            }

            objects = service.getObjects(param, Patient.class);
        } catch (Exception e) {
            Log.e(this.getClass().getSimpleName(), "Exception when trying to load patient", e);
        }

        patients.addAll(objects);
        patientAdapter = new PatientAdapter(this, R.layout.patient_list_item, patients);
        setListAdapter(patientAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        editText.removeTextChangedListener(textWatcher);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean firstRun = settings.getBoolean(PreferencesActivity.KEY_FIRST_RUN, true);

        if (firstRun) {
            // Save first run status
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(PreferencesActivity.KEY_FIRST_RUN, false);
            editor.commit();

            // Start preferences activity
            Intent ip = new Intent(getApplicationContext(), PreferencesActivity.class);
            startActivity(ip);

        } else {
            getPatients();
            editText.setText(editText.getText().toString());
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean(DOWNLOAD_PATIENT_CANCELED_KEY, downloadCanceled);
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
/*    public void addListenerOnButton() {

		button = (Button) findViewById(R.id.button1);

		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				Intent intent = new Intent(arg0.getContext() , LabFormActivity.class);
				startActivityForResult (intent, 0);
			}
		});
*/
}
    
   
   







