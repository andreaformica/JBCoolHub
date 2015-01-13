--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Type COMA_COOL_GTAGTAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_GTAGTAG_TYPE" AS OBJECT (
  cool_gtag cool_nodegtagtag_type,
  coma_gtag cool_nodegtagtag_type
);

/
--------------------------------------------------------
--  DDL for Type COMA_COOL_GTAGTAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_GTAGTAG_TYPE_T" AS table of coma_cool_gtagtag_type;

/
--------------------------------------------------------
--  DDL for Type COMA_COOL_NODES_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_NODES_TYPE" AS OBJECT (
  cool_node cool_schemanode_type,
  coma_node cool_schemanode_type
);

/
--------------------------------------------------------
--  DDL for Type COMA_COOL_NODES_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_NODES_TYPE_T" AS TABLE of coma_cool_nodes_type;

/
--------------------------------------------------------
--  DDL for Type COMA_COOL_TAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_TAG_TYPE" AS OBJECT (
  cool_tag cool_schemanodetag_type,
  coma_tag cool_schemanodetag_type
);

/
--------------------------------------------------------
--  DDL for Type COMA_COOL_TAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMA_COOL_TAG_TYPE_T" AS table of coma_cool_tag_type;

/
--------------------------------------------------------
--  DDL for Type COMACOOL_GTAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_GTAG_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  db_name varchar(30),
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  GTAG_NAME varchar2(255),
  GTAG_LOCK_STATUS NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255), 
  COMA_COOL_SCHEMA VARCHAR2(50), 
  COMA_NODE_FULLPATH VARCHAR2(255), 
  COMA_TAG_NAME VARCHAR2(255),
  COMA_TAG_LOCK_STATUS NUMBER
);

/
--------------------------------------------------------
--  DDL for Type COMACOOL_GTAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_GTAG_TYPE_T" AS TABLE OF COMACOOL_GTAG_TYPE;

/
--------------------------------------------------------
--  DDL for Type COMACOOL_NODE_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_NODE_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar2(30),
  db_name varchar2(30),
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),
  lastmod_date varchar2(255), 
  coma_node_id number,
  coma_node_name varchar2(255),
  coma_node_fullpath varchar2(255),
  coma_node_lastmod_date varchar2(255),
  coma_node_description varchar2(255)
)

/
--------------------------------------------------------
--  DDL for Type COMACOOL_NODE_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_NODE_TYPE_T" AS TABLE OF COMACOOL_NODE_TYPE;

/
--------------------------------------------------------
--  DDL for Type COMACOOL_TAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_TAG_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  db_name varchar(30),
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255),
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255), 
  NIOVS NUMBER,
  NCHANNELS NUMBER,
  MINIOV_SINCE NUMBER,
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  COMA_COOL_SCHEMA VARCHAR2(50), 
  COMA_NODE_FULLPATH VARCHAR2(255), 
  COMA_TAG_NAME VARCHAR2(255),
  COMA_TAG_DESCRIPTION VARCHAR2(255),
  COMA_TAG_LOCK_STATUS NUMBER,
  COMA_ROW_COUNT NUMBER,
  COMA_CHAN_COUNT NUMBER
);

/
--------------------------------------------------------
--  DDL for Type COMACOOL_TAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COMACOOL_TAG_TYPE_T" AS TABLE OF COMACOOL_TAG_TYPE;

/
--------------------------------------------------------
--  DDL for Type COND_LASTMODTIME_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_LASTMODTIME_TYPE" AS OBJECT
( 
  SCHEMA_NAME VARCHAR2(255),
  DB_NAME VARCHAR2(50),
  NODE_NAME VARCHAR2(255),
  COOL_TABLE_NAME VARCHAR2(255),
  SEQ_CURRVALUE NUMBER,
  LASTMOD_TIME DATE
);

/
--------------------------------------------------------
--  DDL for Type COND_LASTMODTIME_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_LASTMODTIME_TYPE_T" AS TABLE OF COND_LASTMODTIME_TYPE;

/
--------------------------------------------------------
--  DDL for Type COND_NODEIOVRANGE_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODEIOVRANGE_TYPE" AS OBJECT
( 
  ROW_ID NUMBER,
  USER_TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  CHANNEL_ID NUMBER,
  CHANNEL_NAME VARCHAR2(255),
  MINIOV_SINCE NUMBER,
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  IOV_HOLE NUMBER,
  HOLE_UNTIL NUMBER,
  NIOVS NUMBER
);

/
--------------------------------------------------------
--  DDL for Type COND_NODEIOVRANGE_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODEIOVRANGE_TYPE_T" AS TABLE OF COND_NODEIOVRANGE_TYPE;

/
--------------------------------------------------------
--  DDL for Type COND_NODESTAT_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODESTAT_TYPE" AS OBJECT
( 
  SCHEMA_NAME VARCHAR2(255),
  DB_NAME VARCHAR2(50),
  NODE_NAME VARCHAR2(255),
  TAG_NAME VARCHAR2(255),
  NCHANNELS NUMBER,
  TOTALIOVS NUMBER,
  TOTALHOLES NUMBER, 
  MINSINCE NUMBER,
  MAXUNTIL NUMBER,
  TOTALRUNS NUMBER,
  TOTALRUNSINHOLE NUMBER,
  MINRUN NUMBER,
  MAXRUN NUMBER
);

/
--------------------------------------------------------
--  DDL for Type COND_NODESTAT_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODESTAT_TYPE_T" AS TABLE OF COND_NODESTAT_TYPE;

/
--------------------------------------------------------
--  DDL for Type COND_NODESTATHOLE_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODESTATHOLE_TYPE" AS OBJECT
( 
  SCHEMA_NAME VARCHAR2(255),
  DB_NAME VARCHAR2(50),
  NODE_NAME VARCHAR2(255),
  TAG_NAME VARCHAR2(255),
  CHANNEL_ID NUMBER,
  HOLE_SINCE NUMBER,
  HOLE_UNTIL NUMBER,
  NRUNS NUMBER,
  MINRUN NUMBER,
  MAXRUN NUMBER,
  MINSINCE TIMESTAMP,
  MAXUNTIL TIMESTAMP
);

/
--------------------------------------------------------
--  DDL for Type COND_NODESTATHOLE_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_NODESTATHOLE_TYPE_T" AS TABLE OF COND_NODESTATHOLE_TYPE;

/
--------------------------------------------------------
--  DDL for Type COND_RUNINFO_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_RUNINFO_TYPE" AS OBJECT
( /* TODO enter attribute and method declarations here */
  RUN_NUMBER NUMBER(10,0),
  RUNSTART NUMBER(20,0),
  RUNEND NUMBER(20,0),
  RUN_TYPE VARCHAR2(255 BYTE),
  FILENAME_TAG VARCHAR2(255 BYTE),
  CLEANSTOP NUMBER(1,0)
);

/
--------------------------------------------------------
--  DDL for Type COND_RUNINFO_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COND_RUNINFO_TYPE_T" AS TABLE OF cond_runinfo_type;

/
--------------------------------------------------------
--  DDL for Type COOL_CHANNEL_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_CHANNEL_TYPE" AS OBJECT
( /* TODO enter attribute and method declarations here */
  CHANNEL_ID NUMBER,
  CHANNEL_NAME VARCHAR(255),
  DESCRIPTION VARCHAR2(255) 
);

/
--------------------------------------------------------
--  DDL for Type COOL_CHANNEL_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_CHANNEL_TYPE_T" AS TABLE OF COOL_CHANNEL_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_GTAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_GTAG_TYPE" AS OBJECT
( 
  SCHEMA_NAME VARCHAR(30),
  DB_NAME VARCHAR(30),
  NODE_ID NUMBER,
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255),
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

/
--------------------------------------------------------
--  DDL for Type COOL_GTAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_GTAG_TYPE_T" AS TABLE OF COOL_GTAG_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_IOV_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOV_TYPE" AS OBJECT
( 
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME TIMESTAMP(6),
  LASTMOD_DATE TIMESTAMP(6),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER,
  CHANNEL_NAME VARCHAR2(255),
  TAG_NAME VARCHAR2(255)
);

/
--------------------------------------------------------
--  DDL for Type COOL_IOV_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOV_TYPE_T" AS TABLE OF COOL_IOV_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_IOVEXT_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVEXT_TYPE" AS OBJECT
( 
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME TIMESTAMP(6),
  LASTMOD_DATE TIMESTAMP(6),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER,
  CHANNEL_NAME VARCHAR2(255),
  TAG_NAME VARCHAR2(255),
  IOV_BASE VARCHAR2(30)
);

/
--------------------------------------------------------
--  DDL for Type COOL_IOVEXT_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVEXT_TYPE_T" AS TABLE OF COOL_IOVEXT_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_IOVPOOLREF_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVPOOLREF_TYPE" AS OBJECT
( 
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME TIMESTAMP(6),
  LASTMOD_DATE TIMESTAMP(6),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER,
  CHANNEL_NAME VARCHAR2(255),
  TAG_NAME VARCHAR2(255),
  POOLREF VARCHAR2(4000),
  NODE_ID NUMBER,
  NODE_FULLPATH VARCHAR2(255)
);

/
--------------------------------------------------------
--  DDL for Type COOL_IOVPOOLREF_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVPOOLREF_TYPE_T" AS TABLE OF COOL_IOVPOOLREF_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_IOVSTAT_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVSTAT_TYPE" AS OBJECT
( 
  CHANNEL_ID NUMBER,
  MINIOV_SINCE NUMBER,
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  NIOVS_PERCHAN NUMBER
);

/
--------------------------------------------------------
--  DDL for Type COOL_IOVSTAT_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_IOVSTAT_TYPE_T" AS TABLE OF COOL_IOVSTAT_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_NODE_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODE_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),  
  node_isleaf number, 
  node_instime varchar2(255),  
  node_tinstime timestamp(6),
  lastmod_date varchar2(255), 
  folder_versioning number,
  folder_payloadspec varchar2(4000),
  folder_iovtablename varchar2(255), 
  folder_tagtablename varchar2(255), 
  folder_channeltablename varchar2(255)
);

/
--------------------------------------------------------
--  DDL for Type COOL_NODE_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODE_TYPE_T" 
AS TABLE OF COOL_NODE_TYPE ;

/
--------------------------------------------------------
--  DDL for Type COOL_NODEGTAGTAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODEGTAGTAG_TYPE" AS OBJECT
( 
  SCHEMA_NAME VARCHAR(30),
  DB_NAME VARCHAR(30),
  GTAG_ID NUMBER,
  GTAG_NAME VARCHAR2(255),
  GTAG_DESCRIPTION VARCHAR2(255),
  GTAG_LOCK_STATUS NUMBER,
  NODE_ID NUMBER,
  NODE_NAME VARCHAR2(255),
  NODE_FULLPATH VARCHAR2(255),
  NODE_DESCRIPTION VARCHAR2(255),
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

/
--------------------------------------------------------
--  DDL for Type COOL_NODEGTAGTAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODEGTAGTAG_TYPE_T" AS table of cool_nodegtagtag_type;

/
--------------------------------------------------------
--  DDL for Type COOL_NODETAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODETAG_TYPE" AS OBJECT
( 
  NODE_ID NUMBER,
  NODE_NAME VARCHAR2(255),
  NODE_FULLPATH VARCHAR2(255),
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

/
--------------------------------------------------------
--  DDL for Type COOL_NODETAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_NODETAG_TYPE_T" AS TABLE OF COOL_NODETAG_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_OWNERS
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_OWNERS" AS OBJECT
( 
  SCHEMA_NAME VARCHAR2(255)
);

/
--------------------------------------------------------
--  DDL for Type COOL_OWNERS_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_OWNERS_T" AS TABLE OF COOL_OWNERS;

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODE_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODE_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  dbname varchar(30),
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),  
  node_isleaf number, 
  node_instime varchar2(255),  
  node_tinstime timestamp(6),
  lastmod_date varchar2(255), 
  folder_versioning number,
  folder_payloadspec varchar2(4000),
  folder_iovtablename varchar2(255), 
  folder_tagtablename varchar2(255), 
  folder_channeltablename varchar2(255),
  iov_base varchar2(30),
  iov_type varchar2(50)
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODE_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODE_TYPE_T" AS TABLE OF COOL_SCHEMANODE_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODECHANNEL_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODECHANNEL_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  dbname varchar(30),
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),  
  node_isleaf number, 
  node_instime varchar2(255),  
  node_tinstime timestamp(6),
  lastmod_date varchar2(255), 
  folder_iovtablename varchar2(255), 
  folder_tagtablename varchar2(255), 
  folder_channeltablename varchar2(255),
  CHANNEL_ID NUMBER,
  CHANNEL_NAME VARCHAR(255),
  DESCRIPTION VARCHAR2(255) 
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODECHANNEL_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODECHANNEL_TYPE_T" AS TABLE OF COOL_SCHEMANODECHANNEL_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODEIOV_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODEIOV_TYPE" AS OBJECT
( 
  schema_name varchar(30),
  dbname varchar(30),
  node_id number,
  node_fullpath varchar2(255), 
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  NIOVS NUMBER,
  NCHANNELS NUMBER,
  MINIOV_SINCE NUMBER,
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  IOV_BASE VARCHAR2(30)
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODEIOV_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODEIOV_TYPE_T" AS TABLE OF COOL_SCHEMANODEIOV_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODETAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODETAG_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  dbname varchar(30),
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),  
  node_isleaf number, 
  node_instime varchar2(255),  
  node_tinstime timestamp(6),
  lastmod_date varchar2(255), 
  folder_iovtablename varchar2(255), 
  folder_tagtablename varchar2(255), 
  folder_channeltablename varchar2(255),
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODETAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODETAG_TYPE_T" AS TABLE OF COOL_SCHEMANODETAG_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODETAGIOV_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODETAGIOV_TYPE" AS OBJECT 
( /* TODO enter attribute and method declarations here */ 
  schema_name varchar(30),
  dbname varchar(30),
  node_id number,
  node_name varchar2(255), 
  node_fullpath varchar2(255), 
  node_description varchar2(255),  
  node_isleaf number, 
  node_instime varchar2(255),  
  node_tinstime timestamp(6),
  lastmod_date varchar2(255), 
  folder_iovtablename varchar2(255), 
  folder_tagtablename varchar2(255), 
  folder_channeltablename varchar2(255),
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255),
  NIOVS NUMBER,
  NCHANNELS NUMBER,
  MINIOV_SINCE NUMBER,
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  IOV_BASE VARCHAR2(30)
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCHEMANODETAGIOV_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCHEMANODETAGIOV_TYPE_T" AS TABLE OF COOL_SCHEMANODETAGIOV_TYPE;

/
--------------------------------------------------------
--  DDL for Type COOL_SCNTIOV_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCNTIOV_TYPE" AS OBJECT
( 
  schema_name varchar(30),
  dbname varchar(30),
  node_fullpath varchar2(255), 
  iov_base varchar2(50),
  TAG_NAME VARCHAR2(255),  
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME TIMESTAMP(6),
  LASTMOD_DATE TIMESTAMP(6),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER,
  CHANNEL_NAME VARCHAR2(255)
);

/
--------------------------------------------------------
--  DDL for Type COOL_SCNTIOV_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_SCNTIOV_TYPE_T" AS TABLE OF COOL_SCNTIOV_TYPE ;

/
--------------------------------------------------------
--  DDL for Type COOL_TAG_TYPE
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_TAG_TYPE" AS OBJECT
( 
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

/
--------------------------------------------------------
--  DDL for Type COOL_TAG_TYPE_T
--------------------------------------------------------

  CREATE OR REPLACE TYPE "ATLAS_COND_TOOLS"."COOL_TAG_TYPE_T" AS TABLE OF COOL_TAG_TYPE;

/
