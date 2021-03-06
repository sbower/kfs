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
package org.kuali.kfs.sys.service;

import java.util.Map;

import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;

/**
 * This represents one "controller" action performed by the ElectronicFundTransfer Action 
 */
public interface ElectronicFundTransferActionHelper {
    /**
     * Performs a web controller action
     * @param form the form that the action is to perform on
     * @param mapping the action mappings to return to
     * @param paramMap the map of parameters from the request
     * @param basePath the basePath of the request
     * @return the ActionForward that represents where the controller should next redirect to
     */
    public abstract ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map parameterMap, String basePath);
}
