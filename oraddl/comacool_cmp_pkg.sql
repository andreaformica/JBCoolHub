--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package COMACOOL_CMP_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "ATLAS_COND_TOOLS"."COMACOOL_CMP_PKG" AUTHID CURRENT_USER  AS 

  /* TODO enter package declarations (types, exceptions, methods etc) here */ 
/* In this package we store all queries that are used to compare COMA and COOL contents */
/* These queries are used for cross checking the 2 databases */

  TYPE IOVEXT_POOLREF_RECORD IS RECORD
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
  TAGNAME VARCHAR2(255),
  PoolRef VARCHAR2(4000)
);


/* Function to compare Nodes list (only leaf nodes) for a group of schemas and a given db 
 * from COOL and from COMA at the same time.
 * Given a search string for schema and an exact name instance for db, 
 * the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_CmpAll_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return comacool_node_type_t pipelined ;                          

/* Function to retrieve Nodes list (only leaf nodes) for a group of schemas and a given db 
 * Given a search string for schema and an exact name instance for db, 
 * the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_Check_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return coma_cool_nodes_type_t pipelined ;                          

/* Function to retrieve Tag list (only leaf nodes) for a group of schemas and a given db 
 * Given a search string for schema and an exact name instance for db, 
 * the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern tag name, ex: '%BLK%'
 */
FUNCTION f_Check_Tags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN coma_cool_tag_type_t pipelined;

function f_CmpAll_NodesDiff(arg_schemaname VARCHAR2)  return comacool_node_type_t pipelined ;                          

FUNCTION f_CmpAll_Tags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN comacool_tag_type_t pipelined;

FUNCTION f_CmpAll_TagsDiff(arg_schemaname VARCHAR2) RETURN comacool_tag_type_t pipelined;


FUNCTION f_CmpAll_GTags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN comacool_gtag_type_t pipelined;


function f_Get_PoolNodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_iovpoolref_type_t pipelined ;                          


END COMACOOL_CMP_PKG;

/
