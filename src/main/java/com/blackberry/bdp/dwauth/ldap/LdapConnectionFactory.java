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

import com.unboundid.ldap.sdk.LDAPConnection;
import com.unboundid.ldap.sdk.LDAPException;
import com.unboundid.util.ssl.SSLUtil;
import com.unboundid.util.ssl.TrustAllTrustManager;
import io.dropwizard.auth.AuthenticationException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LdapConnectionFactory {

	private static final Logger LOG = LoggerFactory.getLogger(LdapConnectionFactory.class);
	private final LdapConfiguration configuration;
	private final List<LdapServer> servers;

	public LdapConnectionFactory(LdapConfiguration configuration) throws Exception {
		this.configuration = configuration;
		this.servers = getServersFromConfiguration(configuration);
	}

	private List<LdapServer> getServersFromConfiguration(LdapConfiguration config) 
		 throws AuthenticationException {
		String ldapServers = config.getLdapServers();
		List<LdapServer> newServers = new ArrayList<>();
		for (String server : ldapServers.split("\\s?,\\s?")) {
			LOG.info("Found a sever from ldap server configuration, {}", server);
			LdapServer newServer = new LdapServer();
			String hostnameWithOptionalPort;
			String[] uriParts = server.split("(ldap|ldaps)://");
			if (uriParts.length != 2) {
				throw new AuthenticationException("server (" + server + ") must start with either ldap:// or ldaps:// ");
			}
			newServer.secured = false;
			if (uriParts[0].equals("ldaps")) {
				newServer.secured = true;
			}
			hostnameWithOptionalPort = uriParts[1];
			String[] hostnameParts = hostnameWithOptionalPort.split(":");
			if (hostnameParts.length == 1) {
				newServer.hostname = hostnameParts[0];
			} else {
				if (hostnameParts.length == 2) {
					newServer.hostname = hostnameParts[0];
					newServer.port = Integer.parseInt(hostnameParts[1]);
				} else {
					throw new AuthenticationException("Failed to parse at least a hostname from " + server);
				}
			}
			newServers.add(newServer);
		}
		if (newServers.isEmpty()) {
			throw new AuthenticationException("Failed to parse ldap servers in config.  "
				 + "Required: at least one (or comma separted) host prefixed with ldap:// or ldaps:// and opitonal ports");
		}
		return newServers;
	}

	public LDAPConnection getLDAPConnection() throws LDAPException, AuthenticationException {
		return getLDAPConnection(configuration.getUsername(), configuration.getPassword());
	}

	public LDAPConnection getLDAPConnection(String userDN, String password) 
		 throws LDAPException, AuthenticationException {
		LDAPConnection ldapConnection = null;
		for (LdapServer server : servers) {
			try {
				if (server.secured) {
					// All this SSL stuff is 100% untested, never been executed code.... 
					SSLUtil sslUtil;
					if (configuration.getTrustAnySecuredHost()) {
						sslUtil = new SSLUtil(new TrustAllTrustManager());
						ldapConnection = new LDAPConnection(sslUtil.createSSLSocketFactory());
					} else {						
						sslUtil = new SSLUtil();
						ldapConnection = new LDAPConnection(sslUtil.createSSLSocketFactory());
					}
				} else {
					ldapConnection = new LDAPConnection();
				}
				ldapConnection.connect(server.hostname, server.getPort());
				ldapConnection.bind(userDN, password);
				break;
			} catch (GeneralSecurityException | LDAPException e) {
				LOG.error("Couldn't connect to server {}",
					 String.format("%s:%d (secured: %s)", server.hostname, server.port, server.secured),
					 e);
			}
		}
		if (ldapConnection == null) {
			throw new AuthenticationException("Unable to connect to any server");
		}
		return ldapConnection;
	}

	protected class LdapServer {
		protected String hostname;
		protected Integer port;
		protected boolean secured;

		protected LdapServer() {
		}

		protected LdapServer(String hostname) {
			this.hostname = hostname;
		}

		protected LdapServer(String hostname, int port) {
			this.hostname = hostname;
			this.port = port;
		}

		protected int getPort() {
			if (port != null) {
				return port;
			} else {
				if (secured) {
					return 636;
				} else {
					return 389;
				}
			}
		}
	}
}
