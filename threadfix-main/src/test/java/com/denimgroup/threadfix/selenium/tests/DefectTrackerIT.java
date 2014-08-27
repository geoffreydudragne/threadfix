////////////////////////////////////////////////////////////////////////
//
//     Copyright (c) 2009-2014 Denim Group, Ltd.
//
//     The contents of this file are subject to the Mozilla Public License
//     Version 2.0 (the "License"); you may not use this file except in
//     compliance with the License. You may obtain a copy of the License at
//     http://www.mozilla.org/MPL/
//
//     Software distributed under the License is distributed on an "AS IS"
//     basis, WITHOUT WARRANTY OF ANY KIND, either express or implied. See the
//     License for the specific language governing rights and limitations
//     under the License.
//
//     The Original Code is ThreadFix.
//
//     The Initial Developer of the Original Code is Denim Group, Ltd.
//     Portions created by Denim Group, Ltd. are Copyright (C)
//     Denim Group, Ltd. All Rights Reserved.
//
//     Contributor(s): Denim Group, Ltd.
//
////////////////////////////////////////////////////////////////////////
package com.denimgroup.threadfix.selenium.tests;

import com.denimgroup.threadfix.CommunityTests;
import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.DefectTrackerIndexPage;
import com.denimgroup.threadfix.selenium.utils.DatabaseUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@Category(CommunityTests.class)
public class DefectTrackerIT extends BaseIT {

    private static final String JIRA_USERNAME = System.getProperty("JIRA_USERNAME");
    private static final String JIRA_PASSWORD = System.getProperty("JIRA_PASSWORD");
    private static final String JIRA_URL = System.getProperty("JIRA_URL");
    private static final String JIRAPROJECTNAME = System.getProperty("JIRAPROJECTNAME");
    private static final String BUGZILLA_USERNAME = System.getProperty("BUGZILLA_USERNAME");
    private static final String BUGZILLA_PASSWORD = System.getProperty("BUGZILLA_PASSWORD");
    private static final String BUGZILLA_URL = System.getProperty("BUGZILLA_URL");
    private static final String BUGZILLAPROJECTNAME = "For ThreadFix";
    private static final String TFS_USERNAME = System.getProperty("TFS_USERNAME");
    private static final String TFS_PASSWORD = System.getProperty("TFS_PASSWORD");
    private static final String TFS_URL = System.getProperty("TFS_URL");
    private static final String TFS_PROJECTNAME = ("Vulnerability Manager Demo");

    static {
        if (JIRA_USERNAME == null){
            throw new RuntimeException("Please set JIRA_USERNAME property.");
        }
        if (JIRA_PASSWORD == null){
            throw new RuntimeException("Please set JIRA_PASSWORD property.");
        }
        if (JIRA_URL == null){
            throw new RuntimeException("Please set JIRA_URL property.");
        }
        if (JIRAPROJECTNAME == null){
            throw new RuntimeException("Please set JIRAPROJECTNAME property.");
        }
        if (BUGZILLA_USERNAME == null){
            throw new RuntimeException("Please set BUGZILLA_USERNAME property.");
        }
        if (BUGZILLA_PASSWORD == null){
            throw new RuntimeException("Please set BUGZILLA PASSWORD property.");
        }
        if (BUGZILLA_URL == null){
            throw new RuntimeException("Please set BUGZILLA_URL property.");
        }
        if (TFS_USERNAME == null){
            throw new RuntimeException("Please set TFS_USERNAME property.");
        }
        if (TFS_PASSWORD == null){
            throw new RuntimeException("Please set TFS_PASSWORD property.");
        }
        if (TFS_URL == null){
            throw new RuntimeException("Please set TFS_URL property.");
        }
    }

    @Test
	public void createDefectTrackerTest() {
		String newDefectTrackerName = "testCreateDefectTracker"+ getRandomString(3);
		String defectTrackerType = "Bugzilla";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink();

		defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(newDefectTrackerName)
				.setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        assertTrue("Success message error.",
                defectTrackerIndexPage.getSuccessMessage().contains("Successfully created defect tracker " + newDefectTrackerName));

		assertTrue("The defectTracker was not present in the table.",
                defectTrackerIndexPage.isTextPresentInDefectTrackerTableBody(newDefectTrackerName));
	}

    @Test
    public void deleteDefectTrackerTest() {
        String newDefectTrackerName = "testDeleteDefectTracker"+ getRandomString(3);
        String defectTrackerType = "Bugzilla";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink()
                .clickAddDefectTrackerButton()
                .setName(newDefectTrackerName)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        defectTrackerIndexPage = defectTrackerIndexPage.clickEditLink(newDefectTrackerName)
                .clickDeleteButton()
                .clickDefectTrackersLink();

        assertFalse("The defectTracker was still present after attempted deletion.",
                defectTrackerIndexPage.isElementPresent("defectTackerName" + newDefectTrackerName));
    }

	@Test
	public void createDefectTrackerFieldValidation() {
		String emptyString = "";
		String whiteSpaceString = "           ";
		String urlFormatString = "asdfwe";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink()
                .clickAddDefectTrackerButton();

		//Test empty and whitespace input
		defectTrackerIndexPage = defectTrackerIndexPage.setName(emptyString)
                .setURL(emptyString)
				.clickAddDefectTrackerButtonInvalid();

        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameRequiredError"));
        assertTrue("The correct error text was not present",defectTrackerIndexPage.getNameRequiredErrorsText().contains("Name is required."));

		defectTrackerIndexPage = defectTrackerIndexPage.setName(whiteSpaceString)
				.setURL(whiteSpaceString)
				.clickAddDefectTrackerButtonInvalid();

        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameRequiredError"));
        assertTrue("The correct error text was not present",defectTrackerIndexPage.getNameRequiredErrorsText().contains("Name is required."));

		// Test URL format checking
		defectTrackerIndexPage = defectTrackerIndexPage.setName("normal name")
                .setURL(urlFormatString)
				.clickAddDefectTrackerButtonInvalid();

        assertTrue("The URL format check error text was not present.",defectTrackerIndexPage.isElementVisible("urlInvalidError"));
	}

    @Test
    public void longNameFormatTest() {
        String longName = getRandomString(60);
        String longNameFormatted = longName.substring(0,49);

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink()
                .clickAddDefectTrackerButton();

        defectTrackerIndexPage.setName(longName)
                .setURL(BUGZILLA_URL)
                .setType("Bugzilla")
                .clickSaveDefectTracker();

        assertTrue("Success message error.",
                defectTrackerIndexPage.getSuccessMessage().contains("Successfully created defect tracker " + longNameFormatted));

        assertTrue("The defectTracker was not present in the table.",
                defectTrackerIndexPage.isTextPresentInDefectTrackerTableBody(longNameFormatted));
    }

	@Test
	public void editDefectTrackerTest() {
		String originalDefectTrackerName = getRandomString(8);
		String editedDefectTrackerName = getRandomString(8);
		String originalDefectTrackerType = "Jira";
        String editedDefectTrackerType = "Bugzilla";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user", "password")
                .clickDefectTrackersLink();

		defectTrackerIndexPage = defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(originalDefectTrackerName)
                .setType(originalDefectTrackerType)
				.setURL(JIRA_URL)
                .clickSaveDefectTracker();

        defectTrackerIndexPage.refreshPage();

        //Edit previously created defect tracker
		defectTrackerIndexPage = defectTrackerIndexPage.clickEditLink(originalDefectTrackerName)
                .setName(editedDefectTrackerName)
                .setType(editedDefectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

		assertTrue("Edit did not change the name.",
                defectTrackerIndexPage.isNamePresent(editedDefectTrackerName));
        assertTrue("Edit did not change the type.",
                defectTrackerIndexPage.isTypeCorrect(editedDefectTrackerType, editedDefectTrackerName));
        assertTrue("Edit did not change url.",
                defectTrackerIndexPage.isUrlCorrect(BUGZILLA_URL, editedDefectTrackerName));
	}

    @Test
	public void editDefectTrackerFieldValidation() {
        String emptyString = "";
        String whiteSpaceString = "           ";

		String newDefectTrackerName = getRandomString(8);
		String defectTrackerNameDuplicateTest = getRandomString(8);

		String defectTrackerType = "Bugzilla";
        String longInput = getRandomString(55);

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink();

        defectTrackerIndexPage.clickAddDefectTrackerButton();
        defectTrackerIndexPage.setName(defectTrackerNameDuplicateTest);
        defectTrackerIndexPage.setType(defectTrackerType);
        defectTrackerIndexPage.setURL(BUGZILLA_URL);
        defectTrackerIndexPage.clickSaveDefectTracker();

        defectTrackerIndexPage.refreshPage();

		defectTrackerIndexPage.clickAddDefectTrackerButton();
        defectTrackerIndexPage.setName(newDefectTrackerName);
        defectTrackerIndexPage.setType(defectTrackerType);
        defectTrackerIndexPage.setURL(BUGZILLA_URL);
        defectTrackerIndexPage.clickSaveDefectTracker();

        defectTrackerIndexPage.refreshPage();

        defectTrackerIndexPage.clickEditLink(newDefectTrackerName);

		// Test empty and whitespace input
		defectTrackerIndexPage.setName(emptyString);
        defectTrackerIndexPage.clickModalSubmitInvalid();
        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameRequiredError"));
		assertTrue("The correct error text was not present",defectTrackerIndexPage.getNameRequiredErrorsText().contains("Name is required."));

		defectTrackerIndexPage.setName(whiteSpaceString);
        defectTrackerIndexPage.clickModalSubmitInvalid();
        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameRequiredError"));
		assertTrue("The correct error text was not present",defectTrackerIndexPage.getNameRequiredErrorsText().contains("Name is required."));

		// Test browser length limit
		defectTrackerIndexPage.setName(longInput);
        defectTrackerIndexPage.clickModalSubmitInvalid();
        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameCharacterLimitError"));

        defectTrackerIndexPage.clickModalCancel();
        defectTrackerIndexPage.refreshPage();

		// Test name duplication checking
		defectTrackerIndexPage.clickDefectTrackersLink();
        defectTrackerIndexPage.clickEditLink(newDefectTrackerName);
        defectTrackerIndexPage.setName(defectTrackerNameDuplicateTest);
        defectTrackerIndexPage.clickModalSubmitInvalid();
        assertTrue("Error message was not visible.",defectTrackerIndexPage.isElementVisible("nameServerError"));
        assertTrue("The correct error text was not present",defectTrackerIndexPage.getNameDuplicateErrorsText().contains("That name is already taken."));
	}

	@Test
	public void jiraEdit() {
		String defectTrackerName = getRandomString(8);
        String replacementName = getRandomString(8);
		String defectTrackerType = "Jira";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink();

        defectTrackerIndexPage = defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(defectTrackerName)
                .setType(defectTrackerType)
                .setURL(JIRA_URL)
                .clickSaveDefectTracker();

        defectTrackerIndexPage.refreshPage();

		defectTrackerIndexPage = defectTrackerIndexPage.clickEditLink(defectTrackerName)
                .setName(replacementName)
                .clickSaveDefectTracker();

		assertTrue("DefectTracker page did not edit jira tracker correctly.",
				defectTrackerIndexPage.isTextPresentInDefectTrackerTableBody(replacementName));
	}

	@Test
	public void bugzillaEdit() {
		String defectTrackerName = getRandomString(8);
        String replacementName = getRandomString(8);
		String defectTrackerType = "Bugzilla";

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink();

        defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(defectTrackerName)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        driver.navigate().refresh();

        assertTrue("DefectTracker Page did not create correctly.", defectTrackerIndexPage.isTextPresentInDefectTrackerTableBody(defectTrackerName));

		defectTrackerIndexPage.clickEditLink(defectTrackerName)
                .setName(replacementName)
                .clickSaveDefectTracker();

		assertTrue("Success message error.",defectTrackerIndexPage.getSuccessMessage().contains("Successfully edited tracker " + replacementName));
        assertTrue("The defectTracker was not present in the table.",defectTrackerIndexPage.isTextPresentInDefectTrackerTableBody(replacementName));
    }

    @Test
    public void switchDefectTrackersTest() {
        String defectTracker1 = "testSwitchDefectTracker1" + getRandomString(3);
        String defectTracker2 = "testSwitchDefectTracker2" + getRandomString(3);
        String defectTrackerType = "Bugzilla";

        String teamName = "bugzillaAAttachTestTeam" + getRandomString(3);
        String appName = "bugzillaAttachTestApp" + getRandomString(3);

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user","password")
                .clickDefectTrackersLink();

        defectTrackerIndexPage = defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(defectTracker1)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        defectTrackerIndexPage = defectTrackerIndexPage.clickDefectTrackersLink()
                .clickAddDefectTrackerButton()
                .setName(defectTracker2)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        ApplicationDetailPage applicationDetailPage = defectTrackerIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .addDefectTracker(defectTracker1, BUGZILLA_USERNAME, BUGZILLA_PASSWORD, BUGZILLAPROJECTNAME);

        assertTrue("Defect tracker wasn't attached correctly",
                applicationDetailPage.clickEditDeleteBtn().isDefectTrackerAttached());
        //assertTrue("Defect Tracker wasn't attached correctly",applicationDetailPage.getDefectTrackerName().contains(defectTracker1));

        applicationDetailPage = applicationDetailPage.clickModalCancel();
        sleep(500);
        applicationDetailPage.addDefectTracker(defectTracker2, BUGZILLA_USERNAME, BUGZILLA_PASSWORD, BUGZILLAPROJECTNAME);

        assertTrue("Defect tracker wasn't attached correctly",
                applicationDetailPage.clickEditDeleteBtn().isDefectTrackerAttached());
        //assertTrue("Defect Tracker wasn't attached correctly",applicationDetailPage.getDefectTrackerName().contains(defectTracker2));
    }

	@Test
	public void attachBugzillaTrackerTest() {
		String defectTrackerName = "attachAppBugzilla" + getRandomString(3);
		String defectTrackerType = "Bugzilla";
		String teamName = "bugzillaAttachTestTeam" + getRandomString(3);
		String appName = "bugzillaAttachTestApp" + getRandomString(3);

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user", "password")
                .clickDefectTrackersLink();

		defectTrackerIndexPage = defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(defectTrackerName)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        ApplicationDetailPage applicationDetailPage = defectTrackerIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
				.clickViewAppLink(appName, teamName)
				.addDefectTracker(defectTrackerName, BUGZILLA_USERNAME, BUGZILLA_PASSWORD, BUGZILLAPROJECTNAME);

		assertTrue("Defect tracker wasn't attached correctly",
				applicationDetailPage.clickEditDeleteBtn().isDefectTrackerAttached());
	}

    @Test
    public void deleteAttachedBugzillaTrackerTest() {
        String defectTrackerName = getRandomString(8);
        String defectTrackerType = "Bugzilla";
        String teamName = getRandomString(8);
        String appName = getRandomString(8);

        DatabaseUtils.createTeam(teamName);
        DatabaseUtils.createApplication(teamName, appName);

        DefectTrackerIndexPage defectTrackerIndexPage = loginPage.login("user", "password")
                .clickDefectTrackersLink();

        defectTrackerIndexPage = defectTrackerIndexPage.clickAddDefectTrackerButton()
                .setName(defectTrackerName)
                .setType(defectTrackerType)
                .setURL(BUGZILLA_URL)
                .clickSaveDefectTracker();

        ApplicationDetailPage applicationDetailPage = defectTrackerIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName)
                .addDefectTracker(defectTrackerName, BUGZILLA_USERNAME, BUGZILLA_PASSWORD, BUGZILLAPROJECTNAME);

        defectTrackerIndexPage = applicationDetailPage.clickDefectTrackersLink()
                .clickEditLink(defectTrackerName)
                .clickDeleteButton()
                .clickDefectTrackersLink();

        assertFalse("The defectTracker was still present after attempted deletion.",
                defectTrackerIndexPage.isElementPresent("defectTackerName" + defectTrackerName));
    }
}
