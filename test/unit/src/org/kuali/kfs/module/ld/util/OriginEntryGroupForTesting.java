/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.businessobject.OriginEntryGroup;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;

public class OriginEntryGroupForTesting extends OriginEntryGroup {

    @Override
    public boolean equals(Object otherOriginEntryGroup) {
        return ObjectUtil.equals(this, otherOriginEntryGroup, this.getKeyValueList());
    }

    public Map getKeyValueMap() {
        return ObjectUtil.buildPropertyMap(this, this.getKeyValueList());
    }

    public List<String> getKeyValueList() {
        List<String> keyValueList = new ArrayList<String>();
        keyValueList.add(KFSPropertyConstants.SOURCE_CODE);
        keyValueList.add(KFSPropertyConstants.PROCESS);
        keyValueList.add(KFSPropertyConstants.VALID);
        keyValueList.add(KFSPropertyConstants.SCRUB);
        return keyValueList;
    }
}
