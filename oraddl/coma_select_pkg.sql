--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package COMA_SELECT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE "ATLAS_COND_TOOLS"."COMA_SELECT_PKG" AS 

/* Function to retrieve Nodes list (only leaf nodes) for a group of schemas and a given db 
 * Given a search string for schema and an exact name instance for db, 
 * the function retrieves the list of folders matching the string arg_folder
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 */
function f_GetAll_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_schemanode_type_t pipelined ;                          

function f_GetAll_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_tagname VARCHAR2)  return cool_gtag_type_t pipelined;

function f_GetAll_TagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname varchar2) 
                     return cool_nodegtagtag_type_t pipelined ;      
     
function f_GetAll_GTagsForTag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_tagname VARCHAR2) 
                     return cool_nodegtagtag_type_t pipelined;
                     
/* Function to retrieve All Tags in a schema pattern, node pattern 
 *   - arg_schemaname : The pattern schema , ex: 'ATLAS_COOLOFL%'
 *   - arg_dbname     : The exact instance , ex: 'COMP200'
 *   - arg_folder     : The pattern folder name, ex: '/XXX%'
 *   - arg_tagname    : The pattern global tag name, ex: 'COMCOND%'
*/
function f_GetAll_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname varchar2) return cool_schemanodetag_type_t pipelined ;                          

                     
function createFolderPayloadSpec(arg_cbfindex NUMBER) return VARCHAR2;

END COMA_SELECT_PKG;

/
