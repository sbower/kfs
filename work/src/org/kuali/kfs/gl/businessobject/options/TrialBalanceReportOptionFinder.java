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
package org.kuali.kfs.gl.businessobject.options;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.gl.Constant;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.valuefinder.ValueFinder;

/**
 * An implmentation of ValueFinder that allows the balance inquiries to choose whether to show blank entries
 */
public class TrialBalanceReportOptionFinder extends KeyValuesBase implements ValueFinder {

    /**
     * Returns the default value for this ValueFinder, in this case, exclude cost share entries
     *
     * @return a String with the default key
     * @see org.kuali.rice.kns.lookup.valueFinder.ValueFinder#getValue()
     */
    @Override
    public String getValue() {
        return Constant.NOT_SHOW_BLANK_LINE;
    }

    /**
     * Returns a list of possible values for this ValueFinder, in this case include cost share entries or exclude cost share entries
     *
     * @return a List of key/value pairs to populate radio buttons
     * @see org.kuali.rice.kns.lookup.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<KeyValue> labels = new ArrayList<KeyValue>();
        labels.add(new ConcreteKeyValue(Constant.NOT_SHOW_BLANK_LINE, Constant.NOT_SHOW_BLANK_LINE));
        labels.add(new ConcreteKeyValue(Constant.SHOW_BLANK_LINE, Constant.SHOW_BLANK_LINE));
        return labels;
    }
}
