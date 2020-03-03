#customer lagom-impl modoule
This is a sample lagom module which implements lagom classes from common-lagom module and lagom-api module

##How to Run
* You need to go to the project's base directory and give the keyspace name and table name for cassandra by `KEYSPACE_NAME = <your keyspace>` `TABLE_NAME = <your table name>` respectively.
*  Then run the command `sbt`.
* After entering in the sbt console go to the customer-lagom-impl module by `project customer-lagom-impl`
* Then `runAll` to run all the services.


##What it does 
This customer-lagom-impl service persists it's data to a cassandra database using lagom's persistence api and here it is intended to show how to persist state using lagom.  
This service also has a readside which is used to handle the events produced by persistent entity and for tracking which events it has handled.
