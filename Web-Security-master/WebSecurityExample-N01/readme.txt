1) Create domains dc=example,dc=com
2) Create organizationUnit ou=groups
3) Create organizationUnit ou=users
4) In ou=users crate person (class person)
     cn=admin,ou=users,dc=example,dc=com
     cn=manager,ou=users,dc=example,dc=com

5) In ou=groups crate person (class groupOfUniqueNames)
       cn=administrator,ou=groups,dc=example,dc=com, add admin as unique member
       cn=manager,ou=groups,dc=example,dc=com, add manager as unique member

Tomcat META_INF/context.xml

<Realm   className="org.apache.catalina.realm.JNDIRealm"
             connectionURL="ldap://localhost:10389"
             userPattern="cn={0},ou=users,dc=example,dc=com"
             roleBase="ou=groups,dc=example,dc=com"
             roleName="cn"
             roleSearch="(uniqueMember={0})"
            />

JBoss/Wildfly

standalone.xml

 <security-domain name="LDAPAuth">
                    <authentication>
                        <login-module code="LdapExtended" flag="required">
                            <module-option name="java.naming.factory.initial" value="com.sun.jndi.ldap.LdapCtxFactory"/>
                            <module-option name="java.naming.provider.url" value="ldap://localhost:10389"/>
                            <module-option name="java.naming.security.authentication" value="simple"/>
                            <module-option name="bindDN" value="uid=admin,ou=system"/>
                            <module-option name="bindCredential" value="secret"/>
                            <module-option name="baseCtxDN" value="ou=users,dc=example,dc=com"/>
                            <module-option name="baseFilter" value="(cn={0})"/>
                            <module-option name="rolesCtxDN" value="ou=groups,dc=example,dc=com"/>
                            <module-option name="roleFilter" value="(uniqueMember={1})"/>
                            <module-option name="roleAttributeID" value="cn"/>
                            <module-option name="searchScope" value="ONELEVEL_SCOPE"/>
                            <module-option name="throwValidateError" value="true"/>
                            <module-option name="allowEmptyPasswords" value="false"/>
                        </login-module>
                    </authentication>
                </security-domain>
            </security-domains>

   jboss-web.xml

    <jboss-web>
        <security-domain>java:/jaas/LDAPAuth</security-domain>
    </jboss-web>


