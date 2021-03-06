/*
 * Copyright 2011 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.integration.cg;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * This interface defines all the necessary methods to define a contracts and grants billing frequency object.
 */
public interface ContractsAndGrantsBillingFrequency extends ExternalizableBusinessObject, Inactivatable {

    /**
     * Gets the gracePeriodDays attribute.
     *
     * @return Returns the gracePeriodDays.
     */
    public String getFrequency();

    /**
     * Gets the frequencyDescription attribute.
     *
     * @return Returns the frequencyDescription.
     */
    public String getFrequencyDescription();

    /**
     * Gets the frequencyDescription attribute.
     *
     * @return Returns the frequencyDescription.
     */
    public Integer getGracePeriodDays();
}
