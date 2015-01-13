--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body COMA_SELECT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "ATLAS_COND_TOOLS"."COMA_SELECT_PKG" AS

  preschemaName VARCHAR2(10) := 'ATLAS_';
  comaSchemaName VARCHAR2(30) := 'ATLAS_TAGS_METADATA';

  function f_GetAll_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_schemanode_type_t pipelined  AS

  v_schemaname varchar2(30); 
  v_iovbase varchar2(30);
  v_iovtype varchar2(50);
  fldmod timestamp(6);
  v_folderpayloadspec varchar2(4000);

  cursor node_cursor is
    select * from cb_view_nodes where owner_name like v_schemaname and cbi_name=arg_dbname and node_fullpath like arg_folder;

  v_node_row node_cursor%ROWTYPE; 
  
  begin
  
  v_schemaname := REPLACE (arg_schemaname,preschemaName);


  -- Open cursor & specify bind argument in USING clause:
--    
--   dbms_output.put_line('parsing node table ' || v_schemaname || '.' || arg_dbname || '_' || 'NODES');

-- Fetch rows from result set one at a time:

  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_iovbase := 'unknown';
    v_iovtype := 'unknown';
    v_schemaname := preschemaName ||  v_node_row.owner_name ;
    fldmod := cool_select_pkg.convertToTimestamp(v_node_row.node_instime);
    begin
    select extractValue(xmltype('<mynode>' || v_node_row.node_description || '</mynode>'),'/mynode/timeStamp'), 
           extractValue(xmltype('<mynode>' || v_node_row.node_description || '</mynode>'),'/mynode/typeName') into v_iovbase,v_iovtype 
    from dual;
    exception
      when OTHERS then
        dbms_output.put_line('Skip ' || v_node_row.node_description);
    end;

    v_folderpayloadspec :=  createFolderPayloadSpec(v_node_row.cbf_index);

    pipe row(cool_schemanode_type(v_schemaname,arg_dbname,v_node_row.node_id,
                            v_node_row.node_name,
                            v_node_row.node_fullpath,
                            v_node_row.node_description,
                            v_node_row.node_isleaf,
                            v_node_row.node_instime,
                            fldmod,
                            v_node_row.lastmod_date,
                            v_node_row.folder_versioning,
                            v_folderpayloadspec, 
                            v_node_row.folder_iovtablename,
                            v_node_row.folder_tagtablename,
                            v_node_row.folder_channeltablename,
                            v_iovbase,
                            v_iovtype));

  END LOOP;
  CLOSE node_cursor;
    
  
  END f_GetAll_Nodes;
  
  
  /*************************************************/
  function f_GetAll_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_tagname VARCHAR2)  return cool_gtag_type_t pipelined  AS
 
  v_schemaname varchar2(30); 
  
  cursor tag_cursor is
    select * from cb_view_schemagts where schema_name like v_schemaname AND gtag_name like arg_tagname AND db_name=arg_dbname;


  v_tag_row tag_cursor%ROWTYPE;
     
  begin

  v_schemaname := REPLACE (arg_schemaname,preschemaName);

  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;
    
    v_schemaname := preschemaName ||  v_tag_row.schema_name ;

    pipe row(cool_gtag_type(v_schemaname,arg_dbname,v_tag_row.node_id,v_tag_row.tag_id,
      v_tag_row.gtag_name,v_tag_row.gtag_description,v_tag_row.gtag_lock_status,v_tag_row.sysdate_instime));

  END LOOP;
  CLOSE tag_cursor;
  
 
  END f_GetAll_GlobalTags;

/*************************************************/
  function f_GetAll_GTagsForTag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_tagname VARCHAR2) 
                     --return varchar2 AS
                     return cool_nodegtagtag_type_t pipelined  AS
 
  v_schemaname varchar2(30);
  gtagid NUMBER;
  
  cursor v_tag_cursor is
    select * from cb_view_ftgts where cool_schema like v_schemaname and db_name=arg_dbname and ftag_name like arg_tagname;
     
  v_row v_tag_cursor%ROWTYPE;
     
  begin
  
  v_schemaname := REPLACE (arg_schemaname,preschemaName);

  OPEN v_tag_cursor;
    LOOP
      FETCH v_tag_cursor INTO v_row; 
    EXIT
    WHEN v_tag_cursor%NOTFOUND;
 
     v_schemaname := preschemaName ||  v_row.cool_schema ;

     pipe row(cool_nodegtagtag_type(v_schemaname,arg_dbname,
      v_row.gtag_id, v_row.gtag_name, v_row.gtag_desc, v_row.gtlock,
      v_row.node_id, v_row.node_name, v_row.node_fullpath,
      v_row.node_description,
      v_row.ftag_id,v_row.ftag_name,
      v_row.ftag_desc,v_row.ftlock, v_row.SYS_INSTIME));

  END LOOP;
  CLOSE v_tag_cursor;
    
  END f_GetAll_GTagsForTag;
  

/*************************************************/
  function f_GetAll_TagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
                     --return varchar2 AS
                     return cool_nodegtagtag_type_t pipelined  AS
 
  v_schemaname varchar2(30);
  gtagid NUMBER;
  
  cursor v_tag_cursor is
    select * from cb_view_ftgts where cool_schema like v_schemaname and db_name=arg_dbname and gtag_name like arg_globaltagname;
     
  v_row v_tag_cursor%ROWTYPE;
     
  begin
  
  v_schemaname := REPLACE (arg_schemaname,preschemaName);

  OPEN v_tag_cursor;
    LOOP
      FETCH v_tag_cursor INTO v_row; 
    EXIT
    WHEN v_tag_cursor%NOTFOUND;
 
     v_schemaname := preschemaName ||  v_row.cool_schema ;

     pipe row(cool_nodegtagtag_type(v_schemaname,arg_dbname,
      v_row.gtag_id, v_row.gtag_name, v_row.gtag_desc, v_row.gtlock,
      v_row.node_id, v_row.node_name, v_row.node_fullpath,
      v_row.node_description,
      v_row.ftag_id,v_row.ftag_name,
      v_row.ftag_desc,v_row.ftlock, v_row.SYS_INSTIME));

  END LOOP;
  CLOSE v_tag_cursor;
    
  END f_GetAll_TagsForGtag;
  
/*************************************************/
  function f_GetAll_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_schemanodetag_type_t pipelined  AS
 
  v_schemaname varchar2(30);
  
  cursor tag_cursor is
    select * from cb_view_tags where schema_name like v_schemaname and db_name=arg_dbname and node_fullpath like arg_folder and tag_name like arg_tagname;


  v_tag_row tag_cursor%ROWTYPE;
  
  fldmod timestamp;   
     
  begin

  v_schemaname := REPLACE (arg_schemaname,preschemaName);

  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;

    v_schemaname := preschemaName ||  v_tag_row.schema_name ;
    fldmod := cool_select_pkg.convertToTimestamp(v_tag_row.lastmod_date);
    pipe row(cool_schemanodetag_type(v_schemaname,arg_dbname,
              v_tag_row.node_id,v_tag_row.node_name,v_tag_row.node_fullpath,
              v_tag_row.node_description,v_tag_row.node_isleaf,v_tag_row.node_instime,
              fldmod,
              v_tag_row.lastmod_date,v_tag_row.folder_iovtablename,
              v_tag_row.folder_tagtablename,v_tag_row.folder_channeltablename,
              v_tag_row.tag_id,v_tag_row.tag_name,
              v_tag_row.tag_description,v_tag_row.tag_lock_status, v_tag_row.SYS_INSTIME));

  END LOOP;
  CLOSE tag_cursor;

 
  END f_GetAll_Tags;

  
  /*************************************************/
  function createFolderPayloadSpec(arg_cbfindex NUMBER) return VARCHAR2 AS
  
  v_folderpayloadspec varchar2(4000);
  cursor payload_cursor is
    select * from ATLAS_TAGS_METADATA.coma_cb_pcolumns where cbf_index=arg_cbfindex;
 
  v_pcolumns_row payload_cursor%ROWTYPE; 
  v_folderpayloadfield varchar2(100);
  v_folderpayloadtype varchar2(100);
 
  BEGIN
  
  v_folderpayloadspec := ',';
  OPEN payload_cursor;
  LOOP
    FETCH payload_cursor INTO v_pcolumns_row;
    EXIT WHEN payload_cursor%NOTFOUND;
    v_folderpayloadfield := v_pcolumns_row.cbp_column;
    v_folderpayloadtype := v_pcolumns_row.cbp_type;
    v_folderpayloadspec := v_folderpayloadspec || v_folderpayloadfield || ':' || v_folderpayloadtype || ',';
  
  END LOOP;
  CLOSE payload_cursor;
  v_folderpayloadspec := TRIM (TRAILING ',' FROM v_folderpayloadspec);
  RETURN v_folderpayloadspec;
  
  END createFolderPayloadSpec;

END COMA_SELECT_PKG;

/
