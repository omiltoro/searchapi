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
package com.mclinic.search.sample.domain;

import com.mclinic.search.api.model.object.BaseSearchable;

/**
 * The Member class will reference to uuid of all patients for which the Member is associated with.
 * <br/>
 * This class is an exception from all of the other model classes where it's a local object but have the name similar
 * with the same remote resource. This class must not be associated with the <code>MemberCohortResolver</code>.
 */
public class Member extends BaseSearchable {

    private String cohortUuid;

    private String patientUuid;

    /**
     * Get the cohort's uuid for this member.
     *
     * @return the cohort's uuid for this member.
     */
    public String getCohortUuid() {
        return cohortUuid;
    }

    /**
     * Set the cohort's uuid for this member.
     *
     * @param cohortUuid the cohort's uuid for this member.
     */
    public void setCohortUuid(final String cohortUuid) {
        this.cohortUuid = cohortUuid;
    }

    /**
     * Get the patient's uuid for the cohort member.
     *
     * @return the patient's uuid for the cohort member.
     */
    public String getPatientUuid() {
        return patientUuid;
    }

    /**
     * Set the patient's uuid for the cohort member.
     *
     * @param patientUuid the patient's uuid for the cohort member.
     */
    public void setPatientUuid(final String patientUuid) {
        this.patientUuid = patientUuid;
    }
}
