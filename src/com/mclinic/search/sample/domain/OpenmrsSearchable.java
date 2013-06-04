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

package com.mclinic.search.sample.domain;

import com.mclinic.search.api.model.object.BaseSearchable;

/**
 */
public abstract class OpenmrsSearchable extends BaseSearchable {

    private String uri;

    /**
     * Get the openmrs object's uri.
     *
     * @return the uri;
     */
    public String getUri() {
        return uri;
    }

    /**
     * Set the openmrs object's uri.
     *
     * @param uri the uri.
     */
    public void setUri(final String uri) {
        this.uri = uri;
    }
}
