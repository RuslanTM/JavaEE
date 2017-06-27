for wildfly

1) Add datasource to standalone.xml

  <datasource jta="true" jndi-name="java:jboss/datasources/securityrealm" pool-name="securityrealmDS" enabled="true" use-java-context="true" use-ccm="true">
                    <connection-url>jdbc:mysql://localhost:3306/securityrealm</connection-url>
                    <driver>mysql</driver>
                    <security>
                        <user-name>root</user-name>
                        <password>root</password>
                    </security>
                </datasource>

2) Add security domain to standalone.xml

  <security-domain name="databaseRealm" cache-type="default">
                    <authentication>
                        <login-module code="Database" flag="required">
                            <module-option name="dsJndiName" value="java:jboss/datasources/securityrealm"/>
                            <module-option name="principalsQuery" value="select user_pass from users where user_name=?"/>
                            <module-option name="rolesQuery" value="select role_name, 'Roles' from user_roles where user_name=?"/>
                        </login-module>
                    </authentication>
                </security-domain>

