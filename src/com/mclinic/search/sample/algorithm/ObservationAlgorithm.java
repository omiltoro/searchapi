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
import com.mclinic.search.sample.domain.Observation;
import com.mclinic.search.util.Constants;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObservationAlgorithm extends BaseOpenmrsAlgorithm {

    /**
     * Implementation of this method will define how the observation will be serialized from the JSON representation.
     *
     * @param json the json representation
     * @return the concrete observation object
     */
    @Override
    public Searchable deserialize(final String json) throws IOException {
        Observation observation = new Observation();

        // get the full json object representation and then pass this around to the next JsonPath.read()
        // this should minimize the time for the subsequent read() call
        Object jsonObject = JsonPath.read(json, "$");

        String uuid = JsonPath.read(jsonObject, "$['uuid']");
        observation.setUuid(uuid);

        String patientUuid = JsonPath.read(jsonObject, "$['person.uuid']");
        observation.setPatientUuid(patientUuid);

        String encounterUuid = JsonPath.read(jsonObject, "$['encounter.uuid']");
        observation.setEncounterUuid(encounterUuid);

        String conceptName = JsonPath.read(jsonObject, "$['concept.name.name']");
        observation.setQuestionName(conceptName);

        String conceptUuid = JsonPath.read(jsonObject, "$['concept.uuid']");
        observation.setQuestionUuid(conceptUuid);

        Integer integer = JsonPath.read(jsonObject, "$['concept.datatype.conceptDatatypeId']");
        observation.setDataType(integer);

        Object valueNumeric = JsonPath.read(jsonObject, "$['valueNumeric']");
        if (valueNumeric != null) {
            observation.setValue(valueNumeric.toString());
            observation.setDataType(Constants.TYPE_FLOAT);
        }

        Object valueDatetime = JsonPath.read(jsonObject, "$['valueDatetime']");
        if (valueDatetime != null) {
            try {
                DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
                Date date = ISO8601Util.toCalendar(valueDatetime.toString()).getTime();
                observation.setValue(dateFormat.format(date));
            } catch (ParseException e) {
                getLogger().error(this.getClass().getSimpleName(), "Unable to parse date data from json payload.", e);
            }
            observation.setDataType(Constants.TYPE_DATE);
        }

        Object valueCodedObject = JsonPath.read(jsonObject, "$['valueCoded']");
        if (valueCodedObject != null) {
            String valueCoded = JsonPath.read(valueCodedObject, "$['display']");
            observation.setValue(valueCoded);
            observation.setDataType(Constants.TYPE_STRING);
        }

        String obsDatetime = JsonPath.read(jsonObject, "$['obsDatetime']");
        try {
            observation.setObservationDate(ISO8601Util.toCalendar(obsDatetime).getTime());
        } catch (ParseException e) {
            getLogger().error(this.getClass().getSimpleName(), "Unable to parse date data from json payload.", e);
        }

        return observation;
    }
}
