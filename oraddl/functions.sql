--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Function MGETCOOLRUN
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "ATLAS_COND_TOOLS"."MGETCOOLRUN" (crun VARCHAR2) RETURN NUMBER AS
LANGUAGE JAVA NAME 'CoolIov.getCoolRun(java.lang.String) return java.math.BigDecimal';

/
--------------------------------------------------------
--  DDL for Function MGETCOOLTIMERUN
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "ATLAS_COND_TOOLS"."MGETCOOLTIMERUN" (ctime NUMBER, iovtype VARCHAR2) RETURN VARCHAR2 AS
LANGUAGE JAVA NAME 'CoolIov.getCoolTimeRunLumiString(java.lang.Long, java.lang.String) return java.lang.String';

/
--------------------------------------------------------
--  DDL for Function MGETRUN
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "ATLAS_COND_TOOLS"."MGETRUN" (ctime NUMBER) RETURN NUMBER AS
LANGUAGE JAVA NAME 'CoolIov.getRun(java.lang.Long) return java.lang.Long';

/
--------------------------------------------------------
--  DDL for Function MYJAVAMESSAGE
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "ATLAS_COND_TOOLS"."MYJAVAMESSAGE" (mss VARCHAR2) RETURN VARCHAR2 AS
LANGUAGE JAVA NAME 'CoolIov.message(java.lang.String) return java.lang.String';

/
