-- ---------------------------------------------------------------------------------------------------------------
-- This script upgrades the ECOMP SDK App database from version 1702 to 1707.
-- For use by partner apps to be compatible with changes for the open-source release.
--
-- rename 2 columns, change size on 3 name columns from fn_user for the AT&T Opensource version
--
-- ---------------------------------------------------------------------------------------------------------------

USE ecomp_sdk;

alter table fn_user
	CHANGE COLUMN FIRST_NAME FIRST_NAME VARCHAR(50) NULL DEFAULT NULL ,
	CHANGE COLUMN MIDDLE_NAME MIDDLE_NAME VARCHAR(50) NULL DEFAULT NULL ,
	CHANGE COLUMN LAST_NAME LAST_NAME VARCHAR(50) NULL DEFAULT NULL,
	CHANGE COLUMN SBCID ORG_USER_ID VARCHAR(20) NULL DEFAULT NULL,
	CHANGE COLUMN MANAGER_ATTUID ORG_MANAGER_USERID VARCHAR(20) NULL DEFAULT NULL;

commit;
