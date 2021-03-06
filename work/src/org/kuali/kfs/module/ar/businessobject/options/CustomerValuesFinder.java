/*
 * Copyright 2008 The Kuali Foundation
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

import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

public class CustomerValuesFinder extends KeyValuesBase {

    @SuppressWarnings("unchecked")
    public List getKeyValues() {

        List<Customer> boList = (List) SpringContext.getBean(KeyValuesService.class).findAll(Customer.class);
        List<KeyValue> keyValues = new ArrayList();
        keyValues.add(new ConcreteKeyValue("", ""));
        for (Customer element : boList) {
            keyValues.add(new ConcreteKeyValue(element.getCustomerNumber(), element.getCustomerName()));
        }

        return keyValues;

    }

}
