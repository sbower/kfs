/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;

import org.kuali.rice.kns.bo.Inactivateable;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;

/**
 * Vendor Stipulation Business Object.
 */
public class VendorStipulation extends PersistableBusinessObjectBase implements Inactivateable{

    private Integer vendorStipulationIdentifier;
    private String vendorStipulationName;
    private String vendorStipulationDescription;
    private boolean active;

    /**
     * Default constructor.
     */
    public VendorStipulation() {

    }

    public Integer getVendorStipulationIdentifier() {
        return vendorStipulationIdentifier;
    }

    public void setVendorStipulationIdentifier(Integer vendorStipulationIdentifier) {
        this.vendorStipulationIdentifier = vendorStipulationIdentifier;
    }

    public String getVendorStipulationName() {
        return vendorStipulationName;
    }

    public void setVendorStipulationName(String vendorStipulationName) {
        this.vendorStipulationName = vendorStipulationName;
    }

    public String getVendorStipulationDescription() {
        return vendorStipulationDescription;
    }

    public void setVendorStipulationDescription(String vendorStipulationDescription) {
        this.vendorStipulationDescription = vendorStipulationDescription;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.vendorStipulationIdentifier != null) {
            m.put("vendorStipulationIdentifier", this.vendorStipulationIdentifier.toString());
        }
        return m;
    }

}
