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
package org.kuali.module.financial.bo;

import org.kuali.kfs.bo.SourceAccountingLine;



/**
 * Extends SourceAccountingLine so that, ultimately, GEC can generate different help text since it marks a field as required which
 * is otherwise optional.
 * 
 * 
 */
public class GECSourceAccountingLine extends SourceAccountingLine {
    /**
     * This constructor needs to initialize the ojbConcreteClass attribute such that it sets it to its class name. This is how OJB
     * knows what grouping of objects to work with.
     */
    public GECSourceAccountingLine() {
        super();
        super.ojbConcreteClass = this.getClass().getName();
    }
}