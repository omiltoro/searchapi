package com.mclinic.search.activities;


import com.nribeka.search.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

public class MuzimaMainActivity extends TabActivity{

	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.main);
	         
	        TabHost tabHost = getTabHost();
	         
	        // Tab for Photos
	        TabSpec searchpatientspec = tabHost.newTabSpec(" Search Patient");
	        // setting Title and Icon for the Tab
	        searchpatientspec.setIndicator(" Search Patient", getResources().getDrawable(R.drawable.icon_searchpatient));
	  Intent searchpatientIntent = new Intent(this, ListPatientActivity.class);
	      searchpatientspec.setContent(searchpatientIntent);
	         
	        // Tab for Songs
	       TabSpec newpatientspec = tabHost.newTabSpec("New Patient");       
	        newpatientspec.setIndicator("New Patient", getResources().getDrawable(R.drawable.icon_newpatient));
	       Intent newpatientIntent = new Intent(this, LabFormActivity.class);
	       newpatientspec.setContent(newpatientIntent);
	         
	        // Tab for Videos
	       TabSpec formspec = tabHost.newTabSpec("Forms");
	       formspec.setIndicator("Forms", getResources().getDrawable(R.drawable.icon_form));
	     Intent formIntent = new Intent(this, LabFormActivity.class);
	       formspec.setContent(formIntent);
	         
	        // Adding all TabSpec to TabHost
	        tabHost.addTab(searchpatientspec); // Adding photos tab
	       tabHost.addTab(newpatientspec); // Adding songs tab
	       tabHost.addTab(formspec); // Adding videos tab
	    }
	 

	  
	    }
	
	
	

