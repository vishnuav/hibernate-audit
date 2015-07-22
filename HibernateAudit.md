# Overview #

The project provides auditing capabilities to Hibernate. It uses a set of tables in the database that do not depend on the object model.

# Resources #
  * Maven Repository: http://hibernate-audit.googlecode.com/svn/repository/releases/
  * Maven Site: http://hibernate-audit.googlecode.com/svn/site/reference/
  * Issue Tracking: http://code.google.com/p/hibernate-audit/issues/list
  * Guide: HibernateAuditUserGuide
  * Mail List TBD


# How do I check out? #

## Anonymous check out ##

```
svn co http://hibernate-audit.googlecode.com/svn/trunk ./hibernate-audit-read-only
```

## Contributor check out ##

```
svn checkout --username <your_google_account> https://hibernate-audit.googlecode.com/svn/trunk/ ./hibernate-audit 
```

## How to obtain the hibernate-audit release ##

Check the [hibernate-audit maven repository](http://hibernate-audit.googlecode.com/svn/repository/releases/) for the latest release.

## How do I build? (for development only) ##

### Prepare the development environment ###
hibernate-audit depends on Oracle JDBC driver during the testing. Oracle JDBC driver is not provided in any maven public repositories so you need to download the driver from [Oracle JDBC driver page](http://www.oracle.com/technology/software/tech/java/sqlj_jdbc/index.html) and use the maven install plugin to install the JDBC driver in your local maven repository. Here is an example maven install command that can do that (note that the jar file should be in the current directory and named classes12.jar - otherwise you need to adjust the maven command)

```
mvn install:install-file -Dfile=classes12.jar -DgroupId=oracle -DartifactId=classes12 -Dversion=10.2.0.2.0 -Dpackaging=jar -DcreateChecksum=true
```

hibernate-audit also uses Oracle database for the test cases (you can change the database and use a different database but you need to change the JDBC driver, possibly the audit HBM file and of course to set the correct DB URL and Hibernate dialect). You can download and install Oracle XE database from [here](http://www.oracle.com/technology/products/database/xe/index.html). Then you need to create a new user called `hba` with password `hba`. You also need to give the hba user admin permissions.

If you want to see the production hibernate-audit DDL then you can execute

```
mvn hibernate3:hbm2ddl
```

The username and password hba/hba are also configurable in the pom.xml file - Note that instead of changing the pom.xml file you can change the maven settings.xml file which is much better than changing the pom.xml file to provide environment specific settings - for more information how to do that please check [Maven site](http://maven.apache.org).

### Perform a build ###
Go to the main hibernate-audit directory which contains the pom.xml file and execute

```
mvn clean install
```

For more information about how to execute the build (including different options like skip the test cases) please check [Maven site](http://maven.apache.org).