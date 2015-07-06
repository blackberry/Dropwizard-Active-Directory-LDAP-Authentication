# Microsoft Active Directory LDAP Auth for Dropwizard

A configurable Dropwizard 0.8.1 based authenticator for Microsoft AD/LDAP.

## Why?!

There's more than a few existing dropwizard plugins that handle LDAP authentication, but none that this author could get working (out of the box) against a Microsoft Active Directory LDAP deployment.  I'm not sure if this was a result of our AD service being having a non-standard configuration, or the existing plugins not configurable enough to support Active Directory LDAP object structure, but after a half day of trying the most popular Google results, I gave up and hacked this together.

## Thanks

The [Yammer LDAP Authentication](https://github.com/yammer/dropwizard-auth-ldap) project and [xaviershay's](https://github.com/xaviershay) (gist)[https://gist.github.com/xaviershay/3167835] provided the much of the code I started with.

## Authentication:

An intial user is authenticated and and then binds to the LDAP connection.  When an authentication request is made this user searches for the principal username within the tree of 'userFilter' for any object whose 'userIdentifierObjectName' contains the username.  Once found, another authentication is perform with the entire DN of the found user and the password provided via BasicCredentials.  Once that authentication suceeds the connection is closed and membership searches are attempted.

## Groups and Memberships

Once a user is authenticated the tree under 'groupBaseDN' is searched for any objects whose name equals 'merbershipIdentifierObjectName' and whose value is the DN of the authenticated user.  The entire DN is then included in a collection of groups.  The user object returned from the authenticate() method is just a simple object with a String name (username) and a Set&lt;String&gt; (memberships).  

One important thing here is that the String's of the memberships Set are full distinguished names.  Mostly, becuse this author knows very little about LDAP and wasn't sure if two objects exist in different places of the same tree and are managed by seperate permission systems if there could be a security risks if they shared duplicate object names.

## Including/Importing

Download this source and perform a 'maven clean install' to create the JAR and import it into your local Maven repo (sorry--not currently published to Maven Central).  After, include this dependency:

&lt;!-- BB Dropwizard AD LDAP Auth --&gt;
<dependency>
	<groupId>com.blackberry.bdp.dwauth</groupId>
	<artifactId>dwMsAdLdapAuth</artifactId>
	<version>0.1.1</version>
</dependency>

## Configuration

```
ldapConfiguration:
  ldapServers: ldap://ldap.company.com
  username: accessuser
  password: secret
  userFilter: OU=department,DC=com,DC=company
  groupBaseDN: OU=groups,DC=com,DC=company
  trustAnySecuredHost: true
  cachePolicy: maximumSize=10000, expireAfterWrite=10m
  userIdentifierObjectName: sAMAccountName
  merbershipIdentifierObjectName: member
```
Where:

* ldapServers: A single or comma seperated list of your LDAP server (i.e. ldap[s]://host1:port1...[ldap[s]://hostN:portN]).  Port is optional and will be infered from the URI schema if ommited (ldap=389, ldaps=636). 
* username: The account that binds to the LDAP connection and is used for searching 
* password: The 'username's password 
* userFilter: The tree where the users are searched for by 'username' when looking for the principals DN
* groupBaseDN: The tree where memberships are searched
* trustAnySecuredHost: Accept non-valid SSL certificates for ldaps:// servers (self signed, expired)
* cachePolicy: How many credentials to cache and for how long?
* userIdentifierObjectName: Object name that identifies the username of a principal
* merbershipIdentifierObjectName: Object name that identifies the object value being a member

## Sample Instantiation

Creating a cached authenticator:

```
import com.blackberry.bdp.dwauth.ldap.LdapAuthenticator;
import com.blackberry.bdp.dwauth.ldap.LdapConnectionFactory;
import com.blackberry.bdp.dwauth.ldap.User;
import com.blackberry.bdp.dwauth.ldap.LdapConfiguration;
.
.
.
public class YourApplication extends Application<YourConfiguration> {

	@Override
	public void run(YourConfiguration configuration, Environment environment) 
	               throws AuthenticationException, Exception {
		.
		.
		.
		LdapConfiguration ldapConfiguration = configuration.getLdapConfiguration();
		LdapConnectionFactory ldapConnFactory = new LdapConnectionFactory(ldapConfiguration);
		LdapAuthenticator ldapAuthenticator = new LdapAuthenticator(ldapConnFactory, ldapConfiguration);		
		Authenticator<BasicCredentials, User> cachedAuthenticator = new CachingAuthenticator<>(
			environment.metrics(),
			ldapAuthenticator,
			configuration.getLdapConfiguration().getCachePolicy());
		environment.jersey().register(AuthFactory.binder
			(new BasicAuthFactory<>(cachedAuthenticator, "realm", User.class)));
```

## Securing Resources

Here's a simple resource that is protected by HTTP basic authentication.  After authentication the method can investigate the groups to determine if memberships's important and re-direct to a 403 as needed.

```
@GET @Timed @Produces(value = MediaType.APPLICATION_JSON)
public ProtectedObject get(@Auth User user) throws Exception {
	RunningConfig runningConfig;
	LOG.info("We have a user: {}", user.getName());		
	for (String role : user.getRoles()) {
		LOG.info("User {} associated to role {}", user.getName(), role);
	}
}
```

## Authorization Handling

The Dropwizard project encourages throwing exceptions instead of custom responses for errors.  Especially, for RESTful services it's much more standard.  If you want to return a 403 error code when the authenticated user's object doesn't contain a specific DN in it's memberships Set, then you need to register the Exception Mapper contained in this project.  

Do this in the same chunk of code where you instantiate the authenticator (required import: com.blackberry.bdp.dwauth.ldap.AccessDeniedHandler).
```
environment.jersey().register(new AccessDeniedHandler());
```

Then, in the above exaple of securing a resource then you'd (required import: com.blackberry.bdp.dwauth.ldap.AccessDeniedException)
```
if (!user.getMemberships().contains("<whatever full DN you expect>")) {
	throw new AccessDeniedException("You are not authorized to access this resource");
}
```

Keep in mind that you shouldn't be externally advertising the actual DNs you are basing your security off in the exception text.  While you are not prevented from using a custom error text, you should keep all error messages as generic as possible.  

## Contributing
To contribute code to this repository you must be [signed up as an official contributor](http://blackberry.github.com/howToContribute.html).

## Disclaimer 
THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
