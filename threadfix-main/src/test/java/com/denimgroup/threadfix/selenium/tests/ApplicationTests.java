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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.denimgroup.threadfix.data.entities.Application;
import com.denimgroup.threadfix.selenium.pages.ApplicationDetailPage;
import com.denimgroup.threadfix.selenium.pages.TeamIndexPage;
import com.denimgroup.threadfix.selenium.pages.WafRulesPage;
import com.denimgroup.threadfix.selenium.pages.WafIndexPage;

public class ApplicationTests extends BaseTest {

	private ApplicationDetailPage applicationDetailPage;
	private TeamIndexPage teamIndexPage;
	private WafIndexPage wafIndexPage;
	private WafRulesPage wafDetailPage;

	@Before
	public void init() {
		super.init();

        teamIndexPage = loginPage.login("user", "password")
                .clickOrganizationHeaderLink();
	}

	@Test 
	public void testCreateBasicApplicationDisplayedTeamIndexPage() {
		String teamName = "testCreateBasicApplicationTeam" + getRandomString(3);
		String appName = "testCreateBasicApplicationApp" + getRandomString(3);
		String urlText = "http://testurl.com";

        //Create Team & Application
        teamIndexPage = teamIndexPage.clickAddTeamButton()
				.setTeamName(teamName)
				.addNewTeam()
				.expandTeamRowByName(teamName)
				.addNewApplication(teamName, appName, urlText, "Low")
				.saveApplication(teamName);

        assertTrue("The application was not added properly.", teamIndexPage.isAppPresent(appName));
	}

    @Test
    public void testCreateBasicApplicationDisplayedApplicationDetailPage() {
        String teamName = "testCreateBasicApplicationTeam" + getRandomString(3);
        String appName = "testCreateBasicApplicationApp" + getRandomString(3);
        String urlText = "http://testurl.com";

        //Create Team & Application
        teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(teamName)
                .addNewTeam()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName, urlText, "Low")
                .saveApplication(teamName);

        //Navigate to Application Detail Page
        applicationDetailPage = teamIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName);

        assertTrue("The name was not preserved correctly on Application Detail Page.",
                applicationDetailPage.getNameText().contains(appName));
    }

    //Validation Test
	@Test 
	public void testCreateBasicApplicationValidation() {
        String teamName = "testCreateBasicApplicationValidationTeam" + getRandomString(3);
		String appName;
		String urlText = "htnotaurl.com";
		
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < Application.NAME_LENGTH + 50; i++) { stringBuilder.append('i'); }
		String longInputName = stringBuilder.toString();
		
		stringBuilder = new StringBuilder("");
		for (int i = 0; i < Application.URL_LENGTH + 50; i++) { stringBuilder.append('i'); }
		String longInputUrl = "http://" + stringBuilder.toString();
		
		String emptyError = "This field cannot be blank";
        String notValidURl = "Not a valid URL";
		
		String emptyString = "";
		String whiteSpace = "     ";
		
		//Team & Application set up...hopefully to be removed later
		teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(teamName)
                .addNewTeam()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, emptyString, emptyString, "Low")
                .saveApplicationInvalid(teamName);
		
		assertTrue("The correct error did not appear for the name field.",
                teamIndexPage.getNameErrorMessage().contains(emptyError));
		
		teamIndexPage = teamIndexPage.clickCloseAddAppModal(teamName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, whiteSpace, whiteSpace, "Low")
                .saveApplicationInvalid(teamName);

		assertTrue("The correct error did not appear for the name field.",
                teamIndexPage.getNameErrorMessage().contains(emptyError));

		assertTrue("The correct error did not appear for the url field.", 
				teamIndexPage.getUrlErrorMessage().contains(notValidURl));
		
		// Test URL format
		teamIndexPage = teamIndexPage.clickCloseAddAppModal(teamName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, "dummyApp", urlText, "Low")
                .saveApplicationInvalid(teamName);
		
		assertTrue("The correct error did not appear for the url field.", 
				teamIndexPage.getUrlErrorMessage().contains(notValidURl));

		// Test browser field length limits
		applicationDetailPage = teamIndexPage.clickCloseAddAppModal(teamName)
				.clickOrganizationHeaderLink()
				.expandTeamRowByName(teamName)
				.addNewApplication(teamName, longInputName, longInputUrl, "Low")
				.saveApplication(teamName)
				.clickOrganizationHeaderLink()
				.expandTeamRowByName(teamName)
				.clickViewAppLink("iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii",teamName);

		assertTrue("The length limit was incorrect for name.",
				applicationDetailPage.getNameText().length() == Application.NAME_LENGTH);
		
		appName = applicationDetailPage.getNameText();
		
		// Test name duplication check
		teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName, "http://dummyurl", "Low")
                .saveApplicationInvalid(teamName);

        //Is this even a good?
		assertTrue("The duplicate message didn't appear correctly.", 
				teamIndexPage.getNameErrorMessage().contains("That name is already taken."));
	}

	@Test
	public void testEditBasicApplicationDisplayedApplicationDetailPage() {
		String teamName = "testCreateBasicApplicationTeam" + getRandomString(3);
		String appName1 = "testCreateBasicApplicationApp" + getRandomString(3);
		String urlText1 = "http://testurl.com";
		String appName2 = "testCreateBasicApplicationApp" + getRandomString(3);
		String urlText2 = "http://testurl.com352";

		// set up an organization
        teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(teamName)
				.addNewTeam()
				.expandTeamRowByName(teamName)
				.addNewApplication(teamName, appName1, urlText1, "Low")
				.saveApplication(teamName);

		applicationDetailPage = teamIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
				.clickViewAppLink(appName1, teamName);

		applicationDetailPage = applicationDetailPage.clickEditDeleteBtn()
                .setNameInput(appName2)
				.setUrlInput(urlText2)
				.clickUpdateApplicationButton();
		
		assertTrue("The name was not preserved correctly on Application Detail Page.",
                appName2.equals(applicationDetailPage.getNameText()));

        applicationDetailPage = applicationDetailPage.clickEditDeleteBtn();
	    assertTrue("The URL was not edited correctly.", applicationDetailPage.getUrlText().contains(urlText2));
	}

    @Test
    public void testEditBasicApplicationDisplayedTeamIndexPage() {
        String teamName = "testCreateBasicApplicationTeam" + getRandomString(3);
        String appName1 = "testCreateBasicApplicationApp" + getRandomString(3);
        String urlText1 = "http://testurl.com";
        String appName2 = "testCreateBasicApplicationApp" + getRandomString(3);
        String urlText2 = "http://testurl.com352";

        // set up an organization
        teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(teamName)
                .addNewTeam()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName1, urlText1, "Low")
                .saveApplication(teamName);

        applicationDetailPage = teamIndexPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName)
                .clickViewAppLink(appName1, teamName);

        applicationDetailPage = applicationDetailPage.clickEditDeleteBtn()
                .setNameInput(appName2)
                .setUrlInput(urlText2)
                .clickUpdateApplicationButton();

        // ensure that the application is present in the organization's app table.
        teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName);

        assertTrue("The edited application does not appear on Team Index Page.", teamIndexPage.isAppPresent(appName2));
    }

	//Validation Test
	@Test
	public void testEditBasicApplicationValidation() {
        String teamName = "testEditBasicApplicationValidationTeam" + getRandomString(3);
		String appName2 = "testApp23";
		String appName = "testApp17";
		String validUrlText = "http://test.com";
		String urlText = "htnotaurl.com";
		
		StringBuilder stringBuilder = new StringBuilder("");
		for (int i = 0; i < Application.NAME_LENGTH + 50; i++) { stringBuilder.append('i'); }
		String longInputName = stringBuilder.toString();
		
		stringBuilder = new StringBuilder("");
		for (int i = 0; i < Application.URL_LENGTH + 50; i++) { stringBuilder.append('i'); }
		String longInputUrl = "http://" + stringBuilder.toString();
		
		String emptyError = "This field cannot be blank";
		
		String emptyString = "";
		String whiteSpace = "     ";
		
		//set up an organization,
		//add an application for duplicate checking,
		//add an application for normal testing,
		// and Test a submission with no changes
		teamIndexPage = teamIndexPage.clickAddTeamButton()
                .setTeamName(teamName)
                .addNewTeam()
                .expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName2, validUrlText, "Low")
                .saveApplication(teamName)
                .clickOrganizationHeaderLink();

        teamIndexPage = teamIndexPage.expandTeamRowByName(teamName)
                .addNewApplication(teamName, appName, validUrlText, "Low")
                .saveApplication(teamName)
                .clickOrganizationHeaderLink();

        applicationDetailPage = teamIndexPage.expandTeamRowByName(teamName)
                .clickViewAppLink(appName, teamName);

		// Test blank input
		applicationDetailPage = applicationDetailPage.clickEditDeleteBtn()
                .setNameInput(emptyString)
                .setUrlInput(emptyString)
                .clickUpdateApplicationButtonInvalid();
		
		assertTrue("The correct error did not appear for the name field.", 
				applicationDetailPage.getNameError().equals(emptyError));
		
		// Test whitespace input
		applicationDetailPage = applicationDetailPage.setNameInput(whiteSpace)
                .setUrlInput(whiteSpace)
                .clickUpdateApplicationButtonInvalid();

		assertTrue("The correct error did not appear for the name field.",
                applicationDetailPage.getNameError().equals(emptyError));
		assertTrue("The correct error did not appear for the url field.",
				applicationDetailPage.getUrlError().equals("Not a valid URL"));
		
		// Test URL format
		applicationDetailPage = applicationDetailPage.setNameInput("dummyName")
                .setUrlInput(urlText)
                .clickUpdateApplicationButtonInvalid();

		assertTrue("The correct error did not appear for the url field.",
				applicationDetailPage.getUrlError().equals("Not a valid URL"));

		// Test name duplication check
		applicationDetailPage = applicationDetailPage.setNameInput(appName2)
                .setUrlInput("")
                .clickUpdateApplicationButtonInvalid();

		assertTrue("The duplicate message didn't appear correctly.", 
				applicationDetailPage.getNameError().equals("That name is already taken."));

		// Test browser field length limits
		applicationDetailPage = applicationDetailPage.setNameInput(longInputName)
                .setUrlInput(longInputUrl)
                .clickUpdateApplicationButton()
                .clickEditDeleteBtn();

        //Is this even good?
		assertTrue("The length limit was incorrect for name.", 
				applicationDetailPage.getNameText().length() == Application.NAME_LENGTH);
	}

	@Test
	public void testAddWafAtApplicationCreationTimeAndDelete() {
		String wafName = "appCreateTimeWaf1";
		String type = "Snort";
		String orgName = "appCreateTimeWafOrg2";
		String appName = "appCreateTimeWafName2";
		String appUrl = "http://testurl.com";
		
		wafIndexPage = teamIndexPage.clickWafsHeaderLink()
                .clickAddWafLink()
                .createNewWaf(wafName, type)
                .clickCreateWaf();

        teamIndexPage = wafIndexPage.clickOrganizationHeaderLink()
                .clickAddTeamButton()
                .setTeamName(orgName)
                .addNewTeam();

		// Add Application with WAF
		applicationDetailPage = teamIndexPage.expandTeamRowByName(orgName)
                .addNewApplication(orgName, appName, appUrl, "Low")
                .saveApplication(orgName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(orgName)
                .clickViewAppLink(appName, orgName)
                .clickEditDeleteBtn()
                .clickAddWaf()
                .addWaf(wafName);

		// Check that it also appears on the WAF page.
		wafDetailPage = applicationDetailPage.clickOrganizationHeaderLink()
                .clickWafsHeaderLink()
                .clickRules(wafName);
		
		assertTrue("The WAF was not added correctly.", 
				wafDetailPage.isTextPresentInApplicationsTableBody(appName));
		
		// Attempt to delete the WAF and ensure that it is a failure because the Application is still there
		// If the page goes elsewhere, this call will fail.
		wafIndexPage = wafDetailPage.clickOrganizationHeaderLink()
                .clickWafsHeaderLink()
                .clickDeleteWaf(wafName);
		
		// Delete app and org and make sure the Application doesn't appear in the WAFs table.
		wafDetailPage = wafIndexPage.clickCloseWafModal(wafName)
                .clickOrganizationHeaderLink()
                .clickViewTeamLink(orgName)
                .clickDeleteButton()
                .clickWafsHeaderLink()
                .clickRules(wafName);
		
		assertFalse("The Application was not removed from the WAF correctly.", 
				wafDetailPage.isTextPresentInApplicationsTableBody(appName));
		
		loginPage = wafDetailPage.clickWafsHeaderLink().clickDeleteWaf(wafName).logout();
		
	}

	@Test
	public void testSwitchWafs() {
		//TODO
		String wafName1 = "firstWaf" + getRandomString(3);
		String wafName2 = "secondWaf" + getRandomString(3);
		String type1 = "Snort" ;
		String type2 = "mod_security";
		String orgName = "testSwitchWafs" + getRandomString(3);
		String appName = "switchWafApp" + getRandomString(3);
		String appUrl = "http://testurl.com";
		
		//Create two WAFs
        wafIndexPage = teamIndexPage.clickWafsHeaderLink()
                .clickAddWafLink()
                .createNewWaf(wafName1, type1)
                .clickCreateWaf()
                .clickWafsHeaderLink()
                .clickAddWafLink()
                .createNewWaf(wafName2, type2)
                .clickCreateWaf();

        //Create team & application
        teamIndexPage = wafIndexPage.clickOrganizationHeaderLink()
                .clickAddTeamButton()
                .setTeamName(orgName)
                .addNewTeam()
                .expandTeamRowByName(orgName)
                .addNewApplication(orgName, appName, appUrl, "Low")
                .saveApplication(orgName)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(orgName);

        //Create second application
        applicationDetailPage = teamIndexPage.clickViewAppLink(appName,orgName)
                .clickEditDeleteBtn()
				.clickAddWaf()
				.addWaf(wafName1);

        teamIndexPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(orgName);

        applicationDetailPage = teamIndexPage.clickViewAppLink(appName, orgName)
                .clickEditDeleteBtn()
				.clickEditWaf()
                .addWaf(wafName2)
                .clickOrganizationHeaderLink()
                .expandTeamRowByName(orgName)
                .clickViewAppLink(appName, orgName)
                .clickEditDeleteBtn();
								
		assertTrue("The edit didn't change the application's WAF.",
                applicationDetailPage.getWafText().contains(wafName2));
	}

	@Test
	public void sameAppNameMultipleTeams(){
		String appName = getRandomString(8);
		String teamName1 = getRandomString(8);
		String teamName2 = getRandomString(8);

        //Set up two teams
		teamIndexPage = teamIndexPage.clickAddTeamButton()
				.setTeamName(teamName1)
				.addNewTeam()
				.clickOrganizationHeaderLink()
				.clickAddTeamButton()
				.setTeamName(teamName2)
				.addNewTeam();

        //Add an app with same name to both teams
        applicationDetailPage = teamIndexPage.expandTeamRowByName(teamName1)
				.addNewApplication(teamName1, appName, "", "Low")
				.saveApplication(teamName1)
				.expandTeamRowByName(teamName2)
				.addNewApplication(teamName2, appName, "", "Low")
				.saveApplication(teamName2)
				.clickOrganizationHeaderLink()
				.expandTeamRowByName(teamName1)
				.clickViewAppLink(appName,teamName1);
		
		Boolean isAppInTeam1 = applicationDetailPage.getNameText().contains(appName);
		
		applicationDetailPage = applicationDetailPage.clickOrganizationHeaderLink()
                .expandTeamRowByName(teamName2)
				.clickViewAppLink(appName, teamName2);
		
		Boolean isAppInTeam2  = applicationDetailPage.getNameText().contains(appName);

		assertTrue("Unable to add apps with the same name to different teams", isAppInTeam1 && isAppInTeam2);
	}
	
	public void sleep(int num) {
		try {
			Thread.sleep(num);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
