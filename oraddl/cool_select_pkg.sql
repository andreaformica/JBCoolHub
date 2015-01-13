--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package COOL_SELECT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "ATLAS_COND_TOOLS"."COOL_SELECT_PKG" AUTHID CURRENT_USER AS
--AUTHID CURRENT_USER AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */
  
  TYPE NODE_RECORD IS RECORD
( 
	NODE_ID NUMBER,
  NODE_NAME VARCHAR2(255), 
  NODE_FULLPATH VARCHAR2(255),
  NODE_DESCRIPTION VARCHAR2(255), 
  NODE_ISLEAF NUMBER, 
  NODE_INSTIME VARCHAR2(255),
--  NODE_TINSTIME TIMESTAMP(6),
  LASTMOD_DATE VARCHAR2(255),
  FOLDER_VERSIONING NUMBER,
  FOLDER_PAYLOADSPEC VARCHAR2(4000),
  FOLDER_IOVTABLENAME VARCHAR2(255),
  FOLDER_TAGTABLENAME VARCHAR2(255),
  FOLDER_CHANNELTABLENAME VARCHAR2(255)
);

  TYPE NODE_FLD_RECORD IS RECORD
( 
	NODE_ID NUMBER,
  FOLDER_IOVTABLENAME VARCHAR2(255),
  FOLDER_TAGTABLENAME VARCHAR2(255),
  FOLDER_CHANNELTABLENAME VARCHAR2(255),
  FOLDER_PAYLOAD_INLINE NUMBER,
  FOLDER_PAYLOAD_EXTREF VARCHAR2(255)
);


  TYPE IOV_RECORD IS RECORD
( 
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME VARCHAR2(255),
  LASTMOD_DATE VARCHAR2(255),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER
);


  TYPE IOVEXT_RECORD IS RECORD
( 
  OBJECT_ID NUMBER,
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER,
  USER_TAG_ID NUMBER,
  SYS_INSTIME VARCHAR2(255),
  LASTMOD_DATE VARCHAR2(255),
  ORIGINAL_ID NUMBER,
  NEW_HEAD_ID NUMBER,
  CHANNAME VARCHAR2(255),
  TAGNAME VARCHAR2(255)
);

  TYPE IOVSHORT_RECORD IS RECORD
( 
  CHANNEL_ID NUMBER,
  IOV_SINCE NUMBER,
  IOV_UNTIL NUMBER
);

  TYPE IOVSTAT_RECORD IS RECORD
( 
  CHANNEL_ID NUMBER,
  MINIOV_SINCE NUMBER, 
  MINIOV_UNTIL NUMBER,
  MAXIOV_SINCE NUMBER,
  MAXIOV_UNTIL NUMBER,
  NIOVPERCHAN NUMBER
);


  TYPE TAG_RECORD IS RECORD
( 
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER, 
  SYS_INSTIME VARCHAR2(255)   
);

  TYPE GTAG_RECORD IS RECORD
( 
	NODE_ID NUMBER,
  TAG_ID NUMBER,
  TAG_NAME VARCHAR2(255),
  TAG_DESCRIPTION VARCHAR2(255), 
  TAG_LOCK_STATUS NUMBER,
  SYS_INSTIME VARCHAR2(255)   
);

  TYPE TAGEXT_RECORD IS RECORD
( 
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


  TYPE CHANNEL_RECORD IS RECORD 
( 
  CHANNEL_ID NUMBER,
  CHANNEL_NAME VARCHAR(255),
  DESCRIPTION VARCHAR2(255) 
);

/* Create cool table name for a given schema/db/folder: arg_tablename should be TAGS, IOVS, or CHANNELS 
 * This function is ment to be used only internally by the other API functions
 */
function f_Get_TableName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tablename VARCHAR2)  return varchar2 ;


/* Function to create a query and dump it: used only for debugging 
 *  
 */
function f_DumpQuery(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return varchar2 ;                          


/* Function to retrieve Nodes list with all branches, not only leaf nodes 
 * Given a full schema and db, the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_isleaf     : The query for isleaf flag: ex: 'node_isleaf>0'
 */
function f_Get_BranchNodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_isleaf VARCHAR2)  return cool_node_type_t pipelined ;                          

/* Function to retrieve Nodes list: only leaf nodes 
 * Given a full schema and db, the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_Get_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_node_type_t pipelined ;                          

/* Function to retrieve Nodes list (only leaf nodes) for a group of schemas and a given db 
 * Given a search string for schema and an exact name instance for db, 
 * the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_GetAll_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_schemanode_type_t pipelined ;                          

/* Function to retrieve Channel list in a schema/db/node 
 * Cannot access multiple nodes.
 */
function f_Get_Channels(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_channame varchar2) return cool_channel_type_t pipelined ;                          

/* Function to retrieve Channel list in a schema/db/node 
 * Can access multiple nodes in the same schema/db
 * The output list contains the schema, the node and the channel informations
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_channame   : The pattern channel name, ex: '%'
 */
function f_GetAll_Channels(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_channame varchar2) return cool_schemanodechannel_type_t pipelined ;                          
 
/* Function to retrieve Global Tags list in a schema/db
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_Get_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_tagname varchar2) return cool_gtag_type_t pipelined ;                          
 
/* Function to retrieve Global Tags list in a group of schemas 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_tagname varchar2) return cool_gtag_type_t pipelined ;                          

/* Function to retrieve Tags list in a node 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_Get_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname varchar2) return cool_tag_type_t pipelined ;                           
 
/* Function to retrieve All Tags in a schema pattern, node pattern 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname varchar2) return cool_schemanodetag_type_t pipelined ; 
                     
/* Function to retrieve All Tags in a schema pattern, node pattern , including the tags for branches
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_BranchTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname varchar2) return cool_schemanodetag_type_t pipelined ;                          

/* Function to retrieve Tags list in a given schema/db, associated to a given global tag name 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_TagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname varchar2) 
                     --return varchar2;
                     return cool_nodegtagtag_type_t pipelined ;                          

function f_GetAll_BranchTagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
                     return cool_nodegtagtag_type_t pipelined  ;
                     
/* Function to retrieve Tags list in a given schema/db, associated to a given global tag name 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_DoubleTagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname varchar2) 
                     --return varchar2;
                     return cool_nodegtagtag_type_t pipelined ;                          

 
/* Function to retrieve IOV list in a node and a tag: full iov list 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern tag name, ex: 'TAG%'
*/
function f_Get_Iovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return cool_iov_type_t pipelined;

/* Function to retrieve IOV ranges in all schemas, folders and tags associated to a given GLOBAL TAG.
 * The iov range is specified using the since and until parameters, and the iovbase argument as well.
 */
function f_Get_IovsRangeFldInGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname varchar2, 
                     arg_since NUMBER, arg_until NUMBER, arg_iovbase VARCHAR2) 
                     return COOL_SCNTIOV_TYPE_T pipelined;

/* Function to retrieve Last IOV in a node and a tag 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern tag name, ex: 'COMCOND%'
*/
function f_Get_LastIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return cool_iov_type_t pipelined;

/* Function to retrieve Last Num IOV in a node and a tag 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern tag name, ex: 'COMCOND%'
 *   - arg_niovs      : The number of IOVs per channel requested
*/
function f_Get_LastNumIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_niovs NUMBER)  
                     return cool_iovext_type_t pipelined;


/* Function to retrieve Last IOV in a node and a tag 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tablename  : The table name, ex: 'CONDBR2_F0030_IOVS'
 *   - arg_lastmod    : The time of last modification in that table
*/
function f_Get_LastModIov(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_objid NUMBER, arg_lastmod DATE) 
                     return cool_iovext_type_t pipelined; 
                     
/* Function to retrieve Single Version IOV list in a node: full iov list 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
*/
function f_Get_SvIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  
                     return cool_iov_type_t pipelined;

/* Function to retrieve IOV list in a node and a tag: full iov list but only channel_id, since and until are filled
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_Get_IovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return cool_iov_type_t pipelined;

/* Function to retrieve IOV statistic in a node and a tag: count niovs, nchannels and max and min for since and until
 * 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL_C%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_IovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return COOL_SCHEMANODEIOV_TYPE_T pipelined;

/* Function to retrieve IOV statistic in a node and a tag: count niovs, nchannels and max and min for since and until
 * Same Function as the previous one, but it contains more output on folder and tag
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL_C%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_TagsIovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return COOL_SCHEMANODETAGIOV_TYPE_T pipelined;

/* Function to retrieve IOV list in a node and a tag: only iovs in a range in time 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
 *   - arg_since      : The since time, in COOL format
 *   - arg_until      : The until time, in COOL format
*/
function f_Get_IovsRange(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_since NUMBER, arg_until NUMBER)  
                     return cool_iov_type_t pipelined;

/* Function to retrieve IOV list in a node and a tag: only iovs in a range in time 
 *   - arg_schemaname : The exact schema , ex: 'ATLAS_COOLOFL_CALO'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
 *   - arg_chanid     : The channel id to select
 *   - arg_since      : The since time, in COOL format
 *   - arg_until      : The until time, in COOL format
*/
function f_Get_IovsRangeForChannel(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_chanid NUMBER, arg_since NUMBER, arg_until NUMBER)  
                     return cool_iovext_type_t pipelined;
function f_Get_IovsRangeForChannelName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_channame VARCHAR2, arg_since NUMBER, arg_until NUMBER)  
                     return cool_iovext_type_t pipelined;
                      
/* Function to retrieve IOV statistic list in a node and a tag: full iov list is used to find 1st and last iovs per channel */
function f_Get_IovsStat(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  
                     return cool_iovstat_type_t pipelined;

/* Function to retrieve IOV statistic list in a node and a tag: full iov list is used to find 1st and last iovs per channel, and in a time range */
function f_Get_IovsRangeStat(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_since NUMBER, arg_until NUMBER) 
                     return cool_iovstat_type_t pipelined;

/* Function to retrieve IOV payload in a node and a tag and at a given time */
function f_Get_PayloadIov(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_time NUMBER, arg_channel NUMBER) 
                     return SYS_REFCURSOR;

/* Function to retrieve IOV payload in a node and a tag and at a given time */
function f_Get_PayloadIovByChanName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_time NUMBER, arg_channel VARCHAR2) 
                     return SYS_REFCURSOR;

/* Function to retrieve IOV payload in a node and a tag and at a given time */
function f_Get_PayloadIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_stime NUMBER,arg_etime NUMBER, arg_channel NUMBER) 
                     return SYS_REFCURSOR;

/* Function to retrieve IOV payload in a node and a tag and at a given time */
function f_Get_PayloadFieldsIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_stime NUMBER,arg_etime NUMBER, arg_channel NUMBER, 
                     arg_payloadstr VARCHAR2) 
                     return SYS_REFCURSOR;

 
/* Function to retrieve IOV payload in a node and a tag and at a given time */
function f_Get_PayloadIovsByChanName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_stime NUMBER,arg_etime NUMBER, arg_channel VARCHAR2) 
                     return SYS_REFCURSOR;
                     
                    
/* Function to convert the COOL time string in timestamp */
function convertToTimestamp(arg_time varchar2) return timestamp;

/* Function to convert the COOL node id into a table name */
function convertNodeIdToFolder(arg_nodeid number) return varchar2;

/* Function to test table existence in a given schema : does not really work...*/
function tableExists(arg_schemaname VARCHAR2, arg_tablename VARCHAR2) return NUMBER;

END COOL_SELECT_PKG;

/
