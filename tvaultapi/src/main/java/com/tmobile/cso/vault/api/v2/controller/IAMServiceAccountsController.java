/** *******************************************************************************
*  Copyright 2020 T-Mobile, US
*
*  Licensed under the Apache License, Version 2.0 (the "License");
*  you may not use this file except in compliance with the License.
*  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software
*  distributed under the License is distributed on an "AS IS" BASIS,
*  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*  See the License for the specific language governing permissions and
*  limitations under the License.
*  See the readme.txt file for additional language around disclaimer of warranties.
*********************************************************************************** */
package com.tmobile.cso.vault.api.v2.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import com.tmobile.cso.vault.api.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tmobile.cso.vault.api.exception.TVaultValidationException;
import com.tmobile.cso.vault.api.service.IAMServiceAccountsService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import springfox.documentation.annotations.ApiIgnore;

@RestController
@CrossOrigin
@Api( description = "Manage IAM Service Account and Secrets", position = 20)
public class IAMServiceAccountsController {

	@Autowired
	private IAMServiceAccountsService iamServiceAccountsService;
	
	private static final String USER_DETAILS_STRING="UserDetails";

	/**
	 * Onbaords a IAM service account for password rotation
	 * @param request
	 * @param token
	 * @param iamServiceAccount
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.onboardIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.onboardIAMServiceAccount.notes}")
	@PostMapping(value="/v2/iamserviceaccounts/onboard", produces="application/json")
	public ResponseEntity<String> onboardIAMServiceAccount( HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody @Valid IAMServiceAccount iamServiceAccount ){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.onboardIAMServiceAccount(token, iamServiceAccount, userDetails);
	}

	/**
	 * Transfers an IAM service account from one owner to another
	 * @param request
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.updateIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.updateIAMServiceAccount.notes}")
	@PostMapping(value="/v2/iamserviceaccounts/update", produces="application/json")
	public ResponseEntity<String> updateIAMServiceAccount( HttpServletRequest request, @RequestHeader(value="vault-token") String token,
																  @RequestBody @Valid IAMServiceAccountTransfer iamServiceAccountTransfer) throws IOException {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.updateIAMServiceAccount(token, userDetails, iamServiceAccountTransfer);
	}

	/**
	 * Add user to IAM service account.
	 * @param iamServiceAccountUser
	 * @returnC
	 */
	@PostMapping(value="/v2/iamserviceaccounts/user",produces="application/json")
	@ApiOperation(value = "${IAMServiceAccountsController.addUserToIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.addUserToIAMServiceAccount.notes}")
	public ResponseEntity<String> addUserToIAMServiceAccount(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody @Valid IAMServiceAccountUser iamServiceAccountUser){
	   UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
	   return iamServiceAccountsService.addUserToIAMServiceAccount(token, userDetails, iamServiceAccountUser, false);
	}

	/**
	 * Add Group to IAM service account.
	 * @param request
	 * @param token
	 * @param iamServiceAccountGroup
	 * @return
	 */
	@PostMapping(value="/v2/iamserviceaccounts/group",produces="application/json")
	@ApiOperation(value = "${IAMServiceAccountsController.addGroupToIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.addGroupToIAMServiceAccount.notes}")
	public ResponseEntity<String> addGroupToIAMServiceAccount(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody @Valid IAMServiceAccountGroup iamServiceAccountGroup){
	   UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
	   return iamServiceAccountsService.addGroupToIAMServiceAccount(token, iamServiceAccountGroup, userDetails, false);
	}
	
	/**
	 * Gets the list of iam service accounts onboarded for password rotation
	 * @param request
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.getOnboardedIAMServiceAccounts.value}", notes = "${IAMServiceAccountsController.getOnboardedIAMServiceAccounts.notes}")
	@GetMapping(value="/v2/iamserviceaccounts", produces="application/json")
	public ResponseEntity<String> getOnboardedIAMServiceAccounts(HttpServletRequest request, @RequestHeader(value="vault-token") String token){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.getOnboardedIAMServiceAccounts(token, userDetails);
	}
	
	/**
	 * Gets the list of all IAM Service accounts onbaorded in t-vault. Works only for Admins
	 * @param request
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.listAllOnboardedIAMServiceAccounts.value}", notes = "${IAMServiceAccountsController.listAllOnboardedIAMServiceAccounts.notes}")
	@GetMapping(value="/v2/iamserviceaccounts/all", produces="application/json")
	public ResponseEntity<String> getAllOnboardedIAMServiceAccounts(HttpServletRequest request, @RequestHeader(value="vault-token") String token){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.listAllOnboardedIAMServiceAccounts(token, userDetails);
	}
	
	/**
	 * Get the list of iam service accounts for users with permissions.
	 * @param request
	 * @param token
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.getIAMServiceAccountsList.value}", notes = "${IAMServiceAccountsController.getIAMServiceAccountsList.notes}",hidden = false)
	@GetMapping (value="/v2/iamserviceaccounts/list",produces="application/json")
	public ResponseEntity<String> getIAMServiceAccountsList(HttpServletRequest request, @RequestHeader(value="vault-token") String token){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.getIAMServiceAccountsList(userDetails, token);
	}
	
	/**
	 * Get iam service account detail from metadata
	 * @param request
	 * @param token
	 * @param iamsvcname
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.getIAMServiceAccountDetail.value}", notes = "${IAMServiceAccountsController.getIAMServiceAccountDetail.notes}", hidden = true)
	@GetMapping(value = "/v2/iamserviceaccounts/{iam_svc_name:.+}", produces = "application/json")
	public ResponseEntity<String> getIAMServiceAccountDetail(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token, @PathVariable("iam_svc_name") String iamsvcname){
		return iamServiceAccountsService.getIAMServiceAccountDetail(token, iamsvcname);
	}
	
	
	/**
	 * Get iam service account detail with secretkey
	 * @param request
	 * @param token
	 * @param iamsvcname
	 * @param folderName
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.getIAMServiceAccountSecretKey.value}", notes = "${IAMServiceAccountsController.getIAMServiceAccountSecretKey.notes}", hidden = true)
	@GetMapping(value = "/v2/iamserviceaccounts/secrets/{iam_svc_name}/{folderName}", produces = "application/json")
	public ResponseEntity<String> getIAMServiceAccountSecretKey(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token, @PathVariable("iam_svc_name") String iamsvcname,
			@PathVariable("folderName") String folderName) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.getIAMServiceAccountSecretKey(userDetails.isAdmin()?userDetails.getSelfSupportToken():token, iamsvcname, folderName);
	}


	/**
	 * Read secrets from vault
	 * @param token
	 * @param path
	 * @param fetchOption
	 * @return
	 * @throws IOException 
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.readFolders.value}", notes = "${IAMServiceAccountsController.readFolders.notes}", hidden = true)
	@GetMapping(value = "/v2/iamserviceaccounts/folders/secrets", produces = "application/json")
	public ResponseEntity<String> readFolders(HttpServletRequest request, @RequestHeader(value = "vault-token") String token,
			@RequestParam("path") String path) throws IOException {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.readFolders(userDetails.isAdmin()? userDetails.getSelfSupportToken():token, path);
	}

	/**
	 * Removes permission for a user from the IAM service account
	 * @param request
	 * @param token
	 * @param iamServiceAccountUser
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.removeUserFromIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.removeUserFromIAMServiceAccount.notes}")
	@DeleteMapping(value="/v2/iamserviceaccounts/user", produces="application/json")
	public ResponseEntity<String> removeUserFromIAMServiceAccount( HttpServletRequest request, @RequestHeader(value="vault-token") String token, @Valid @RequestBody IAMServiceAccountUser iamServiceAccountUser ){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.removeUserFromIAMServiceAccount(token, iamServiceAccountUser, userDetails);
	}

	/**
	 * Removes permission for a group from the IAM service account
	 * @param request
	 * @param token
	 * @param iamServiceAccountGroup
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.removeGroupFromIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.removeGroupFromIAMServiceAccount.notes}")
	@DeleteMapping(value="/v2/iamserviceaccounts/group", produces="application/json")
	public ResponseEntity<String> removeGroupFromIAMServiceAccount( HttpServletRequest request, @RequestHeader(value="vault-token") String token, @Valid @RequestBody IAMServiceAccountGroup iamServiceAccountGroup ){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.removeGroupFromIAMServiceAccount(token, iamServiceAccountGroup, userDetails);
	}

	/**
	 * Add approle to IAM Service Account
	 *
	 * @param request
	 * @param token
	 * @param iamServiceAccountApprole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.associateApproletoIAMSvcAcc.value}", notes = "${IAMServiceAccountsController.associateApproletoIAMSvcAcc.notes}")
	@PostMapping(value = "/v2/iamserviceaccounts/approle", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> associateApproletoIAMSvcAcc(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token,
			@Valid @RequestBody IAMServiceAccountApprole iamServiceAccountApprole) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.associateApproletoIAMsvcacc(userDetails, token, iamServiceAccountApprole);
	}

	/**
	 * Remove approle from IAM Service Account
	 *
	 * @param request
	 * @param token
	 * @param iamServiceAccountApprole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.removeApproleFromIAMSvcAcc.value}", notes = "${IAMServiceAccountsController.removeApproleFromIAMSvcAcc.notes}")
	@DeleteMapping(value = "/v2/iamserviceaccounts/approle", consumes = "application/json", produces = "application/json")
	public ResponseEntity<String> removeApproleFromIAMSvcAcc(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token,
			@Valid @RequestBody IAMServiceAccountApprole iamServiceAccountApprole) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.removeApproleFromIAMSvcAcc(userDetails, token, iamServiceAccountApprole);
	}

	/**
	 * Activate IAM Service Account.
	 * @param request
	 * @param token
	 * @param iamServiceAccountName
	 * @param awsAccountId
	 * @return
	 */
	@ApiIgnore
	@PostMapping(value="/v2/iamserviceaccount/activate",produces="application/json")
	@ApiOperation(value = "${IAMServiceAccountsController.activateIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.activateIAMServiceAccount.notes}", hidden = true)
	public ResponseEntity<String> activateIAMServiceAccount(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestParam("serviceAccountName" ) String iamServiceAccountName, @RequestParam("awsAccountId" ) String awsAccountId){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.activateIAMServiceAccount(token, userDetails, iamServiceAccountName, awsAccountId);
	}

	/**
	 * Rotate IAM Service account secret by accessKeyId.
	 * @param request
	 * @param token
	 * @param iamServiceAccountRotateRequest
	 * @return
	 */
	@PostMapping(value="/v2/iamserviceaccount/rotate",produces="application/json")
	@ApiOperation(value = "${IAMServiceAccountsController.rotateIAMServiceAccountCreds.value}", notes = "${IAMServiceAccountsController.rotateIAMServiceAccountCreds.notes}")
	public ResponseEntity<String> rotateIAMServiceAccountCreds(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody @Valid IAMServiceAccountRotateRequest iamServiceAccountRotateRequest){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.rotateIAMServiceAccount(token, iamServiceAccountRotateRequest, userDetails);
	}
	
	/**
	 * Read Secrets
	 * @param token
	 * @param awsAccountID
	 * @param iamSvcName
	 * @return
	 * @throws IOException 
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.readSecrets.value}", notes = "${IAMServiceAccountsController.readSecrets.notes}", hidden = false)
	@GetMapping(value = "/v2/iamserviceaccounts/secrets/{aws_account_id}/{iam_svc_name}/{accessKey}", produces = "application/json")
	public ResponseEntity<String> readSecrets(HttpServletRequest request, @RequestHeader(value = "vault-token") String token,
			@PathVariable("aws_account_id") String awsAccountID, @PathVariable("iam_svc_name") String iamSvcName,
			@PathVariable("accessKey") String accessKey) throws IOException {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.readSecrets(token, awsAccountID, iamSvcName, accessKey, userDetails);
	}
	
	/**
	 * Offboard IAM service account.
	 * @param request
	 * @param token
	 * @param iamServiceAccount
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.offboardIAMServiceAccount.value}", notes = "${IAMServiceAccountsController.offboardIAMServiceAccount.notes}")
	@PostMapping(value="/v2/iamserviceaccounts/offboard", produces="application/json")
	public ResponseEntity<String> offboardIAMServiceAccount( HttpServletRequest request, @RequestHeader(value="vault-token") String token, @Valid @RequestBody IAMServiceAccountOffboardRequest iamServiceAccount ){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.offboardIAMServiceAccount(token, iamServiceAccount, userDetails);
	}
	
	/**
	 * Method to create an aws app role
	 * @param token
	 * @param awsLoginRole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.createAWSRole.value}", notes = "${IAMServiceAccountsController.createAWSRole.notes}")
	@PostMapping(value="/v2/iamserviceaccounts/aws/role",consumes="application/json",produces="application/json")
	public ResponseEntity<String> createAWSRole(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody AWSLoginRole awsLoginRole) throws TVaultValidationException {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.createAWSRole(userDetails, token, awsLoginRole);
	}
	
	/**
	 * Method to create aws iam role
	 * @param token
	 * @param awsiamRole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.createIamRole.value}", notes = "${IAMServiceAccountsController.createIamRole.notes}")
	@PostMapping(value="/v2/iamserviceaccounts/aws/iam/role",produces="application/json")
	public ResponseEntity<String> createIAMRole(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody AWSIAMRole awsiamRole) throws TVaultValidationException{
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.createIAMRole(userDetails, token, awsiamRole);
	}
	
	/**
	 * Adds AWS role to IAM Service Account
	 * 
	 * @param token
	 * @param iamServiceAccountAWSRole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.addAwsRoleToIAMSvcacc.value}", notes = "${IAMServiceAccountsController.addAwsRoleToIAMSvcacc.notes}")
	@PostMapping(value = "/v2/iamserviceaccounts/role", produces = "application/json")
	public ResponseEntity<String> addAwsRoleToIAMSvcacc(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token,
			@Valid @RequestBody IAMServiceAccountAWSRole iamServiceAccountAWSRole) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.addAwsRoleToIAMSvcacc(userDetails, token, iamServiceAccountAWSRole);
	}

	/**
	 * Remove AWS role from IAM Service Account
	 * 
	 * @param token
	 * @param iamServiceAccountAWSRole
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.removeAWSRoleFromIAMSvcacc.value}", notes = "${IAMServiceAccountsController.removeAWSRoleFromIAMSvcacc.notes}")
	@DeleteMapping(value = "/v2/iamserviceaccounts/role",  produces = "application/json")
	public ResponseEntity<String> removeAWSRoleFromIAMSvcacc(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token,
			@RequestBody @Valid IAMServiceAccountAWSRole iamServiceAccountAWSRole) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.removeAWSRoleFromIAMSvcacc(userDetails, token, iamServiceAccountAWSRole);
	}

	/**
	 * Delete IAM Service account access key and secret.
	 * @param request
	 * @param token
	 * @param iamServiceAccountAccessKey
	 * @return
	 */
	@DeleteMapping(value="/v2/iamserviceaccount/secrets/keys", produces="application/json")
	@ApiOperation(value = "${IAMServiceAccountsController.deleteIAMServiceAccountCreds.value}", notes = "${IAMServiceAccountsController.deleteIAMServiceAccountCreds.notes}")
	public ResponseEntity<String> deleteIAMServiceAccountCreds(HttpServletRequest request, @RequestHeader(value="vault-token") String token, @RequestBody @Valid IAMServiceAccountAccessKey iamServiceAccountAccessKey){
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.deleteIAMServiceAccountCreds(userDetails, token, iamServiceAccountAccessKey);
	}

	/**
	 * Write AccessKey/SecertKey for a give IAM Service Account. Need to be called from external Apps
	 * @param token
	 * @param awsAccountID
	 * @param iamSvcName
	 * @param iamServiceAccountSecret
	 * @return
	 * @throws IOException
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.writeKeys.value}", notes = "${IAMServiceAccountsController.writeKeys.notes}", hidden = false)
	@PostMapping(value = "/v2/iam/iamserviceaccounts/keys", produces = "application/json")
	public ResponseEntity<String> writeIAMKey(@RequestHeader(value = "vault-token") String token, @Valid @RequestBody IAMServiceAccountKey iamServiceAccountKey) throws IOException {
		IAMServiceAccountSecret iamServiceAccountSecret = new IAMServiceAccountSecret();
		iamServiceAccountSecret.setAccessKeyId(iamServiceAccountKey.getAccessKeyId());
		iamServiceAccountSecret.setAccessKeySecret(iamServiceAccountKey.getAccessKeySecret());
		iamServiceAccountSecret.setExpiryDateEpoch(iamServiceAccountKey.getExpiryDateEpoch());
		iamServiceAccountSecret.setUserName(iamServiceAccountKey.getUserName());
		iamServiceAccountSecret.setAwsAccountId(iamServiceAccountKey.getAwsAccountId());
		iamServiceAccountSecret.setExpiryDate(iamServiceAccountKey.getExpiryDate());
		iamServiceAccountSecret.setCreateDate(iamServiceAccountKey.getCreateDate());
		iamServiceAccountSecret.setStatus(iamServiceAccountKey.getStatus());
		return iamServiceAccountsService.writeIAMKey(token, iamServiceAccountSecret);
	}
	
	/**
	 * Method to get the list of access keys for an IAM service account
	 * @param request
	 * @param token
	 * @param awsAccountId
	 * @param iamSvcName
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.getListOfIAMServiceAccountAccessKeys.value}", notes = "${IAMServiceAccountsController.getListOfIAMServiceAccountAccessKeys.notes}")
	@GetMapping(value = "/v2/iamserviceaccounts/{aws_account_id}/{iam_svc_name}/keys", produces = "application/json")
	public ResponseEntity<String> getListOfIAMServiceAccountAccessKeys(HttpServletRequest request,
			@RequestHeader(value = "vault-token") String token, @PathVariable("aws_account_id") String awsAccountId,
			@PathVariable("iam_svc_name") String iamSvcName) {
		return iamServiceAccountsService.getListOfIAMServiceAccountAccessKeys(token, iamSvcName, awsAccountId);
	}

	/**
	 * To create access key - access key secret pair for IAM Service Account.
	 * @param request
	 * @param token
	 * @param awsAccountId
	 * @param iamSvcName
	 * @return
	 */
	@ApiOperation(value = "${IAMServiceAccountsController.createAccessKeys.value}", notes = "${IAMServiceAccountsController.createAccessKeys.notes}")
	@PostMapping(value = "/v2/iamserviceaccounts/{aws_account_id}/{iam_svc_name}/keys", produces = "application/json")
	public ResponseEntity<String> createAccessKeys(HttpServletRequest request,
														@RequestHeader(value = "vault-token") String token,
			@PathVariable("aws_account_id") String awsAccountId, @PathVariable("iam_svc_name") String iamSvcName) {
		UserDetails userDetails = (UserDetails) request.getAttribute(USER_DETAILS_STRING);
		return iamServiceAccountsService.createAccessKeys(userDetails, token, iamSvcName, awsAccountId);
	}

}
