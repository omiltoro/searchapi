/**
 * Copyright 2012 Muzima Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mclinic.search.sample.algorithm;

import com.jayway.jsonpath.JsonPath;
import com.mclinic.search.api.model.object.Searchable;
import com.mclinic.search.api.util.ISO8601Util;
import com.mclinic.search.sample.domain.Patient;

import java.io.IOException;
import java.text.ParseException;

public class PatientAlgorithm extends BaseOpenmrsAlgorithm {

    /**
     * Implementation of this method will define how the observation will be serialized from the JSON representation.
     *
     * @param json the json representation
     * @return the concrete observation object
     */
    @Override
    public Searchable deserialize(final String json) throws IOException {

        Patient patient = new Patient();

        // get the full json object representation and then pass this around to the next JsonPath.read()
        // this should minimize the time for the subsequent read() call
        Object jsonObject = JsonPath.read(json, "$");

        String uuid = JsonPath.read(jsonObject, "$['uuid']");
        patient.setUuid(uuid);

        String givenName = JsonPath.read(jsonObject, "$['personName.givenName']");
        patient.setGivenName(givenName);

        String middleName = JsonPath.read(jsonObject, "$['personName.middleName']");
        patient.setMiddleName(middleName);

        String familyName = JsonPath.read(jsonObject, "$['personName.familyName']");
        patient.setFamilyName(familyName);

        String identifier = JsonPath.read(jsonObject, "$['patientIdentifier.identifier']");
        patient.setIdentifier(identifier);

        String gender = JsonPath.read(jsonObject, "$['gender']");
        patient.setGender(gender);

        String birthdate = JsonPath.read(jsonObject, "$['birthdate']");
        try {
            patient.setBirthdate(ISO8601Util.toCalendar(birthdate).getTime());
        } catch (ParseException e) {
            getLogger().error(this.getClass().getSimpleName(), "Unable to parse date data from json payload.", e);
        }

        return patient;
    }
}
