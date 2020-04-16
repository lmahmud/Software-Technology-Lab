# CrowdFundSwt

## SWT - Group 24

Requires java 8 or above and a MySQL database with required tables. (Requires an internet connection for the first time to download dependencies)

Due to difficulty of manually managing dependencies, maven dependency manager is used.

Database(MySQL/MariaDB) details need to be configured in `src/main/webapp/META-INF/context.xml`. 

If tables don't exist in the given database, required tables will be automatically created.

SMTP details of the email needs to be configured in `src/main/resources/email.properties`

**To run webapp on tomcat**:

- if maven is installed : `mvn`
 
- if maven is not installed: 
  - Linux and MacOS: 
    ```./mvnw```
    
  - Windows:
    ```./mvnw.cmd```
    
    
    
**To run tests**:
- if maven is installed : `mvn clean test`
 
- if maven is not installed: 
  - Linux and MacOS: 
    ```./mvnw clean test```
    
  - Windows:
    ```./mvnw.cmd clean test```


**Project Structure**
```
CrowdFundSwt
└── src
    ├── main
    │   ├── java          <----  source files
    │   ├── resources
    │   └── webapp        <----  webapp related files (*.ftl)
    │       ├── META-INF
    │       └── WEB-INF
    └── test
        └── java          <----  test source files       
```
