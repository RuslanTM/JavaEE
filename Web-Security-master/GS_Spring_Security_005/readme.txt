1) Create domains dc=example,dc=com
2) Create organizationUnit ou=groups
3) Create organizationUnit ou=users
4) In ou=users crate person (class person)
     cn=admin,ou=users,dc=example,dc=com
     cn=manager,ou=users,dc=example,dc=com

5) In ou=groups crate person (class groupOfUniqueNames)
       cn=administrator,ou=groups,dc=example,dc=com, add admin as unique member
       cn=manager,ou=groups,dc=example,dc=com, add manager as unique member




