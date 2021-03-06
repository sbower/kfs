/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.ar.businessobject.DunningLetterTemplate;
import org.kuali.kfs.module.ar.document.service.DunningLetterService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Value finder class for Dunning Letter Template.
 */
public class DunningLetterTemplateValuesFinder extends KeyValuesBase {
    protected static volatile DunningLetterService dunningLetterService;

    protected List<KeyValue> keyValues = new ArrayList();

    /**
     * @see org.kuali.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    @SuppressWarnings("unchecked")
    public List<KeyValue> getKeyValues() {
        final Person currentUser = GlobalVariables.getUserSession().getPerson();

        List<DunningLetterTemplate> boList = (List<DunningLetterTemplate>) SpringContext.getBean(BusinessObjectService.class).findAll(DunningLetterTemplate.class);
        for (DunningLetterTemplate element : boList) {
            if (!element.isRestrictUseByChartOrg() && element.isActive()) {
                keyValues.add(new ConcreteKeyValue(element.getDunningLetterTemplateCode(), element.getDunningLetterTemplateDescription()));
            }
            else {
                if (getDunningLetterService().isValidOrganizationForTemplate(element, currentUser) && element.isActive()) {
                    keyValues.add(new ConcreteKeyValue(element.getDunningLetterTemplateCode(), element.getDunningLetterTemplateDescription()));
                }
            }
        }
        return keyValues;
    }

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesBase#clearInternalCache()
     */
    @Override
    public void clearInternalCache() {
        keyValues = null;
    }

    public DunningLetterService getDunningLetterService() {
        if (dunningLetterService == null) {
            dunningLetterService = SpringContext.getBean(DunningLetterService.class);
        }
        return dunningLetterService;
    }
}
