--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View CB_VIEW_FTGTS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_FTGTS" ("FTIDX", "FIDX", "GTIDX", "GTFTIDX", "COOL_SCHEMA", "DB_NAME", "NODE_ID", "NODE_NAME", "NODE_FULLPATH", "NODE_DESCRIPTION", "FTAG_ID", "FTAG_NAME", "FTAG_DESC", "FTLOCK", "GTAG_ID", "GTAG_NAME", "GTAG_DESC", "GTLOCK", "SYS_INSTIME") AS 
  SELECT cb_ftags.cbft_index AS ftidx,
    cb_ftags.cbf_index       AS fidx,
    cb_gtags.cbgt_index      AS gtidx,
    cb_gtfts.cbg2f_index     AS gtftidx,
    cb_nodes.owner_name      AS cool_schema,
    cb_nodes.cbi_name        AS db_name,
    cb_nodes.node_id,
    cb_nodes.node_name,
    cb_nodes.node_fullpath,
    cb_nodes.node_description,
    cb_ftags.cbft_index                      AS ftag_id,
    cb_ftags.tag_name                        AS ftag_name,
    cb_ftags.tag_description                 AS ftag_desc,
    cb_ftags.tag_lock_status                 AS ftlock,
    cb_gtags.cbgt_index                      AS gtag_id,
    cb_gtags.tag_name                        AS gtag_name,
    cb_gtags.tag_description                 AS gtag_desc,
    cb_gtags.tag_lock_status                 AS gtlock,
    CAST(cb_ftags.cbft_instime AS TIMESTAMP) AS sys_instime
  FROM ATLAS_TAGS_METADATA.coma_cb_ftags cb_ftags,
    ATLAS_TAGS_METADATA.coma_cb_gtags cb_gtags,
    ATLAS_TAGS_METADATA.coma_cb_gt_to_fts cb_gtfts,
    cb_view_nodes cb_nodes
  WHERE cb_gtags.cbgt_index=cb_gtfts.cbgt_index
  AND cb_ftags.cbft_index  =cb_gtfts.cbft_index
  AND cb_ftags.cbf_index   = cb_nodes.cbf_index
  ORDER BY cb_ftags.tag_name;
--------------------------------------------------------
--  DDL for View CB_VIEW_GTS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_GTS" ("GTAG_NAME", "N_SCHEMAS", "DB_NAME", "SYS_INSTIME", "GTAG_LOCK_STATUS", "GTAG_DESCRIPTION") AS 
  SELECT 
    cb_gtags.tag_name        AS gtag_name,
    count(distinct cb_nodes.cool_schema) AS n_schemas,
    max(cb_nodes.cbi_name) AS db_name,
    max(cb_gtags.cbgt_instime) AS SYS_INSTIME,
    max(cb_gtags.tag_lock_status) AS GTAG_LOCK_STATUS,
    max(cb_gtags.tag_description) AS gtag_description
  FROM ATLAS_TAGS_METADATA.coma_cb_ftags cb_ftags,
    ATLAS_TAGS_METADATA.coma_cb_gtags cb_gtags,
    ATLAS_TAGS_METADATA.coma_cb_gt_to_fts cb_gtfts,
    cb_view_nodes cb_nodes
  WHERE cb_gtags.cbgt_index=cb_gtfts.cbgt_index
  AND cb_ftags.cbft_index  =cb_gtfts.cbft_index
  AND cb_ftags.cbf_index   = cb_nodes.cbf_index
  GROUP BY cb_gtags.tag_name;
--------------------------------------------------------
--  DDL for View CB_VIEW_NODES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_NODES" ("OWNER_NAME", "CBI_NAME", "CBS_NAME", "COOL_SCHEMA", "CBF_INDEX", "NODE_ID", "NODE_NAME", "NODE_FULLPATH", "NODE_DESCRIPTION", "NODE_ISLEAF", "NODE_INSTIME", "NODE_TINSTIME", "LASTMOD_DATE", "FOLDER_VERSIONING", "CBF_IOV_BASE", "CBF_CHAN_COUNT", "CBF_PAYLOAD_COUNT", "FOLDER_IOVTABLENAME", "FOLDER_TAGTABLENAME", "FOLDER_CHANNELTABLENAME") AS 
  SELECT owner_name,
    cbi_name,
    cbs_name,
    cool_schema,
    cb_nodes.cbf_index,
    node_id,
    node_name,
    node_fullpath,
    node_description,
    node_isleaf,
    node_instime,
    CAST(cb_nodes.cbf_lastmod_date AS TIMESTAMP) as node_tinstime,
    lastmod_date,
    folder_versioning,
    cbf_iov_base,
    cbf_chan_count,
    cbf_payload_count,
    folder_iovtablename,
    folder_tagtablename,
    folder_channeltablename
  FROM ATLAS_TAGS_METADATA.coma_cb_owner_instances cb_oi,
    ATLAS_TAGS_METADATA.coma_cb_nodes cb_nodes,
    ATLAS_TAGS_METADATA.coma_cb_references cb_refs
  WHERE cb_oi.cboi_index= cb_nodes.cboi_index
  AND cb_refs.cbf_index =cb_nodes.cbf_index
  AND node_isleaf       =1;
--------------------------------------------------------
--  DDL for View CB_VIEW_NODETAGS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_NODETAGS" ("TAG_NAME", "TAG_LOCK_STATUS", "TAG_DESCRIPTION", "CBFT_ROWCOUNT", "CBFT_INSTIME", "CBFT_LAST_OBJTIME", "CBF_CHAN_COUNT", "COOL_SCHEMA", "NODE_FULLPATH") AS 
  SELECT tag_name,
    tag_lock_status,
    tag_description,
    cbft_rowcount,
    cbft_instime,
    cbft_last_objtime,
    cbf_chan_count,
    nodes.cool_schema,
    nodes.node_fullpath
  FROM ATLAS_TAGS_METADATA.coma_cb_ftags ftags,
    cb_view_nodes nodes
  WHERE ftags.cbf_index= nodes.cbf_index;
--------------------------------------------------------
--  DDL for View CB_VIEW_PARENT_PERIODS_LUMI
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_PARENT_PERIODS_LUMI" ("P_PERIOD", "P_PROJECT", "DELIVERED_LUMI") AS 
  SELECT regexp_substr(cpd.P_PERIOD,'[[:alpha:]]+') P_PERIOD,
    cpd.P_PROJECT P_PROJECT,
    SUM((crl.INTEG_LUMI*crl.READY_FRACTION/1000000.)) AS DELIVERED_LUMI
  FROM ATLAS_TAGS_METADATA.COMA_PERIOD_P1_TO_RUNS cpptr
  LEFT JOIN ATLAS_TAGS_METADATA.COMA_PERIOD_DEFS cpd
  ON cpptr.P_INDEX = cpd.P_INDEX
  LEFT JOIN ATLAS_TAGS_METADATA.COMA_RUN_LUMS crl
  ON cpptr.RUN_INDEX = crl.RUN_INDEX
  GROUP BY regexp_substr(cpd.P_PERIOD,'[[:alpha:]]+'),
    cpd.P_PROJECT
  ORDER BY cpd.P_PROJECT,
    P_PERIOD;
--------------------------------------------------------
--  DDL for View CB_VIEW_PERIODS_LUMI
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_PERIODS_LUMI" ("P_PERIOD", "P_PROJECT", "DELIVERED_LUMI") AS 
  SELECT cpd.P_PERIOD P_PERIOD,
    cpd.P_PROJECT P_PROJECT,
    SUM((crl.INTEG_LUMI*crl.READY_FRACTION/1000000.)) AS DELIVERED_LUMI
  FROM ATLAS_TAGS_METADATA.COMA_PERIOD_P1_TO_RUNS cpptr
  LEFT JOIN ATLAS_TAGS_METADATA.COMA_PERIOD_DEFS cpd
  ON cpptr.P_INDEX =cpd.P_INDEX
  LEFT JOIN ATLAS_TAGS_METADATA.COMA_RUN_LUMS crl
  ON cpptr.RUN_INDEX = crl.RUN_INDEX
  GROUP BY cpd.P_PERIOD,
    cpd.P_PROJECT
  ORDER BY cpd.P_PROJECT,
    cpd.P_PERIOD;
--------------------------------------------------------
--  DDL for View CB_VIEW_SCHEMAGTS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_SCHEMAGTS" ("NODE_ID", "TAG_ID", "GTAG_NAME", "GTAG_LOCK_STATUS", "GTAG_DESCRIPTION", "SCHEMA_NAME", "DB_NAME", "SYS_INSTIME", "SYSDATE_INSTIME") AS 
  SELECT 0                AS node_id,
    gtags.cbgt_index      AS tag_id,
    gtags.tag_name        AS gtag_name,
    gtags.tag_lock_status AS gtag_lock_status,
    gtags.tag_description AS gtag_description,
    owner_name            AS schema_name,
    cbi_name              AS db_name,
    gtags.sys_instime     AS sys_instime,
    CAST(gtags.cbgt_instime AS TIMESTAMP)    AS sysdate_instime
  FROM ATLAS_TAGS_METADATA.coma_cb_owner_instances schemas,
    ATLAS_TAGS_METADATA.coma_cb_gt_to_ois gtags2schema,
    ATLAS_TAGS_METADATA.coma_cb_gtags gtags
  WHERE schemas.cboi_index    =gtags2schema.cboi_index
  AND gtags2schema.cbgt_index = gtags.cbgt_index;
--------------------------------------------------------
--  DDL for View CB_VIEW_TAGS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CB_VIEW_TAGS" ("TAG_ID", "TAG_NAME", "TAG_DESCRIPTION", "TAG_LOCK_STATUS", "SYS_INSTIME", "SCHEMA_NAME", "DB_NAME", "NODE_ID", "NODE_NAME", "NODE_FULLPATH", "NODE_DESCRIPTION", "NODE_ISLEAF", "NODE_INSTIME", "NODE_TINSTIME", "LASTMOD_DATE", "FOLDER_IOVTABLENAME", "FOLDER_TAGTABLENAME", "FOLDER_CHANNELTABLENAME") AS 
  SELECT 
    ftags.cbft_index as tag_id,
    ftags.tag_name,
    ftags.tag_description,
    ftags.tag_lock_status,
    ftags.sys_instime,
    nodes.owner_name as schema_name,
    nodes.cbi_name as db_name,
    nodes.node_id,
    nodes.node_name,
    nodes.node_fullpath,
    nodes.node_description,
    nodes.node_isleaf,
    nodes.node_instime,
    nodes.node_tinstime,
    nodes.lastmod_date,
    nodes.folder_iovtablename,
    nodes.folder_tagtablename,
    nodes.folder_channeltablename
  FROM 
    ATLAS_TAGS_METADATA.coma_cb_ftags ftags,
    cb_view_nodes nodes 
  WHERE
    ftags.cbf_index= nodes.cbf_index;
--------------------------------------------------------
--  DDL for View COMA_CB_PLSQL_GTAGS
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."COMA_CB_PLSQL_GTAGS" ("TAG_NAME", "TAG_DESCRIPTION", "TAG_LOCK_STATUS", "SYS_INSTIME") AS 
  (select 
    distinct tag_name, tag_description, tag_lock_status, sys_instime
  from table (atlas_cond_tools.cool_select_pkg.f_getall_globaltags('ATLAS_COOL%','OFLP200','%')))
  union
  (select 
    distinct tag_name, tag_description, tag_lock_status, sys_instime
  from table (atlas_cond_tools.cool_select_pkg.f_getall_globaltags('ATLAS_COOL%','COMP200','%')))
  union  
  (select 
    distinct tag_name, tag_description, tag_lock_status, sys_instime
  from table (atlas_cond_tools.cool_select_pkg.f_getall_globaltags('ATLAS_COOL%','CONDBR2','%')));
--------------------------------------------------------
--  DDL for View COMA_CB_PLSQL_GTAGS_TEST
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."COMA_CB_PLSQL_GTAGS_TEST" ("MTAG_NAME", "MTAG_DESCRIPTION", "MTAG_LOCK_STATUS", "MSYS_INSTIME") AS 
  select 
     ggt.tag_name, ggt.tag_description, ggt.tag_lock_status, ggt.sys_instime
  from table(cool_select_pkg.f_getall_globaltags('ATLAS_COOL%','OFLP200','%')) ggt, 
       ATLAS_TAGS_METADATA.COMA_CB_GTAGS gtag
  WHERE ggt.tag_name=gtag.TAG_NAME(+);
--------------------------------------------------------
--  DDL for View CR_VIEW_OLDRUNINFO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CR_VIEW_OLDRUNINFO" ("RUN_NUMBER", "START_TIME", "END_TIME", "DATA_SOURCE", "RUN_TYPE", "RUN_TYPE_DESC", "P_PROJECT", "P_DESC", "INTEG_LUMI", "START_LBN", "END_LBN", "P_PERIOD", "L1_EVENTS", "L2_EVENTS", "EF_EVENTS", "RECORDED_EVENTS", "PARTITION_NAME") AS 
  SELECT run_number,
    CAST(cruns.start_time AS TIMESTAMP) start_time,
    CAST(cruns.end_time AS TIMESTAMP) end_time,
    data_source,
    cruns.run_type,
    runtype.run_type_desc,
    p_project,
    p_desc,
    (runlumi.integ_lumi*runlumi.ready_fraction)/1000000 integ_lumi,
    start_lbn,
    end_lbn,
    p_period,
    runevts.l1_events,
    runevts.l2_events,
    runevts.ef_events,
    runevts.recorded_events,
    runevts.partition_name
  FROM ATLAS_TAGS_METADATA.COMA_RUNS cruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_P1_TO_RUNS pruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_DEFS pdefs,
    ATLAS_TAGS_METADATA.COMA_RUN_TYPES runtype,
    ATLAS_TAGS_METADATA.COMA_RUN_EVENTS runevts,
    ATLAS_TAGS_METADATA.COMA_RUN_LUMS runlumi
  WHERE cruns.run_index  =pruns.run_index(+)
  AND pruns.p_index      =pdefs.p_index(+)
  AND cruns.run_index    =runevts.run_index(+)
  AND cruns.run_index    =runlumi.run_index(+)
  AND runtype.run_type(+)=cruns.run_type;
--------------------------------------------------------
--  DDL for View CR_VIEW_RUNINFO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CR_VIEW_RUNINFO" ("RUN_NUMBER", "START_TIME", "END_TIME", "DATA_SOURCE", "RUN_TYPE", "RUN_TYPE_DESC", "P_PROJECT", "P_DESC", "INTEG_LUMI", "START_LBN", "END_LBN", "P_PERIOD", "L1_EVENTS", "L2_EVENTS", "EF_EVENTS", "RECORDED_EVENTS", "PARTITION_NAME") AS 
  SELECT rn.runnumber as run_number,
    rn.startatts start_time,
    rn.endatts end_time,
    coldruns.data_source,
    cruns.run_type,
    runtype.run_type_desc,
    p_project,
    p_desc,
    (runlumi.integ_lumi*runlumi.ready_fraction)/1000000 integ_lumi,
    cruns.start_lbn,
    cruns.end_lbn,
    p_period,
    runevts.l1_events,
    runevts.l2_events,
    runevts.ef_events,
    runevts.recorded_events,
    rn.partitionname
  FROM ATLAS_COND_TOOLS.RUN_VIEW_RUNNUMBERMOD rn,
    ATLAS_TAGS_METADATA.COMA_CB_RUNS cruns,
    ATLAS_TAGS_METADATA.COMA_RUNS coldruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_P1_TO_RUNS pruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_DEFS pdefs,
    ATLAS_TAGS_METADATA.COMA_RUN_TYPES runtype,
    ATLAS_TAGS_METADATA.COMA_RUN_EVENTS runevts,
    ATLAS_TAGS_METADATA.COMA_RUN_LUMS runlumi
  WHERE rn.runnumber=cruns.run_index(+)
  AND cruns.run_index  =pruns.run_index(+)
  AND cruns.run_index =coldruns.run_index(+)
  AND pruns.p_index      =pdefs.p_index(+)
  AND cruns.run_index    =runevts.run_index(+)
  AND cruns.run_index    =runlumi.run_index(+)
  AND runtype.run_type(+)=cruns.run_type;
--------------------------------------------------------
--  DDL for View CR_VIEW_RUNINFO_V1
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."CR_VIEW_RUNINFO_V1" ("RUN_NUMBER", "START_TIME", "END_TIME", "DATA_SOURCE", "RUN_TYPE", "RUN_TYPE_DESC", "P_PROJECT", "P_DESC", "INTEG_LUMI", "START_LBN", "END_LBN", "P_PERIOD", "L1_EVENTS", "L2_EVENTS", "EF_EVENTS", "RECORDED_EVENTS", "PARTITION_NAME") AS 
  SELECT cruns.run_index as run_number,
    CAST(cruns.start_time AS TIMESTAMP) start_time,
    CAST(cruns.end_time AS TIMESTAMP) end_time,
    coldruns.data_source,
    cruns.run_type,
    runtype.run_type_desc,
    p_project,
    p_desc,
    (runlumi.integ_lumi*runlumi.ready_fraction)/1000000 integ_lumi,
    cruns.start_lbn,
    cruns.end_lbn,
    p_period,
    runevts.l1_events,
    runevts.l2_events,
    runevts.ef_events,
    runevts.recorded_events,
    runevts.partition_name
  FROM ATLAS_TAGS_METADATA.COMA_CB_RUNS cruns,
    ATLAS_TAGS_METADATA.COMA_RUNS coldruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_P1_TO_RUNS pruns,
    ATLAS_TAGS_METADATA.COMA_PERIOD_DEFS pdefs,
    ATLAS_TAGS_METADATA.COMA_RUN_TYPES runtype,
    ATLAS_TAGS_METADATA.COMA_RUN_EVENTS runevts,
    ATLAS_TAGS_METADATA.COMA_RUN_LUMS runlumi
  WHERE cruns.run_index  =pruns.run_index(+)
  AND cruns.run_index =coldruns.run_index(+)
  AND pruns.p_index      =pdefs.p_index(+)
  AND cruns.run_index    =runevts.run_index(+)
  AND cruns.run_index    =runlumi.run_index(+)
  AND runtype.run_type(+)=cruns.run_type;
--------------------------------------------------------
--  DDL for View DEBUG_COMACLASS_CONDBR2
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."DEBUG_COMACLASS_CONDBR2" ("SCHEMA_NAME", "NODE_FULLPATH", "FOLDER_CLASS", "OWNER_NAME", "CLASSPATH", "COMA_INS_DATE", "COMA_UPD_DATE") AS 
  select 
   condbr.schema_name, condbr.node_fullpath, nodeclass.folder_class, 
   nodeclass.owner_name, nodeclass.node_fullpath as classpath, nodeclass.coma_ins_date, nodeclass.coma_upd_date 
from table(cool_select_pkg.f_getall_nodes('ATLAS_COOL%','CONDBR2','%')) condbr,
          ATLAS_TAGS_METADATA.coma_cb_class nodeclass
where nodeclass.node_fullpath=condbr.node_fullpath(+) 
order by owner_name,    classpath;
--------------------------------------------------------
--  DDL for View DEBUG_PKG
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."DEBUG_PKG" ("SCHEMA_NAME", "NODE_FULLPATH") AS 
  select 
   condbr.schema_name, condbr.node_fullpath
from table(cool_select_pkg.f_getall_nodes('ATLAS_COOL%','CONDBR2','%')) condbr;
--------------------------------------------------------
--  DDL for View NEMOP_VIEW_RUN
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."NEMOP_VIEW_RUN" ("RUN", "RUNSTART", "RUNEND", "ACTIVE", "NLB") AS 
  SELECT 
run, runstart, runend, active, nlb    
FROM
ATLAS_COOL_GLOBAL.NEMOP_RUN;
--------------------------------------------------------
--  DDL for View RUN_VIEW_RUNNUMBER
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."RUN_VIEW_RUNNUMBER" ("NAME", "RUNNUMBER", "STARTAT", "DURATION", "CREATEDBY", "HOST", "PARTITIONNAME", "CONFIGSCHEMA", "CONFIGDATA", "COMMENTS") AS 
  SELECT "NAME","RUNNUMBER","STARTAT","DURATION","CREATEDBY","HOST","PARTITIONNAME","CONFIGSCHEMA","CONFIGDATA","COMMENTS"
    
FROM ATLAS_RUN_NUMBER.RUNNUMBER;
--------------------------------------------------------
--  DDL for View RUN_VIEW_RUNNUMBERMOD
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "ATLAS_COND_TOOLS"."RUN_VIEW_RUNNUMBERMOD" ("NAME", "RUNNUMBER", "STARTAT", "DURATION", "CREATEDBY", "HOST", "PARTITIONNAME", "CONFIGSCHEMA", "CONFIGDATA", "COMMENTS", "STARTATTS", "ENDATTS") AS 
  select name, runnumber, startat, duration, 
createdby, host, partitionname, configschema, configdata,
comments,
TO_TIMESTAMP(startat,'YYYYMMDD"T"HH24MISS') as startatts,
TO_TIMESTAMP(startat,'YYYYMMDD"T"HH24MISS') + numtodsinterval(duration,'SECOND') as endatts
from ATLAS_RUN_NUMBER.RUNNUMBER;
