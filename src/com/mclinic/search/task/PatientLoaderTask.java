/**
 * The contents of this file are subject to the OpenMRS Public License
 * Version 1.0 (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 * http://license.openmrs.org
 *
 * Software distributed under the License is distributed on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
 * License for the specific language governing rights and limitations
 * under the License.
 *
 * Copyright (C) OpenMRS, LLC.  All Rights Reserved.
 */

package com.mclinic.search.task;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import com.mclinic.search.api.context.ServiceContext;
import com.mclinic.search.api.filter.Filter;
import com.mclinic.search.api.filter.FilterFactory;
import com.mclinic.search.api.resource.Resource;
import com.mclinic.search.api.service.RestAssuredService;
import com.mclinic.search.api.util.StringUtil;
import com.mclinic.search.module.Context;
import com.mclinic.search.module.ContextFactory;
import com.mclinic.search.sample.domain.Cohort;
import com.mclinic.search.sample.domain.Member;
import org.apache.lucene.queryParser.ParseException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class PatientLoaderTask extends AsyncTask<String, String, String> {

    private ProgressBar progressBar;

    private Resources resources;

    public PatientLoaderTask(final ProgressBar progressBar, final Resources resources) {
        this.progressBar = progressBar;
        this.resources = resources;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(final String s) {
        super.onPostExecute(s);
        progressBar.setVisibility(ProgressBar.INVISIBLE);
    }

    @Override
    protected String doInBackground(String... strings) {
        String username = strings[0];
        String password = strings[1];
        String server = strings[2];

        Resource resource;
        try {
            Context context = ContextFactory.createContext(resources);
            context.configure(username, password, server);

            ServiceContext serviceContext = context.getInstance(ServiceContext.class);
            RestAssuredService service = context.getInstance(RestAssuredService.class);

            resource = serviceContext.getResource("Search Cohort Resource");
            service.loadObjects(StringUtil.EMPTY, resource);
            List<Cohort> cohorts = service.getObjects(StringUtil.EMPTY, Cohort.class);
            for (Cohort cohort : cohorts) {
                resource = serviceContext.getResource("Local Member Resource");
                service.loadObjects(cohort.getUuid(), resource);
                Filter filter = FilterFactory.createFilter("cohortUuid", cohort.getUuid());
                List<Member> members = service.getObjects(Arrays.asList(filter), Member.class);
                for (Member member : members) {
                    resource = serviceContext.getResource("Uuid Patient Resource");
                    service.loadObjects(member.getPatientUuid(), resource);
                    resource = serviceContext.getResource("Search Observation Resource");
                    service.loadObjects(member.getPatientUuid(), resource);
                }
            }
        } catch (ParseException e) {
            Log.e(this.getClass().getSimpleName(), "ParseException when trying to load patient", e);
        } catch (IOException e) {
            Log.e(this.getClass().getSimpleName(), "IOException when trying to load patient", e);
        }
        return "Success";
    }
}
