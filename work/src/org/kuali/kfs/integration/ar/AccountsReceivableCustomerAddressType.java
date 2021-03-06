/*
 * Copyright 2007-2009 The Kuali Foundation
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
package org.kuali.kfs.integration.ar;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface AccountsReceivableCustomerAddressType extends ExternalizableBusinessObject {

	public String getCustomerAddressTypeCode();

	public void setCustomerAddressTypeCode(String customerAddressTypeCode);

	public String getCustomerAddressTypeDescription();

	public void setCustomerAddressTypeDescription(String customerAddressTypeDescription);

	public boolean isActive();

	public void setActive(boolean active);
}
