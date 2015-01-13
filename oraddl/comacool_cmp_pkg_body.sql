--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body COMACOOL_CMP_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "ATLAS_COND_TOOLS"."COMACOOL_CMP_PKG" 
AS
  preschemaName VARCHAR2(10) := 'ATLAS';


  /**/
  /* This function compares the nodes tables */
  /*************************/
FUNCTION f_CmpAll_Nodes(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2)
  RETURN comacool_node_type_t pipelined
AS
  schemaname VARCHAR2(30);
  comaschema VARCHAR2(50);
  
  CURSOR coolcoma_node_cursor
  IS
    SELECT comanodes.node_id as coma_node_id,
      comanodes.node_name as coma_node_name,
      'none' as coma_node_lastmod_date,
      comanodes.node_fullpath AS coma_node_fullpath,
      comanodes.node_description AS coma_node_description,
      coolnodes.*
    FROM cb_view_nodes comanodes 
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_nodes(schemaname,arg_dbname,arg_folder))
      ) coolnodes ON
      comanodes.node_fullpath=coolnodes.node_fullpath
  AND comanodes.cool_schema LIKE comaschema;
  
  v_comacool_node_row coolcoma_node_cursor%ROWTYPE;


BEGIN
  schemaname := preschemaName || '_' || arg_schemaname;
  comaschema := arg_schemaname || '/' || arg_dbname;
  --        raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
  OPEN coolcoma_node_cursor;
  LOOP
    FETCH coolcoma_node_cursor INTO v_comacool_node_row;
    EXIT
  WHEN coolcoma_node_cursor%NOTFOUND;
    pipe row(comacool_node_type(v_comacool_node_row.schema_name,v_comacool_node_row.dbname, 
                                v_comacool_node_row.node_id, v_comacool_node_row.node_name, 
                                v_comacool_node_row.node_fullpath, 
                                v_comacool_node_row.node_description, 
                                v_comacool_node_row.lastmod_date, 
                                v_comacool_node_row.coma_node_id, 
                                v_comacool_node_row.coma_node_name, 
                                v_comacool_node_row.coma_node_fullpath, 
                                v_comacool_node_row.coma_node_lastmod_date,
                                v_comacool_node_row.coma_node_description           
                                ));
  END LOOP;
  CLOSE coolcoma_node_cursor;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || schemaname);
  raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
END f_CmpAll_Nodes;
/*********************/

  /**/
  /* This function compares the nodes tables */
  /*************************/
FUNCTION f_Check_Nodes(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2)
  RETURN coma_cool_nodes_type_t pipelined
AS

  CURSOR coolcoma_node_cursor
  IS
  SELECT 
  coolnodes.schema_name clschema_name,
  coolnodes.dbname cldbname,
  coolnodes.node_id clnode_id,
  coolnodes.node_name clnode_name, 
  coolnodes.node_fullpath clnode_fullpath, 
  coolnodes.node_description clnode_description,  
  coolnodes.node_isleaf clnode_isleaf, 
  coolnodes.node_instime clnode_instime,  
  coolnodes.node_tinstime clnode_tinstime,
  coolnodes.lastmod_date cllastmod_date, 
  coolnodes.folder_versioning clfolder_versioning,
  coolnodes.folder_payloadspec clfolder_payloadspec,
  coolnodes.folder_iovtablename clfolder_iovtablename, 
  coolnodes.folder_tagtablename clfolder_tagtablename, 
  coolnodes.folder_channeltablename clfolder_channeltablename,
  coolnodes.iov_base cliov_base,
  coolnodes.iov_type cliov_type,
  comanodes.schema_name cmschema_name,
  comanodes.dbname cmdbname,
  comanodes.node_id cmnode_id,
  comanodes.node_name cmnode_name, 
  comanodes.node_fullpath cmnode_fullpath, 
  comanodes.node_description cmnode_description,  
  comanodes.node_isleaf cmnode_isleaf, 
  comanodes.node_instime cmnode_instime,  
  comanodes.node_tinstime cmnode_tinstime,
  comanodes.lastmod_date cmlastmod_date, 
  comanodes.folder_versioning cmfolder_versioning,
  comanodes.folder_payloadspec cmfolder_payloadspec,
  comanodes.folder_iovtablename cmfolder_iovtablename, 
  comanodes.folder_tagtablename cmfolder_tagtablename, 
  comanodes.folder_channeltablename cmfolder_channeltablename,
  comanodes.iov_base cmiov_base,
  comanodes.iov_type cmiov_type  
  FROM
  (select * from table(cool_select_pkg.f_getall_nodes(arg_schemaname,arg_dbname,arg_folder))) coolnodes
    full outer join 
  (select * from table(coma_select_pkg.f_getall_nodes(arg_schemaname,arg_dbname,arg_folder))) comanodes
  on coolnodes.schema_name=comanodes.schema_name 
  AND coolnodes.node_fullpath=comanodes.node_fullpath ;
--  where coolnodes.node_name is null OR comanodes.node_name is null;
  
  v_row coolcoma_node_cursor%ROWTYPE;


BEGIN
  OPEN coolcoma_node_cursor;
  LOOP
    FETCH coolcoma_node_cursor INTO v_row;
    EXIT
  WHEN coolcoma_node_cursor%NOTFOUND;
    
    pipe row(coma_cool_nodes_type(
              cool_schemanode_type(v_row.clschema_name,v_row.cldbname,v_row.clnode_id,
                            v_row.clnode_name,
                            v_row.clnode_fullpath,
                            v_row.clnode_description,
                            v_row.clnode_isleaf,
                            v_row.clnode_instime,
                            v_row.clnode_tinstime,
                            v_row.cllastmod_date,
                            v_row.clfolder_versioning, 
                            v_row.clfolder_payloadspec, 
                            v_row.clfolder_iovtablename,
                            v_row.clfolder_tagtablename,
                            v_row.clfolder_channeltablename,
                            v_row.cliov_base,
                            v_row.cliov_type),
              cool_schemanode_type(v_row.cmschema_name,
                            v_row.cmdbname,v_row.cmnode_id,
                            v_row.cmnode_name,
                            v_row.cmnode_fullpath,
                            v_row.cmnode_description,
                            v_row.cmnode_isleaf,
                            v_row.cmnode_instime,
                            v_row.cmnode_tinstime,
                            v_row.cmlastmod_date,
                            v_row.cmfolder_versioning, 
                            v_row.cmfolder_payloadspec, 
                            v_row.cmfolder_iovtablename,
                            v_row.cmfolder_tagtablename,
                            v_row.cmfolder_channeltablename,
                            v_row.cmiov_base,
                            v_row.cmiov_type)
                            ));
  END LOOP;
  CLOSE coolcoma_node_cursor;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || arg_schemaname);
  raise_application_error(-20001, 'Error in schema ' || arg_schemaname );
END f_Check_Nodes;


  /**/
  /* This function compares the nodes tables */
  /*************************/
FUNCTION f_Check_Tags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN coma_cool_tag_type_t pipelined
AS

  CURSOR coolcoma_tag_cursor
  IS
  SELECT 
  coolnodes.schema_name clschema_name,
  coolnodes.dbname cldbname,
  coolnodes.node_id clnode_id,
  coolnodes.node_name clnode_name, 
  coolnodes.node_fullpath clnode_fullpath, 
  coolnodes.node_description clnode_description,  
  coolnodes.node_isleaf clnode_isleaf, 
  coolnodes.node_instime clnode_instime,  
  coolnodes.node_tinstime clnode_tinstime,
  coolnodes.lastmod_date cllastmod_date, 
  coolnodes.folder_iovtablename clfolder_iovtablename, 
  coolnodes.folder_tagtablename clfolder_tagtablename, 
  coolnodes.folder_channeltablename clfolder_channeltablename,
  coolnodes.tag_id cltag_id,
  coolnodes.tag_name cltag_name,
  coolnodes.tag_description cltag_description,
  coolnodes.tag_lock_status cltag_lock_status,
  coolnodes.sys_instime clsys_instime,
  comanodes.schema_name cmschema_name,
  comanodes.dbname cmdbname,
  comanodes.node_id cmnode_id,
  comanodes.node_name cmnode_name, 
  comanodes.node_fullpath cmnode_fullpath, 
  comanodes.node_description cmnode_description,  
  comanodes.node_isleaf cmnode_isleaf, 
  comanodes.node_instime cmnode_instime,  
  comanodes.node_tinstime cmnode_tinstime,
  comanodes.lastmod_date cmlastmod_date, 
  comanodes.folder_iovtablename cmfolder_iovtablename, 
  comanodes.folder_tagtablename cmfolder_tagtablename, 
  comanodes.folder_channeltablename cmfolder_channeltablename,
  comanodes.tag_id cmtag_id,
  comanodes.tag_name cmtag_name,
  comanodes.tag_description cmtag_description,
  comanodes.tag_lock_status cmtag_lock_status,
  comanodes.sys_instime cmsys_instime
  FROM
  (select * from table(cool_select_pkg.f_getall_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname))) coolnodes
    full outer join 
  (select * from table(coma_select_pkg.f_getall_tags(arg_schemaname,arg_dbname,arg_folder,arg_tagname))) comanodes
  on coolnodes.schema_name=comanodes.schema_name 
  AND coolnodes.node_fullpath=comanodes.node_fullpath AND coolnodes.tag_name=comanodes.tag_name;
--  where coolnodes.node_name is null OR comanodes.node_name is null;
  
  v_row coolcoma_tag_cursor%ROWTYPE;


BEGIN
  OPEN coolcoma_tag_cursor;
  LOOP
    FETCH coolcoma_tag_cursor INTO v_row;
    EXIT
  WHEN coolcoma_tag_cursor%NOTFOUND;
    
    pipe row(coma_cool_tag_type(
              cool_schemanodetag_type(v_row.clschema_name,v_row.cldbname,v_row.clnode_id,
                            v_row.clnode_name,
                            v_row.clnode_fullpath,
                            v_row.clnode_description,
                            v_row.clnode_isleaf,
                            v_row.clnode_instime,
                            v_row.clnode_tinstime,
                            v_row.cllastmod_date,
                            v_row.clfolder_iovtablename,
                            v_row.clfolder_tagtablename,
                            v_row.clfolder_channeltablename,
                            v_row.cltag_id,
                            v_row.cltag_name,
                            v_row.cltag_description,
                            v_row.cltag_lock_status,
                            v_row.clsys_instime),
              cool_schemanodetag_type(v_row.cmschema_name,
                            v_row.cmdbname,v_row.cmnode_id,
                            v_row.cmnode_name,
                            v_row.cmnode_fullpath,
                            v_row.cmnode_description,
                            v_row.cmnode_isleaf,
                            v_row.cmnode_instime,
                            v_row.cmnode_tinstime,
                            v_row.cmlastmod_date,
                            v_row.cmfolder_iovtablename,
                            v_row.cmfolder_tagtablename,
                            v_row.cmfolder_channeltablename,
                            v_row.cmtag_id,
                            v_row.cmtag_name,
                            v_row.cmtag_description,
                            v_row.cmtag_lock_status,
                            v_row.cmsys_instime)
                            ));
  END LOOP;
  CLOSE coolcoma_tag_cursor;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || arg_schemaname);
  raise_application_error(-20001, 'Error in schema ' || arg_schemaname );
END f_Check_Tags;



/*********************/

FUNCTION f_CmpAll_NodesDiff(
    arg_schemaname VARCHAR2)
  RETURN comacool_node_type_t pipelined
AS
  schemaname VARCHAR2(30);
  comaschema VARCHAR2(50);
  dbname VARCHAR(30);
  foldername VARCHAR(255);
  
  CURSOR db_cursor 
  IS
    select distinct cbi_name from cb_view_nodes;
  
  
  CURSOR coolcoma_node_cursor
  IS
    (SELECT comanodes.node_id as coma_node_id,
      comanodes.node_name as coma_node_name,
      'none' as coma_node_lastmod_date,
      comanodes.node_fullpath AS coma_node_fullpath,
      comanodes.node_description AS coma_node_description,
      coolnodes.*
    FROM cb_view_nodes comanodes 
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_nodes(schemaname,dbname,foldername))
      ) coolnodes ON
      comanodes.node_fullpath=coolnodes.node_fullpath
      AND comanodes.cool_schema LIKE comaschema
    WHERE
      (comanodes.node_id is null OR coolnodes.node_id is null) OR
      (comanodes.node_description != coolnodes.node_description)
      --comanodes.node_id<10
    )
  UNION
      (SELECT comanodes.node_id as coma_node_id,
      comanodes.node_name as coma_node_name,
      'none' as coma_node_lastmod_date,
      comanodes.node_fullpath AS coma_node_fullpath,
      comanodes.node_description AS coma_node_description,
      coolnodes.*
    FROM cb_view_nodes comanodes
    RIGHT OUTER JOIN
    (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_nodes(schemaname,dbname,foldername))
      ) coolnodes ON
      comanodes.node_fullpath=coolnodes.node_fullpath
      AND comanodes.cool_schema LIKE comaschema
    WHERE
      (comanodes.node_id is null OR coolnodes.node_id is null) OR
      (comanodes.node_description != coolnodes.node_description)
  );
  
  v_comacool_node_row coolcoma_node_cursor%ROWTYPE;

BEGIN
  schemaname := preschemaName || '_' || arg_schemaname;
  foldername := '%';
  OPEN db_cursor;
  LOOP
    FETCH db_cursor INTO dbname;
    EXIT
  WHEN db_cursor%NOTFOUND;
  
  
  comaschema := arg_schemaname || '/' || dbname;
  --        raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
  OPEN coolcoma_node_cursor;
  LOOP
    FETCH coolcoma_node_cursor INTO v_comacool_node_row;
    EXIT
  WHEN coolcoma_node_cursor%NOTFOUND;
    pipe row(comacool_node_type(v_comacool_node_row.schema_name,v_comacool_node_row.dbname, 
                                v_comacool_node_row.node_id, v_comacool_node_row.node_name, 
                                v_comacool_node_row.node_fullpath, 
                                v_comacool_node_row.node_description, 
                                v_comacool_node_row.lastmod_date, 
                                v_comacool_node_row.coma_node_id, 
                                v_comacool_node_row.coma_node_name, 
                                v_comacool_node_row.coma_node_fullpath, 
                                v_comacool_node_row.coma_node_lastmod_date,
                                v_comacool_node_row.coma_node_description           
                                ));
  END LOOP;
  CLOSE coolcoma_node_cursor;

  END LOOP;
  CLOSE db_cursor;

EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || schemaname);
  raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
END f_CmpAll_NodesDiff;



/*************************************************/
FUNCTION f_CmpAll_Tags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_folder     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN comacool_tag_type_t pipelined
AS
  schemaname VARCHAR2(30);
  comaschema VARCHAR2(50);
  
  CURSOR coolcoma_tag_cursor
  IS
    SELECT comanodes.tag_name as coma_tag_name,
      comanodes.tag_lock_status as coma_tag_lock_status,
      comanodes.tag_description as coma_tag_description,      
      comanodes.cool_schema,
      comanodes.node_fullpath as comanodefp,
      comanodes.cbft_rowcount as comatagrowcount,
      comanodes.cbf_chan_count as comatagchancount,
      cooltags.niovs, cooltags.nchannels, cooltags.miniov_since, cooltags.miniov_until, cooltags.maxiov_since, cooltags.maxiov_until,
      coolnodes.*
    FROM cb_view_nodetags comanodes
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_tags(schemaname,arg_dbname,arg_folder,arg_tagname))
      ) coolnodes
  ON comanodes.node_fullpath=coolnodes.node_fullpath
    AND comanodes.tag_name=coolnodes.tag_name
    AND comanodes.cool_schema LIKE comaschema
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_iovsshort(schemaname,arg_dbname,arg_folder,arg_tagname))
      ) cooltags
  ON comanodes.node_fullpath=cooltags.node_fullpath
    AND comanodes.tag_name=cooltags.tag_name
    AND comanodes.cool_schema LIKE comaschema
  where comanodes.tag_name like arg_tagname 
    AND comanodes.node_fullpath like arg_folder;
  
  v_comacool_tag_row coolcoma_tag_cursor%ROWTYPE;

BEGIN
  schemaname := preschemaName || '_' || arg_schemaname;
  comaschema := arg_schemaname || '/' || arg_dbname;

  OPEN coolcoma_tag_cursor;
  LOOP
    FETCH coolcoma_tag_cursor INTO v_comacool_tag_row;
    EXIT
  WHEN coolcoma_tag_cursor%NOTFOUND;
    pipe row(comacool_tag_type(v_comacool_tag_row.schema_name,v_comacool_tag_row.dbname, 
    v_comacool_tag_row.node_name, v_comacool_tag_row.node_fullpath, 
    v_comacool_tag_row.tag_id,
    v_comacool_tag_row.tag_name, 
    v_comacool_tag_row.tag_description, 
    v_comacool_tag_row.tag_lock_status, 
    v_comacool_tag_row.SYS_INSTIME,
  v_comacool_tag_row.NIOVS ,
  v_comacool_tag_row.NCHANNELS ,
  v_comacool_tag_row.MINIOV_SINCE ,
  v_comacool_tag_row.MINIOV_UNTIL ,
  v_comacool_tag_row.MAXIOV_SINCE ,
  v_comacool_tag_row.MAXIOV_UNTIL ,
    v_comacool_tag_row.cool_schema, 
    v_comacool_tag_row.comanodefp, 
    v_comacool_tag_row.coma_tag_name, 
    v_comacool_tag_row.coma_tag_description, 
    v_comacool_tag_row.coma_tag_lock_status,
    v_comacool_tag_row.comatagrowcount,
    v_comacool_tag_row.comatagchancount
    ));

  END LOOP;
  CLOSE coolcoma_tag_cursor;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || schemaname);
  raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
END f_CmpAll_Tags;
/*************************************************/
/*
* This function can be used to select only differences among tags in COMA and COOL
*
*/
FUNCTION f_CmpAll_TagsDiff(arg_schemaname VARCHAR2)
  RETURN comacool_tag_type_t pipelined
AS
  schemaname VARCHAR2(30);
  comaschema VARCHAR2(50);
  dbname VARCHAR(30);
  foldername VARCHAR(255);
  
  CURSOR db_cursor 
  IS
    select distinct cbi_name from cb_view_nodes;
  /*
      LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_iovsshort(schemaname,dbname,foldername,'%'))
      ) cooltags
  ON comanodes.node_fullpath=cooltags.node_fullpath
    AND comanodes.tag_name=cooltags.tag_name
    AND comanodes.cool_schema LIKE comaschema
  */
  CURSOR coolcoma_tag_cursor
  IS
    (SELECT comanodes.tag_name as coma_tag_name,
      comanodes.tag_lock_status as coma_tag_lock_status,
      comanodes.tag_description as coma_tag_description,
      comanodes.cool_schema,
      comanodes.node_fullpath as comanodefp,
      comanodes.cbft_rowcount as comatagrowcount,
      comanodes.cbf_chan_count as comatagchancount,
--      cooltags.niovs, cooltags.nchannels, cooltags.miniov_since, cooltags.miniov_until, cooltags.maxiov_since, cooltags.maxiov_until,
      coolnodes.*
    FROM cb_view_nodetags comanodes
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_tagsiovsshort(schemaname,dbname,foldername,'%'))
      ) coolnodes
  ON comanodes.node_fullpath=coolnodes.node_fullpath
    AND comanodes.tag_name=coolnodes.tag_name
    AND comanodes.cool_schema LIKE comaschema
    WHERE
      (comanodes.tag_name is null OR coolnodes.tag_name is null) OR (comanodes.tag_lock_status != coolnodes.tag_lock_status)
      OR (comanodes.tag_description != coolnodes.tag_description) OR (comanodes.cbft_rowcount != coolnodes.niovs))
  UNION
    (SELECT comanodes.tag_name as coma_tag_name,
      comanodes.tag_lock_status as coma_tag_lock_status,
      comanodes.tag_description as coma_tag_description,
      comanodes.cool_schema,
      comanodes.node_fullpath as comanodefp,
      comanodes.cbft_rowcount as comatagrowcount,
      comanodes.cbf_chan_count as comatagchancount,
--      cooltags.niovs, cooltags.nchannels, cooltags.miniov_since, cooltags.miniov_until, cooltags.maxiov_since, cooltags.maxiov_until,
      coolnodes.*
    FROM cb_view_nodetags comanodes
    RIGHT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_tagsiovsshort(schemaname,dbname,foldername,'%'))
      ) coolnodes
  ON comanodes.node_fullpath=coolnodes.node_fullpath
    AND comanodes.tag_name=coolnodes.tag_name
    AND comanodes.cool_schema LIKE comaschema
    WHERE
      (comanodes.tag_name is null OR coolnodes.tag_name is null)  OR (comanodes.tag_lock_status != coolnodes.tag_lock_status)
      OR (comanodes.tag_description != coolnodes.tag_description)  OR (comanodes.cbft_rowcount != coolnodes.niovs));
  
  v_comacool_tag_row coolcoma_tag_cursor%ROWTYPE;

BEGIN
  schemaname := preschemaName || '_' || arg_schemaname;
  foldername := '%';
  OPEN db_cursor;
  LOOP
    FETCH db_cursor INTO dbname;
    EXIT
  WHEN db_cursor%NOTFOUND;
  
  
  comaschema := arg_schemaname || '/' || dbname;

  OPEN coolcoma_tag_cursor;
  LOOP
    FETCH coolcoma_tag_cursor INTO v_comacool_tag_row;
    EXIT
  WHEN coolcoma_tag_cursor%NOTFOUND;
    pipe row(comacool_tag_type(v_comacool_tag_row.schema_name,v_comacool_tag_row.dbname, 
    v_comacool_tag_row.node_name, v_comacool_tag_row.node_fullpath, 
    v_comacool_tag_row.tag_id,
    v_comacool_tag_row.tag_name, 
    v_comacool_tag_row.tag_description, 
    v_comacool_tag_row.tag_lock_status, 
    v_comacool_tag_row.SYS_INSTIME,
  v_comacool_tag_row.NIOVS ,
  v_comacool_tag_row.NCHANNELS ,
  v_comacool_tag_row.MINIOV_SINCE ,
  v_comacool_tag_row.MINIOV_UNTIL ,
  v_comacool_tag_row.MAXIOV_SINCE ,
  v_comacool_tag_row.MAXIOV_UNTIL ,
    v_comacool_tag_row.cool_schema, 
    v_comacool_tag_row.comanodefp, 
    v_comacool_tag_row.coma_tag_name, 
    v_comacool_tag_row.coma_tag_description, 
    v_comacool_tag_row.coma_tag_lock_status,
    v_comacool_tag_row.comatagrowcount,
    v_comacool_tag_row.comatagchancount
    ));
  END LOOP;
  CLOSE coolcoma_tag_cursor;
  
  END LOOP;
  CLOSE db_cursor;

EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || schemaname);
  raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
END f_CmpAll_TagsDiff;



/*************************************************/
FUNCTION f_CmpAll_GTags(
    arg_schemaname VARCHAR2,
    arg_dbname     VARCHAR2,
    arg_tagname    VARCHAR2)
  RETURN comacool_gtag_type_t pipelined
AS
  schemaname VARCHAR2(30);
  comaschema VARCHAR2(50);
  
  CURSOR coolcoma_tag_cursor
  IS
    SELECT comagtags.ftag_name as coma_tag_name,
      comagtags.ftlock as coma_tag_lock_status,      
      comagtags.gtag_name as coma_gtag_name,
      comagtags.gtlock as coma_gtag_lock_status,      
      comagtags.cool_schema,
      comagtags.node_fullpath as comanodefp,
      cooltags.*
    FROM cb_view_ftgts comagtags
    LEFT OUTER JOIN
      (SELECT *
      FROM TABLE(cool_select_pkg.f_getall_tagsforgtag(schemaname,arg_dbname,arg_tagname))
      ) cooltags
  ON comagtags.node_fullpath=cooltags.node_fullpath
    AND comagtags.cool_schema LIKE comaschema
    AND comagtags.gtag_name=cooltags.gtag_name
  where comagtags.gtag_name like arg_tagname;
  
  v_comacool_tag_row coolcoma_tag_cursor%ROWTYPE;

BEGIN
  schemaname := preschemaName || '_' || arg_schemaname;
  comaschema := arg_schemaname || '/' || arg_dbname;

  OPEN coolcoma_tag_cursor;
  LOOP
    FETCH coolcoma_tag_cursor INTO v_comacool_tag_row;
    EXIT
  WHEN coolcoma_tag_cursor%NOTFOUND;
    pipe row(comacool_gtag_type(v_comacool_tag_row.schema_name,v_comacool_tag_row.db_name, 
    v_comacool_tag_row.node_name, v_comacool_tag_row.node_fullpath, 
    v_comacool_tag_row.gtag_name, v_comacool_tag_row.gtag_lock_status, 
    v_comacool_tag_row.tag_name, v_comacool_tag_row.tag_lock_status, 
    v_comacool_tag_row.SYS_INSTIME,
    v_comacool_tag_row.cool_schema, v_comacool_tag_row.comanodefp, 
    v_comacool_tag_row.coma_tag_name, v_comacool_tag_row.coma_tag_lock_status));
  END LOOP;
  CLOSE coolcoma_tag_cursor;
EXCEPTION
WHEN OTHERS THEN
  dbms_output.put_line('Skip ' || schemaname);
  raise_application_error(-20001, 'Error in schema ' || schemaname || ' ' || comaschema);
END f_CmpAll_GTags;

/**
 * Function to retrieve a payload containing the poolref value for a given schema/db , folder and tag.
 * It works only for folders containing a pool payload type inline.
 */
function f_Get_PoolNodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tagname VARCHAR2)  return cool_iovpoolref_type_t pipelined 
AS
  cool_db_link    varchar2(100);
  v_nodefullpath varchar2(255);
  v_iovtablename varchar2(255);
  v_chantablename varchar2(255);
  v_stmt_str      VARCHAR2(1000);  
   
  cursor node_cursor is
    select * from table(cool_select_pkg.f_get_nodes(arg_schemaname,arg_dbname,arg_folder)) where folder_payloadspec like 'Pool%';
  
  node_id number;
   
  TYPE IovCurTyp  IS REF CURSOR;
  v_iov_cursor    IovCurTyp; 
  v_row IOVEXT_POOLREF_RECORD;
  
  foldernamestr varchar2(10);
  iovinstime timestamp(6);
  iovmoddate timestamp(6);
  channame varchar2(255);
  tagname varchar2(255);

  v_node_row node_cursor%ROWTYPE;

  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
  
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    node_id := v_node_row.node_id;
    v_nodefullpath := v_node_row.node_fullpath;
    v_iovtablename := v_node_row.folder_iovtablename;
    v_chantablename := v_node_row.folder_channeltablename;
/*
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO node_id;
      EXIT
    WHEN node_cursor%NOTFOUND;
  END LOOP;
  CLOSE node_cursor;
*/  
--  foldernamestr := cool_select_pkg.convertNodeIdToFolder(node_id);

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
  'channels.channel_name,' ||
  'tags.tag_name,' ||
  'iovs."PoolRef" as poolref '||
  ' from ' 
--  || arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'IOVS' ||  cool_db_link || ' iovs ' ||
  || arg_schemaname || '.' || v_iovtablename ||  cool_db_link || ' iovs ' ||
--  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || 'CHANNELS' || cool_db_link || ' channels ' ||
  'left join '|| arg_schemaname || '.' || v_chantablename || cool_db_link || ' channels ' ||
  'on iovs.channel_id=channels.channel_id ' ||
  'left join '|| arg_schemaname || '.' || arg_dbname || '_' || 'TAGS' || cool_db_link || ' tags ' ||
  'on iovs.user_tag_id=tags.tag_id and tags.node_id=' || node_id ||
  ' where (tags.tag_name like :tagname) AND (iovs.new_head_id=0)';
      
-- Open cursor & specify bind argument in USING clause:
-- Fetch rows from result set one at a time:

  OPEN v_iov_cursor FOR v_stmt_str using arg_tagname;
  LOOP
    FETCH v_iov_cursor INTO v_row;
  EXIT WHEN v_iov_cursor%NOTFOUND;
    iovinstime := cool_select_pkg.convertToTimestamp(v_row.sys_instime);
    iovmoddate := cool_select_pkg.convertToTimestamp(v_row.lastmod_date);
    pipe row(cool_iovpoolref_type(v_row.object_id,v_row.channel_id,v_row.iov_since,v_row.iov_until,
                          v_row.user_tag_id, iovinstime, iovmoddate, v_row.original_id, 
                          v_row.new_head_id, v_row.channame,v_row.tagname,v_row.poolref,node_id, v_nodefullpath));
  END LOOP; 
  -- Close cursor on iovs 
  CLOSE v_iov_cursor; 
  
  END LOOP;
  CLOSE node_cursor;

END f_Get_PoolNodes;


END COMACOOL_CMP_PKG;

/
