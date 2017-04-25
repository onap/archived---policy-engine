This Readme file contains a description of all the database scripts located in  

  epsdk-app-att    / db-scripts / 
  epsdk-app-att    / db-scripts / previous-releases / 
  epsdk-app-common / db-scripts / 
  epsdk-app-os     / db-scripts / 
  
***************************************************************************************************************************************

Directions: 

DDL
For an Opensource instance run only script 3; for an AT&T instance add script 1
EEcompSdkDDLMySql_1707_Common.sql  - this is the DDL entries that both Opensource and AT&T have in common
EcompSdkDDLMySql_1707_ATT.sql -  this is the specific DDL entries that only AT&T needs

DML

For an Opensource instance run  script 4 and script 8; for an AT&T instance run script 4 and script 2
EcompSdkDMLMySql_1707_Common.sql  - this is the DML entries that both Opensource and AT&T have in common
EcompSdkDMLMySql_1707_ATT.sql - this is the specific DML entries that only AT&T needs
EcompSdkDMLMySql_1707_OS.sql - this is the specific DML entries that only Opensource needs

Our Partner Apps need to change the names and sizes of some columns in fn_user; use script 5 for that
and if needed to remove the change use script 6.
EcompSdkMySql_Upgrade_1702_to_1707.sql
EcompSdkMySql_Rollback_1707_to_1702.sql
	
***************************************************************************************************************************************
  epsdk-app-att / db-scripts / 
***************************************************************************************************************************************	
1.EcompSdkDDLMySql_1707_ATT.sql    			This script adds tables for the 1707 AT&T version of the ECOMP SDK application database.
											The DDL 1707 COMMON script must be executed first
2.EcompSdkDMLMySql_1707_ATT.sql    			This script populates tables in the 1707 AT&T version of the ECOMP SDK application database.
											The DML 1707 COMMON script must be executed first!	
***************************************************************************************************************************************
  epsdk-app-common / db-scripts / 
***************************************************************************************************************************************	
3.EcompSdkDDLMySql_1707_Common.sql   		This script creates tables in the 1707 COMMON version of the ECOMP SDK application database.
											Additional DDL scripts may be required for the AT&T version or the OPEN-SOURCE version!
4.EcompSdkDMLMySql_1707_Common.sql   		This script populates tables in the 1707 COMMON version of the ECOMP SDK application database.
											Additional DML scripts are required for the AT&T version or the OPEN-SOURCE version!
5.EcompSdkMySql_Upgrade_1702_to_1707.sql    This script upgrades the ECOMP SDK App database from version 1702 to 1707.
											For use by partner apps to be compatible with changes for the open-source release.
6.EcompSdkMySql_Rollback_1707_to_1702.sql   This script rolls-back the upgrade for the ECOMP SDK App database from version 1707 to 1702.
											For use by partner apps to undo changes for the open-source release.
***************************************************************************************************************************************
epsdk-app-os / db-scripts / 
***************************************************************************************************************************************	
7.EcompSdkDDLMySql_1707_OS.sql  			It is empty for now; just a logical placeholder
8.EcompSdkDMLMySql_1707_OS.sql  			This script populates tables in the 1707 OPEN-SOURCE version of the ECOMP SDK application database.
											After The DML 1707 COMMON script is run.

***************************************************************************************************************************************
epsdk-app-att / db-scripts / previous-releases /
***************************************************************************************************************************************	
cleanup_EcompSdk.sql						This script drops tables no longer needed that were in the original schema.
EcompSdkTestDML.sql							This scripts adds user/roles for a test envirionment

*****************************************************
Upgrading from 1607 SDK to 1610 SDK
*****************************************************
EcompSdkDDL_1610_Add.sql					This is the Upgrade script for the 1610 Version of the SDK database called ecomp_sdk; 
											upgrading from the 1607 version
EcompSdkDML_1610_Add.sql					This is the Upgrade script for the default data for the 1610 Version of the SDK database called ecomp_sdk; 
											upgrading from the 1607 version
*****************************************************
Upgrading from 1610 SDK to 1702 SDK
*****************************************************	
If you are starting with a 1610 environment and want to bring it up to 1702,
you can run the following scripts in this order:
EcompSdkDML_1702_Add_1.sql
EcompSdkDML_1702_Add_2.sql
EcompSdkDML_1702_Add_3.sql
EcompSdkDML_1702_Add_4.sql
EcompSdkDML_1702_Add_5.sql

You can roll back the changes from the corresponding 1702 Add scripts with these rollback scripts:
EcompSdkDML_1702_Rollback_1.sql
EcompSdkDML_1702_Rollback_2.sql
EcompSdkDML_1702_Rollback_3.sql
EcompSdkDML_1702_Rollback_4.sql
EcompSdkDML_1702_Rollback_5.sql	
									
*****************************************************
Complete Scripts for 1702
*****************************************************											
EcompSdkDDLMySql_1702.sql					This is for the 1702 DDL Version of SDK database called ecomp_sdk
EcompSdkDMLMySql_1702.sql					This is for the default data for 1702 Version of SDK database called ecomp_sdk
