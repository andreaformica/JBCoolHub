--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package COND_TOOLS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "ATLAS_COND_TOOLS"."COND_TOOLS_PKG" AUTHID CURRENT_USER AS


  TYPE NODESTAT_RECORD IS RECORD
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

  TYPE NODESTAT_HOLES_RECORD IS RECORD
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


  TYPE TAGSEQ_RECORD IS RECORD
( 
  SEQUENCE_NAME VARCHAR2(255 BYTE),
  CURRENT_VALUE NUMBER,
  LASTMOD_DATE VARCHAR2(255 BYTE)
);

  TYPE RUN_RECORD IS RECORD
(
  RUN_NUMBER NUMBER(10,0),
  RUNSTART NUMBER(20,0),
  RUNEND NUMBER(20,0),
  RUN_TYPE VARCHAR2(255 BYTE),
  FILENAME_TAG VARCHAR2(255 BYTE),
  CLEANSTOP NUMBER(1,0)
);

/* Function to retrieve Run information from COOL TDAQ tables
 */
function f_GetRun_Info(arg_dbname VARCHAR2) 
--    return varchar2 ;
return cond_runinfo_type_t pipelined ;                          


/* Function to retrieve Nodes statistics from COND_TOOLS_XXX tables
 * It extract total number of channels, iovs and holes per folder.
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_GetStat_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestat_type_t pipelined ;                          

/* Function to retrieve Nodes bad holes verifying info with COMA.
 * It extract total number of channels, iovs and holes per folder.
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_GetBadHoles_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestathole_type_t pipelined ;                          

/* Function to retrieve Nodes ranges verifying info with COMA.
 * It extract total number of channels, iovs and holes per folder.
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_GetRanges_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestathole_type_t pipelined ;                          

/* Function to retrieve Nodes ranges verifying info with COMA.
 * It extract total number of channels, iovs and holes per folder.
 *   - arg_schemaname : The full schema name.
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The folder name, ex: '/XXX/YYY'
 *   - arg_tag        : The pattern tag name, ex: 'ATAG%'
 */
function f_GetSummary_Ranges(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tag VARCHAR2, arg_since NUMBER, arg_until NUMBER)  return cond_nodeiovrange_type_t pipelined ;                          

/* Function to convert dates and run numbers using java functions.
 * Take in input the CoolIov time, and convert it either to run or to date.
 * In order to be compatible the format of the output will be varchar2. 
 */
function f_CheckTime(arg_cooltimesince NUMBER, arg_cooltimeuntil NUMBER, arg_iovbase VARCHAR2, 
                     arg_runnum NUMBER, arg_startrun TIMESTAMP, arg_endrun TIMESTAMP)  return NUMBER;                          

/* Function to convert dates and run numbers using java functions.
 * Take in input the CoolIov time, and convert it either to run or to date.
 * In order to be compatible the format of the output will be varchar2. 
 */
function f_GetCool_Time(arg_cooltime NUMBER, arg_iovbase VARCHAR2)  return VARCHAR2;                          

/* Function to extract last modification time from schema, folder, tag for a given DB instance. 
 */
function f_GetLastMod_Time(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tableextension VARCHAR2)  return cond_lastmodtime_type_t pipelined;                          

/*
 * Procedure to update iov ranges with information from COMA about runs in the holes range
 */
procedure p_UpdSummary_ChannelName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2);
/*
 * Procedure to update iov ranges with information from COMA about runs in the holes range
 */
procedure p_UpdRanges_RunInfo(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2);

/*
 * Procedure to update IOV_RANGES and IOV_SUMMARY tables using COOL selections.
 */
procedure p_UpdRanges_Summary(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2, arg_tag VARCHAR2);

END COND_TOOLS_PKG;

/
