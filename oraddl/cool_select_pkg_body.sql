--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body COOL_SELECT_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "ATLAS_COND_TOOLS"."COOL_SELECT_PKG" AS


function convertNodeIdToFolder(arg_nodeid number) return varchar2 as
  folderstr varchar2(10);
  begin
  folderstr := trim(TO_CHAR(arg_nodeid,'0009'));
  return 'F'|| folderstr;
end convertNodeIdToFolder;

/*************************************************/
function tableExists(arg_schemaname VARCHAR2, arg_tablename VARCHAR2) return NUMBER as
  tbl_exists NUMBER;
  rolename varchar2(30);
  plsqlblock varchar(100);
  begin
  rolename := 'ATLAS_COOLREAD_ROLE'; 
  plsqlblock := 'set role ' || rolename;
--  execute immediate 'set role ATLAS_COOLREAD_ROLE';
--  dbms_session.set_role('ATLAS_COOLREAD_ROLE');
  select count(table_name) into tbl_exists from SYS.all_tables where table_name = arg_tablename and owner = arg_schemaname;
  return tbl_exists;
end tableExists;

/*************************************************/
/* Retrieve statistic on set of iovs on a folder */
function f_Get_IovsRangeStat(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2,arg_since NUMBER, arg_until NUMBER) 
                     return cool_iovstat_type_t pipelined  AS
     
  cursor iov_cursor is
    select 
      channel_id, min(iov_since) as miniov_since, min(iov_until) as miniov_until,
      max(iov_since) as maxiov_since, max(iov_until) as maxiov_until, count(channel_id) as niovperchan 
        from table(f_get_iovsrange(arg_schemaname,arg_dbname,arg_folder,arg_tagname,arg_since,arg_until)) group by channel_id;
  
  v_row iovstat_record;
  
  begin
  
  OPEN iov_cursor;
    LOOP
      FETCH iov_cursor INTO v_row;
      EXIT
    WHEN iov_cursor%NOTFOUND;
    pipe row(cool_iovstat_type(v_row.channel_id, v_row.miniov_since,v_row.miniov_until,
                              v_row.maxiov_since,v_row.maxiov_until,v_row.niovperchan));
  END LOOP;
  CLOSE iov_cursor;

  END f_Get_IovsRangeStat;

/*************************************************/
  function f_DumpQuery(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return varchar2  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(cool_select_pkg.f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
    
  iovstablename varchar2(255);
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  issingleversion number;
  tagtablejoin varchar2(4000);
  tagsearch varchar2(1000);
  tagfield varchar2(50);
  headsearch varchar2(50);
  
  v_tagname varchar2(255);
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 
 
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor; 
  
  foldernamestr := convertNodeIdToFolder(node_id);
  dbms_output.put_line('folder name is ' || foldernamestr);


  v_tagname := NULL;
  tagfield := ' , null as tag_name  ';
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
                    'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND ';
     v_tagname := arg_tagname;
     tagfield := ',tags.tag_name ';
  headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;


    --create a query for nodes --
  iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name ' ||
  tagfield ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
--  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  ' where ' || tagsearch ||  headsearch ||
  '  iovs.channel_id=channels.channel_id AND ' ||
  '  (iovs.iov_since >=COALESCE(( SELECT MAX(iovs2.iov_since) FROM ' || iovstablename || ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  ' AND iovs.iov_since <= :etime) ';

    --create a query for nodes --
/*
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name ' ||
  tagfield ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  ' where ' || tagsearch ||  ' (iovs.new_head_id=0) '||
  ' AND (((iovs.iov_since >= :stime) AND (iovs.iov_since <= :etime)) OR ((iovs.iov_until >= :stime) AND (iovs.iov_until <= :etime)) OR ((:stime > iovs.iov_since) AND (:stime < iovs.iov_until))) ';
*/

/*
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  --  cursor node_cursor is
--    select node_id from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));

  cursor tag_cursor is
    select tag_id from table(f_get_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname));

 
   
  TYPE NodeCurTyp  IS REF CURSOR;
  v_node_cursor    NodeCurTyp; 
  v_row node_record;
  
  fldmod timestamp(6);
   
   
  tag_id number;
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  tagsearch varchar2(255);
  tagjoin varchar2(1024);
  tagnameoutput varchar2(30);
 
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

    --create a query for nodes --
  v_stmt_str := 'select ' || 
  'node_id,' ||
  'node_name,' || 
  'node_fullpath,' || 
  'node_description,' || 
  'node_isleaf,' || 
  'node_instime,' || 
  'lastmod_date,' || 
  'folder_iovtablename,' ||  
  'folder_tagtablename,' || 
  'folder_channeltablename' || 
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || 'nodes' || ' nodes ' || 
  ' where node_isleaf=1 and node_fullpath like :nfp' ;
      
  -- Open cursor & specify bind argument in USING clause:
--    
  dbms_output.put_line('parsing table ' || arg_schemaname || '.' || arg_dbname || '_' || 'NODES');

  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
*/  
    /* Selecting table name for folder */
--  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'IOVS') into foldernamestr from dual;
/*
  foldernamestr := convertNodeIdToFolder(node_id);


  tag_id := 1;
  tagsearch := '';
  tagjoin := '';
  tagnameoutput := ' null as tag_name';
  IF issingleversion > 0 THEN
  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO tag_id;
      EXIT
    WHEN tag_cursor%NOTFOUND;
  END LOOP;
  CLOSE tag_cursor;
  tagnameoutput := ' tags.tag_name'; 
  tagjoin := ' left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' || cool_db_link || ' tags ' ||
              'on iovs.user_tag_id=tags.tag_id '; 
  tagsearch := ' (tags.tag_name = :tagname) AND ';
  END IF;

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name, ' ||
  tagnameoutput ||
--  'tags.tag_name' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' || cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagjoin ||
  ' where ' || tagsearch || '(iovs.new_head_id=0)';
--  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' || cool_db_link || ' tags ' ||
--  'on iovs.user_tag_id=tags.tag_id ' ||
--  ' where (tags.tag_name = :tagname) AND (iovs.new_head_id=0)';
*/      
--  dbms_output.put_line('executing query ' || v_stmt_str);


-- Fetch rows from result set one at a time:
  return v_stmt_str;
  
  END f_DumpQuery;

/*************************************************/
  function f_Get_BranchNodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_isleaf VARCHAR2)  return cool_node_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  TYPE NodeCurTyp  IS REF CURSOR;
  v_node_cursor    NodeCurTyp; 
  v_row node_record;
  
  fldmod timestamp(6);
  tbl_exist number; 
  tablename varchar2(50);
  nodeisleaf varchar2(30);
  
  begin
  
  nodeisleaf := ' node_isleaf = 1 and ';
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  tbl_exist := 1;
  tablename := arg_dbname || '_' || 'nodes' ;
  
  if arg_isleaf is not NULL then
     nodeisleaf := arg_isleaf || ' and ' ;
  end if;
  --select tableExists(arg_schemaname,tablename) into tbl_exist from dual;

  --create a query for nodes --
    
    
  v_stmt_str := 'select ' || 
  'node_id,' ||
  'node_name,' || 
  'node_fullpath,' || 
  'node_description,' || 
  'node_isleaf,' || 
  'node_instime,' || 
  'lastmod_date,' || 
  'folder_versioning,' ||  
  'dbms_lob.substr(folder_payloadspec,4000,1) as folder_payloadspec, ' ||
  'folder_iovtablename,' ||  
  'folder_tagtablename,' || 
  'folder_channeltablename' || 
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || 'nodes' || cool_db_link || ' nodes ' || 
  ' where ' || nodeisleaf || ' node_fullpath like :nfp' ;
      
  -- Open cursor & specify bind argument in USING clause:
--    
--  dbms_output.put_line('parsing table ' || arg_schemaname || '.' || arg_dbname || '_' || 'NODES');
   
    if tbl_exist > 0 then 


-- Fetch rows from result set one at a time:

  OPEN v_node_cursor FOR v_stmt_str USING arg_folder;
  LOOP
    FETCH v_node_cursor INTO v_row;
  EXIT WHEN v_node_cursor%NOTFOUND; 
    fldmod := convertToTimestamp(v_row.node_instime);
    pipe row(cool_node_type(v_row.node_id,v_row.node_name,v_row.node_fullpath,
                            v_row.node_description,v_row.node_isleaf,v_row.node_instime,
                            fldmod,
                            v_row.lastmod_date,v_row.folder_versioning, v_row.folder_payloadspec, v_row.folder_iovtablename,
                            v_row.folder_tagtablename,v_row.folder_channeltablename));
  END LOOP;
  -- Close cursor on channels :
  CLOSE v_node_cursor; 
  else 
    raise_application_error(-20003, 'Error in schema ' || arg_schemaname || ' and table ' || tablename);
  end if;
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || arg_schemaname);
--      raise_application_error(-20001, 'Error in schema ' || v_schemaname);
    

  END f_Get_BranchNodes;


/*************************************************/
  function f_Get_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_node_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  TYPE NodeCurTyp  IS REF CURSOR;
  v_node_cursor    NodeCurTyp; 
  v_row node_record;
  
  fldmod timestamp(6);
  tbl_exist number; 
  tablename varchar2(50);

  begin

--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  tbl_exist := 1;
  tablename := arg_dbname || '_' || 'nodes' ;
  
  --select tableExists(arg_schemaname,tablename) into tbl_exist from dual;

  --create a query for nodes --
    
    
  v_stmt_str := 'select ' || 
  'node_id,' ||
  'node_name,' || 
  'node_fullpath,' || 
  'node_description,' || 
  'node_isleaf,' || 
  'node_instime,' || 
  'lastmod_date,' || 
  'folder_versioning,' ||  
  'dbms_lob.substr(folder_payloadspec,4000,1) as folder_payloadspec, ' ||
  'folder_iovtablename,' ||  
  'folder_tagtablename,' || 
  'folder_channeltablename' || 
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || 'nodes' || cool_db_link || ' nodes ' || 
  ' where node_isleaf=1 and node_fullpath like :nfp' ;
      
  -- Open cursor & specify bind argument in USING clause:
--    
--  dbms_output.put_line('parsing table ' || arg_schemaname || '.' || arg_dbname || '_' || 'NODES');
   
  if tbl_exist > 0 then 


-- Fetch rows from result set one at a time:

  OPEN v_node_cursor FOR v_stmt_str USING arg_folder;
  LOOP
    FETCH v_node_cursor INTO v_row;
  EXIT WHEN v_node_cursor%NOTFOUND; 
    fldmod := convertToTimestamp(v_row.node_instime);
    pipe row(cool_node_type(v_row.node_id,v_row.node_name,v_row.node_fullpath,
                            v_row.node_description,v_row.node_isleaf,v_row.node_instime,
                            fldmod,
                            v_row.lastmod_date,v_row.folder_versioning, v_row.folder_payloadspec, v_row.folder_iovtablename,
                            v_row.folder_tagtablename,v_row.folder_channeltablename));
  END LOOP;
  -- Close cursor on channels :
  CLOSE v_node_cursor; 
  else 
    raise_application_error(-20003, 'Error in schema ' || arg_schemaname || ' and table ' || tablename);
  end if;
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || arg_schemaname);
--      raise_application_error(-20001, 'Error in schema ' || v_schemaname);
    

  END f_Get_Nodes;

/*************************************************/
/* Function to retrieve node from every schema in one shot */
/*************************************************/
  function f_GetAll_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cool_schemanode_type_t pipelined  AS
 
   
  cool_db_link    varchar2(100);
  v_schemaname varchar2(30);
  v_iovbase varchar2(30);
  v_iovtype varchar2(50);
  
  cursor schema_cursor is
    select username from SYS.ALL_USERS where (username like 'ATLAS_COOLO%' and username not like '%_W') and username like arg_schemaname;

  cursor node_cursor is
    select * from table(cool_select_pkg.f_Get_Nodes(v_schemaname,arg_dbname,arg_folder));

  v_node_row node_cursor%ROWTYPE;
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  -- Open cursor & specify bind argument in USING clause:
--    
--   dbms_output.put_line('parsing node table ' || v_schemaname || '.' || arg_dbname || '_' || 'NODES');

-- Fetch rows from result set one at a time:

  OPEN schema_cursor;
    LOOP
      FETCH schema_cursor INTO v_schemaname;
    EXIT
    WHEN schema_cursor%NOTFOUND;
      
  begin   
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_iovbase := 'unknown';
    v_iovtype := 'unknown';
    begin
    select extractValue(xmltype('<mynode>' || v_node_row.node_description || '</mynode>'),'/mynode/timeStamp'), 
           extractValue(xmltype('<mynode>' || v_node_row.node_description || '</mynode>'),'/mynode/typeName') into v_iovbase,v_iovtype 
    from dual;
    exception
      when OTHERS then
        dbms_output.put_line('Skip ' || v_node_row.node_description);
    end;

    pipe row(cool_schemanode_type(v_schemaname,arg_dbname,v_node_row.node_id,
                            v_node_row.node_name,
                            v_node_row.node_fullpath,
                            v_node_row.node_description,
                            v_node_row.node_isleaf,
                            v_node_row.node_instime,
                            v_node_row.node_tinstime,
                            v_node_row.lastmod_date,
                            v_node_row.folder_versioning,
                            v_node_row.folder_payloadspec,
                            v_node_row.folder_iovtablename,
                            v_node_row.folder_tagtablename,
                            v_node_row.folder_channeltablename,
                            v_iovbase,
                            v_iovtype));

  END LOOP;
  CLOSE node_cursor;
  
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || v_schemaname);
      raise_application_error(-20001, 'Error in schema ' || v_schemaname);
    
  end;
  
  END LOOP;
  -- Close cursor on schemas :
  CLOSE schema_cursor; 
  
  
  END f_GetAll_Nodes;




/*************************************************/
  function f_Get_Channels(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_channame VARCHAR2)  return cool_channel_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
--  cursor node_cursor is
--    select node_id from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
--  node_id number;
   
  TYPE ChanCurTyp  IS REF CURSOR;
  v_chan_cursor    ChanCurTyp; 
  v_row channel_record;
  
  foldernamestr varchar2(100);
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  /* Selecting table name for TAGS */
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'CHANNELS') into foldernamestr from dual;

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'channel_id,' || 
  'channel_name,' || 
  'description' ||
  ' from ' 
  || arg_schemaname || '.' || foldernamestr || cool_db_link || ' channels ' ||
  ' where (channel_name is not null and channel_name like :chn) or (channel_name is null)';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_chan_cursor FOR v_stmt_str using arg_channame;
  LOOP
    FETCH v_chan_cursor INTO v_row;
  EXIT WHEN v_chan_cursor%NOTFOUND; 
    pipe row(cool_channel_type(v_row.channel_id,v_row.channel_name,v_row.description));
  END LOOP;
  -- Close cursor on channels 
  CLOSE v_chan_cursor; 

  END f_Get_Channels;

/*************************************************/
  function f_GetAll_Channels(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_channame VARCHAR2)  return cool_schemanodechannel_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_schemaname varchar2(30);
  v_nodename   varchar2(255);
  
  cursor schema_cursor is
    select username from SYS.ALL_USERS where (username like 'ATLAS_COOLO%' and username not like '%_W') and username like arg_schemaname;

  cursor node_cursor is
    select * from table(cool_select_pkg.f_Get_Nodes(v_schemaname,arg_dbname,arg_folder));

  cursor channel_cursor is
    select * from table(cool_select_pkg.f_Get_Channels(v_schemaname,arg_dbname,v_nodename,arg_channame));


  v_node_row node_cursor%ROWTYPE;
  v_channel_row channel_cursor%ROWTYPE;
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  -- Open cursor & specify bind argument in USING clause:
--    
--   dbms_output.put_line('parsing node table ' || v_schemaname || '.' || arg_dbname || '_' || 'NODES');

-- Fetch rows from result set one at a time:

  OPEN schema_cursor;
    LOOP
      FETCH schema_cursor INTO v_schemaname;
    EXIT
    WHEN schema_cursor%NOTFOUND;
      
  begin   
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_nodename := v_node_row.node_fullpath;
  OPEN channel_cursor;
  LOOP
    FETCH channel_cursor INTO v_channel_row;
    EXIT WHEN channel_cursor%NOTFOUND;
    
    pipe row(cool_schemanodechannel_type(
                  v_schemaname,arg_dbname,v_node_row.node_id,v_node_row.node_name,v_node_row.node_fullpath,
                            v_node_row.node_description,v_node_row.node_isleaf,v_node_row.node_instime,
                            v_node_row.node_tinstime,
                            v_node_row.lastmod_date,v_node_row.folder_iovtablename,
                            v_node_row.folder_tagtablename,v_node_row.folder_channeltablename,
                  v_channel_row.channel_id,v_channel_row.channel_name,v_channel_row.description));

  END LOOP;
  CLOSE channel_cursor;
  END LOOP;
  CLOSE node_cursor;
  
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || v_schemaname);
      raise_application_error(-20001, 'Error in schema ' || v_schemaname);
    
  end;
  
  END LOOP;
  -- Close cursor on schemas :
  CLOSE schema_cursor; 

  END f_GetAll_Channels;

/*************************************************/
  function f_Get_TableName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tablename VARCHAR2)  return varchar2   AS
    
--  cursor node_cursor is
--    select node_id,FOLDER_IOVTABLENAME,FOLDER_TAGTABLENAME,FOLDER_CHANNELTABLENAME 
--      from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
--  node_id number;
  nodefldrec node_fld_record; 
   
  foldernamestr varchar2(255);
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  TYPE NodeCurTyp  IS REF CURSOR;
  v_node_cursor    NodeCurTyp; 
  
  tbl_exist number; 
  tablename varchar2(50);

  begin

--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  tbl_exist := 1;
  
    v_stmt_str := 'select ' || 
  'node_id,' ||
  'folder_iovtablename,' ||  
  'folder_tagtablename,' || 
  'folder_channeltablename,' || 
  'folder_payload_inline,' ||  
  'to_char(folder_payload_extref) as folder_payload_extref ' ||  
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || 'nodes' || cool_db_link || ' nodes ' || 
  ' where node_isleaf=1 and node_fullpath like :nfp' ;
      
  -- Open cursor & specify bind argument in USING clause:
--    
--  dbms_output.put_line('parsing table ' || arg_schemaname || '.' || arg_dbname || '_' || 'NODES');
   
    if tbl_exist > 0 then 


-- Fetch rows from result set one at a time:

  OPEN v_node_cursor FOR v_stmt_str USING arg_folder;
  LOOP
    FETCH v_node_cursor INTO nodefldrec;
  EXIT WHEN v_node_cursor%NOTFOUND; 
    IF arg_tablename = 'TAGS' THEN
    foldernamestr := nodefldrec.folder_tagtablename;
    ELSIF arg_tablename = 'IOVS' THEN
    foldernamestr := nodefldrec.folder_iovtablename;
    ELSIF arg_tablename = 'CHANNELS' THEN
    foldernamestr := nodefldrec.folder_channeltablename;
    ELSIF arg_tablename = 'PAYLOAD' THEN
    IF nodefldrec.FOLDER_PAYLOAD_INLINE > 0 THEN
    foldernamestr := nodefldrec.FOLDER_PAYLOAD_EXTREF;
    ELSE
    foldernamestr := NULL;
    END IF;
    END IF;

  END LOOP;
  -- Close cursor on channels :
  CLOSE v_node_cursor; 

  
/*  
  select node_id,FOLDER_IOVTABLENAME,FOLDER_TAGTABLENAME,FOLDER_CHANNELTABLENAME, FOLDER_PAYLOAD_INLINE, TO_CHAR(FOLDER_PAYLOAD_EXTREF) as FOLDER_PAYLOAD_EXTREF 
    into nodefldrec from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));

    IF arg_tablename = 'TAGS' THEN
    foldernamestr := nodefldrec.folder_tagtablename;
    ELSIF arg_tablename = 'IOVS' THEN
    foldernamestr := nodefldrec.folder_iovtablename;
    ELSIF arg_tablename = 'CHANNELS' THEN
    foldernamestr := nodefldrec.folder_channeltablename;
    ELSIF arg_tablename = 'PAYLOAD' THEN
    IF nodefldrec.FOLDER_PAYLOAD_INLINE > 0 THEN
    foldernamestr := nodefldrec.FOLDER_PAYLOAD_EXTREF;
    ELSE
    foldernamestr := nodefldrec.folder_iovtablename;
    END IF;
    END IF;
*/    
    RETURN foldernamestr;
  END IF;
  EXCEPTION
    WHEN TOO_MANY_ROWS THEN  
       foldernamestr := 'TOO_MANY_ROWS';
       RETURN foldernamestr;
       
  END f_Get_TableName;

/*************************************************/
  function f_Get_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                      arg_tagname VARCHAR2)  return cool_gtag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
   
  TYPE TagCurTyp  IS REF CURSOR;
  v_tag_cursor    TagCurTyp; 
  v_row gtag_record;
   
  taginstime timestamp(6);
  tbl_exist number;
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  /* Check if table exists */
  tbl_exist := 1;
--  select count(*) into tbl_exist from all_tables where table_name = foldernamestr and owner = arg_schemaname;
  if tbl_exist > 0 then 


    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'node_id,' ||
  'tag_id,' || 
  'tag_name,' || 
  'tag_description,' ||
  'tag_lock_status,' ||
  'sys_instime' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_TAGS' || cool_db_link || ' tags ' ||
  ' where ((tag_name is not null and tag_name like :chn) or (tag_name is null)) and node_id=0 ';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_tag_cursor FOR v_stmt_str using arg_tagname;
  LOOP
    FETCH v_tag_cursor INTO v_row;
  EXIT WHEN v_tag_cursor%NOTFOUND;
    taginstime := convertToTimestamp(v_row.sys_instime);
    pipe row(cool_gtag_type(arg_schemaname,arg_dbname,v_row.node_id,v_row.tag_id,v_row.tag_name,v_row.tag_description,v_row.tag_lock_status,taginstime));
  END LOOP; 
  -- Close cursor on channels 
  CLOSE v_tag_cursor; 
  end if;
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || arg_schemaname);

  END f_Get_GlobalTags;

/*************************************************/
  function f_GetAll_GlobalTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_tagname VARCHAR2)  return cool_gtag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_schemaname varchar2(30);
  
  cursor schema_cursor is
    select username from SYS.ALL_USERS where (username like 'ATLAS_COOLO%' and username not like '%_W') and username like arg_schemaname;

  cursor tag_cursor is
    select * from table(cool_select_pkg.f_Get_GlobalTags(v_schemaname,arg_dbname,arg_tagname));


  v_tag_row tag_cursor%ROWTYPE;
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 
  OPEN schema_cursor;
    LOOP
      FETCH schema_cursor INTO v_schemaname;
    EXIT
    WHEN schema_cursor%NOTFOUND;
  begin   
  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;
    pipe row(cool_gtag_type(v_schemaname,arg_dbname,v_tag_row.node_id,v_tag_row.tag_id,v_tag_row.tag_name,v_tag_row.tag_description,v_tag_row.tag_lock_status,v_tag_row.sys_instime));

  END LOOP;
  CLOSE tag_cursor;
  
  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname );
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  END LOOP;
  -- Close cursor on schemas :
  CLOSE schema_cursor; 
 
  END f_GetAll_GlobalTags;

/*************************************************/
  function f_Get_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_tag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
--  cursor node_cursor is
--    select node_id,FOLDER_IOVTABLENAME,FOLDER_TAGTABLENAME,FOLDER_CHANNELTABLENAME from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
--  node_id number;
   
  TYPE TagCurTyp  IS REF CURSOR;
  v_tag_cursor    TagCurTyp; 
  v_row tag_record;
--  nodefldrec node_fld_record; 
   
--  foldernamestr varchar2(255);
  taginstime timestamp(6);
  tbl_exist number;
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  /* Selecting table name for TAGS */
--  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'TAGS') into foldernamestr from dual;

  /* Check if table exists */
  tbl_exist := 1;
--  select count(*) into tbl_exist from all_tables where table_name = foldernamestr and owner = arg_schemaname;
  if tbl_exist > 0 then 


    --create a query for tags --
  v_stmt_str := 'select ' ||
  'tag_id,' || 
  'tag_name,' || 
  'tag_description,' ||
  'tag_lock_status,' ||
  'sys_instime' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_TAGS' || cool_db_link || ' tags ' ||
  ' LEFT JOIN '
  || arg_schemaname || '.' || arg_dbname || '_NODES' || cool_db_link || ' nodes ' ||
  ' ON tags.node_id=nodes.node_id ' || 
  ' where nodes.node_fullpath like :fld and (tag_name is not null and tag_name like :chn) or (tag_name is null)';
 
--  raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and folder ' || v_stmt_str);

--   || arg_schemaname || '.' || foldernamestr || cool_db_link || ' tags ' ||
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_tag_cursor FOR v_stmt_str using arg_folder, arg_tagname;
  LOOP
    FETCH v_tag_cursor INTO v_row;
  EXIT WHEN v_tag_cursor%NOTFOUND;
    taginstime := convertToTimestamp(v_row.sys_instime);
    pipe row(cool_tag_type(v_row.tag_id,v_row.tag_name,v_row.tag_description,v_row.tag_lock_status,taginstime));
  END LOOP; 
  -- Close cursor on channels 
  CLOSE v_tag_cursor; 
  end if;
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || arg_schemaname || ' and folder ' || arg_folder);

--  pipe row(cool_tag_type(node_id, foldernamestr, v_stmt_str,null));
  END f_Get_Tags;

/*************************************************/
  function f_GetAll_Tags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_schemanodetag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  
  cursor schema_cursor is
    select username from SYS.ALL_USERS where (username like 'ATLAS_COOLO%' and username not like '%_W') and username like arg_schemaname;

  cursor node_cursor is
    select * from table(cool_select_pkg.f_Get_Nodes(v_schemaname,arg_dbname,arg_folder));

  cursor tag_cursor is
    select * from table(cool_select_pkg.f_Get_Tags(v_schemaname,arg_dbname,v_nodefullpath,arg_tagname));


  v_tag_row tag_cursor%ROWTYPE;
  v_node_row node_cursor%ROWTYPE;
  
  fldmod timestamp;   
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 
  OPEN schema_cursor;
    LOOP
      FETCH schema_cursor INTO v_schemaname;
    EXIT
    WHEN schema_cursor%NOTFOUND;
  begin   
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_nodefullpath := v_node_row.node_fullpath;
    
  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;
    fldmod := convertToTimestamp(v_node_row.node_instime);
    pipe row(cool_schemanodetag_type(v_schemaname,arg_dbname,
              v_node_row.node_id,v_node_row.node_name,v_node_row.node_fullpath,
              v_node_row.node_description,v_node_row.node_isleaf,v_node_row.node_instime,
              fldmod,
              v_node_row.lastmod_date,v_node_row.folder_iovtablename,
              v_node_row.folder_tagtablename,v_node_row.folder_channeltablename,
              v_tag_row.tag_id,v_tag_row.tag_name,
              v_tag_row.tag_description,v_tag_row.tag_lock_status, v_tag_row.SYS_INSTIME));

  END LOOP;
  CLOSE tag_cursor;

  END LOOP;
  CLOSE node_cursor;
  
  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  /*    
  begin   
  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;
    pipe row(cool_tag_type(v_tag_row.tag_id,v_tag_row.tag_name,v_tag_row.tag_description,v_tag_row.SYS_INSTIME));

  END LOOP;
  CLOSE tag_cursor;
  
  exception
    when OTHERS then
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;
  */
  END LOOP;
  -- Close cursor on schemas :
  CLOSE schema_cursor; 
 
  END f_GetAll_Tags;
  
/***** AF: TEST include BRANCH tags ******/

/*************************************************/
  function f_GetAll_BranchTags(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_schemanodetag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  
  cursor schema_cursor is
    select username from SYS.ALL_USERS where (username like 'ATLAS_COOLO%' and username not like '%_W') and username like arg_schemaname;

  cursor node_cursor is
    select * from table(cool_select_pkg.f_Get_BranchNodes(v_schemaname,arg_dbname,arg_folder,' node_isleaf >= 0 '));

  cursor tag_cursor is
    select * from table(cool_select_pkg.f_Get_Tags(v_schemaname,arg_dbname,v_nodefullpath,arg_tagname));


  v_tag_row tag_cursor%ROWTYPE;
  v_node_row node_cursor%ROWTYPE;
  
  fldmod timestamp;   
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 
  OPEN schema_cursor;
    LOOP
      FETCH schema_cursor INTO v_schemaname;
    EXIT
    WHEN schema_cursor%NOTFOUND;
  begin   
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_nodefullpath := v_node_row.node_fullpath;
    
  OPEN tag_cursor;
  LOOP
    FETCH tag_cursor INTO v_tag_row;
    EXIT WHEN tag_cursor%NOTFOUND;
    fldmod := convertToTimestamp(v_node_row.node_instime);
    pipe row(cool_schemanodetag_type(v_schemaname,arg_dbname,
              v_node_row.node_id,v_node_row.node_name,v_node_row.node_fullpath,
              v_node_row.node_description,v_node_row.node_isleaf,v_node_row.node_instime,
              fldmod,
              v_node_row.lastmod_date,v_node_row.folder_iovtablename,
              v_node_row.folder_tagtablename,v_node_row.folder_channeltablename,
              v_tag_row.tag_id,v_tag_row.tag_name,
              v_tag_row.tag_description,v_tag_row.tag_lock_status, v_tag_row.SYS_INSTIME));

  END LOOP;
  CLOSE tag_cursor;

  END LOOP;
  CLOSE node_cursor;
  
  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;
  END LOOP;
  -- Close cursor on schemas :
  CLOSE schema_cursor; 
 
  END f_GetAll_BranchTags;


/*************************************************/
  function f_GetAll_BranchTagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
                     --return varchar2 AS
                     return cool_nodegtagtag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);   
  v_stmt_hierarchy VARCHAR2(1000);
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  
  TYPE TagCurTyp  IS REF CURSOR;
  v_tag_cursor    TagCurTyp; 
  v_row tagext_record;
  
  
  tag2tag varchar2(30);
  tags varchar2(30);
  nodes varchar2(30); 
  
  gtagid NUMBER;
  
  cursor gtag_cursor is
    select * from table(cool_select_pkg.f_GetAll_GlobalTags(arg_schemaname,arg_dbname,arg_globaltagname));
     
  v_gtag_row gtag_cursor%ROWTYPE;
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 

  tag2tag := arg_dbname || '_TAG2TAG'; 
  tags := arg_dbname || '_TAGS'; 
  nodes := arg_dbname || '_NODES'; 
  
 
  OPEN gtag_cursor;
    LOOP
      FETCH gtag_cursor INTO v_gtag_row; 
    EXIT
    WHEN gtag_cursor%NOTFOUND;
begin    

  v_schemaname := v_gtag_row.schema_name;
  
  v_stmt_hierarchy := 'select t.*' ||
   ' from ' 
  || v_schemaname || '.' || tag2tag || cool_db_link || ' t ' ||
  ' START WITH  t.parent_nodeid=0 and t.parent_tagid=' || v_gtag_row.tag_id ||
  ' CONNECT BY t.parent_nodeid = PRIOR t.child_nodeid and t.parent_tagid = PRIOR t.child_tagid';
 
    --create a query for hierarchical tag associations --
  v_stmt_str := 'select ' ||
  'cnodes.node_id, ' ||
  'cnodes.node_name, ' || 
  'cnodes.node_fullpath, ' || 
  'cnodes.node_description, ' || 
  'ctags.tag_id, ' || 
  'ctags.tag_name, ' || 
  'ctags.tag_description, ' ||
  'ctags.tag_lock_status, ' ||
  'ctags.sys_instime' || 
  ' from ' 
  || v_schemaname || '.' || tags || cool_db_link || ' ptags, ' 
  || v_schemaname || '.' || tags || cool_db_link || ' ctags, ' 
  || v_schemaname || '.' || nodes || cool_db_link || ' pnodes, ' 
  || v_schemaname || '.' || nodes || cool_db_link || ' cnodes, ' 
  || '(' || v_stmt_hierarchy || ') t2t ' ||
  ' where pnodes.node_id=t2t.parent_nodeid and ptags.tag_id=t2t.parent_tagid and ' ||
  ' cnodes.node_id=t2t.child_nodeid and ctags.tag_id=t2t.child_tagid ' ||
  ' and ptags.node_id=pnodes.node_id  and ctags.node_id=cnodes.node_id ';

  --return v_stmt_str;
 
  OPEN v_tag_cursor FOR v_stmt_str;
  LOOP
    FETCH v_tag_cursor INTO v_row;
  EXIT WHEN v_tag_cursor%NOTFOUND;
    pipe row(cool_nodegtagtag_type(v_schemaname,arg_dbname,
      v_gtag_row.tag_id, v_gtag_row.tag_name, v_gtag_row.tag_description, v_gtag_row.TAG_LOCK_STATUS,
      v_row.node_id, v_row.node_name, v_row.node_fullpath,v_row.node_description, v_row.tag_id,v_row.tag_name,
      v_row.tag_description,v_row.tag_lock_status, v_row.SYS_INSTIME));

  END LOOP;
  CLOSE v_tag_cursor;
  
  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  END LOOP;
  -- Close cursor on schemas :
  CLOSE gtag_cursor; 
  
  
  END f_GetAll_BranchTagsForGtag;


/*************************************************/
  function f_GetAll_TagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
                     --return varchar2 AS
                     return cool_nodegtagtag_type_t pipelined  AS
 
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);   
  v_stmt_hierarchy VARCHAR2(1000);
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  
  TYPE TagCurTyp  IS REF CURSOR;
  v_tag_cursor    TagCurTyp; 
  v_row tagext_record;
  
  
  tag2tag varchar2(30);
  tags varchar2(30);
  nodes varchar2(30); 
  
  gtagid NUMBER;
  
  cursor gtag_cursor is
    select * from table(cool_select_pkg.f_GetAll_GlobalTags(arg_schemaname,arg_dbname,arg_globaltagname));
     
  v_gtag_row gtag_cursor%ROWTYPE;
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 

  tag2tag := arg_dbname || '_TAG2TAG'; 
  tags := arg_dbname || '_TAGS'; 
  nodes := arg_dbname || '_NODES'; 
  
 
  OPEN gtag_cursor;
    LOOP
      FETCH gtag_cursor INTO v_gtag_row; 
    EXIT
    WHEN gtag_cursor%NOTFOUND;
begin    

  v_schemaname := v_gtag_row.schema_name;
  
  v_stmt_hierarchy := 'select t.*' ||
   ' from ' 
  || v_schemaname || '.' || tag2tag || cool_db_link || ' t ' ||
  ' START WITH  t.parent_nodeid=0 and t.parent_tagid=' || v_gtag_row.tag_id ||
  ' CONNECT BY t.parent_nodeid = PRIOR t.child_nodeid and t.parent_tagid = PRIOR t.child_tagid';
 
    --create a query for hierarchical tag associations --
  v_stmt_str := 'select ' ||
  'cnodes.node_id, ' ||
  'cnodes.node_name, ' || 
  'cnodes.node_fullpath, ' || 
  'cnodes.node_description, ' || 
  'ctags.tag_id, ' || 
  'ctags.tag_name, ' || 
  'ctags.tag_description, ' ||
  'ctags.tag_lock_status, ' ||
  'ctags.sys_instime' || 
  ' from ' 
  || v_schemaname || '.' || tags || cool_db_link || ' ptags, ' 
  || v_schemaname || '.' || tags || cool_db_link || ' ctags, ' 
  || v_schemaname || '.' || nodes || cool_db_link || ' pnodes, ' 
  || v_schemaname || '.' || nodes || cool_db_link || ' cnodes, ' 
  || '(' || v_stmt_hierarchy || ') t2t ' ||
  ' where pnodes.node_id=t2t.parent_nodeid and ptags.tag_id=t2t.parent_tagid and ' ||
  ' cnodes.node_id=t2t.child_nodeid and ctags.tag_id=t2t.child_tagid ' ||
  ' and ptags.node_id=pnodes.node_id  and ctags.node_id=cnodes.node_id and cnodes.node_isleaf=1';

  --return v_stmt_str;
 
  OPEN v_tag_cursor FOR v_stmt_str;
  LOOP
    FETCH v_tag_cursor INTO v_row;
  EXIT WHEN v_tag_cursor%NOTFOUND;
    pipe row(cool_nodegtagtag_type(v_schemaname,arg_dbname,
      v_gtag_row.tag_id, v_gtag_row.tag_name, v_gtag_row.tag_description, v_gtag_row.TAG_LOCK_STATUS,
      v_row.node_id, v_row.node_name, v_row.node_fullpath,v_row.node_description, v_row.tag_id,v_row.tag_name,
      v_row.tag_description,v_row.tag_lock_status, v_row.SYS_INSTIME));

  END LOOP;
  CLOSE v_tag_cursor;
  
  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  END LOOP;
  -- Close cursor on schemas :
  CLOSE gtag_cursor; 
  
  
  END f_GetAll_TagsForGtag;


/*************************************************/
  function f_GetAll_DoubleTagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
                     --return varchar2 AS
                     return cool_nodegtagtag_type_t pipelined  AS
 
  
  cursor gtag_cursor is
    select * from (
    select
      SCHEMA_NAME ,
      DB_NAME ,
      GTAG_ID ,
      GTAG_NAME ,
      GTAG_DESCRIPTION ,
      GTAG_LOCK_STATUS ,
      NODE_ID ,
      NODE_NAME ,
      NODE_FULLPATH ,
      NODE_DESCRIPTION,
      TAG_ID ,
      TAG_NAME ,
      TAG_DESCRIPTION , 
      TAG_LOCK_STATUS ,
      SYS_INSTIME,
      COUNT(NODE_FULLPATH) OVER (PARTITION BY NODE_FULLPATH) as NFLD
    from table(cool_select_pkg.f_GetAll_TagsForGtag(arg_schemaname,arg_dbname,arg_globaltagname)))
    where NFLD>1;
     
  v_row gtag_cursor%ROWTYPE;
     
  begin
 
 
  OPEN gtag_cursor;
    LOOP
      FETCH gtag_cursor INTO v_row; 
    EXIT
    WHEN gtag_cursor%NOTFOUND;
    pipe row(cool_nodegtagtag_type(v_row.SCHEMA_NAME,v_row.DB_NAME,
      v_row.GTAG_ID, v_row.GTAG_NAME, v_row.GTAG_DESCRIPTION, v_row.GTAG_LOCK_STATUS, 
      v_row.NODE_ID ,
      v_row.NODE_NAME ,
      v_row.NODE_FULLPATH ,
      v_row.NODE_DESCRIPTION,
      v_row.TAG_ID ,
      v_row.TAG_NAME ,
      v_row.TAG_DESCRIPTION , 
      v_row.TAG_LOCK_STATUS ,
      v_row.SYS_INSTIME
      ));

  END LOOP;
  CLOSE gtag_cursor;
    
  
  END f_GetAll_DoubleTagsForGtag;


/*************************************************/
  function f_Get_Iovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return cool_iov_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
--  cursor node_cursor is
--    select node_id from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));

  cursor tag_cursor is
    select tag_id from table(f_get_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname));

  tag_id number;
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  tagsearch varchar2(255);
  tagjoin varchar2(1024);
  tagnameoutput varchar2(30);
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
    /* Selecting table name for folder */
--  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'IOVS') into foldernamestr from dual;

  foldernamestr := convertNodeIdToFolder(node_id);


  tag_id := 1;
  tagsearch := ' :tagname is null AND ';
  tagjoin := '';
  tagnameoutput := ' null as tag_name';
  IF issingleversion > 0 THEN
  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO tag_id;
      EXIT
    WHEN tag_cursor%NOTFOUND;
  END LOOP;
  CLOSE tag_cursor;
  tagnameoutput := ' tags.tag_name'; 
--  tagjoin := ' left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' || cool_db_link || ' tags ' ||
  tagjoin := ' left join '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' || cool_db_link || ' tags ' ||
              'on iovs.user_tag_id=tags.tag_id '; 
  tagsearch := ' (tags.tag_name like :tagname) AND ';
  END IF;

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name, ' ||
  tagnameoutput ||
--  'tags.tag_name' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' || cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagjoin ||
  ' where ' || tagsearch || '(iovs.new_head_id=0)';
--  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' || cool_db_link || ' tags ' ||
--  'on iovs.user_tag_id=tags.tag_id ' ||
--  ' where (tags.tag_name = :tagname) AND (iovs.new_head_id=0)';
      
  dbms_output.put_line('executing query ' || v_stmt_str);
   
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using arg_tagname;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := convertToTimestamp(v_row.sys_instime);
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iov_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,v_row.tagname));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_Iovs;
 
/*************************************************/
  function f_Get_SvIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2) 
                     return cool_iov_type_t pipelined  AS
  
  cool_db_link    varchar2(100); 
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  foldernamestr := convertNodeIdToFolder(node_id);

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name, ' ||
  ''' ' || 'none' || '''' || 'as tag_name ' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' || cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  ' where (iovs.new_head_id=0)';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str ;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := convertToTimestamp(v_row.sys_instime);
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iov_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,null));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_SvIovs;
  
/*************************************************/
  function f_Get_LastModIov(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_objid NUMBER, arg_lastmod DATE) 
                     return cool_iovext_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
  node_id number;
  foldernamestr varchar2(10);
  iovmoddate timestamp(6);
  iovinstime timestamp(6);
  v_iovbase varchar2(50);

  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;


  cursor node_cursor is
    select node_id,iov_base from table(f_getall_nodes(arg_schemaname,arg_dbname,arg_folder));

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id,v_iovbase;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  foldernamestr := convertNodeIdToFolder(node_id);
   
    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'null as channel_name, ' ||
  'null as tag_name' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  ' where iovs.object_id=:objid or (iovs.object_id > (:objid - 100) and cool_select_pkg.convertToTimestamp(iovs.lastmod_date)=:lmd) ' ||
  ' order by iovs.iov_since desc ';
      
  dbms_output.put_line('executing query ' || v_stmt_str);
   
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using arg_objid,arg_objid,arg_lastmod;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    iovinstime := convertToTimestamp(v_row.sys_instime);
--    IF iovmoddate = arg_lastmod THEN
       pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame, v_row.tagname,v_iovbase));
--    END IF;
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_LastModIov;

/*************************************************/
  function f_Get_LastIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return cool_iov_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
  maxuntil        NUMBER;
   
  cursor iov_cursor is
    select object_id,channel_id,iov_since,iov_until,
           user_tag_id, sys_instime, lastmod_date, original_id, 
           new_head_id, channel_name,tag_name
    from table(f_get_iovs(arg_schemaname,arg_dbname,arg_folder,arg_tagname)) where iov_until=maxuntil;

  cursor sviov_cursor is
    select object_id,channel_id,iov_since,iov_until,
           user_tag_id, sys_instime, lastmod_date, original_id, 
           new_head_id, channel_name,tag_name
    from table(f_get_sviovs(arg_schemaname,arg_dbname,arg_folder)) where iov_until=maxuntil;
    
  v_row iov_cursor%ROWTYPE;
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  if arg_tagname is not null then  
    select max(iov_until) into maxuntil
      from table(f_get_iovs(arg_schemaname,arg_dbname,arg_folder,arg_tagname));
  OPEN iov_cursor;
    LOOP
      FETCH iov_cursor INTO v_row;
      EXIT
    WHEN iov_cursor%NOTFOUND;
    
    pipe row(cool_iov_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, v_row.sys_instime, v_row.lastmod_date, v_row.original_id, 
                          v_row.new_head_id, v_row.channel_name,v_row.tag_name));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE iov_cursor; 
 
  ELSE 
    select max(iov_until) into maxuntil
      from table(f_get_sviovs(arg_schemaname,arg_dbname,arg_folder));
  OPEN sviov_cursor;
    LOOP
      FETCH sviov_cursor INTO v_row;
      EXIT
    WHEN sviov_cursor%NOTFOUND;
    
    pipe row(cool_iov_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, v_row.sys_instime, v_row.lastmod_date, v_row.original_id, 
                          v_row.new_head_id, v_row.channel_name,v_row.tag_name));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE sviov_cursor; 

  END IF;  

  END f_Get_LastIovs;
/*************************************************/
  function f_Get_LastNumIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_niovs NUMBER) 
                     return cool_iovext_type_t pipelined  AS
  
  cool_db_link    varchar2(100);

  v_stmt_str      VARCHAR2(1000);  
  node_id number;
  tagid number;
  foldernamestr varchar2(10);
  iovmoddate timestamp(6);
  iovinstime timestamp(6);
  v_iovbase varchar2(50);

  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;

  lastniovs        NUMBER;

  cursor node_cursor is
    select node_id,iov_base from table(f_getall_nodes(arg_schemaname,arg_dbname,arg_folder));

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  lastniovs := 10;
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id,v_iovbase;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  foldernamestr := convertNodeIdToFolder(node_id);

  select count(channel_id)*arg_niovs into lastniovs
      from table(f_getall_channels(arg_schemaname,arg_dbname,arg_folder,'%'));
   
  tagid:=0;

  IF arg_tagname is not NULL THEN 
    select tag_id into tagid
      from table(f_Get_Tags(arg_schemaname,arg_dbname,arg_folder,'%'));
  END IF;
  
  IF lastniovs > 10000 THEN
    lastniovs := 10000;
  END IF;
  
    --create a query for nodes --
  v_stmt_str := 'select * from (select ' ||
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'null as channel_name, ' ||
  'null as tag_name' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  ' where iovs.user_tag_id=:tagid ' ||
  ' order by iovs.object_id desc ) where rownum<:lastn';
      
  dbms_output.put_line('executing query ' || v_stmt_str);
   
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using tagid,lastniovs;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    iovinstime := convertToTimestamp(v_row.sys_instime);
--    IF iovmoddate = arg_lastmod THEN
       pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame, v_row.tagname,v_iovbase));
--    END IF;
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 



/*
  v_stmt_str      VARCHAR2(1000);  
  lastniovs        NUMBER;
  node_id number;
  v_iovbase varchar2(50);

  cursor iov_cursor is
    select object_id,channel_id,iov_since,iov_until,
           user_tag_id, sys_instime, lastmod_date, original_id, 
           new_head_id, channel_name,tag_name
    from table(f_get_iovs(arg_schemaname,arg_dbname,arg_folder,arg_tagname)) where rownum<lastniovs;

  cursor sviov_cursor is
    select object_id,channel_id,iov_since,iov_until,
           user_tag_id, sys_instime, lastmod_date, original_id, 
           new_head_id, channel_name,tag_name
    from table(f_get_sviovs(arg_schemaname,arg_dbname,arg_folder)) where rownum<lastniovs;
    
  cursor node_cursor is
    select node_id,iov_base from table(f_getall_nodes(arg_schemaname,arg_dbname,arg_folder));
     
 
  v_row iov_cursor%ROWTYPE;
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id,v_iovbase;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  
  select count(channel_id)*arg_niovs into lastniovs
      from table(f_getall_channels(arg_schemaname,arg_dbname,arg_folder,'%'));
  if arg_tagname is not null then  
    
  OPEN iov_cursor;
    LOOP
      FETCH iov_cursor INTO v_row;
      EXIT
    WHEN iov_cursor%NOTFOUND;
--    IF iovmoddate = arg_lastmod THEN
       pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, v_row.sys_instime, v_row.lastmod_date, v_row.original_id, 
                          v_row.new_head_id, v_row.channel_name, v_row.tag_name,v_iovbase));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE iov_cursor; 
 
  ELSE 
    
  OPEN sviov_cursor;
    LOOP
      FETCH sviov_cursor INTO v_row;
      EXIT
    WHEN sviov_cursor%NOTFOUND;
       pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, v_row.sys_instime, v_row.lastmod_date, v_row.original_id, 
                          v_row.new_head_id, v_row.channel_name, v_row.tag_name,v_iovbase));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE sviov_cursor; 

  END IF;  
  */

  END f_Get_LastNumIovs;
/*************************************************/
  function f_Get_IovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return cool_iov_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  cursor tag_cursor is
    select tag_id from table(f_get_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname));
  
  tag_id number;
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovshort_record;
  
  foldernamestr varchar2(100);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);

  tagsearch varchar2(255);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  /* Selecting table name for TAGS */
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'IOVS') into foldernamestr from dual;

  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  tag_id := 1;
  tagsearch := ' where 1 = :tag_id ';
  IF issingleversion > 0 THEN
  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO tag_id;
      EXIT
    WHEN tag_cursor%NOTFOUND;
  END LOOP;
  CLOSE tag_cursor;
  
  tagsearch := ' where (iovs.user_tag_id = :tagid) AND (iovs.new_head_id=0) ';
  END IF;
--  foldernamestr := convertNodeIdToFolder(node_id);

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until ' ||
  ' from ' 
  || arg_schemaname || '.' || foldernamestr ||  cool_db_link || ' iovs ' ||
  tagsearch ;
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using tag_id;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    pipe row(cool_iov_type(1,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          null, null, null, null, 
                          0, null,null));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

--  pipe row(cool_iov_type(node_id, null, null, null, null, null, null,null,null, foldernamestr, v_stmt_str));
--  return v_stmt_str;
  END f_Get_IovsShort;
 
 
/*************************************************/
  function f_GetAll_IovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return COOL_SCHEMANODEIOV_TYPE_T pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select * from table(f_getall_nodes(arg_schemaname,arg_dbname,arg_folder));

  
  node_id number;
  issingleversion number;
  tagsearch varchar2(255);

  v_nodeid number;
  v_tag_id number;
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  v_tagname varchar2(255);
  v_iovbase varchar2(30);

   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row IOVSTAT_RECORD;

  cursor tag_cursor is
    select tag_id, tag_name from table(f_get_tags(v_schemaname,arg_dbname,v_nodefullpath,arg_tagname));

  v_tag_row tag_cursor%ROWTYPE;
  v_node_row node_cursor%ROWTYPE;
  
  foldernamestr varchar2(100);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO v_node_row;
      EXIT
    WHEN node_cursor%NOTFOUND;
  

  begin

  v_schemaname := v_node_row.schema_name;
  v_nodeid := v_node_row.node_id;
  v_nodefullpath := v_node_row.node_fullpath;
  v_iovbase := v_node_row.iov_base;
  foldernamestr := v_node_row.folder_iovtablename;
  issingleversion := v_node_row.folder_versioning;
  
--  select extractValue(xmltype('<mynode>' || v_node_row.node_description || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;
--  v_iovbase := extractValue(xmltype('<mynode>' || v_tag_row.node_description || '</mynode>'),'/mynode/timeStamp');

  /* Selecting table name for TAGS */
--  select f_get_tablename(v_schemaname,arg_dbname,v_nodefullpath,'IOVS') into foldernamestr from dual;

  v_tag_id := 1;
  v_tagname := 'single version';
  tagsearch := ' :tagid=1 AND ';
  IF issingleversion > 0 THEN
  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO v_tag_id, v_tagname;
      EXIT
    WHEN tag_cursor%NOTFOUND;
--  foldernamestr := convertNodeIdToFolder(node_id);
--  v_tagname := v_tag_row.tag_name;
--  v_tag_id := v_tag_row.tag_id;
  END LOOP;
  CLOSE tag_cursor;
  tagsearch := ' (iovs.user_tag_id = :tagid) AND ';  
   
  END IF;

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'count(distinct iovs.channel_id) as CHANNEL_ID,' ||
  'min(iovs.iov_since) as MINIOV_SINCE,' || 
  'min(iovs.iov_until) as MINIOV_UNTIL,' ||
  'max(iovs.iov_since) as MAXIOV_SINCE,' || 
  'max(iovs.iov_until) as MAXIOV_UNTIL, ' ||
  'count(iovs.object_id) as NIOVPERCHAN ' ||
  ' from ' 
  || v_schemaname || '.' || foldernamestr ||  cool_db_link || ' iovs ' ||
  ' where ' || tagsearch || ' (iovs.new_head_id=0) ';-- ||
--  ' group by iovs.channel_id ';

-- Folder is single version, then do not access tag information

  
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using v_tag_id;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    pipe row(COOL_SCHEMANODEIOV_TYPE(v_schemaname,arg_dbname,
      v_nodeid,
      v_nodefullpath,
      v_tag_id,
      v_tagname,
      v_row.NIOVPERCHAN,
      v_row.channel_id,
      v_row.MINIOV_SINCE,
      v_row.MINIOV_UNTIL,
      v_row.MAXIOV_SINCE,
      v_row.MAXIOV_UNTIL,
      v_iovbase));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  END LOOP;
  CLOSE node_cursor;
  

--  pipe row(cool_iov_type(node_id, null, null, null, null, null, null,null,null, foldernamestr, v_stmt_str));
--  return v_stmt_str;
  END f_GetAll_IovsShort;

/*************************************************/
  function f_GetAll_TagsIovsShort(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return COOL_SCHEMANODETAGIOV_TYPE_T pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
--  cursor node_cursor is
--    select node_id from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  cursor tag_cursor is
    select * from table(f_getall_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname));
  
  v_nodeid number;
  v_tag_id number;
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  v_tagname varchar2(255);
  v_iovbase varchar2(30);

  fldmod timestamp;   

   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row IOVSTAT_RECORD;

  v_tag_row tag_cursor%ROWTYPE;
  
  foldernamestr varchar2(100);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO v_tag_row;
      EXIT
    WHEN tag_cursor%NOTFOUND;
--  foldernamestr := convertNodeIdToFolder(node_id);

  begin

  v_tag_id := v_tag_row.tag_id;
  v_schemaname := v_tag_row.schema_name;
  v_nodeid := v_tag_row.node_id;
  v_nodefullpath := v_tag_row.node_fullpath;
  v_tagname := v_tag_row.tag_name;
  
  select extractValue(xmltype('<mynode>' || v_tag_row.node_description || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;
--  v_iovbase := extractValue(xmltype('<mynode>' || v_tag_row.node_description || '</mynode>'),'/mynode/timeStamp');

  /* Selecting table name for TAGS */
  select f_get_tablename(v_schemaname,arg_dbname,v_nodefullpath,'IOVS') into foldernamestr from dual;

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'count(distinct iovs.channel_id) as CHANNEL_ID,' ||
  'min(iovs.iov_since) as MINIOV_SINCE,' || 
  'min(iovs.iov_until) as MINIOV_UNTIL,' ||
  'max(iovs.iov_since) as MAXIOV_SINCE,' || 
  'max(iovs.iov_until) as MAXIOV_UNTIL, ' ||
  'count(iovs.object_id) as NIOVPERCHAN ' ||
  ' from ' 
  || v_schemaname || '.' || foldernamestr ||  cool_db_link || ' iovs ' ||
  ' where (iovs.user_tag_id = :tagid) AND (iovs.new_head_id=0) ';-- ||
--  ' group by iovs.channel_id ';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using v_tag_id;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    fldmod := convertToTimestamp(v_tag_row.node_instime);
 
    pipe row(COOL_SCHEMANODETAGIOV_TYPE(v_schemaname,arg_dbname,
      v_tag_row.node_id,v_tag_row.node_name,v_tag_row.node_fullpath,
      v_tag_row.node_description,v_tag_row.node_isleaf,v_tag_row.node_instime,
      fldmod,
      v_tag_row.lastmod_date,v_tag_row.folder_iovtablename,
      v_tag_row.folder_tagtablename,v_tag_row.folder_channeltablename,
      v_tag_row.tag_id,v_tag_row.tag_name,
      v_tag_row.tag_description,v_tag_row.tag_lock_status, v_tag_row.SYS_INSTIME,
      v_row.NIOVPERCHAN,
      v_row.channel_id,
      v_row.MINIOV_SINCE,
      v_row.MINIOV_UNTIL,
      v_row.MAXIOV_SINCE,
      v_row.MAXIOV_UNTIL,
      v_iovbase));

  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  exception
    when OTHERS then
      raise_application_error(-20001, 'Error in schema ' || v_schemaname || ' and folder ' || v_nodefullpath);
      DBMS_OUTPUT.PUT_LINE('Cannot find table');
    
  end;

  END LOOP;
  CLOSE tag_cursor;
  
  END f_GetAll_TagsIovsShort;


 
/*************************************************/
/* Retrieve statistic on set of iovs on a folder */
  function f_Get_IovsStat(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2) 
                     return cool_iovstat_type_t pipelined  AS
     

  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  cursor tag_cursor is
    select tag_id from table(f_get_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname));
  
  tag_id number;
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovstat_record;
  
  foldernamestr varchar2(100);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);

  tagsearch varchar2(255);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';

  /* Selecting table name for TAGS */
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'IOVS') into foldernamestr from dual;

  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  tag_id := 1;
  tagsearch := ' where 1 = :tag_id ';
  IF issingleversion > 0 THEN
  
  OPEN tag_cursor;
    LOOP
      FETCH tag_cursor INTO tag_id;
      EXIT
    WHEN tag_cursor%NOTFOUND;
  END LOOP;
  CLOSE tag_cursor;
  
  tagsearch := ' where (iovs.user_tag_id = :tagid) AND (iovs.new_head_id=0) ';
  END IF;
--  foldernamestr := convertNodeIdToFolder(node_id);

    --create a query for nodes --
  v_stmt_str := 'select ' ||
  'iovs.channel_id, ' ||
  'min(iovs.iov_since) as miniov_since,' || 
  'min(iovs.iov_until) as miniov_until,' ||
  'max(iovs.iov_since) as maxiov_since,' || 
  'max(iovs.iov_until) as maxiov_until,' ||
  'count(iovs.channel_id) as niovsperchan ' ||
  ' from ' 
  || arg_schemaname || '.' || foldernamestr ||  cool_db_link || ' iovs ' ||
  tagsearch || 'group by channel_id ';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using tag_id;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    pipe row(cool_iovstat_type(v_row.channel_id, v_row.miniov_since,v_row.miniov_until,
                              v_row.maxiov_since,v_row.maxiov_until,v_row.niovperchan));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

/*
  cursor iov_cursor is 
    select 
      channel_id, min(iov_since) as miniov_since, min(iov_until) as miniov_until,
      max(iov_since) as maxiov_since, max(iov_until) as maxiov_until, count(channel_id) as niovperchan 
        from table(f_get_iovsshort(arg_schemaname,arg_dbname,arg_folder,arg_tagname)) group by channel_id;
  
  v_row iovstat_record;
  
  begin
  
  OPEN iov_cursor;
    LOOP
      FETCH iov_cursor INTO v_row;
      EXIT
    WHEN iov_cursor%NOTFOUND;
    pipe row(cool_iovstat_type(v_row.channel_id, v_row.miniov_since,v_row.miniov_until,
                              v_row.maxiov_since,v_row.maxiov_until,v_row.niovperchan));
  END LOOP;
  CLOSE iov_cursor;
*/
--  pipe row(cool_iov_type(node_id, null, null, null, null, null, null,null,null, foldernamestr, v_stmt_str));
--  return v_stmt_str;
  END f_Get_IovsStat;
/*************************************************/
/* Test function */
function f_Get_IovsRangeFldInGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname varchar2,
                     arg_since NUMBER, arg_until NUMBER, arg_iovbase VARCHAR2) 
                     return COOL_SCNTIOV_TYPE_T pipelined AS

  schemaname varchar2(255);
  foldername varchar2(255);
  tagname varchar2(255);
  v_iovbase varchar2(50);
  
--  f_GetAll_TagsForGtag(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_globaltagname VARCHAR2) 
--                     --return varchar2 AS
--                     return cool_nodegtagtag_type_t
  cursor gtag_cursor is
    select * from table(f_GetAll_TagsForGtag(arg_schemaname,arg_dbname,arg_globaltagname));
  cursor iov_cursor is
    select * from table(f_Get_IovsRange(schemaname,arg_dbname,foldername,tagname,arg_since,arg_until));

  v_tag_row gtag_cursor%ROWTYPE;
  v_row iov_cursor%ROWTYPE;

BEGIN
  
  OPEN gtag_cursor;
    LOOP
      FETCH gtag_cursor INTO v_tag_row;
      EXIT
      WHEN gtag_cursor%NOTFOUND;
  
      schemaname := v_tag_row.schema_name;
      foldername := v_tag_row.node_fullpath;
      tagname := v_tag_row.tag_name;
      select extractValue(xmltype('<mynode>' || v_tag_row.node_description || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;
      IF v_iovbase = arg_iovbase THEN
      OPEN iov_cursor;
        LOOP
          FETCH iov_cursor INTO v_row;
        EXIT
        WHEN iov_cursor%NOTFOUND;
        pipe row(COOL_SCNTIOV_TYPE(schemaname, arg_dbname, foldername, v_iovbase, tagname, v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, v_row.SYS_INSTIME, v_row.LASTMOD_DATE, v_row.original_id, 
                          v_row.new_head_id, v_row.CHANNEL_NAME));
      END LOOP;
      CLOSE iov_cursor;     
      END IF;
  END LOOP;
  CLOSE gtag_cursor;     
 

END f_Get_IovsRangeFldInGtag;

/*************************************************/
  function f_Get_IovsRange(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_since NUMBER, arg_until NUMBER) 
                     return cool_iov_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  issingleversion number;
  tagtablejoin varchar2(4000);
  tagsearch varchar2(1000);
  tagfield varchar2(50);
  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);
  
  v_tagname varchar2(255);
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor; 
  
  foldernamestr := convertNodeIdToFolder(node_id);

  v_tagname := NULL;
  tagfield := ' , null as tag_name  ';
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
     headsearch := ' (iovs.new_head_id=0) AND ';
  ELSIF issingleversion > 0 THEN
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--                    'on iovs.user_tag_id=tags.tag_id ';
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
                   -- 'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfield := ',tags.tag_name ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;

    --create a query for nodes --
    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';

  v_stmt_str := 'select ' || coolhint || 
  ' iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name ' ||
  tagfield ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
--  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  ' where iovs.channel_id=channels.channel_id AND ' || tagsearch ||  headsearch ||
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || 
  ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime AND iovs.iov_until > :stime) ';
--  ' AND (((iovs.iov_since >= :stime) AND (iovs.iov_since <= :etime)) OR ((iovs.iov_until >= :stime) AND (iovs.iov_until <= :etime)) OR ((:stime > iovs.iov_since) AND (:stime < iovs.iov_until))) ';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_since,arg_since,arg_until,arg_since; --,arg_since,arg_until,arg_since,arg_since;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := convertToTimestamp(v_row.sys_instime);
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iov_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,v_tagname));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_IovsRange;

/*************************************************/
  function f_Get_IovsRangeForChannel(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_chanid NUMBER, arg_since NUMBER, arg_until NUMBER) 
                     return cool_iovext_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
    
  cursor node_cursor is
    select node_id, folder_versioning,node_description  from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  node_descr varchar2(255);
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  issingleversion number;
  tagtablejoin varchar2(4000);
  tagsearch varchar2(1000);
  tagfield varchar2(50);
  chansearch varchar2(255);
  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);
  
  v_tagname varchar2(255);
  v_iovbase varchar2(30);
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion, node_descr;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor; 
  
  foldernamestr := convertNodeIdToFolder(node_id);

  select extractValue(xmltype('<mynode>' || node_descr || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;

  v_tagname := NULL;
  tagfield := ' , null as tag_name  ';
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
     headsearch := ' (iovs.new_head_id=0) AND ';
  ELSIF issingleversion > 0 THEN
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--                    'on iovs.user_tag_id=tags.tag_id ';
--     tagsearch := '(tags.tag_name = :tagname) AND ';
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfield := ',tags.tag_name ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;
  chansearch := ' AND :chanid is null ';
  IF arg_chanid IS NOT NULL THEN
     chansearch := 'AND channels.channel_id=:chanid '; 
  END IF;
  


    --create a query for nodes --
    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';

  v_stmt_str := 'select ' || coolhint || 
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name ' ||
  tagfield ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  tagtablejoin ||
  ' where iovs.channel_id=channels.channel_id AND ' || tagsearch ||  headsearch ||
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || 
  ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime AND iovs.iov_until > :stime) '||
  chansearch;
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_since,arg_since,arg_until,arg_since,arg_chanid;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := convertToTimestamp(v_row.sys_instime);
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,v_tagname,v_iovbase));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_IovsRangeForChannel;

/*************************************************/
  function f_Get_IovsRangeForChannelName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_channame VARCHAR2, arg_since NUMBER, arg_until NUMBER) 
                     return cool_iovext_type_t pipelined  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
   
  cursor node_cursor is
    select node_id, folder_versioning, node_description from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  node_descr varchar2(255);
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row iovext_record;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  issingleversion number;
  tagtablejoin varchar2(4000);
  tagsearch varchar2(1000);
  tagfield varchar2(50);
  chansearch varchar2(255);
  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);
  
  v_iovbase varchar2(30);
  v_tagname varchar2(255);
  
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion, node_descr;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor; 
  
  foldernamestr := convertNodeIdToFolder(node_id);

  v_tagname := NULL;
  tagfield := ' , null as tag_name  ';
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
     headsearch := ' (iovs.new_head_id=0) AND ';
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfield := ',tags.tag_name ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;
  chansearch := ' AND (:channame IS NULL)';
  IF arg_channame IS NOT NULL THEN
     chansearch := 'AND (channels.channel_name like :channame OR channels.channel_name IS NULL) '; 
  END IF;
  

  select extractValue(xmltype('<mynode>' || node_descr || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;

    --create a query for nodes --
    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';

  v_stmt_str := 'select ' || coolhint || 
  'iovs.object_id,' || 
  'iovs.channel_id,' ||
  'iovs.iov_since,' || 
  'iovs.iov_until,' ||
  'iovs.user_tag_id,' ||
  'iovs.sys_instime,' ||
  'iovs.lastmod_date,' ||
  'iovs.original_id,' ||
  'iovs.new_head_id,' || 
  'channels.channel_name ' ||
  tagfield ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  tagtablejoin ||
  ' where iovs.channel_id=channels.channel_id AND ' || tagsearch ||  headsearch ||
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || 
  ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime AND iovs.iov_until > :stime) '||
  chansearch;
      
--  raise_application_error(-20003, 'Error in schema ' || arg_schemaname || ' and db ' || arg_dbname  || ' for query ' || v_stmt_str);
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_since,arg_since,arg_until,arg_since,arg_channame;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := convertToTimestamp(v_row.sys_instime);
    iovmoddate := convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iovext_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,v_tagname,v_iovbase));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 

  END f_Get_IovsRangeForChannelName;

/*************************************************/
  function f_Get_PayloadIov(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_time NUMBER, arg_channel NUMBER) 
                     return SYS_REFCURSOR  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  v_tagname varchar2(255);
  tagtablejoin varchar2(1000);
  tagsearch varchar2(255);
  
  payloadtable varchar2(1000);
  joinpayloadtable varchar2(1000);
  channelwherecondition varchar2(1000);
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  foldernamestr := convertNodeIdToFolder(node_id);

  -- check if payload table exists in a separate table or inside the IOVS table
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'PAYLOAD') into payloadtable from dual;
  IF payloadtable is not NULL THEN
     joinpayloadtable := 'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ' ||
  'on iovs.payload_id=pyld.payload_id ';
  ELSE
    joinpayloadtable := '';
  END IF;
  
  v_tagname := NULL;
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  IF arg_tagname = 'HEAD' THEN
    tagsearch := ' :tagname is not null AND iovs.user_tag_id=0 AND ';   
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
                    'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND ';
     v_tagname := arg_tagname;
  END IF;

  IF arg_channel is not NULL THEN
    channelwherecondition := ' AND channels.CHANNEL_ID=' || arg_channel;
  ELSE
    channelwherecondition := '';
  END IF;
    --create a query for nodes --
  v_stmt_str := 'select * ' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where ' || tagsearch ||
  ' (iovs.new_head_id=0) AND (:ptime BETWEEN iovs.iov_since and iovs.iov_until) ' ||
  channelwherecondition ;
--  ' AND (iovs.iov_until>:ptime)';

-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:
--  raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and statment  ' || v_stmt_str);

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_time;
  RETURN v_iov_cursor;

  END f_Get_PayloadIov;


/*************************************************/
  function f_Get_PayloadIovByChanName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_time NUMBER, arg_channel VARCHAR2) 
                     return SYS_REFCURSOR  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select node_id, folder_versioning from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  issingleversion number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  v_tagname varchar2(255);
  tagtablejoin varchar2(1000);
  tagsearch varchar2(255);
  
  payloadtable varchar2(1000);
  joinpayloadtable varchar2(1000);
  channelwherecondition varchar2(1000);
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  foldernamestr := convertNodeIdToFolder(node_id);

  -- check if payload table exists in a separate table or inside the IOVS table
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'PAYLOAD') into payloadtable from dual;
  IF payloadtable is not NULL THEN
     joinpayloadtable := 'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ' ||
  'on iovs.payload_id=pyld.payload_id ';
  ELSE
    joinpayloadtable := '';
  END IF;
  
  v_tagname := NULL;
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  IF issingleversion > 0 THEN
     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
                    'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND ';
     v_tagname := arg_tagname;
  END IF;

  IF arg_channel is not NULL THEN
    channelwherecondition := ' AND (channels.CHANNEL_NAME like :chan OR channels.CHANNEL_NAME IS NULL)';
  ELSE
    channelwherecondition := '  AND (:chan IS NULL) ';
  END IF;
    --create a query for nodes --
  v_stmt_str := 'select * ' ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where ' || tagsearch ||
  ' (iovs.new_head_id=0) AND (:ptime BETWEEN iovs.iov_since and iovs.iov_until) ' ||
  channelwherecondition ;
--  ' AND (iovs.iov_until>:ptime)';

-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:
--  raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and statment  ' || v_stmt_str);

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_time,arg_channel;
  RETURN v_iov_cursor;

  END f_Get_PayloadIovByChanName;

/*************************************************/
  function f_Get_PayloadIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_stime NUMBER, arg_etime NUMBER, arg_channel NUMBER) 
                     return SYS_REFCURSOR  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
   
  cursor node_cursor is
    select node_id,folder_versioning,node_description from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  issingleversion number;
  node_descr varchar2(255);
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  iovsfrom varchar2(1000);
  
  payloadtable varchar2(1000);
  joinpayloadtable varchar2(1000);
  channelwherecondition varchar2(1000);
  payloadfrom varchar2(30);
  payloadwhere varchar2(100);

  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);

  v_tagname varchar2(255);
  tagtablejoin varchar2(1000);
  tagsearch varchar2(255);
  tagfrom varchar2(30);
  
  v_iovbase varchar2(30);
  v_iovbasefrom varchar2(35);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  IF arg_channel is not NULL THEN
    channelwherecondition := ' AND channels.CHANNEL_ID=' || arg_channel;
  ELSE
    channelwherecondition := '';
  END IF;

  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion, node_descr;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  select extractValue(xmltype('<mynode>' || node_descr || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;

  foldernamestr := convertNodeIdToFolder(node_id);
  iovsfrom :=  ' iovs.object_id, iovs.user_tag_id, iovs.iov_since, iovs.iov_until, iovs.sys_instime, iovs.lastmod_date, iovs.new_head_id, ';
  
  payloadwhere := '';
  -- check if payload table exists in a separate table or inside the IOVS table
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'PAYLOAD') into payloadtable from dual;
  IF payloadtable is not NULL THEN
--     joinpayloadtable := 'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ' ||
--  'on iovs.payload_id=pyld.payload_id ';
     joinpayloadtable := ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ';
     payloadwhere := ' iovs.payload_id=pyld.payload_id  AND ';
     payloadfrom := ' pyld.*, ';     
  ELSE
    joinpayloadtable := '';
    payloadfrom := '';
    iovsfrom := ' iovs.*, ';
  END IF;


  v_tagname := NULL;
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  tagfrom := '';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
  ELSIF issingleversion > 0 THEN
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--                    'on iovs.user_tag_id=tags.tag_id ';
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
                   -- 'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfrom := ' tags.*, ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;
  
/*  
  
  IF arg_tagname = 'HEAD' THEN
    tagsearch := ' (:tagname is null AND iovs.user_tag_id=0) AND ';   
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   ' left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
                    'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND ';
     v_tagname := arg_tagname;
     tagfrom := ' tags.*, ';
  END IF;
*/
    --create a query for nodes --
    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';

  v_iovbasefrom := '''' || v_iovbase || ''' as IOV_BASE '; 
    --create a query for nodes --
  v_stmt_str := 'select ' || coolhint || 
  iovsfrom ||
  ' channels.*, ' ||
    tagfrom || payloadfrom || v_iovbasefrom ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where iovs.channel_id=channels.channel_id AND ' || payloadwhere || tagsearch ||  headsearch ||
--  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
--  'on iovs.channel_id=channels.channel_id ' ||
--  tagtablejoin ||
--  ' where ' || tagsearch || 
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime) ' || channelwherecondition;

--  ' AND (((iovs.iov_since >= :stime) AND (iovs.iov_since <= :etime)) OR ((iovs.iov_until >= :stime) AND (iovs.iov_until <= :etime)) OR ((:stime > iovs.iov_since) AND (:stime < iovs.iov_until))) ' ||
--  channelwherecondition ;
--  ' AND (iovs.iov_until>:ptime)';

-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:
--  raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and statment  ' || v_stmt_str);

--  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_stime,arg_etime,arg_stime,arg_etime,arg_stime,arg_stime;
  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_stime,arg_stime,arg_etime;
  RETURN v_iov_cursor;

  END f_Get_PayloadIovs;

/*************************************************/
  function f_Get_PayloadIovsByChanName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_stime NUMBER, arg_etime NUMBER, arg_channel VARCHAR2) 
                     return SYS_REFCURSOR  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
   
  cursor node_cursor is
    select node_id,folder_versioning,node_description from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  issingleversion number;
  node_descr varchar2(255);
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  iovsfrom varchar2(1000);

  payloadtable varchar2(1000);
  joinpayloadtable varchar2(1000);
  channelwherecondition varchar2(1000);
  payloadfrom varchar2(30);
  payloadwhere varchar2(100);
  orderbycond varchar2(100);

  v_tagname varchar2(255);
  tagtablejoin varchar2(1000);
  tagsearch varchar2(255);
  tagfrom varchar2(30);

  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);

  
  v_iovbase varchar2(30);
  v_iovbasefrom varchar2(35);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  IF arg_channel is not NULL THEN
    channelwherecondition := ' AND (channels.CHANNEL_NAME like :channame OR channels.CHANNEL_NAME IS NULL) ';
  ELSE
    channelwherecondition := ' AND (:channame IS NULL)';
  END IF;

  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion, node_descr;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  select extractValue(xmltype('<mynode>' || node_descr || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;

  
  iovsfrom :=  ' iovs.object_id, iovs.user_tag_id, iovs.iov_since, iovs.iov_until, iovs.sys_instime, iovs.lastmod_date, iovs.new_head_id, ';

  foldernamestr := convertNodeIdToFolder(node_id);

  payloadwhere := '';
  -- check if payload table exists in a separate table or inside the IOVS table
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'PAYLOAD') into payloadtable from dual;
  IF payloadtable is not NULL THEN
--     joinpayloadtable := 'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ' ||
--  'on iovs.payload_id=pyld.payload_id ';
     joinpayloadtable := ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ';
     payloadwhere := ' iovs.payload_id=pyld.payload_id  AND ';
     payloadfrom := ' pyld.*, ';     
  ELSE
    joinpayloadtable := '';
    payloadfrom := '';
    iovsfrom := ' iovs.*, ';
  END IF;

  v_tagname := NULL;
  tagtablejoin := '';
  tagfrom := '';
  tagsearch := ' :tagname is null AND ';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
  ELSIF issingleversion > 0 THEN
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--     tagtablejoin :=   'left join '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
--                    'on iovs.user_tag_id=tags.tag_id ';
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
                   -- 'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfrom := ' tags.*, ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;

/*
  IF arg_tagname = 'HEAD' THEN
    tagsearch := ' (:tagname is null AND iovs.user_tag_id=0) AND ';   
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   ' left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'TAGS' ||  cool_db_link || ' tags ' ||
                    'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND ';
     v_tagname := arg_tagname;
     tagfrom := ' tags.*, ';
  END IF;
*/
-- set order by --
  orderbycond := ' ORDER BY channels.channel_id ASC, iovs.IOV_SINCE ASC ';

    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';


    --create a query for nodes --
  v_iovbasefrom := '''' || v_iovbase || ''' as IOV_BASE '; 
    
  v_stmt_str := 'select ' || coolhint || 
  iovsfrom ||
  ' channels.*, ' ||
    tagfrom || payloadfrom || v_iovbasefrom ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where iovs.channel_id=channels.channel_id AND ' || payloadwhere || tagsearch ||  headsearch ||
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime) ' || channelwherecondition || orderbycond;

/*    
  v_stmt_str := 'select iovs.*, channels.*, ' ||
    tagfrom || payloadfrom || v_iovbasefrom ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where ' || tagsearch || ' (iovs.new_head_id=0) ' || 
  ' AND ( ' || 
       ' ((iovs.iov_since >= :stime) AND (iovs.iov_since <= :etime)) ' || 
    ' OR ((iovs.iov_until >= :stime) AND (iovs.iov_until <= :etime)) ' || 
    ' OR ((:stime >= iovs.iov_since) AND (:stime < iovs.iov_until))   ' || 
    ' OR ((:etime >= iovs.iov_since) AND (:etime < iovs.iov_until))   ' || 
    ' ) ' ||
  channelwherecondition || orderbycond;
  */
--  ' AND (iovs.iov_until>:ptime)';

-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:
--raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and statement  ' || v_stmt_str);

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_stime,arg_stime,arg_etime,arg_channel;

--  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_stime,arg_etime,arg_stime,arg_etime,arg_stime,arg_stime,arg_etime,arg_etime,arg_channel;
  RETURN v_iov_cursor;

  END f_Get_PayloadIovsByChanName;

/*************************************************/
  function f_Get_PayloadFieldsIovs(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2, arg_stime NUMBER, arg_etime NUMBER, arg_channel NUMBER,
                     arg_payloadstr VARCHAR2) 
                     return SYS_REFCURSOR  AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(4000);  
   
  cursor node_cursor is
    select node_id,folder_versioning,node_description from table(f_get_nodes(arg_schemaname,arg_dbname,arg_folder));
  
  node_id number;
  issingleversion number;
  node_descr varchar2(255);
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);
  
  iovsfrom varchar2(1000);
  
  payloadtable varchar2(1000);
  joinpayloadtable varchar2(1000);
  channelwherecondition varchar2(1000);
  payloadfrom varchar2(1000);
  payloadwhere varchar2(100);

  headsearch varchar2(50);
  coolhint varchar2(1000);
  
  iovstablename varchar2(255);

  v_tagname varchar2(255);
  tagtablejoin varchar2(1000);
  tagsearch varchar2(255);
  tagfrom varchar2(30);
  
  v_iovbase varchar2(30);
  v_iovbasefrom varchar2(35);

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  IF arg_channel is not NULL THEN
    channelwherecondition := ' AND channels.CHANNEL_ID=' || arg_channel;
  ELSE
    channelwherecondition := '';
  END IF;

  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id, issingleversion, node_descr;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
  
  select extractValue(xmltype('<mynode>' || node_descr || '</mynode>'),'/mynode/timeStamp') into v_iovbase from dual;

  foldernamestr := convertNodeIdToFolder(node_id);
  iovsfrom :=  ' iovs.object_id, iovs.user_tag_id, iovs.iov_since, iovs.iov_until, iovs.sys_instime, iovs.lastmod_date, iovs.new_head_id, ';
  
  payloadwhere := '';
  -- check if payload table exists in a separate table or inside the IOVS table
  select f_get_tablename(arg_schemaname,arg_dbname,arg_folder,'PAYLOAD') into payloadtable from dual;
  IF payloadtable is not NULL THEN
--     joinpayloadtable := 'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ' ||
--  'on iovs.payload_id=pyld.payload_id ';
     joinpayloadtable := ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'PAYLOAD' ||  cool_db_link || ' pyld ';
     payloadwhere := ' iovs.payload_id=pyld.payload_id  AND ';
     payloadfrom := ' pyld.*, ';     
     IF arg_payloadstr is not NULL THEN
        payloadfrom := arg_payloadstr;
     END IF;
  ELSE
    joinpayloadtable := '';
    payloadfrom := '';
    iovsfrom := ' iovs.*, ';
  END IF;


  v_tagname := NULL;
  tagtablejoin := '';
  tagsearch := ' :tagname is null AND ';
  tagfrom := '';
  headsearch := ' ';
  IF arg_tagname = 'HEAD' THEN
     tagsearch := ' (iovs.user_tag_id=0 AND :tagname is null) AND '; 
  ELSIF issingleversion > 0 THEN
     tagtablejoin :=   ', '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' ||  cool_db_link || ' tags ' ;
                   -- 'on iovs.user_tag_id=tags.tag_id ';
     tagsearch := '(tags.tag_name = :tagname) AND iovs.user_tag_id=tags.tag_id AND ';
     v_tagname := arg_tagname;
     tagfrom := ' tags.*, ';
     headsearch := ' (iovs.new_head_id=0) AND ';
  END IF;
  
    --create a query for nodes --
    iovstablename :=  arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs2 ';
    coolhint := '/*+ QB_NAME(MAIN) NO_BIND_AWARE INDEX(@MAIN iovs@MAIN ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL")) LEADING(@MAIN channels@MAIN iovs@MAIN) USE_NL(@MAIN iovs@MAIN) INDEX(@MAX1 iovs2@MAX1 ("CHANNEL_ID" "IOV_SINCE" "IOV_UNTIL"))*/';

  v_iovbasefrom := '''' || v_iovbase || ''' as IOV_BASE '; 
    --create a query for nodes --
  v_stmt_str := 'select ' || coolhint || 
  iovsfrom ||
  ' channels.*, ' ||
    tagfrom || payloadfrom || v_iovbasefrom ||
  ' from ' 
  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS'||  cool_db_link || ' iovs ' ||
  ', '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' ||  cool_db_link || ' channels ' ||
  tagtablejoin ||
  joinpayloadtable ||
  ' where iovs.channel_id=channels.channel_id AND ' || payloadwhere || tagsearch ||  headsearch ||
  ' (iovs.iov_since >=COALESCE(( SELECT /*+ QB_NAME(MAX1)*/ MAX(iovs2.iov_since) FROM ' || iovstablename || ' WHERE iovs2.channel_id=channels.channel_id AND iovs2.iov_since <= :stime),:stime) ' ||
  '   AND iovs.iov_since <= :etime) ' || channelwherecondition;
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:
--  raise_application_error(-20001, 'Error in schema ' || arg_schemaname || ' and statment  ' || v_stmt_str);

  OPEN v_iov_cursor FOR v_stmt_str using v_tagname,arg_stime,arg_stime,arg_etime;
  RETURN v_iov_cursor;

  END f_Get_PayloadFieldsIovs;


/*************************************************/
function convertToTimestamp(arg_time varchar2) return timestamp as
  tfmt varchar2(100);
  timevar timestamp(6);
  begin
  tfmt := 'YYYY-MM-DD_HH24:MI:SS';
  timevar := TO_TIMESTAMP(SUBSTR(arg_time,0,19),tfmt);
  return timevar;
end convertToTimestamp; 
 

END COOL_SELECT_PKG;

/
