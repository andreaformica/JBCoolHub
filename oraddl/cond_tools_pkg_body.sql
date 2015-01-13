--------------------------------------------------------
--  File created - Tuesday-January-13-2015   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Package Body COND_TOOLS_PKG
--------------------------------------------------------

  CREATE OR REPLACE PACKAGE BODY "ATLAS_COND_TOOLS"."COND_TOOLS_PKG" AS


  function f_GetRun_Info(arg_dbname VARCHAR2) 
--    return varchar2 AS
                      return cond_runinfo_type_t pipelined AS
    
     sor_foldernamestr VARCHAR2(255);
     eor_foldernamestr VARCHAR2(255);
     
     cool_db_link    varchar2(100);
     v_stmt_str      VARCHAR2(1000);
     v_schemaname    VARCHAR2(50);
     v_dbname        VARCHAR2(10);
     v_sorfolder     VARCHAR2(100);
     v_eorfolder     VARCHAR2(100);
     
    TYPE RunCurTyp  IS REF CURSOR;
    v_run_cursor    RunCurTyp; 
    v_row           RUN_RECORD;

  begin
    cool_db_link := '';

    v_schemaname := 'ATLAS_COOLONL_TDAQ';
    v_dbname := arg_dbname;
    v_sorfolder := '/TDAQ/RunCtrl/SOR_Params';
    v_eorfolder := '/TDAQ/RunCtrl/EOR_Params';

    dbms_output.put_line('/TDAQ/RunCtrl/SOR_Params and EOR_Params');

    select cool_select_pkg.f_get_tablename(v_schemaname, v_dbname, v_eorfolder,'IOVS') into eor_foldernamestr from dual;
    select cool_select_pkg.f_get_tablename(v_schemaname, v_dbname, v_sorfolder,'IOVS') into sor_foldernamestr from dual;

    dbms_output.put_line('Folder tables are ' || sor_foldernamestr || ' ' || eor_foldernamestr);

    --create a query for run --
    v_stmt_str := 'select ' ||
    'COALESCE (sor."RunNumber",eor."RunNumber") as RUN_NUMBER, ' || 
--    '1 as RUN_NUMBER, ' ||
    'COALESCE (sor."SORTime",eor."SORTime") / 1000000000 as RUNSTART, ' || 
--    '1 as RUNSTART, ' ||
    'COALESCE (eor."EORTime",0) / 1000000000 as RUNEND, ' ||
    'COALESCE (sor."RunType", eor."RunType") as RUN_TYPE, ' ||
--    '1 as CLEANSTOP, ' ||
    'eor."FilenameTag" as FILENAME_TAG, ' ||
    'eor."CleanStop" as CLEANSTOP ' ||
    ' from ' 
      || v_schemaname || '.' || sor_foldernamestr || cool_db_link || ' sor ' ||
    ' FULL OUTER JOIN '
      || v_schemaname || '.' || eor_foldernamestr || cool_db_link || ' eor ' ||
    'ON sor."RunNumber" = eor."RunNumber" ';
            
-- Open cursor 
-- Fetch rows from result set one at a time:
-- 
    OPEN v_run_cursor FOR v_stmt_str;
    LOOP
      FETCH v_run_cursor INTO v_row;
    EXIT WHEN v_run_cursor%NOTFOUND; 
      dbms_output.put_line('ROW ' || v_row.RUN_NUMBER || ' ' || v_row.RUNSTART);
      pipe row(cond_runinfo_type(v_row.RUN_NUMBER, v_row.RUNSTART, v_row.RUNEND, v_row.RUN_TYPE, v_row.FILENAME_TAG, 1));
    END LOOP;
    CLOSE v_run_cursor; 
-- 
--    return v_stmt_str;
--    exception
--    when OTHERS then
--      dbms_output.put_line('Skip ' || v_row.RUN_NUMBER);

  END f_GetRun_Info;

  function f_GetStat_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestat_type_t pipelined  AS
  cursor node_cursor is
    select schema_name, 
      dbname,
      cool_node_fullpath, 
      cool_tag_name, 
      count(cool_node_fullpath) nchannels, 
      max(niovsperchan) totaliovs, 
      max(nholesperchan) totalholes,
      min(minsinceperchan) minsince, 
      max(maxuntilperchan) maxuntil,
      max(nrunsperchan) totalruns,
      max(nrunsinholeperchan) totalrunsinhole,
      min(minrunperchan) minrun,
      min(maxrunperchan) maxrun      
    from (
      select iovs.schema_name, 
        iovs.db dbname, 
        iovs.cool_node_fullpath, 
        iovs.cool_tag_name, 
        iovs.cool_channel_id,
        sum(iovr.cool_iovrange_niovs) niovsperchan,
        sum(iovr.cool_iovrange_ishole) nholesperchan,
        min(iovr.cool_iovrange_since) minsinceperchan,
        max(iovr.cool_iovrange_until) maxuntilperchan,
        sum(iovr.coma_nruns) nrunsperchan,
        sum(iovr.coma_nruns*iovr.cool_iovrange_ishole) nrunsinholeperchan,
        min(iovr.coma_run_min) minrunperchan,
        max(iovr.coma_run_max) maxrunperchan
      from cool_iov_summary iovs, cool_iov_ranges iovr
      where iovs.cool_iovsummary_id=iovr.cool_iovsummary_fk
        and (iovs.cool_node_fullpath like arg_folder and iovs.db=arg_dbname and iovs.schema_name like arg_schemaname)
        group by iovs.schema_name, iovs.db, iovs.cool_node_fullpath, iovs.cool_tag_name, iovs.cool_channel_id
        order by iovs.schema_name, iovs.db, iovs.cool_node_fullpath, iovs.cool_tag_name, iovs.cool_channel_id
    ) group by schema_name, dbname, cool_node_fullpath, cool_tag_name;      
  
  v_row nodestat_record;
  
  begin
  
  OPEN node_cursor;
    LOOP
      FETCH node_cursor INTO v_row; 
      EXIT
    WHEN node_cursor%NOTFOUND;
    pipe row(cond_nodestat_type(v_row.schema_name,
        v_row.db_name, 
        v_row.node_name, 
        v_row.tag_name, 
        v_row.nchannels, 
        v_row.totaliovs, 
        v_row.totalholes,
        v_row.minsince,
        v_row.maxuntil,
        v_row.totalruns, 
        v_row.totalrunsinhole,
        v_row.minrun,
        v_row.maxrun
        ));
  END LOOP;
  CLOSE node_cursor;
  END f_GetStat_Nodes;

/* Function to convert COOL time into VARCHAR2 */
  function f_GetBadHoles_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestathole_type_t pipelined AS
  
  cursor iovr_cursor is
      select iovs.schema_name, 
        iovs.db dbname, 
        iovs.cool_node_fullpath, 
        iovs.cool_tag_name, 
        iovs.cool_channel_id,
        iovr.cool_iovrange_since,
        iovr.cool_iovrange_until,
        iovr.cool_iovrange_ishole,
        iovr.cool_iovbase
      from cool_iov_summary iovs, cool_iov_ranges iovr
      where iovs.cool_iovsummary_id=iovr.cool_iovsummary_fk
        and (iovs.cool_node_fullpath like arg_folder and iovs.db=arg_dbname and iovs.schema_name like arg_schemaname)
        and iovr.cool_iovrange_ishole=1 
        order by iovs.schema_name, iovs.db, iovs.cool_node_fullpath, iovs.cool_tag_name, iovs.cool_channel_id;   
  
  runmin NUMBER;
  runmax NUMBER;
  sincetime TIMESTAMP;
  untiltime TIMESTAMP;
  cooltimesince VARCHAR2(255);
  cooltimeuntil VARCHAR2(255);

  cursor runinfo_cursor is
      select 
        min(run_number) as minrun,
        max(run_number) as maxrun,
        min(start_time) as minsince,
        max(end_time) as maxuntil,
        count(run_number) as nruns
/*
        start_time,
        end_time,
        data_source,
        run_type,
        p_project,
        p_desc*/
      from cr_view_runinfo runinfo
      where ((runinfo.run_number >= runmin and runinfo.run_number<=runmax)
        OR (runinfo.start_time >= sincetime and runinfo.start_time <= untiltime)) 
        AND runinfo.p_project like 'data%';   

  
  v_holerow iovr_cursor%ROWTYPE;
  v_runrow runinfo_cursor%ROWTYPE;
  v_row NODESTAT_HOLES_RECORD;
  
  isinhole NUMBER;
  iovbase VARCHAR2(10);
  countrun NUMBER;
  
  begin
    OPEN iovr_cursor;
    LOOP
    FETCH iovr_cursor INTO v_holerow;
    EXIT WHEN iovr_cursor%NOTFOUND;
    iovbase := substr(v_holerow.cool_iovbase,1,3);
    cooltimesince := f_GetCool_Time(v_holerow.cool_iovrange_since, v_holerow.cool_iovbase);
    cooltimeuntil := f_GetCool_Time(v_holerow.cool_iovrange_until, v_holerow.cool_iovbase);
    runmin := 0;
    runmax := 0;
    sincetime := CURRENT_TIMESTAMP;
    untiltime := CURRENT_TIMESTAMP;
    IF iovbase = 'run' THEN 
       runmin := TO_NUMBER(cooltimesince);
       runmax := TO_NUMBER(cooltimeuntil);
    END IF;
    IF iovbase = 'tim' THEN 
       IF cooltimesince = '0' THEN
          cooltimesince := '1970/01/01 00:00:00';
       END IF;
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '2050/01/01 00:00:00';
       END IF;
       sincetime := TO_TIMESTAMP(cooltimesince,'YYYY/MM/DD HH24:MI:SS');
       untiltime := TO_TIMESTAMP(cooltimeuntil,'YYYY/MM/DD HH24:MI:SS');
    END IF;
    v_row.schema_name := v_holerow.schema_name;
    v_row.db_name := v_holerow.dbname;
    v_row.node_name := v_holerow.cool_node_fullpath;
    v_row.tag_name := v_holerow.cool_tag_name;
    v_row.channel_id := v_holerow.cool_channel_id;
    v_row.hole_since := v_holerow.cool_iovrange_since;
    v_row.hole_until := v_holerow.cool_iovrange_until;
    v_row.nruns := 0;
    v_row.minrun := 0;
    v_row.maxrun := 0;
    v_row.minsince := CURRENT_TIMESTAMP;
    v_row.maxuntil := CURRENT_TIMESTAMP;
    
    countrun := 0;
      OPEN runinfo_cursor;
      LOOP 
      FETCH runinfo_cursor INTO v_runrow;
      EXIT WHEN runinfo_cursor%NOTFOUND;
        countrun := countrun+1;
        v_row.nruns := v_runrow.nruns;
        v_row.minrun := v_runrow.minrun;
        v_row.maxrun := v_runrow.maxrun;
        v_row.minsince := v_runrow.minsince;
        v_row.maxuntil := v_runrow.maxuntil;
      END LOOP;
      CLOSE runinfo_cursor;
       pipe row(cond_nodestathole_type(v_row.schema_name,
        v_row.db_name, 
        v_row.node_name, 
        v_row.tag_name, 
        v_row.channel_id, 
        v_row.hole_since, 
        v_row.hole_until,
        v_row.nruns,
        v_row.minrun,
        v_row.maxrun,
        v_row.minsince,
        v_row.maxuntil));  
    END LOOP;
    CLOSE iovr_cursor;
   -- RETURN countrun;
  END f_GetBadHoles_Nodes;

/* Function to get ranges for given schema instance and node */
  function f_GetRanges_Nodes(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2)  return cond_nodestathole_type_t pipelined AS
  
  cursor iovr_cursor is
      select iovs.schema_name, 
        iovs.db dbname, 
        iovs.cool_node_fullpath, 
        iovs.cool_tag_name, 
        iovs.cool_channel_id,
        iovr.cool_iovrange_since,
        iovr.cool_iovrange_until,
        iovr.cool_iovrange_ishole,
        iovr.cool_iovbase
      from cool_iov_summary iovs, cool_iov_ranges iovr
      where iovs.cool_iovsummary_id=iovr.cool_iovsummary_fk
        and (iovs.cool_node_fullpath like arg_folder and iovs.db=arg_dbname and iovs.schema_name like arg_schemaname)
        --and iovr.cool_iovrange_ishole=1 
        order by iovs.schema_name, iovs.db, iovs.cool_node_fullpath, iovs.cool_tag_name, iovs.cool_channel_id;   
  
  runmin NUMBER;
  runmax NUMBER;
  sincetime TIMESTAMP;
  untiltime TIMESTAMP;
  cooltimesince VARCHAR2(255);
  cooltimeuntil VARCHAR2(255);

  cursor runinfo_cursor is
      select 
        min(run_number) as minrun,
        max(run_number) as maxrun,
        min(start_time) as minsince,
        max(end_time) as maxuntil,
        count(run_number) as nruns
/*
        start_time,
        end_time,
        data_source,
        run_type,
        p_project,
        p_desc*/
      from cr_view_runinfo runinfo
      where ((runinfo.run_number >= runmin and runinfo.run_number<=runmax)
        OR (runinfo.start_time >= sincetime and runinfo.start_time <= untiltime)) 
        AND runinfo.p_project like 'data%';   

  
  v_holerow iovr_cursor%ROWTYPE;
  v_runrow runinfo_cursor%ROWTYPE;
  v_row NODESTAT_HOLES_RECORD;
  
  isinhole NUMBER;
  iovbase VARCHAR2(10);
  countrun NUMBER;
  
  begin
    OPEN iovr_cursor;
    LOOP
    FETCH iovr_cursor INTO v_holerow;
    EXIT WHEN iovr_cursor%NOTFOUND;
    iovbase := substr(v_holerow.cool_iovbase,1,3);
    cooltimesince := f_GetCool_Time(v_holerow.cool_iovrange_since, v_holerow.cool_iovbase);
    cooltimeuntil := f_GetCool_Time(v_holerow.cool_iovrange_until, v_holerow.cool_iovbase);
    runmin := 0;
    runmax := 0;
    sincetime := CURRENT_TIMESTAMP;
    untiltime := CURRENT_TIMESTAMP;
    IF iovbase = 'run' THEN 
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '99999999999';
       END IF;
       runmin := TO_NUMBER(cooltimesince);
       runmax := TO_NUMBER(cooltimeuntil);
    END IF;
    IF iovbase = 'tim' THEN 
       IF cooltimesince = '0' THEN
          cooltimesince := '1970/01/01 00:00:00';
       END IF;
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '2050/01/01 00:00:00';
       END IF;
       sincetime := TO_TIMESTAMP(cooltimesince,'YYYY/MM/DD HH24:MI:SS');
       untiltime := TO_TIMESTAMP(cooltimeuntil,'YYYY/MM/DD HH24:MI:SS');
    END IF;
    v_row.schema_name := v_holerow.schema_name;
    v_row.db_name := v_holerow.dbname;
    v_row.node_name := v_holerow.cool_node_fullpath;
    v_row.tag_name := v_holerow.cool_tag_name;
    v_row.channel_id := v_holerow.cool_channel_id;
    v_row.hole_since := v_holerow.cool_iovrange_since;
    v_row.hole_until := v_holerow.cool_iovrange_until;
    v_row.nruns := 0;
    v_row.minrun := 0;
    v_row.maxrun := 0;
    v_row.minsince := CURRENT_TIMESTAMP;
    v_row.maxuntil := CURRENT_TIMESTAMP;
    
    countrun := 0;
      OPEN runinfo_cursor;
      LOOP 
      FETCH runinfo_cursor INTO v_runrow;
      EXIT WHEN runinfo_cursor%NOTFOUND;
        countrun := countrun+1;
        v_row.nruns := v_runrow.nruns;
        v_row.minrun := v_runrow.minrun;
        v_row.maxrun := v_runrow.maxrun;
        v_row.minsince := v_runrow.minsince;
        v_row.maxuntil := v_runrow.maxuntil;
      END LOOP;
      CLOSE runinfo_cursor;
       pipe row(cond_nodestathole_type(v_row.schema_name,
        v_row.db_name, 
        v_row.node_name, 
        v_row.tag_name, 
        v_row.channel_id, 
        v_row.hole_since, 
        v_row.hole_until,
        v_row.nruns,
        v_row.minrun,
        v_row.maxrun,
        v_row.minsince,
        v_row.maxuntil));  
    END LOOP;
    CLOSE iovr_cursor;
   -- RETURN countrun;
  END f_GetRanges_Nodes;


/*
 * This function retrieves ranges for a given set of schema,db,folder and pattern tag.
 * The ranges retrieved represent periods of full covered iovs and holes.
 */
function f_GetSummary_Ranges(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tag VARCHAR2, arg_since NUMBER, arg_until NUMBER)  return cond_nodeiovrange_type_t pipelined AS

    CURSOR iovrange_cursor IS
      select 
				 rownum as row_id, 
				user_tag_id, tag_name, 
        channel_id, 
				channel_name, 
				lowest_since as miniov_since, 
				iov_until as miniov_until, 
				iov_since as maxiov_since, 
				highest_until as maxiov_until, 
				hole as iov_hole, 
				next_since as hole_until,
				niovs 
				from (select 
				 user_tag_id, tag_name, 
				 channel_id, channel_name, iov_since, iov_until, next_since, hole, iov_sum, 
				 FIRST_VALUE(iov_since) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) AS lowest_since, 
				 LAST_VALUE(iov_until) IGNORE NULLS OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since RANGE BETWEEN 
				  UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS highest_until, 
				 COUNT(iov_sum) OVER (PARTITION BY channel_id,iov_sum ORDER BY iov_since) as niovs 
				 from ( select 
				 user_tag_id, tag_name, 
				 channel_id, channel_name,iov_since, iov_until,  next_since, iov_number, hole, 
				 iov_number+LAG(iov_number,1,0) OVER (PARTITION BY channel_id ORDER BY iov_since) as iov_sum 
				 from ( select 
				 user_tag_id, tag_name, 
				 channel_id, channel_name,iov_since, iov_until, next_since, 
				 (case when (next_since-iov_until)>1 then (next_since-iov_until) else 0 end) as hole, 
				 SUM(case when (next_since-iov_until)>1 then 1 else 0 end) OVER (PARTITION BY channel_id ORDER BY iov_since 
				 RANGE UNBOUNDED PRECEDING) as iov_number 
				 from ( select 
				 user_tag_id, tag_name, 
				 channel_id, channel_name, iov_since, iov_until,  LEAD(iov_since) OVER (ORDER BY channel_id, iov_since) next_since 
				 from table(cool_select_pkg.f_get_iovsrange(arg_schemaname,arg_dbname,arg_folder,arg_tag,arg_since,arg_until)) order by channel_id asc )))) 
				 where iov_until=highest_until;
         
    v_row iovrange_cursor%ROWTYPE;
begin
      OPEN iovrange_cursor;
      LOOP 
      FETCH iovrange_cursor INTO v_row;
      EXIT WHEN iovrange_cursor%NOTFOUND;
       pipe row(cond_nodeiovrange_type(v_row.row_id,
        v_row.user_tag_id,
        v_row.tag_name, 
        v_row.channel_id, 
        v_row.channel_name, 
        v_row.miniov_since, 
        v_row.miniov_until,
        v_row.maxiov_since,
        v_row.maxiov_until,
        v_row.iov_hole,
        v_row.hole_until,
        v_row.niovs));  
    END LOOP;
    CLOSE iovrange_cursor;
    
END f_GetSummary_Ranges;

  /* This function allow to check the time condition to match the COMA Run info table */
  function f_CheckTime(arg_cooltimesince NUMBER, arg_cooltimeuntil NUMBER, arg_iovbase VARCHAR2, 
                     arg_runnum NUMBER, arg_startrun TIMESTAMP, arg_endrun TIMESTAMP)  return NUMBER AS
    isinrange NUMBER;
    iovbase VARCHAR2(10);
    cooltimesince VARCHAR2(255);
    cooltimeuntil VARCHAR2(255);
    cooltimesince_run NUMBER;
    cooltimeuntil_run NUMBER;
    cooltimesince_tstamp TIMESTAMP;
    cooltimeuntil_tstamp TIMESTAMP;
  begin
    isinrange := 0;
    iovbase := substr(arg_iovbase,1,3);
    cooltimesince := f_GetCool_Time(arg_cooltimesince, arg_iovbase);
    cooltimeuntil := f_GetCool_Time(arg_cooltimeuntil, arg_iovbase);
    IF iovbase = 'run' THEN 
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '99999999999';
       END IF;
       cooltimesince_run := TO_NUMBER(cooltimesince);
       cooltimeuntil_run := TO_NUMBER(cooltimeuntil);
       IF (arg_runnum > cooltimesince_run) AND (arg_runnum < cooltimeuntil_run) THEN
         isinrange := 1;
       END IF;
    END IF;
    IF iovbase = 'tim' THEN 
       IF cooltimesince = '0' THEN
          cooltimesince := '1970/01/01 00:00:00';
       END IF;
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '2050/01/01 00:00:00';
       END IF;
       cooltimesince_tstamp := TO_TIMESTAMP(cooltimesince,'YYYY/MM/DD HH24:MI:SS');
       cooltimeuntil_tstamp := TO_TIMESTAMP(cooltimeuntil,'YYYY/MM/DD HH24:MI:SS');
       IF (arg_startrun > cooltimesince_tstamp) AND (arg_startrun < cooltimeuntil_tstamp) THEN
         isinrange := 1;
       END IF;
    END IF;
  
  RETURN isinrange;
  END f_CheckTime;


/* Function to convert COOL time into VARCHAR2 */
  function f_GetCool_Time(arg_cooltime NUMBER, arg_iovbase VARCHAR2)  return VARCHAR2 AS
  
    outtime VARCHAR2(255);
    lblock VARCHAR2(50);
    iovbase VARCHAR2(10);
    pos NUMBER;
    runnum NUMBER;
  begin
    pos :=0;
    iovbase := substr(arg_iovbase,1,3);
    outtime := mgetcooltimerun(arg_cooltime, arg_iovbase);
    IF iovbase = 'run' THEN
      pos := instr(outtime,'-');
--        dbms_output.put_line('Position is ' || pos);
      IF pos > 0 THEN
        lblock := substr(outtime,pos+2,LENGTH(outtime));
        outtime := substr(outtime,1,pos-2);
--        dbms_output.put_line('selected range ' || lblock);
        IF lblock = 'maxlb' THEN
          runnum := TO_NUMBER(outtime);
          runnum := runnum + 1;
          outtime := TO_CHAR(runnum);
        END IF;
      END IF;  
    END IF;
    RETURN outtime;
  END f_GetCool_Time;
  
/* Function to convert COOL time into VARCHAR2 */
  function f_GetLastMod_Time(arg_schemaname VARCHAR2, arg_dbname VARCHAR2,
                     arg_folder VARCHAR2, arg_tableextension VARCHAR2)  return cond_lastmodtime_type_t pipelined AS
  
  cool_db_link    varchar2(100);
  v_stmt_str      VARCHAR2(1000);  
  v_schemaname varchar2(30);
  v_nodefullpath varchar2(255);
  seq_tablename varchar2(1000);
  taginstime timestamp(6);

  cursor node_cursor is
    select * from table(cool_select_pkg.f_GetAll_Nodes(arg_schemaname,arg_dbname,arg_folder));

  TYPE TagSeqCurTyp  IS REF CURSOR;
  v_tagseq_cursor    TagSeqCurTyp; 

  v_node_row node_cursor%ROWTYPE;
  v_row tagseq_record;
  
  foldernamestr varchar2(10);
  fldmod timestamp;   
     
  begin
--  cool_db_link := '@COOL.CERN.CH';
  cool_db_link := '';
 
 
  OPEN node_cursor;
  LOOP
    FETCH node_cursor INTO v_node_row;
    EXIT WHEN node_cursor%NOTFOUND;
    v_schemaname := v_node_row.schema_name;
    v_nodefullpath := v_node_row.node_fullpath;
    
    foldernamestr := cool_select_pkg.convertNodeIdToFolder(v_node_row.node_id);

    seq_tablename := v_schemaname || '.' || arg_dbname || '_' || foldernamestr || '_' || arg_tableextension || '_SEQ' ||  cool_db_link;
    --create a query for tags --
    v_stmt_str := 'select ' ||
     'sequence_name, ' || 
     'current_value, ' || 
     'lastmod_date  from ' || seq_tablename || cool_db_link || ' seqtags ';

--    raise_application_error(-20003, 'Error in schema ' || v_schemaname || ' and query ' || v_stmt_str);
  begin
    OPEN v_tagseq_cursor FOR v_stmt_str;
    LOOP
      FETCH v_tagseq_cursor INTO v_row;
    EXIT WHEN v_tagseq_cursor%NOTFOUND;
      taginstime := cool_select_pkg.convertToTimestamp(v_row.lastmod_date);
      pipe row(cond_lastmodtime_type(v_schemaname,arg_dbname,v_nodefullpath,v_row.SEQUENCE_NAME,v_row.CURRENT_VALUE,taginstime));
    END LOOP; 
  exception
    when OTHERS then
      dbms_output.put_line('Skip ' || seq_tablename);
    
    --pipe row(cond_lastmodtime_type(v_schemaname,arg_dbname,seq_tablename,0,current_date));
  end;  
  END LOOP;
  CLOSE node_cursor;

  END f_GetLastMod_Time;
  

  
/*
 * Update cool_iov_summary table to complete channel name information when existing in COOL.
 */
procedure p_UpdSummary_ChannelName(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2) AS
  
  arg_chanid NUMBER;
  
  CURSOR summary_cursor
  IS
    SELECT schema_name, db, 
      cool_node_fullpath, 
      cool_channel_id, 
      cool_channel_name, 
      cool_iovsummary_id
    FROM cool_iov_summary isumm
    WHERE 
        isumm.schema_name like arg_schemaname
    AND isumm.db = arg_dbname 
    AND isumm.cool_node_fullpath like arg_folder
    FOR UPDATE OF COOL_CHANNEL_NAME;
 
  CURSOR coolchan_cursor
  IS
    SELECT CHANNEL_NAME FROM TABLE(cool_select_pkg.f_Get_Channels(arg_schemaname, arg_dbname, arg_folder, '%')) 
    WHERE CHANNEL_ID=arg_chanid;
 
  v_row summary_cursor%ROWTYPE;
  v_chan coolchan_cursor%ROWTYPE;
  
  new_channelname VARCHAR2(255);
  
BEGIN
  OPEN summary_cursor;
  LOOP
    FETCH summary_cursor INTO v_row ;
      EXIT
      WHEN summary_cursor%NOTFOUND;
    dbms_output.put_line('analyze ' || v_row.cool_channel_name || ' ' || v_row.cool_node_fullpath);
    IF v_row.cool_channel_name = 'tobefilled' THEN

      arg_chanid := v_row.cool_channel_id;
      OPEN coolchan_cursor;
      LOOP
        FETCH coolchan_cursor INTO v_chan;
        EXIT
        WHEN coolchan_cursor%NOTFOUND;
      
        new_channelname := v_chan.CHANNEL_NAME;
        dbms_output.put_line('new name is ' || new_channelname);
      END LOOP;
      CLOSE coolchan_cursor;
      UPDATE cool_iov_summary 
            SET cool_channel_name=new_channelname
            WHERE CURRENT OF summary_cursor;
    END IF;
  END LOOP;
  COMMIT;
  CLOSE summary_cursor;
EXCEPTION
WHEN OTHERS THEN
dbms_output.put_line
  (
    'Handling exception: ' || dbms_utility.format_error_backtrace
  );
  ROLLBACK;
  
END p_UpdSummary_ChannelName;

procedure p_UpdRanges_RunInfo(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2) AS
  
  CURSOR range_cursor
  IS
    SELECT schema_name, db, 
      cool_node_fullpath, 
      cool_iovrange_since, 
      cool_iovrange_until, 
      cool_iovrange_ishole, 
      cool_iovbase,
      cool_iovrange_pk
    FROM cool_iov_ranges irange, cool_iov_summary isumm
    WHERE irange.cool_iovsummary_fk=isumm.cool_iovsummary_id
    AND isumm.schema_name like arg_schemaname
    AND isumm.db = arg_dbname 
    AND isumm.cool_node_fullpath like arg_folder
    AND coma_run_min is null;
 
  iovrangepk NUMBER;
 
  CURSOR rangeupd_cursor
  IS
    SELECT 
      cool_iovrange_pk,
      cool_iovrange_since, 
      cool_iovrange_until, 
      cool_iovrange_ishole, 
      coma_run_min,
      coma_run_max,
      coma_nruns,
      coma_run_starttime,
      coma_run_endtime
    FROM cool_iov_ranges irange
    WHERE cool_iovrange_pk = iovrangepk
    FOR UPDATE OF COMA_RUN_MIN, COMA_RUN_MAX, COMA_NRUNS, COMA_RUN_STARTTIME, COMA_RUN_ENDTIME;
    
  runmin NUMBER;
  runmax NUMBER;
  sincetime TIMESTAMP;
  untiltime TIMESTAMP;
  cooltimesince VARCHAR2(255);
  cooltimeuntil VARCHAR2(255);
  iovbase VARCHAR2(10);

  cursor runinfo_cursor is
      select 
        min(run_number) as minrun,
        max(run_number) as maxrun,
        min(start_time) as minsince,
        max(end_time) as maxuntil,
        count(run_number) as nruns
      from cr_view_runinfo runinfo
      where ((runinfo.run_number >= runmin and runinfo.run_number<runmax)
        OR (runinfo.start_time >= sincetime and runinfo.start_time <untiltime));
        --AND runinfo.p_project like 'data%';   
    
  v_row range_cursor%ROWTYPE;
  v_runrow runinfo_cursor%ROWTYPE;
  v_rangeupdrow rangeupd_cursor%ROWTYPE;
  
BEGIN
  OPEN range_cursor;
  LOOP
    FETCH range_cursor INTO v_row ;
      EXIT
      WHEN range_cursor%NOTFOUND;
--    dbms_output.put_line('selected range ' || v_row.cool_iovrange_pk || ' ' || v_row.cool_iovrange_since || ' ' || v_row.cool_iovrange_until);

    iovrangepk := v_row.cool_iovrange_pk;
    
    iovbase := substr(v_row.cool_iovbase,1,3);
    cooltimesince := f_GetCool_Time(v_row.cool_iovrange_since, v_row.cool_iovbase);
    cooltimeuntil := f_GetCool_Time(v_row.cool_iovrange_until, v_row.cool_iovbase);
    runmin := 0;
    runmax := 0;
    sincetime := CURRENT_TIMESTAMP;
    untiltime := CURRENT_TIMESTAMP;
--    dbms_output.put_line('use iovbase and times from cool ' || iovbase || ' ' || cooltimesince || ' ' || cooltimeuntil);
    IF iovbase = 'run' THEN 
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '99999999999';
       END IF;
       runmin := TO_NUMBER(cooltimesince);
       runmax := TO_NUMBER(cooltimeuntil);
    END IF;
    IF iovbase = 'tim' THEN  
--       dbms_output.put_line('use time iovbase...');
       IF cooltimesince = '0' THEN
          cooltimesince := '1970/01/01 00:00:00';
       END IF;
       sincetime := TO_TIMESTAMP(cooltimesince,'YYYY/MM/DD HH24:MI:SS');
       IF cooltimeuntil = 'Inf' THEN
          cooltimeuntil := '2050/01/01 00:00:00';
       END IF;
       untiltime := TO_TIMESTAMP(cooltimeuntil,'YYYY/MM/DD HH24:MI:SS');
--       dbms_output.put_line('use time ranges...' || sincetime || ' ' || untiltime);
    END IF;
    
--    dbms_output.put_line('use run range ' || runmin || ' ' || runmin || ' ' || sincetime);
   
    OPEN runinfo_cursor;
    LOOP
      FETCH runinfo_cursor INTO v_runrow;
      EXIT
      WHEN runinfo_cursor%NOTFOUND;
      
      dbms_output.put_line('use run info ' || v_runrow.minrun || ' ' || v_runrow.maxrun || ' for range id ' || iovrangepk);
      OPEN rangeupd_cursor;
        LOOP
        FETCH rangeupd_cursor INTO v_rangeupdrow;
        EXIT
        WHEN rangeupd_cursor%NOTFOUND;
          UPDATE cool_iov_ranges 
            SET COMA_RUN_MIN=v_runrow.minrun ,
                COMA_RUN_MAX=v_runrow.maxrun ,
                COMA_NRUNS=v_runrow.nruns ,
                COMA_RUN_STARTTIME=v_runrow.minsince ,
                COMA_RUN_ENDTIME=v_runrow.maxuntil
            WHERE CURRENT OF rangeupd_cursor;
        END LOOP; 
        CLOSE rangeupd_cursor;
      END LOOP;
    CLOSE runinfo_cursor;
    COMMIT;
  END LOOP;
  CLOSE range_cursor;
EXCEPTION
WHEN OTHERS THEN
dbms_output.put_line
  (
    'Handling exception: ' || dbms_utility.format_error_backtrace
  );
  ROLLBACK;
  
END p_UpdRanges_RunInfo;
  
/*
 * This procedure uses low level function to retrieve coverage information from cool and to fill the COOL_IOV_SUMMARY and COOL_IOV_RANGES tables.
 */
procedure p_UpdRanges_Summary(arg_schemaname VARCHAR2, arg_dbname VARCHAR2, arg_folder VARCHAR2, arg_tag VARCHAR2) AS

  coolinf NUMBER;
  seltag VARCHAR2(255);

  CURSOR newrange_cursor
  IS
    SELECT row_id, tag_name, channel_id, channel_name, miniov_since, miniov_until, maxiov_since, maxiov_until, hole_until, niovs, iov_hole
    FROM TABLE(f_GetSummary_Ranges(arg_schemaname,arg_dbname,arg_folder,seltag,0,coolinf));

  CURSOR newsummary_cursor
  IS
    SELECT channel_id, miniov_since, miniov_until, maxiov_since, maxiov_until, niovs_perchan
    FROM TABLE(cool_select_pkg.f_Get_IovsRangeStat(arg_schemaname,arg_dbname,arg_folder,arg_tag,0,coolinf));

  CURSOR node_cursor
  IS
    SELECT schema_name, dbname, node_fullpath, tag_name, iov_base
    FROM TABLE(cool_select_pkg.f_GetAll_TagsIovsShort(arg_schemaname,arg_dbname,arg_folder,arg_tag));

  node_iovbase VARCHAR2(50);
  thesummary_id NUMBER;
  
  CURSOR sumrange_cursor
  IS
    SELECT schema_name, db, 
      cool_node_fullpath, 
      cool_tag_name,
      cool_iovsummary_id
    FROM cool_iov_summary isumm
    WHERE 
        isumm.schema_name = arg_schemaname
    AND isumm.db = arg_dbname 
    AND isumm.cool_node_fullpath = arg_folder
    AND isumm.cool_tag_name = seltag;

  CURSOR iovrange_cursor
  IS
    SELECT count(*) as nranges
    FROM cool_iov_summary isumm, cool_iov_ranges irange
    WHERE 
        isumm.schema_name = arg_schemaname
    AND isumm.db = arg_dbname 
    AND isumm.cool_node_fullpath = arg_folder
    AND isumm.cool_tag_name = seltag
    AND isumm.cool_iovsummary_id=irange.COOL_IOVSUMMARY_FK;

  v_newrangerow newrange_cursor%ROWTYPE;
  v_newsummaryrow newsummary_cursor%ROWTYPE;
  v_noderow node_cursor%ROWTYPE;
  v_sumrangerow sumrange_cursor%ROWTYPE;
  v_iovrangerow iovrange_cursor%ROWTYPE;

  ranges_already_existing NUMBER;
  tag_already_existing NUMBER;
  chandump NUMBER;
  rangedump NUMBER;
  cool_iovsummary_id NUMBER;
  cool_iovrange_pk NUMBER;
  ishole NUMBER;
  prevchan NUMBER;
  
  cooltimesince VARCHAR2(255);
  cooltimeuntil VARCHAR2(255);
  
  rangeentryprev COOL_IOV_RANGES%ROWTYPE;
  rangeentry COOL_IOV_RANGES%ROWTYPE;

BEGIN
  coolinf := mgetcoolrun('Inf');
  chandump := 10;
  rangedump := 20;
  OPEN node_cursor;
  LOOP 
    FETCH node_cursor INTO v_noderow;
    EXIT WHEN node_cursor%NOTFOUND;
    dbms_output.put_line('use node info ' || v_noderow.schema_name || ' ' || v_noderow.dbname || ' for node  ' || v_noderow.node_fullpath);
    seltag := v_noderow.tag_name;
    node_iovbase := v_noderow.iov_base;
    tag_already_existing := 0;
    ranges_already_existing :=0;
    dbms_output.put_line('set argument ' || seltag || ' as tag name ');

    OPEN sumrange_cursor;
    LOOP
    FETCH sumrange_cursor INTO v_sumrangerow;
    EXIT WHEN sumrange_cursor%NOTFOUND;
      dbms_output.put_line('found node and tag in summary ' || v_sumrangerow.schema_name || ' ' || v_sumrangerow.db || ' for node  ' || v_sumrangerow.cool_node_fullpath || ' tag is ' || v_sumrangerow.cool_tag_name);     
      tag_already_existing :=1;
      cool_iovsummary_id := v_sumrangerow.cool_iovsummary_id;
      EXIT;
    END LOOP;
    CLOSE sumrange_cursor;

    OPEN iovrange_cursor;
    LOOP
    FETCH iovrange_cursor INTO v_iovrangerow;
    EXIT WHEN iovrange_cursor%NOTFOUND;
      dbms_output.put_line('found entries in iovrange ' || v_iovrangerow.nranges);     
      IF v_iovrangerow.nranges > 0 THEN
        ranges_already_existing :=1;
      END IF;
      EXIT;
    END LOOP;
    CLOSE iovrange_cursor;

    IF tag_already_existing > 0 THEN
      dbms_output.put_line('Tag is already existing in summary table...skip it');
    ELSE
      dbms_output.put_line('Tag do not exists in summary table...fill summary');
      OPEN newsummary_cursor;
        LOOP
        FETCH newsummary_cursor INTO v_newsummaryrow;
        EXIT WHEN newsummary_cursor%NOTFOUND;
          IF chandump > 0 THEN
            dbms_output.put_line('Summary found for ' || v_newsummaryrow.channel_id || ' ' ||  v_newsummaryrow.miniov_since || ' ' || v_newsummaryrow.maxiov_until || ' ' || v_newsummaryrow.niovs_perchan);
            chandump := chandump - 1;
          END IF;
          cool_iovsummary_id := COOLIOVSUMMARY_SEQ.NEXTVAL;
          INSERT INTO ATLAS_COND_TOOLS.COOL_IOV_SUMMARY 
               (COOL_IOVSUMMARY_ID,DB,
                SCHEMA_NAME,COOL_NODE_FULLPATH,COOL_TAG_NAME,
                COOL_NODE_IOVBASE,COOL_CHANNEL_ID,COOL_CHANNEL_NAME,
                COOL_MINIOVSINCE,COOL_MINIOVUNTIL,COOL_MAXIOVSINCE,COOL_MAXIOVUNTIL,COOL_TOTALIOVS) 
                  VALUES 
                (cool_iovsummary_id,v_noderow.dbname,
                 v_noderow.schema_name,v_noderow.node_fullpath,seltag,
                 node_iovbase,v_newsummaryrow.channel_id,'tobefilled',
                 v_newsummaryrow.miniov_since,v_newsummaryrow.miniov_until,v_newsummaryrow.maxiov_since,v_newsummaryrow.maxiov_until,v_newsummaryrow.niovs_perchan);
        END LOOP;
        COMMIT;
      CLOSE newsummary_cursor;
    END IF;
    
    IF ranges_already_existing > 0 THEN
      dbms_output.put_line('Ranges are already existing in iov ranges table...skip it');
    ELSE
      dbms_output.put_line('Ranges do not exists in summary table...fill ranges');
      prevchan := -1;
      OPEN newrange_cursor;
        LOOP
        FETCH newrange_cursor INTO v_newrangerow;
        EXIT WHEN newrange_cursor%NOTFOUND;
            IF rangedump > 0 THEN
              dbms_output.put_line('got range summary '|| v_newrangerow.channel_id || ' '||  v_newrangerow.miniov_since || ' ' ||  v_newrangerow.maxiov_since || ' ' ||  v_newrangerow.iov_hole);
              rangedump := rangedump-1;
            END IF;
            
            SELECT cool_iovsummary_id INTO thesummary_id
                   FROM cool_iov_summary isumm
                   WHERE 
                        isumm.schema_name = arg_schemaname
                    AND isumm.db = arg_dbname 
                    AND isumm.cool_node_fullpath = arg_folder
                    AND isumm.cool_tag_name = seltag
                    AND isumm.cool_channel_id = v_newrangerow.channel_id;
            IF thesummary_id IS NOT NULL THEN
            
              UPDATE cool_iov_summary SET COOL_CHANNEL_NAME=v_newrangerow.channel_name
                   WHERE cool_iovsummary_id=thesummary_id;
                   
              ishole := 0;
              -- If it is a hole I need to modify the previous value (if it exists)
              -- then I need to store 2 entries: the present and the previous after modification of
              -- last until time and number of iovs.
              -- If it is not a hole then I can only fill temporary object
              -- If it is a new channel Id I can store the temporary object previously filled,
              -- and I need to fill a new temporary object
              IF prevchan <> v_newrangerow.channel_id THEN
                prevchan := v_newrangerow.channel_id;
                IF rangeentryprev.cool_iovrange_pk IS NOT NULL THEN
                  INSERT INTO COOL_IOV_RANGES 
                  (COOL_IOVSUMMARY_FK, COOL_IOVRANGE_SINCE, COOL_IOVRANGE_UNTIL, COOL_IOVRANGE_NIOVS, COOL_IOVRANGE_ISHOLE, COOL_IOVBASE, COOL_IOVSINCE_STR, COOL_IOVUNTIL_STR, COOL_IOVRANGE_PK)
                  VALUES
                  (rangeentryprev.cool_iovsummary_fk, rangeentryprev.cool_iovrange_since, rangeentryprev.cool_iovrange_until, rangeentryprev.cool_iovrange_niovs, 
                   rangeentryprev.cool_iovrange_ishole, node_iovbase, 'todo','todo',rangeentryprev.cool_iovrange_pk);  
                   rangeentryprev.cool_iovrange_pk := NULL;
                END IF;     
              END IF;     
            
              IF v_newrangerow.iov_hole>0 THEN
                ishole := 1;
                IF rangeentryprev.cool_iovrange_pk IS NOT NULL THEN
                  rangeentryprev.cool_iovrange_until := v_newrangerow.maxiov_until;
                  rangeentryprev.cool_iovrange_niovs := rangeentryprev.cool_iovrange_niovs + 1;  
                ELSE
                  cool_iovrange_pk := COOLIOVRANGE_SEQ.NEXTVAL;
                  rangeentryprev.cool_iovsummary_fk := thesummary_id;
                  rangeentryprev.cool_iovrange_pk := cool_iovrange_pk;
                  rangeentryprev.cool_iovrange_ishole := ishole;
                  rangeentryprev.cool_iovrange_since := v_newrangerow.miniov_since;
                  rangeentryprev.cool_iovrange_until := v_newrangerow.maxiov_until;
                  rangeentryprev.cool_iovrange_niovs := v_newrangerow.niovs; 
                END IF;
                INSERT INTO COOL_IOV_RANGES 
                  (COOL_IOVSUMMARY_FK, COOL_IOVRANGE_SINCE, COOL_IOVRANGE_UNTIL, COOL_IOVRANGE_NIOVS, COOL_IOVRANGE_ISHOLE, COOL_IOVBASE, COOL_IOVSINCE_STR, COOL_IOVUNTIL_STR, COOL_IOVRANGE_PK)
                  VALUES
                  (rangeentryprev.cool_iovsummary_fk, rangeentryprev.cool_iovrange_since, rangeentryprev.cool_iovrange_until, rangeentryprev.cool_iovrange_niovs, 
                   rangeentryprev.cool_iovrange_ishole, node_iovbase, rangeentryprev.cool_iovsince_str, rangeentryprev.cool_iovuntil_str,rangeentryprev.cool_iovrange_pk); 
                  rangeentryprev.cool_iovrange_pk := NULL;

                cool_iovrange_pk := COOLIOVRANGE_SEQ.NEXTVAL;
                rangeentry.cool_iovrange_pk := cool_iovrange_pk;
                rangeentry.cool_iovsummary_fk := thesummary_id;
                rangeentry.cool_iovrange_ishole := ishole;
                rangeentry.cool_iovrange_since := v_newrangerow.maxiov_until;
                rangeentry.cool_iovrange_until := v_newrangerow.hole_until;
                rangeentry.cool_iovrange_niovs := v_newrangerow.niovs;  

                
                cooltimesince := f_GetCool_Time(rangeentry.cool_iovrange_since, node_iovbase);
                cooltimeuntil := f_GetCool_Time(rangeentry.cool_iovrange_until, node_iovbase);
                
                rangeentry.cool_iovsince_str := cooltimesince;
                rangeentry.cool_iovuntil_str := cooltimeuntil;


                INSERT INTO COOL_IOV_RANGES 
                  (COOL_IOVSUMMARY_FK, COOL_IOVRANGE_SINCE, COOL_IOVRANGE_UNTIL, COOL_IOVRANGE_NIOVS, COOL_IOVRANGE_ISHOLE, COOL_IOVBASE, COOL_IOVSINCE_STR, COOL_IOVUNTIL_STR, COOL_IOVRANGE_PK)
                  VALUES
                  (rangeentry.cool_iovsummary_fk, rangeentry.cool_iovrange_since, rangeentry.cool_iovrange_until, rangeentry.cool_iovrange_niovs, 
                   rangeentry.cool_iovrange_ishole, node_iovbase, rangeentry.cool_iovsince_str,rangeentry.cool_iovuntil_str,rangeentry.cool_iovrange_pk); 
                
              ELSE
                cool_iovrange_pk := COOLIOVRANGE_SEQ.NEXTVAL;
                rangeentryprev.cool_iovsummary_fk := thesummary_id;
                rangeentryprev.cool_iovrange_pk := cool_iovrange_pk;
                rangeentryprev.cool_iovrange_ishole := ishole;
                rangeentryprev.cool_iovrange_since := v_newrangerow.miniov_since;
                rangeentryprev.cool_iovrange_until := v_newrangerow.maxiov_until;
                rangeentryprev.cool_iovrange_niovs := v_newrangerow.niovs;  
                
                cooltimesince := f_GetCool_Time(rangeentryprev.cool_iovrange_since, node_iovbase);
                cooltimeuntil := f_GetCool_Time(rangeentryprev.cool_iovrange_until, node_iovbase);
                
                rangeentryprev.cool_iovsince_str := cooltimesince;
                rangeentryprev.cool_iovuntil_str := cooltimeuntil;

              END IF;
            END IF;
        END LOOP;
      CLOSE newrange_cursor; 
 
  -- Store last range
        IF rangeentryprev.cool_iovrange_pk IS NOT NULL THEN
         INSERT INTO COOL_IOV_RANGES 
                  (COOL_IOVSUMMARY_FK, COOL_IOVRANGE_SINCE, COOL_IOVRANGE_UNTIL, COOL_IOVRANGE_NIOVS, COOL_IOVRANGE_ISHOLE, COOL_IOVBASE, COOL_IOVSINCE_STR, COOL_IOVUNTIL_STR, COOL_IOVRANGE_PK)
                  VALUES
                  (rangeentryprev.cool_iovsummary_fk, rangeentryprev.cool_iovrange_since, rangeentryprev.cool_iovrange_until, rangeentryprev.cool_iovrange_niovs, 
                   rangeentryprev.cool_iovrange_ishole, node_iovbase, rangeentryprev.cool_iovsince_str,rangeentryprev.cool_iovuntil_str,rangeentryprev.cool_iovrange_pk); 
        END IF;
    END IF;
  END LOOP;
  CLOSE node_cursor;
EXCEPTION
WHEN OTHERS THEN
dbms_output.put_line
  (
    'Handling exception: ' || dbms_utility.format_error_backtrace
  );
  ROLLBACK;

END p_UpdRanges_Summary;


END COND_TOOLS_PKG;

/
