/** Copyright 2015 BlackBerry, Limited.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */

package com.blackberry.bdp.dwauth.ldap;

import com.google.common.cache.CacheBuilderSpec;
import io.dropwizard.util.Duration;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@SuppressWarnings("unused")
public class LdapConfiguration {

	/************************************************************************************************************
	* URI 
	************************************************************************************************************/
	@NotNull @Valid private String ldapServers = "ldap://www.example.com:389";

	public LdapConfiguration setLdapServers(String ldapServers) {
		this.ldapServers = ldapServers;
		return this;
	}
	
	public String getLdapServers() {
		return ldapServers;
	}	

	/************************************************************************************************************
	* Trust any secure host?
	************************************************************************************************************/
	@NotNull @Valid private boolean trustAnySecuredHost = false;

	public LdapConfiguration setTrustAnySecuredHost (boolean trustAnySecuredHost) {
		this.trustAnySecuredHost = trustAnySecuredHost;
		return this;
	}
	
	public boolean getTrustAnySecuredHost() {
		return trustAnySecuredHost;
	}	
	
	/************************************************************************************************************
	* Base DNs for looking up memberships
	************************************************************************************************************/
	@NotNull @Valid private String  groupBaseDN;

	public LdapConfiguration setGroupBaseDN (String groupBaseDNs) {
		this.groupBaseDN = groupBaseDNs;
		return this;
	}
	
	public String getGroupBaseDN() {
		return groupBaseDN;
	}	
	
	/************************************************************************************************************
	* The object name of the user object that identifies the username
	************************************************************************************************************/
	@NotNull @Valid private String userIdentifierObjectName = "sAMAccountName";
	
	public LdapConfiguration setUserIdentifierObjectName (String userIdentifierObjectName) {
		this.userIdentifierObjectName = userIdentifierObjectName;
		return this;
	}
	
	public String getUserIdentifierObjectName() {
		return userIdentifierObjectName;
	}	

	/************************************************************************************************************
	* The object name of an object that identifies a member
	************************************************************************************************************/
	@NotNull @Valid private String membershipIdentifierObjectName = "sAMAccountName";
	
	public LdapConfiguration setMembershipIdentifierObjectName (String membershipIdentifierObjectName) {
		this.membershipIdentifierObjectName = membershipIdentifierObjectName;
		return this;
	}
	
	public String getMembershipIdentifierObjectName() {
		return membershipIdentifierObjectName;
	}	
	
	/************************************************************************************************************
	* Cache Builder
	************************************************************************************************************/
	@NotNull @Valid private CacheBuilderSpec cachePolicy = CacheBuilderSpec.disableCaching();
	
	public CacheBuilderSpec getCachePolicy() {
		return cachePolicy;
	}

	public LdapConfiguration setCachePolicy(CacheBuilderSpec cachePolicy) {
		this.cachePolicy = cachePolicy;
		return this;
	}
	
	/************************************************************************************************************
	* User Filter
	************************************************************************************************************/
	@NotNull @NotEmpty private String userFilter = "ou=people,dc=example,dc=com";

	public String getUserFilter() {
		return userFilter;
	}

	public LdapConfiguration setUserFilter(String userFilter) {
		this.userFilter = userFilter;
		return this;
	}
	
	
	/************************************************************************************************************
	* Group FIllter
	************************************************************************************************************/
	/*
	@NotNull @NotEmpty private String groupFilter = "ou=groups,dc=example,dc=com";
	
	public String getGroupFilter() {
		return groupFilter;
	}

	public LdapConfiguration setGroupFilter(String groupFilter) {
		this.groupFilter = groupFilter;
		return this;
	}
	*/
	
	/************************************************************************************************************
	* Username
	************************************************************************************************************/
	@NotNull @NotEmpty private String username = "ldapreader";
	
	public String getUsername() {
		return username;
	}

	public LdapConfiguration setUsername(String username) {
		this.username = username;
		return this;
	}	

	/************************************************************************************************************
	* Password
	************************************************************************************************************/	
	@NotNull @NotEmpty private String password = "changeme";
	
	public String getPassword() {
		return password;
	}

	public LdapConfiguration setPassword(String password) {
		this.password = password;
		return this;
	}

	/************************************************************************************************************
	* Connect Timeout
	************************************************************************************************************/
 	//@NotNull 
	@Valid 	private Duration connectTimeout = Duration.milliseconds(500);
	
	public Duration getConnectTimeout() {
		return connectTimeout;
	}

	public LdapConfiguration setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
		return this;
	}
	
	/************************************************************************************************************
	* Read Timeout
	************************************************************************************************************/
	//@NotNull
	@Valid private Duration readTimeout = Duration.milliseconds(500);

	public Duration getReadTimeout() {
		return readTimeout;
	}

	public LdapConfiguration setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
		return this;
	}

}