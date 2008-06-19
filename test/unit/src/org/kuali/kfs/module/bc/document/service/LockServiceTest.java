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
package org.kuali.module.budget.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;


import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;

import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants.LockStatus;
import org.kuali.kfs.KFSConstants.BudgetConstructionConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.financial.bo.FiscalYearFunctionControl;
import org.kuali.module.budget.BCConstants;
import org.kuali.module.budget.BCPropertyConstants;
import org.kuali.module.budget.bo.BudgetConstructionFundingLock;
import org.kuali.module.budget.bo.BudgetConstructionHeader;
import org.kuali.module.budget.bo.BudgetConstructionPosition;
import org.kuali.module.budget.bo.PendingBudgetConstructionAppointmentFunding;
import org.kuali.module.budget.dao.BudgetConstructionDao;
import org.kuali.module.budget.service.impl.BudgetConstructionLockStatus;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the Lock Service
 */
@ConfigureContext
public class LockServiceTest extends KualiTestBase {

    private LockService lockService;
    private BudgetConstructionDao bcHeaderDao;

    private DocumentHeader docHeader;
    private BudgetConstructionHeader bcHeader;
    private BudgetConstructionHeader bcHeaderTwo;
    private BudgetConstructionPosition bcPosition;
    private PendingBudgetConstructionAppointmentFunding bcAFunding;
    private BudgetConstructionLockStatus bcLockStatus;
    private LockStatus lockStatus;
    private SortedSet<BudgetConstructionFundingLock> fundingLocks;
    Iterator<BudgetConstructionFundingLock> fundingIter;
    private BudgetConstructionFundingLock fundingLock;
    
    private static boolean dataExists;

    /*
     *   these values are filled in from the database, taking the first row that comes along.
     *   these fields are also static, so we don't have to return to the database for each of the tests.
     *   therefore, it follows that deleting data from the database while the tests are running could result
     *   in failed tests, even though nothing is wrong with the code itself.
     */
    private String fdocNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private String subAccountNumber;
    private String financialObjectCode;
    private String financialSubObjectCode;
    private String emplid;
    private Integer universityFiscalYear;
    private String positionNumber;
    private  String pUIdOne = "3670600494"; // MCGUIRE
    private  String pUIdTwo = "6162502038"; // KHUNTLEY
    boolean posExist = false;
    boolean hdrExist = false;
    boolean docHdrExist = false;
    boolean bcafExist = false;
    
    // set up some data for the tests.
    // we will run everything in one test method, so this only needs to be done once
    public void setUp()
    {
      // get the services we need 
      lockService = SpringContext.getBean(LockService.class);
      bcHeaderDao = SpringContext.getBean(BudgetConstructionDao.class);
      // find a test fiscal year 
      universityFiscalYear = setTestFiscalYear(); 
      dataExists = (universityFiscalYear != 0);
      // find a test funding row for this fiscal year. 
      // (rely on short circuit evaluation)
      dataExists = (dataExists) && (setTestFunding());
      // finally, get the parent document for this funding and position
      dataExists = (dataExists) && (setTestDocumentNumber());
    } 
    
    public void tearDown()
    {
        clearTestRowLocks();
    }

    private boolean runTests() { // change this to return false to prevent running tests
        return true;
    }

    @ConfigureContext(shouldCommitTransactions = true)
    public void testOne() {



        if (!runTests())
            return;

        //
        // (the tests below will check that the unlock activity here took effect). 
        clearTestRowLocks();
        
        // trivial account lock/unlock
        assertFalse("test header was unlocked on initialization", lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("account lock attempt on header succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("test header has an account lock", lockService.isAccountLocked(bcHeader));
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("test header locked successfully on second attempt", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        lockService.unlockAccount(bcHeader);
        assertFalse("unlock attempt on test header succeeded--no lock found", lockService.isAccountLocked(bcHeader));

        // account lock attempt with account lock set by other
        bcLockStatus = lockService.lockAccount(bcHeader, pUIdOne);
        assertTrue("initial header lock by "+pUIdOne+" succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue("header locked by "+pUIdOne, lockService.isAccountLocked(bcHeader));
        // bcHeaderTwo is a different object representing the same budget construction header
        // it must be fetched AFTER the object is locked
        bcHeaderTwo = bcHeaderDao.getByCandidateKey(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        assertTrue("two objects pointing to the same header have the same account", bcHeaderTwo.getAccountNumber().equals(accountNumber));
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(pUIdTwo+" could not get a lock on header already locked by "+pUIdOne, bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("lock is owned by "+pUIdOne, bcLockStatus.getAccountLockOwner().equals(pUIdOne));
        assertTrue(pUIdTwo+"'s pointer to the test header row shows a lock", lockService.isAccountLocked(bcHeaderTwo));

        // funding lock attempt with account lock set in previous test
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue("failed funding lock attempt on a header with an existing acccount lock", bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue("no funding locks exist after failed attempt", lockService.getFundingLocks(bcHeader).isEmpty());

        // account unlock by other - needs account lock in previous test
        // this tests opimistic lock exception catch
        // this configuration of the test must run in a test method that
        // is annotated as ShouldCommitTransactions
        lockService.unlockAccount(bcHeaderTwo);
        assertFalse("account was unlocked successfully",lockService.isAccountLocked(bcHeaderTwo));
        assertTrue("unlock with a different object against the same row triggers an OJB optimistic lock exception", lockService.unlockAccount(bcHeader) == LockStatus.OPTIMISTIC_EX);
        assertFalse("the account is still unlocked", lockService.isAccountLocked(bcHeader));

        // trivial funding lock/unlock
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(pUIdOne+" obtained a funding lock", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(pUIdOne+"'s re-attempt to fetch the same lock also succeeded", bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(pUIdTwo+" can also get a funding lock for the same accounting key",bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse("the set of funding locks for this accounting key is not empty", lockService.getFundingLocks(bcHeader).isEmpty());
        fundingLocks = lockService.getFundingLocks(bcHeader);
        fundingIter = fundingLocks.iterator();
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue("the first funding lock is by accounting key, not by position",fundingLock.getPositionNumber().equals("NotFnd"));
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue("the second funding lock is also not by position", fundingLock.getPositionNumber().equals("NotFnd"));
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(pUIdOne+"'s funding lock was successfully removed", lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(pUIdTwo+"'s funding lock was successfully removed", lockStatus == LockStatus.SUCCESS);
        assertTrue("there are no remaining funding locks for this accounting key", lockService.getFundingLocks(bcHeader).isEmpty());

        // account lock attempt with funding locks set
        // one funding lock has an associated position lock, the other is an orphan
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockFunding(bcHeader, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertFalse(lockService.getFundingLocks(bcHeader).isEmpty());
        bcLockStatus = lockService.lockAccount(bcHeaderTwo, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.FLOCK_FOUND);
        assertFalse(bcLockStatus.getFundingLocks().isEmpty());
        fundingIter = bcLockStatus.getFundingLocks().iterator();
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals("NotFnd")); // orphan
        assertTrue(fundingIter.hasNext());
        fundingLock = fundingIter.next();
        assertTrue(fundingLock.getPositionNumber().equals(positionNumber)); // associated position
        assertFalse(lockService.isAccountLocked(bcHeaderTwo));
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        lockStatus = lockService.unlockFunding(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertTrue(lockService.getFundingLocks(bcHeader).isEmpty());
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue(lockStatus == LockStatus.SUCCESS);

        // trivial position lock/unlock
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isPositionLocked(positionNumber, universityFiscalYear));
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isPositionLocked(positionNumber, universityFiscalYear));

        // position lock attempt with position lock by other
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        bcLockStatus = lockService.lockPosition(positionNumber, universityFiscalYear, pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(lockService.isPositionLocked(positionNumber, universityFiscalYear));
        lockStatus = lockService.unlockPosition(positionNumber, universityFiscalYear);
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isPositionLocked(positionNumber, universityFiscalYear));

        // trivial transaction lock/unlock
        // this test bcHeader, but the application will probably derive the params from BCAppointmentFunding
        lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertFalse(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));

        // transaction lock attempt with transaction lock by other
        // this test uses bcHeader, but the application will probably derive the params from BCAppointmentFunding
        assertFalse(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdOne);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.SUCCESS);
        assertTrue(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        bcLockStatus = lockService.lockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear(), pUIdTwo);
        assertTrue(bcLockStatus.getLockStatus() == LockStatus.BY_OTHER);
        assertTrue(bcLockStatus.getTransactionLockOwner().equals(pUIdOne));
        assertTrue(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));
        lockStatus = lockService.unlockTransaction(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear());
        assertTrue(lockStatus == LockStatus.SUCCESS);
        assertFalse(lockService.isTransactionLocked(bcHeader.getChartOfAccountsCode(), bcHeader.getAccountNumber(), bcHeader.getSubAccountNumber(), bcHeader.getUniversityFiscalYear()));

    }

    private boolean setTestDocumentNumber()
    {
        boolean returnValue = false;
        // use the accounting key to find the document number associated with this funding
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(4);
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,universityFiscalYear);
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE,chartOfAccountsCode);
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER,accountNumber);
        fieldValues.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER,subAccountNumber);
        Collection<BudgetConstructionHeader> bcDocuments = SpringContext.getBean(BusinessObjectService.class).findMatching(BudgetConstructionHeader.class,fieldValues);
        // here there should only be one row
        Iterator<BudgetConstructionHeader> bcHeaderRows = bcDocuments.iterator();
        while (bcHeaderRows.hasNext())
        {
            BudgetConstructionHeader bcHeaderRow = bcHeaderRows.next();
            fdocNumber = bcHeaderRow.getDocumentNumber();
            returnValue = true;
            // save the header that we've chosen
            bcHeader = bcHeaderRow;
            while (bcHeaderRows.hasNext())
            {
                bcHeaderRows.next();
            }
        }
        return returnValue;
    }
    
    private Integer setTestFiscalYear()
    {
        Integer fiscalYear = new Integer(0);
        // find a fiscal year for which there is active budget construction data.
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(2);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_CONTROL_CODE,BudgetConstructionConstants.BUDGET_CONSTRUCTION_ACTIVE);
        fieldValues.put(KFSPropertyConstants.FINANCIAL_SYSTEM_FUNCTION_ACTIVE_INDICATOR,new Boolean(true));
        Collection<FiscalYearFunctionControl> returnedYears = SpringContext.getBean(BusinessObjectService.class).findMatching(FiscalYearFunctionControl.class,fieldValues);
        // there should be only one, but who knows with test data involved--we'll take the fiscal year from the first one
        Iterator<FiscalYearFunctionControl> activeYears = returnedYears.iterator();
        if (activeYears.hasNext())
        {
            fiscalYear = activeYears.next().getUniversityFiscalYear();
            // just run the iterator out, to be tidy
            while (activeYears.hasNext())
            {
                activeYears.next();
            }
        }
        return fiscalYear;
    }
    
    private boolean setTestFunding()
    {
        boolean returnValue = false;
        // all we need is a single funding line (not deleted, not vacant) for a real person
        // but we'll apparently have to get them all and just take the first one
        HashMap<String,Object> fieldValues = new HashMap<String,Object>(2);
        fieldValues.put(BCPropertyConstants.APPOINTMENT_FUNDING_DELETE_INDICATOR,new Boolean(false));
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR,universityFiscalYear);
        // get the complete set of rows and look for the first one that does not have a vacant EMPLID
        Collection<PendingBudgetConstructionAppointmentFunding> resultSet = SpringContext.getBean(BusinessObjectService.class).findMatching(PendingBudgetConstructionAppointmentFunding.class,fieldValues);
        Iterator<PendingBudgetConstructionAppointmentFunding> fundingRows = resultSet.iterator();
        while (fundingRows.hasNext())
        {
            PendingBudgetConstructionAppointmentFunding fundingRow = fundingRows.next();
            if (!fundingRow.getEmplid().equals(BCConstants.VACANT_EMPLID))
            {
                returnValue = true;
                // set all the test funding values from this row
                chartOfAccountsCode = fundingRow.getChartOfAccountsCode();
                accountNumber = fundingRow.getAccountNumber();
                subAccountNumber = fundingRow.getSubAccountNumber();
                financialObjectCode = fundingRow.getFinancialObjectCode();
                financialSubObjectCode = fundingRow.getFinancialSubObjectCode();
                emplid = fundingRow.getEmplid();
                positionNumber = fundingRow.getPositionNumber();
                // save the row we've selected
                bcAFunding = fundingRow;
                // run out the iterator
                while (fundingRows.hasNext())
                {
                    fundingRows.next();
                }
            }
        }
        return returnValue;
    }
    
    @ConfigureContext(shouldCommitTransactions = true)
    private void clearTestRowLocks()
    {
        // clear all the locks on the test rows used in this TestCase
        // make sure there are no locks or transaction locks in our test header
        lockService.unlockAccount(bcHeader);
        lockService.unlockTransaction(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear);
        // make sure the position we intend to use is unlocked
        lockService.unlockPosition(positionNumber, universityFiscalYear);
        // make sure funding locks we intend to use aren't there
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdOne);
        lockService.unlockFunding(chartOfAccountsCode, accountNumber, subAccountNumber, universityFiscalYear, pUIdTwo);
    }
    
}

