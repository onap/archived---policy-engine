-- ---------------------------------------------------------------------------------------------------------------
-- This script rolls-back the upgrade for the ECOMP SDK App database from version 1702 to 1707.
-- For use by partner apps to undo changes for the open-source release.
--
-- rename 2 columns, change size on 3 name columns from fn_user for the AT&T Opensource version
--
-- ---------------------------------------------------------------------------------------------------------------

USE ecomp_sdk;

alter table fn_user
	CHANGE COLUMN FIRST_NAME FIRST_NAME VARCHAR(25) NULL DEFAULT NULL ,
	CHANGE COLUMN MIDDLE_NAME MIDDLE_NAME VARCHAR(25) NULL DEFAULT NULL ,
	CHANGE COLUMN LAST_NAME LAST_NAME VARCHAR(25) NULL DEFAULT NULL,
	CHANGE COLUMN ORG_USER_ID SBCID VARCHAR(6) NULL DEFAULT NULL,
	CHANGE COLUMN ORG_MANAGER_USERID MANAGER_ATTUID VARCHAR(6) NULL DEFAULT NULL;

commit;
