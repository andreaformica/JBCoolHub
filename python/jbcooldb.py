'''
Created on Feb 05, 2014

@author: formica
'''

import sys, os, pickle, getopt,re
import json
import pycurl
import cStringIO
import os.path

from xml.dom import minidom
from clint.textui import colored
from datetime import datetime

# DEFINE THE RUN 222222 AS THE ORIGIN FOR OFFICIAL RUN2 DB
# THIS IS COMPLETELY ARTIFICAL, BUT WE DECIDED THAT WE WANT TO USE THIS
# IN ANY CASE I WILL PUT DATA FROM RUN 215090, EXCEPT FOR TDAQ AND TRIGGER SV FOLDERS
# WHICH ARE TOO BIG...FOR THOSE ONLY 215090-215093 RANGE and >222222 ARE COPIED
# THE RUN TO BE USED FOR TESTING SHOULD BE 215091
# REMARKS FROM RICHARD H. : Some Athena software uses EXT/DCS/MAGNETSENSORSDATA in order
# to check if the magnet is on. This python should use either the same instance as CONDDB.py
# or we should add a flag in globalflags to force its instance to something different from IOVDbSvc.
# This is very useful for testing using RUN1 data.

class JBRestConnection:
    
    __baseurl = 'aiatlas062.cern.ch:8080/JBRestCool/rest/'
    __debug = False
    
    def getBaseUrl(self):
        return self.__baseurl
    
    def setSocks(self, host, port):
        self.__host = host
        self.__port = port
        self.__curl.setopt(pycurl.PROXY, self.__host)
        self.__curl.setopt(pycurl.PROXYPORT, self.__port)
        self.__curl.setopt(pycurl.PROXYTYPE, pycurl.PROXYTYPE_SOCKS5)
        
    def setUrl(self, url):
        if self.__debug:
            print "Setting url ", url
        self.__url = self.__baseurl + url
        self.__curl.setopt(pycurl.URL, self.__url)
        
    def getData(self):
        #print "Get data using url ", self.__url
        buf = cStringIO.StringIO()
        self.__curl.setopt(self.__curl.WRITEFUNCTION, buf.write)
        self.__curl.perform()
        data = buf.getvalue()
        try:
        #json.loads(data)
            jsondata = json.loads(data)
            return jsondata
        except json, e:
            print e
            return {}
    
    def __init__(self):
        self.__curl = pycurl.Curl()


class JBCoolDB:

    def getSchemas(self,schema=None, db=None):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        
        url = 'plsqlcooljson/' + schema + '/' + db + '/schemas' 
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def getFolders(self,schema=None, db=None):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        
        url = "plsqlcooljson/%s/%s/nodes" % (schema, db)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def getTags(self, schema=None, db=None, folder='/'):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/tags" % (schema, db, folder)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def getTraceTags(self, globaltag, schema=None, db=None):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/trace" % (schema, db, globaltag)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data
    
    def getFullTraceTags(self, globaltag, schema=None, db=None):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/fulltrace" % (schema, db, globaltag)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data

    def getBackTraceTags(self, tag, schema=None, db=None):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "comajson/%s/%s/%s/backtrace" % (schema, db, tag)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data

    def getIovStatPerChannel(self, schema, db, folder, tag):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/fld/%s/tag/iovsperchan" % (schema, db, folder, tag)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data
 
    def getChannels(self, schema, db, folder, chid):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        if chid is None:
            chid = 'all'
        url = "plsqlcooljson/%s/%s/%s/fld/%s/channels" % (schema, db, folder, chid)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data
 
    def listIovsSummaryInNodesSchemaTagRangeAsList(self, schema, db, folder, tag, since, until, tspan):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/fld/%s/tag/%s/%s/%s/rangesummary/list" % (schema, db, folder, tag, since, until, tspan)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data
           
    def listIovsInNodesSchemaTagRangeAsList(self, schema, db, folder, tag, since, until, tspan):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/fld/%s/tag/all/channel/%s/%s/%s/iovs/list" % (schema, db, folder, tag, since, until, tspan)
        self.__curl.setUrl(url)
        data = {}
        try:
            data = self.__curl.getData()
        except ValueError, e:
            print e
        return data
           
    def listPayloadInNodesSchemaTagRangeAsList(self, schema, db, folder, tag, chan, chansel, since, until, tspan):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "plsqlcooljson/%s/%s/%s/fld/%s/tag/%s/%s/%s/%s/%s/data/list" % (schema, db, folder, tag, chan, chansel, since, until, tspan)
        self.__curl.setUrl(url)
        return self.__curl.getData()
    
    def findCoverage(self, gtag):
        url = "coolgtagjson/%s/coverage" % (gtag)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def findGtagList(self,  schema, db, gtag):
        url = "coolgtagjson/%s/%s/%s/list" % (schema,db,gtag)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def findGtagState(self, state):
        url = "comajson/%s/gtagstate" % (state)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def findClassification(self, schema,folder):
        url = "comajson/%s/%s/classification" % (schema,folder)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def findNemoRange(self, rmin,rmax,tspan):
        url = "comajson/%s/%s/%s/nemotimerange" % (rmin,rmax,tspan)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    def listIovsSummaryInNodesSchemaAsList(self, schema, db, gtag):
        if schema is None:
            schema = self.__schema
        if db is None:
            db = self.__db
        url = "coolgtagjson/%s/%s/%s/iovsummary/list" % (schema,db,gtag)
        self.__curl.setUrl(url)
        return self.__curl.getData()

    @staticmethod 
    def pickFolderMigrationList(migfolderlist, filename='picklemigfolderlist.pik'):
            output_file = open(filename, "w")
            subwpic = pickle.Pickler(output_file)
            subwpic.dump(migfolderlist)
            output_file.close()
            
    @staticmethod 
    def pickObsoleteFolderList(obsoletefolderlist, filename='pickleobsoletefolderlist.pik'):
            output_file = open(filename, "w")
            subwpic = pickle.Pickler(output_file)
            subwpic.dump(obsoletefolderlist)
            output_file.close()

    
    def printList(self, folderListFile='picklemigfolderlist.pik'):
        input_file = open(folderListFile, "r")
        self.folderList = pickle.load(input_file)     
        input_file.close()
        for fld in self.folderList:
#            print aschema.items()
            print fld["schemaName"], fld["nodeFullpath"], fld["tagName"]
        
       
    def __init__(self, schema, db, usesocks=False):
        print 'Create instance of JBCoolDB'
        self.__schema = schema
        self.__db = db
        self.__curl = JBRestConnection()
        if usesocks == True:
            self.__curl.setSocks('localhost', 3129)
        print 'Init Business Delegate with schema, db ', self.__schema, self.__db

class JBCoolCopyTools():
    '''
    This class handle creation of scripts to copy folders and tags
    '''
    _source = '$1'
    _dest = 'sqlite://;schema=mytest.db;dbname=COMP200'
    _sourcefld = 'none'
    _destfld = 'none'
    _sourcetag = 'none'
    _desttag = 'none'
    _sourcefldtaglist = []
    _destfldstring = None
    
    def __init__(self,source, dest, sourcefldtaglist, destfldstring=None):
        self._source = source
        self._dest = dest
        self._sourcefldtaglist = sourcefldtaglist
        if destfldstring is not None:
            self._destfldstring = destfldstring
    
    def createScript(self, db,passwd):
        outf = open("copyscript.sh",'w')
        for afolder in self._sourcefldtaglist:
            #print afolder
            #print row
            instance = afolder['dbName']
            schemaname = afolder['schemaName']
            print 'schemaname ',schemaname
            if not schemaname.startswith('ATLAS'):
                schemaname = 'ATLAS_'+schemaname

            connect = schemaname[len('ATLAS_'):] + '/' + instance
            fldarr = afolder['nodeFullpath'].split('/')
            fldarr.insert(2,self._destfldstring)
            newfolder = '/'.join(fldarr)
            
            fldroot = fldarr[1]
            newfldroot = fldroot+self._destfldstring
            print 'Replace ',fldroot,' with ',newfldroot
            newtag = afolder['tagName'].replace(fldroot, newfldroot)
            newtag = re.sub('(?i)' + re.escape(fldroot), newfldroot, afolder['tagName']) 
            if newtag == afolder['tagName']:
                newtag = (newfldroot+newtag)
            options = ''
            if db == "sqlite":
                destconnection = ('sqlite://;schema=./%s.sqlite;dbname=%s') % (self._dest,instance)
                options = '-create'
            elif db == "oracle":
                destconnection = ('oracle://ATLAS_COOLPROD;schema=%s;dbname=%s;user=%s_W;password=%s') % (self._dest,instance,self._dest,passwd)
            else:
                raise NameError('db technology is unkown') 
            
            print 'instance ',instance, ' connect string ',connect
            command = ('AtlCoolCopy.exe "%s" "%s" %s -f %s -of %s -tag %s -outtag %s' % (connect, destconnection, options, afolder['nodeFullpath'], newfolder, afolder['tagName'], newtag))
            print command
            outf.write(command+"\n")
        outf.close()


class JBCoolMigTools():
    
    '''
    classdocs
    '''
    _defaultFilename = 'CONDBR2.sqlite'
    _schemafilename = ''
    _folderfilename = ''
    _tagsfilename = ''
    _folderList = None
    _tagsList = None
    _schemaList = None
    _loggedmig = {}
    _mergedsqlite = False
    _skipobsoletecheck = False
    _skipgtagcheck = False
    _skipathenacheck = False
    _addedbyhandfolders = {}
    
    def __init__(self, schemafile, fldlistfile, taglistfile, newinstance):
        self._schemafilename = schemafile
        self._folderfilename = fldlistfile
        self._tagsfilename = taglistfile
        self._instancename = newinstance
        self._mintsobjrun2 = None
        self._forcegtagonly = False
        jsonfile = open(self._schemafilename,'r').read()
        data = json.loads(jsonfile)
        self._schemaList = data

        jsonfldfile = open(self._folderfilename,'r').read()
        flddata = json.loads(jsonfldfile)
        self._folderList = flddata
        jsontagfile = open(self._tagsfilename,'r').read()
        tagsdata = json.loads(jsontagfile)
        self._tagsList = tagsdata
        print 'Initialised objects for migration...'

        print 'Load nodes from list...'
        self._addedbyhandfolders = ExpertsFoldersMgr.createExpertsFolderList()
        for key in self._addedbyhandfolders.keys():
            if key == 'nodes':
                print 'List of nodes added by hand is : '
                for anode in self._addedbyhandfolders['nodes']:
                    print 'Node ',anode['nodeFullpath']
                    
        print 'Load nodes from Athena list...'
        self._addedbyathenafolders = ExpertsFoldersMgr.loadAthenaOutput('athena.txt')
        for key in self._addedbyathenafolders.keys():
            if key == 'nodes':
                print 'List of nodes added by Athena is : '
                for anode in self._addedbyathenafolders['nodes']:
                    print 'Node ',anode['nodeFullpath']
        #print self._schemaList
#        print self._folderList
#        print self._tagsList
        
    def skipObsoleteCheck(self, skipobsolete=False):
    	self._skipobsoletecheck = skipobsolete
    def skipGtagCheck(self, skipgtag=False):
    	self._skipgtagcheck = skipgtag
    def skipAthenaCheck(self, skipathena=False):
    	self._skipathenacheck = skipathena

    def forceGtagOnly(self, forcegtagonly=False):
    	self._forcegtagonly = forcegtagonly
    	
    def mergeOutput(self, ismerged=False):
        self._mergedsqlite = ismerged

    def setMinRun2(self, minr2tsobj):
        self._mintsobjrun2 = minr2tsobj
        
    def createInstanceBySchema(self, schemafilename=_schemafilename):
        jsonfile = open(schemafilename,'r').read()
        data = json.loads(jsonfile)
        instancename = data['db']
        for aschema in data['schemas']:
            scriptname = aschema['shortName']+'-condbr2.sh'
            outfname = aschema['shortName']+'-%s.sqlite' % instancename
            if self._mergedsqlite:
                outfname="ALL%s.sqlite" % instancename
            print 'Create script for migration of ',aschema['shortName']
            buf = self.createInstance(aschema['shortName'], outfname)
            outf = open(scriptname,'w')
            outf.write(buf)
            outf.close()

    def copyInstanceBySchema(self, timestampobj, schemafilename=_schemafilename):
        jsonfile = open(schemafilename,'r').read()
        data = json.loads(jsonfile)
        instancename = data['db']
        for aschema in data['schemas']:
            scriptname = aschema['shortName']+'-copycondbr2.sh'
            outfname = aschema['shortName']+'-%s.sqlite' % instancename
            if self._mergedsqlite:
                outfname="ALL%s.sqlite" % instancename
            print 'Create script for copy of ',aschema['shortName']
            buf = self.copy2Instance(aschema['shortName'], timestampobj, outfname)
            outf = open(scriptname,'w')
            outf.write(buf)
            outf.close()
    
    def checkObsolete(self, fldentry, isobsolete):
    	if self._skipobsoletecheck:
    		return False
        print 'checkObsolete',fldentry, isobsolete
        # if isobsolete in fldentry.keys():
        run1only = isobsolete.startswith('Run1Only')
        run2only = isobsolete.startswith('Run2Only')
        obsolete = isobsolete.startswith('Obsolete')
        unknown = isobsolete.startswith('Unknown')
        allruns = isobsolete.startswith('AllRun')
        underreview = isobsolete.startswith('Under')
        if  (obsolete):
            return True
        if  (unknown):
# Include additional L1Calo folders
            if (fldentry.startswith('/TRIGGER/Receivers')):
                return False
            return True
# The following SET of condition has been introduced for RUN2 instance in production: A.Formica 2014/07/24
        if  (underreview):
            return True
        if  (run1only):
            return True
        return False

    def checkIsInGtag(self, schema, fldentry):
#        if 'gtagName' in fldentry.keys() and fldentry['gtagName'] is not None and fldentry['tagName'] is not None:
#            return True
        seltags = []
        isingtag = False
        for agtag in self._tagsList['gtags']:
            print 'Search for tag associated to folder ',fldentry,' for global tag ',agtag
            gtagfldlist = self._tagsList[agtag]
            for atag in gtagfldlist:
                if atag['shortName'] == schema and atag['nodeFullpath'] == fldentry:
                    print 'Found tag ',atag['tagName'],' for schema ',schema,' and folder ',fldentry
                    isingtag = True
                    if atag['tagName'] not in seltags:
                        seltags.append(atag['tagName'])
                    
        return (isingtag, seltags)

    def gettaglist(self, schema, fldentry):
# Added for single folder and schema migration script
        seltags = []
        for atag in self._tagsList['tags']:
            print 'Found tag associated to folder ',fldentry
            if atag['shortName'] == schema and atag['nodeFullpath'] == fldentry:
                print 'Found tag ',atag['tagName'],' for schema ',schema,' and folder ',fldentry
                isingtag = True
                if atag['tagName'] not in seltags:
                    seltags.append(atag['tagName'])
                    
        return (isingtag, seltags)
    
    
    def checkIsSingleVersion(self, schema, fldentry):
#        if 'gtagName' in fldentry.keys() and fldentry['gtagName'] is not None and fldentry['tagName'] is not None:
#            return True
        svnodes = []
        issv = False
        for anode in self._folderList['nodes']:
            print 'Search for SV folder ',fldentry
            if anode['nodeFullpath'] == fldentry and anode['versioning']==0: 
                issv = True
                svnodes.append(anode)
                    
        return (issv, svnodes)
    
    def printlog(self):
        msg = ('MIG>>>>> %22s %40s %6s %10s %10s %6s %3s %3s') % ('shortName','nodeFullpath','iovbase','isobsolete', 'flag', 'isingtag','vrs','ntags')
        print (msg)
        for aschema in self._loggedmig.keys():
            _schema = self._loggedmig[aschema]
            for anode in _schema.keys():
                _node = _schema[anode]
                createstep = _node['create']
                copystep = _node['copy']
                msg = ('MIG>>>>> %22s %55s %10s %14s %14s %8s %3s %3s') % (aschema,anode,copystep['iovbase'],createstep['isobsolete'],createstep['flag'],copystep['isingtag'],createstep['versioning'],createstep['ntags'])
#                 if "Obsolete" in createstep['isobsolete']:
#                     print 'Folder is OBSOLETE'
#                 if createstep['isingtag']:
#                     print 'Is in Global tag is True'
                if "Obsolete" in createstep['isobsolete'] and copystep['isingtag'] and int(createstep['versioning'])>0:
                    print colored.yellow(msg)
                elif "Obsolete" not in createstep['isobsolete'] and "Under" not in createstep['isobsolete'] and not copystep['isingtag'] and int(createstep['versioning'])>0:
                    print colored.red(msg)
                elif "Obsolete" not in createstep['isobsolete'] and "Under" in createstep['isobsolete'] and not copystep['isingtag'] and int(createstep['versioning'])>0:
                    print colored.blue(msg)
                else:
                    print colored.green(msg)
                
    def createInstance(self, selschema, filename=_defaultFilename):
        
        output = cStringIO.StringIO()
        output.write('# script to generate instance for schema {0} \n'.format(selschema))
        #print self._folderList
        instance = self._folderList['db']
        if selschema not in self._loggedmig.keys():
            schemalog = {}
        else:
            schemalog = self._loggedmig[selschema]
            
        for row in self._folderList['nodes']:
            #print row
            schemaname = 'ATLAS_'+row['shortName']
            #print 'schemaname ',schemaname, ' selected schema ',selschema
            #print row
            if selschema != schemaname[len('ATLAS_'):] and selschema != schemaname:
                continue
            if not schemaname.startswith('ATLAS'):
                schemaname = 'ATLAS_'+schemaname

            connect = schemaname[len('ATLAS_'):] + '/' + instance
            
            print 'instance ',instance, ' connect string ',connect
           
            # get folder list
            #folders = row['folders']
            #for afolder in folders:
            folder = row['nodeFullpath']
            if folder not in schemalog.keys():
                folderlog = {}
            else:
                folderlog = schemalog[folder]
                
            # Apply isobsolete cut
            obsoleteclass = row['isobsolete']

            # Do not modify the classification at the create step
#           for addednode in self._addedbyhandfolders['nodes']:
#               addedname = addednode['nodeFullpath']
#               addedschema = 'ATLAS_'+addednode['shortName']
#               if addedname == folder and addedschema == schemaname:
#                   obsoleteclass = addednode['isobsolete']
#                   
#           for addednode in self._addedbyathenafolders['nodes']:
#               addedname = addednode['nodeFullpath']
#               if addedname == folder  and (obsoleteclass=='Unknown'):
#                   obsoleteclass = addednode['isobsolete']
                    
            isobsolete = self.checkObsolete(folder,obsoleteclass)
            print '>>>>> ISOBSOLETE check during createInstance: ',folder, isobsolete
                #### Change this two lines if gtag list
            isingtag=True
            tags=[]
            if (not self._skipgtagcheck):
                (isingtag, tags) = self.checkIsInGtag(selschema, folder)

            if (not isingtag and self._forcegtagonly):
                print 'SKIP: force gtag mode is ACTIVE'
                continue
            issv = (row['versioning'] == 0)
            folderlog['create'] = { 'isingtag' : isingtag, 'isobsolete' : obsoleteclass, 'flag': row['isobsolete'], 'versioning' : row['versioning'], 'ntags' : len(tags) }
            schemalog[folder] = folderlog
            self._loggedmig[selschema] = schemalog

            # Check inconsistent information in iovbase
            iovbase = row['iovbase']
            if iovbase is None:
                folderlog['error'] = {'iovbase' : iovbase }
                schemalog[folder] = folderlog
                self._loggedmig[selschema] = schemalog
                print "ERROR: iovbase is not correct....!"
                if not isobsolete:
                    print "FATAL: because of iovbase not correct this folder will be skipped !"
                continue 
                #isingtag = True
                #info = afolder['info']
            newfolder = folder
            separatepayloadoption = '-fp'
            isdone=False
            if issv:
                separatepayloadoption = ''
            if ((len(tags) > 0 and (not isobsolete and isingtag)) or (issv and (not isobsolete)) or (obsoleteclass.startswith('Run2Only'))):
#                if ((not isobsolete and isingtag)):
                    #retcode = os.system('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=CONDBR2" -create  -hitag   -tag %s   -magic ATLAS- -f %s -gettime -rs %s -ru %s  -readoracle -fp ' % (connect, filename, tag, folderT[0], run, run))
                    # Option used: 
                    # -fp : create separate payload tables
                    # -nd : nodata, create only folder structure
                    #
                print 'Folder,isobsolete,isingtag,lentags ',folder, isobsolete, isingtag, len(tags)
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -nd -nch -f %s -of %s -readoracle %s  ' % (connect, filename, self._instancename, folder, newfolder, separatepayloadoption))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                isdone=True
                print >>output, command, '\n'

            if (not isobsolete and folder == "/GLOBAL/TrackingGeo/LayerMaterialV2" and not isdone):
                # Create folder
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -nd -nch -f %s -of %s -readoracle  ' % (connect, filename, self._instancename, folder, newfolder))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                isdone=True
                print >>output, command, '\n'
  
            if (not isobsolete and folder.startswith("/TRIGGER") and not isdone):
                # Create folder
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -nd -nch -f %s -of %s -readoracle %s ' % (connect, filename, self._instancename, folder, newfolder, separatepayloadoption))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                isdone=True
                print >>output, command, '\n'
            if (folder.startswith('/TDAQ/Resources') and not isdone):
                # Create folder
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -nd -nch -f %s -of %s -readoracle %s ' % (connect, filename, self._instancename, folder, newfolder, separatepayloadoption))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                isdone=True
                print >>output, command, '\n'
       

        return output.getvalue()
    
    def copy2Instance(self, selschema, timestampsince, filename=_defaultFilename ):
        output = cStringIO.StringIO()
        output.write('# script to copy iov to new instance for schema {0} \n'.format(selschema))
        instance = self._folderList['db']
        if selschema not in self._loggedmig.keys():
            schemalog = {}
        else:
            schemalog = self._loggedmig[selschema]
        for row in self._folderList['nodes']:
            #print row
            schemaname = 'ATLAS_'+row['shortName']
#            print 'schemaname ',schemaname, ' selected schema ',selschema
            #print row
            if selschema != schemaname[len('ATLAS_'):] and selschema != schemaname:
                continue
            if not schemaname.startswith('ATLAS'):
                schemaname = 'ATLAS_'+schemaname
            connect = schemaname[len('ATLAS_'):] + '/' + instance
            
            # get folder list
            folder = row['nodeFullpath']
            if folder not in schemalog.keys():
                folderlog = {}
            else:
                folderlog = schemalog[folder]
                
            #for afolder in folders:
            print 'Copy2Instance: analyse folder ',folder
            
            blkpclass = row['gtagclass']
            obsoleteclass = row['isobsolete']
            isinathena=False
            isaddedbyhand=False
            obsoleteclassorig = row['isobsolete']

            for addednode in self._addedbyhandfolders['nodes']:
                addedname = addednode['nodeFullpath']
                addedschema = 'ATLAS_'+addednode['shortName']
                if addedname == folder:
                    isaddedbyhand = True
                if addedname == folder and addedschema == schemaname:
                    print 'HAND: ',folder,' changes classification from ',obsoleteclass,' to ',addednode['isobsolete']
                    obsoleteclass = addednode['isobsolete']
                                        
            for addednode in self._addedbyathenafolders['nodes']:
                addedname = addednode['nodeFullpath']
                if addedname == folder:
                    isinathena = True
                # This means that we override completely the classification using athena log in case any classification is found
#                if addedname == folder and (obsoleteclass=='Unknown' or obsoleteclass=='UnderReview' or obsoleteclass=='Obsolete'):

# We change the classification only if the classification is unknown
                if addedname == folder and (obsoleteclass=='Unknown'):
                    print 'ATHENA: ',folder,' changes classification from ',obsoleteclass,' to ',addednode['isobsolete']
                    obsoleteclass = addednode['isobsolete']
                # TEMPORARY TO PATCH SCT case    
                #if addedname == folder and (obsoleteclass=='UnderReview' and (folder.startswith('/SCT/DAQ/Calibration/N') or folder.startswith('/SCT/DAQ/Configuration'))):
                #    print 'ATHENA: ',folder,' changes classification from ',obsoleteclass,' to ',addednode['isobsolete']
                #    obsoleteclass = addednode['isobsolete']
                # TEMPORARY TO PATCH SCT case    
                if addedname == folder and (obsoleteclass=='Obsolete' and (folder.startswith('/TILE/') or folder.startswith('/SCT/DAQ/Configuration'))):
                    print 'ATHENA: ',folder,' changes classification from ',obsoleteclass,' to ',addednode['isobsolete']
                    obsoleteclass = addednode['isobsolete']

            if (self._skipathenacheck):
                isinathena=False
                isaddedbyhand=False
                    
            isobsolete = self.checkObsolete(folder,obsoleteclass)
            print 'DIFF: ',('%s class-%s origclass-%s obs-%s hand-%s ath-%s') % (folder,obsoleteclass,obsoleteclassorig,isobsolete,isaddedbyhand,isinathena)        
            #info = afolder['info']
            #tag = info['tagName']
            isingtag=True
            taglist=[]
            if (not self._skipgtagcheck):
                (isingtag, taglist) = self.checkIsInGtag(selschema, folder)
            else:
                fulltaglist = self._tagsList['tags']
                for atag in fulltaglist:
                    tag=atag['tagName']
                    taglist.append(tag)
                 
            if (not isingtag and self._forcegtagonly):
                print 'SKIP: force gtag mode is ACTIVE'
                continue

            if len(taglist) == 0:
                tag = None
   
            if blkpclass.startswith('AllRuns') and not isinathena and isingtag:
                print 'ATHENA: WARNING AllRuns folder is in gtag but not in athena log ',schemaname,' ',folder,' ',obsoleteclassorig, blkpclass
            if blkpclass.startswith('Run2Only') and not isinathena and isingtag:
                print 'ATHENA: WARNING Run2Only folder is in gtag but not in athena log ',schemaname,' ',folder,' ',obsoleteclassorig, blkpclass
         
            iovbase = row['iovbase']

            minRun2since=self._mintsobjrun2['coolsince']/1000000000.
            minRun2option= (' -ts %d ') % minRun2since
                
            since = timestampsince['coolsince']/1000000000.
            timeoption = (' -ts %d ') % since
            timeoptionuntil = ''
            if timestampsince['cooluntil'] is not None:
                until = timestampsince['cooluntil']/1000000000.
                timeoptionuntil = (' -tu %d ') % until
            print 'Iovbase is ',iovbase
            issv = (row['versioning'] == 0)
            folderlog['copy'] = { 'isingtag' : isingtag, 'isobsolete' : obsoleteclass, 'flag': row['isobsolete'], 'versioning' : row['versioning'], 'ntags' : len(taglist), 'tags' : taglist, 'iovbase' : iovbase }
            schemalog[folder] = folderlog
            self._loggedmig[selschema] = schemalog

            # Check inconsistent information in iovbase
            if iovbase is None:
                folderlog['error'] = {'iovbase' : iovbase }
                schemalog[folder] = folderlog
                self._loggedmig[selschema] = schemalog
                print "ERROR: iovbase is not correct....!"
                if not isobsolete:
                    print "FATAL: because of iovbase not correct this folder will be skipped !"
                continue 
            
            # Change time range options in case of run based folders
            if iovbase[:len('run')] == 'run':
                
                minRun2since=self._mintsobjrun2['runmin']
                minRun2option= (' -rs %d ') % minRun2since

                since = timestampsince['runmin']
                timeoption= (' -rs %s ') % (since)
                if timestampsince['runmax'] is not None:
                    until = timestampsince['runmax']
                    timeoptionuntil = (' -ru %s ') % until
            sincestr = timestampsince['sincedate']
           
            if timestampsince['nocut']>0:
                timeoption=''
            if timestampsince['nountilcut']>0:
                timeoptionuntil='' 
            newfolder = folder

# Some use commands that I found in Misha's scripts:
#                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -hitag -tag %s -magic ATLAS- -f %s -gettime -readoracle -fp ' % (connect, filename, self._instanceName, tag, folder))
#######                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -create  -hitag -tag %s -magic ATLAS- -f %s -gettime -readoracle -fp ' % (connect, filename, self._instanceName, tag, folder))

# Set option to skip channel copy: this can be done later via an sql procedure
            nochannelsopt = '-nch'
            # loop over tags associated
            for tag in taglist:
                print 'Check folder,tag,isobsolete,issv: ',folder,tag,isobsolete,issv
                if (tag is not None and (not isobsolete and isingtag and not issv)):
                    tagnamearr = tag.split('-')
                    tagnamearr.insert(1,'RUN2')
                    newtag = '-'.join(tagnamearr)
                    command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -cti %s -tag %s -outtag %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, tag, newtag, folder, newfolder,timeoption,timeoptionuntil))
                    if command == '':
                        raise ('ERROR: Problem with %s creation ' % filename)
                    print command
                    print >>output, command, '\n'
            # For single version folder do something different
            if issv and not isobsolete:
                # Add channel descriptions for the MDT/DCS folders....In principle WE DO NOT COPY DCS FOLDERS !!! Slava takes care of that...
                if "MDT/DCS" in folder:
                    nochannelsopt = ''
                if ("TDAQ" in folder or "TRIGGER" in folder) and timestampsince['runmin']<222222:
                    # Generate 2 copy commands, other wise the total size is too big for single version folders
                    # For final production, we need -rs 215090 -ru 215093 and -rs 222222
                    command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" %s -f %s -of %s -rs 215090 -ru 215093 -gettime ' % (connect, filename, self._instancename, nochannelsopt, folder, newfolder))
                    print command
                    print >>output, command, '\n'
                    command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, folder, newfolder,minRun2option,timeoptionuntil))
                else:
                # Generic command to copy single version folders
                    command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, folder, newfolder,timeoption,timeoptionuntil))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                print >>output, command, '\n'
                
            #Add geo tags by hand: verify what is the classification...there are 2 folders LayerMaterial and LayerMaterialV2: which one should be migrated ????
            if (not isobsolete and folder == "/GLOBAL/TrackingGeo/LayerMaterialV2"):
                # retrieve all tags
                geotags = ['AtlasLayerMat_v16_ATLAS-GEO-18','AtlasLayerMat_v16_ATLAS-GEO-16','AtlasLayerMat_v16_ATLAS-GEO-20']
                for geotag in geotags:
                    command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" -cti %s -tag %s -outtag %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, geotag, geotag, folder, newfolder,timeoption,timeoptionuntil))
                    if command == '':
                        raise ('ERROR: Problem with %s creation ' % filename)
                    print command
                    print >>output, command, '\n'

            if (not isobsolete and folder.startswith("/TRIGGER") and len(taglist) == 0 and not issv):
                # Copy folder using the head
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, folder, newfolder,timeoption,timeoptionuntil))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                print >>output, command, '\n'
               
            if (folder.startswith('/TDAQ/Resources') and issv):
                # Copy folder using the head
                command = ('AtlCoolCopy.exe "%s" "sqlite://;schema=./%s;dbname=%s" %s -f %s -of %s %s %s' % (connect, filename, self._instancename, nochannelsopt, folder, newfolder,timeoption,timeoptionuntil))
                if command == '':
                    raise ('ERROR: Problem with %s creation ' % filename)
                print command
                print >>output, command, '\n'
                


        return output.getvalue()

class ExpertsFoldersMgr():
    '''
    classdocs
    '''
    def __init__(self):
        '''
        Constructor
        '''

    @staticmethod
    def loadAthenaOutput(filename):
        athfolderlist = {}
        inp_file = open(filename,"r")
        folderlist = []
        for line in inp_file:
            print 'Reading line : ' , line
            #linearr = line.split(" ")
            linearr = [x.strip() for x in line.split()]
            isobsolete = 'Unknown'
            print 'folder usage in athena ', linearr
            usagearr = linearr[2].split('/')
            #Uncomment to see what is really used
#            if int(usagearr[0]) >= 1:
            if int(usagearr[0]) >= 0:
                isobsolete = "AddedByAthena"
            folderlist.append({"nodeFullpath" : linearr[0], "isobsolete" : isobsolete, "shortName" : 'unknown'})
        
        athfolderlist['nodes']= folderlist
        athfolderlist['db']="COMP200"

        return athfolderlist

    @staticmethod 
    def pickExpertsFolderList(expfolderlist, filename='pickleexpfolderlist.pik'):
            output_file = open(filename, "w")
            subwpic = pickle.Pickler(output_file)
            subwpic.dump(expfolderlist)
            output_file.close()

    @staticmethod 
    def createExpertsFolderList():
        expfolderlist = {}
        '''
        The AddedByHand applies either to Unknown that are needed by Andrei job on Run1 data, or to
        Obsolete that are in any case needed when running the job on Run1 data.
        I think Obsolete should be removed from Global Tag (COMCOND-BLKPA-RUN1-06 has been used),
        also for Run1 instance, and removed from joboptions in the new release ????? 
        '''
        folderlist = [ 
            {"nodeFullpath": "/Indet/Align", "isobsolete" : "AddedByHand", "shortName": "COOLOFL_INDET"}, 
            {"nodeFullpath": "/Indet/TrkErrorScaling", "isobsolete" : "AddedByHand", "shortName": "COOLOFL_INDET"},
            {"nodeFullpath": "/Indet/PixelDist", "isobsolete" : "AddedByHand", "shortName": "COOLOFL_INDET"},
            {"nodeFullpath": "/LAR/Identifier/CalibIdMap", "isobsolete" : "AddedByHand", "shortName": "COOLONL_LAR"}, 
            {"nodeFullpath": "/LAR/AlignOfl", "isobsolete" : "Obsolete", "shortName": "COOLOFL_LAR"},
            {"nodeFullpath": "/LAR/Identifier/OnOffIdMap", "isobsolete" : "AddedByHand", "shortName": "COOLONL_LAR"}, 
# Added folders from online TILE since are appearing in athena (but difficult to add them via the athena log)
# but declared as obsolete for the moment
            {"nodeFullpath": "/TILE/OFL01/CALIB/CIS/FIT/NLN", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/CALIB/LAS/FIBER", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/CALIB/LAS/NLN", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/NOISE/AUTOCR", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/NOISE/SAMPLE", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/PULSESHAPE/PHY", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/STATUS/ADC", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"},
            {"nodeFullpath": "/TILE/OFL01/TIME/CHANNELOFFSET/PHY", "isobsolete" : "AddedByHand", "shortName": "COOLONL_TILE"}
        ]
            
        expfolderlist['nodes']= folderlist
        expfolderlist['db']="COMP200"
        
        return expfolderlist


class JBCoolDriver():
    def __init__(self):
    # process command line options
        try:
            self._command = sys.argv[0]
            self.folderpattern = "/"
            self.schemapattern = "ATLAS_COOL"
            self.useSocks = False
            self.dumpCoverageReport = False
            self.t0 = 0
            self.tMax = 'Inf'
            self.iovspan = 'time'
            self.jsondump=False
            self.twikidump=False
            self.skipObsolete = False
            self.skipObsoleteCheck = False
            self.dump=False
            self.nodesource=False
            self.outfilename=''
            longopts=['folder=','schema=','help','socks','out=','report','jsondump','twikidump','skipobsolete=','skipobsoletecheck','t0=','tMax=','iovspan=','updnodes']
            opts,args=getopt.getopt(sys.argv[1:],'',longopts)
            print opts, args
        except getopt.GetoptError,e:
            print e
            self.usage()
            sys.exit(-1)
        self.procopts(opts,args)
        self.execute()

    def usage(self):
        print
        print "usage: jbcooldb.py {<options>} <action> <dbinstance> {<args>}"
        print "Search cool content based on database instance, using args"
        print "Subcommands are:"
        print "jbcooldb.py listSchemas <dbinstance> [COMP200,OFLP200...]"
        print "jbcooldb.py listNodes <dbinstance> {channelid}: use --schema to list only nodes in selected schemas"
        print "jbcooldb.py listChannels <dbinstance> : use --schema and --folder to provide a precise selection"
        print "jbcooldb.py listTags <dbinstance> : use --schema and --folder"
        print "jbcooldb.py listGTags <dbinstance> {globaltag}: use --schema "
        print "jbcooldb.py listStates <state> : list Current or Next states from COMA "
        print "jbcooldb.py traceTags <dbinstance> {globaltag} [full or simple]: use --schema for searching only in selected schemas"
        print "jbcooldb.py backTraceTags <dbinstance> {tag}: use --schema for searching only in selected schemas"
        print "jbcooldb.py tagCoverage <dbinstance> {tagname} [{since} {until} {iovspan}]: use --schema and --folder for a specific schema and folder selection, and --t0 --tMax --iovspan"
        print "jbcooldb.py globaltagCoverage <dbinstance> {tagname}"
        print "jbcooldb.py listIovs <dbinstance> {tagname} [{since} {until} {iovspan}]: use --schema and --folder for a specific schema and folder selection, and --t0 --tMax --iovspan"
        print "jbcooldb.py listPayload <dbinstance> {tagname} {chan} {chansel} {since} {until} {iovspan}: use --schema and --folder for a specific schema and folder selection, and --t0 --tMax --iovspan"
        print "jbcooldb.py listFolderClass: use --schema and --folder for a specific schema and folder selection"
        print "jbcooldb.py MIGSCRIPT <dbinstance> <mergedoutput>: takes in input LISTSCHEMA.json, LISTNODES.json, TRACETAGS.json"
        print "jbcooldb.py Copy <dbinstance> {dest} {tagmatch} {new branch, added after the first / } {dbtech} {passwd}: copy given schema folders tags into dest schema "
        print "Options: "
        print "  --schema={schemaname pattern} "
        print "  --folder={foldername pattern} "
        print "  --socks activate socks proxy on localhost 3129 "
        print "  --out={filename} activate dump on filename "
        print "  --report activate single line report for every folder associated with a global tag; use with action globaltagCoverage"
        print "  --jsondump activate a dump of output lines in json format "
        print "  --skipobsolete={filename.pickle} Skip folders listed in the pickle file from the report; use with action globaltagCoverage"
        print "  --skipobsoletecheck Skip the check using classification from COMA during the migration script procedure"
        print "  --t0={t0 for iovs} "
        print "  --tMax={tMax for iovs} "
        print "  --iovspan={time|date|runlb|timerun|daterun} "
        print "         time: iov in COOL format, allows Inf for infinity"
        print "         date: iov in yyyyMMddHHmmss format"
        print "         runlb: iov in run-lb format, only for run based folders "
        print "         the others make the conversion to run number, does not allow Inf for infinity "
        print "Examples: "
        print "  1) Create migration scripts: jbcooldb.py --socks --t0=222222 --iovspan=run --schema=ATLAS_COOL MIGSCRIPT CONDBR2 True"
        print "  1) List nodes: jbcooldb.py --socks --schema=ATLAS_COOL --folder=/MDT listNodes COMP200"

    def traceTags(self, dbcool):
        tagList = dbcool.getTraceTags(self.args[0], self.schemapattern, self.dbinst)
        msg = ('%4s >>>> %20s %40s %80s') % ('row','shortName','nodeFullpath','tagName')
        print colored.blue(msg)
        i=0
        for atag in tagList:
            if i==0:
                print atag
            i += 1
            msg = ('%4d >>>> %20s %40s %80s') % (i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"])
            print colored.green(msg)

    def createFolderList(self, nodes):
        self.__folderList = []
        for anode in nodes:
            entry = {anode['schemaName'], anode['nodeFullpath'], anode['nodeIovBase'], anode['folderVersioning']}
            self.__folderList.append(entry)
        return self.__folderList
    
    def checkObsolete(self, schema, instance, node):
        for aschema in self.obsoleteFolderList:
            schemaname = aschema['schemaName']
            instancename = aschema['dbName']
            if schema == schemaname and instance == instancename :
                if node in aschema['folders']:
                    return True
        return False

    def initObsoleteFoldersLists(self, obsoletefile):
        input_obsfile = open(obsoletefile, "r")
        self.obsoleteFolderList = pickle.load(input_obsfile)     
        input_obsfile.close()
        
    def createMigrationFolderList(self, foldersList):
        dictmap = []
        dictentry = {}
        
        for node in foldersList:
            iovbase = node['iovbase']
            tagname = node['tagName']
            gtag = node['gtagName']
            schemaname = node['shortName']
            isobs = node['isobsolete']
            version = node['versioning']
            folderinfo = { 'tagName' : tagname, 
                           'gtagName' : gtag, 
                           'lastsince' : None, 
                           'lastsinceStr' : None, 
                           'isobsolete': isobs,
                           'versioning': version,
                           'iovbase' : iovbase }
            row = { 'nodeFullpath' : node["nodeFullpath"], 'info' : folderinfo}
            if ('schemaName' in dictentry) and (schemaname == dictentry['schemaName']):
                fldlist = dictentry['folders']
                fldlist.append(row)
                print 'adding row ' 
                print row 
                print ' folder list ' 
                print fldlist 
                print '    dictentry ' 
                print dictentry
            else:
                if (dictentry != {}):
                    dictmap.append(dictentry)
                dictentry = {}
                folderlist = []
                folderlist.append(row)
                dictentry = { 'schemaName' : schemaname, 'dbName' : node["dbName"], 'folders' : folderlist}
                print 'Create NEW ' 
                print 'adding row ' 
                print row 
                print ' folder list ' 
                print folderlist 
                print '    dictentry ' 
                print dictentry
                # dictmap.append(dictentry)

        dictmap.append(dictentry)
        return dictmap

        

    def procopts(self,opts,args):
        "Process the command line parameters"
        for o,a in opts:
            print 'Analyse options ' + o + ' ' + a
            if (o=='--help'):
                self.usage()
                sys.exit(0)
            if (o=='--folder'):
                self.folderpattern=a
            if (o=='--schema'):
                self.schemapattern=a
            if (o=='--socks'):
                self.useSocks=True
            if (o=='--out'):
                self.dump=True
                self.outfilename=a
            if (o=='--report'):
                self.dumpCoverageReport=True
            if (o=='--jsondump'):
                self.jsondump=True
            if (o=='--twikidump'):
                self.twikidump=True
            if (o=='--skipobsoletecheck'):
                self.skipObsoleteCheck=True
            if (o=='--skipobsolete'):
                self.skipObsolete=True
                self.initObsoleteFoldersLists(a)
            if (o=='--t0'):
                self.t0=a
            if (o=='--tMax'):
                self.tMax=a
            if (o=='--iovspan'):
                self.iovspan=a
            if (o=='--updnodes'):
                self.nodesource = True
                
        print "Using options "+self.schemapattern
        if (len(args)<2):
            raise getopt.GetoptError("Insufficient arguments - need at least 3, or try --help")
        self.action=args[0].upper()
        self.dbinst=args[1]
        self.args=args[2:]
        print self.args
        
    def execute(self):
        "Execute the command for action "+self.action+" and using arguments "
        start = datetime.now()
        print self.args
        print 'Using schema,db ', self.schemapattern, self.dbinst
        dbcool = JBCoolDB(self.schemapattern,self.dbinst, self.useSocks)
        nodeList = dbcool.getFolders()
        self.createFolderList(nodeList)
        if self.dump:
            outfile = open(self.outfilename,"w")
        _dict = {}
        dictarr = []
        dictkeys = []
        dictvalues = []

        if (self.action=='LISTNODES'):
            trace = 'simple'
            secondaryInstance = None
            secondaryFolderList = None
            if (len(self.args)>=2):
                trace = self.args[0]
                secondaryInstance = self.args[1]
                secondaryFolderList = dbcool.getFolders(self.schemapattern,secondaryInstance)
                
            print 'Nodes: '
            dictkeys = ['row','shortName','nodeFullpath','levels','versioning','iovbase','isobsolete','oflmigtype','oflmigname','oflmigdest','key','gtagclass']
            msg = ('%4s >>>> %22s %40s %6s %12s %10s %10s %10s %10s %10s %5s %10s') % ('row','shortName','nodeFullpath','levels','versioning','iovbase','isobsolete','oflmigtype','oflmigname','oflmigdest','key','gtagclass')
            print colored.blue(msg)
            i = 0
            for anode in nodeList:
                if i==0:
                    print anode

                skipit=False
                if trace == 'diff' and secondaryFolderList is not None:
                    for secnode in secondaryFolderList:
                        if anode['schemaName'] == secnode['schemaName'] and anode['nodeFullpath'] == secnode['nodeFullpath']:
                            skipit=True
                            continue

                if skipit:
                    continue
                
                i += 1
                nlevels = len(anode['nodeFullpath'].split('/'))
                isobsolete = 'tobefilled'
                gtagclass = 'tobefilled'
                fldclassList = dbcool.findClassification(anode['schemaName'][len('ATLAS_'):],anode['nodeFullpath'])
                for fld in fldclassList:
                    #print 'Found folder in COMA classification ',fld['nodeFullpath']
                    #print fld
                    if fld['nodeFullpath'] == anode['nodeFullpath'] and fld['cbcDataMc'] == 'data':
                        isobsolete = fld['folderClass']
                        gtagclass = fld['gtagClass']
                oflmigname = None
                oflmigdestname = None
                oflmigkeyname = 'none'
                oflmigtext = 'none'
                oflmigtype = 0
#                 oflmig = re.search('<oflmig>',anode['nodeDescription'])
                if anode['nodeDescription'] is not None:
                    xmldoc = minidom.parseString('<node>'+anode['nodeDescription']+'</node>')
                    oflmigtag = xmldoc.getElementsByTagName('oflmig') 
                    if oflmigtag is not None and len(oflmigtag) > 0:
                        oflmigtext = oflmigtag[0].firstChild.nodeValue
                        oflmigvalue = oflmigtag[0].firstChild.nodeValue.split()
                        if len(oflmigvalue)>0:
                            oflmigtype = oflmigvalue[0]
                        if len(oflmigvalue)>1:
                            oflmigname = oflmigvalue[1]
                        if len(oflmigvalue)>2:
                            oflmigdestname = oflmigvalue[2]
                        #print oflmigvalue
                    if oflmigname is not None and oflmigname == anode['nodeFullpath']:
                        if isobsolete != 'Obsolete':
                            isobsolete = 'Obsolete-autoset-'+isobsolete
                    if oflmigtype is not None and int(oflmigtype) >= 3:
                        if isobsolete != 'Obsolete':
                            isobsolete = 'Obsolete-autosetmigtype-'+isobsolete
#                    else:
#                        isobsolete = ('src=%s dest=%s') % (oflmigname,oflmigdestname)

                    oflmigkey = xmldoc.getElementsByTagName('key') 
                    if oflmigkey is not None and len(oflmigkey) > 0:
                        #print oflmigtag[0].firstChild.nodeValue
                        oflmigkeyvalue = oflmigkey[0].firstChild.nodeValue.split()
                        if len(oflmigkeyvalue)>0:
                            oflmigkeyname = oflmigkeyvalue[0]
                        
                #msg = ('>>>> %20s %50s %2d %250s %100s') % (anode['schemaName'], anode['nodeFullpath'], anode['folderVersioning'],anode['nodeDescription'],anode['folderPayloadSpec'])
                dictvalues = [i,anode['schemaName'][len('ATLAS_'):], anode['nodeFullpath'], nlevels-1, anode['folderVersioning'],anode['nodeIovBase'], isobsolete, oflmigtype, oflmigname, oflmigdestname, oflmigkeyname,gtagclass]
                msg = ('%4d >>>> %20s %60s %2d %2d %12s %10s %3s %40s %40s %40s %15s') % (i,anode['schemaName'][len('ATLAS_'):], anode['nodeFullpath'], nlevels-1, anode['folderVersioning'],anode['nodeIovBase'], isobsolete, oflmigtype, oflmigname, oflmigdestname, oflmigkeyname,gtagclass)
                if self.dump:
                    outfile.write(msg+"\n")
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)

                if anode['folderVersioning']==1:
                    print colored.green(msg)
                else:
                    print colored.red(msg)
                    
            if self.jsondump:
                _dict = { 'db' : self.dbinst, 'nodes' : dictarr}
                #olddata = None
                db = None
                nodes = None
                if os.path.isfile(self.action+'.json'):
                    inpf = open(self.action+'.json','r').read()
                    data = json.loads(inpf)
                    db = data['db']
                    if 'nodes' in data.keys():
                        nodes = data['nodes']
            
                        if _dict['db'] == db:
                # add to nodes only if the DB instance is the same
                            _dict['nodes'].extend(nodes)
                # dump pickle file
                #_dictmap = dbcool.createFolderListDict(dictarr,self.dbinst)
                #dbcool.pickFolderMigrationList(_dictmap, 'picklemigfolderlist.pik')    
            if self.twikidump and self.jsondump:
                twikiheader='| *Migrated* | *Classified* | *Versioning(1=MV)* | *IovBase* | '
                msg = ('TWIKI %s \nTWIKI %s') % (self.schemapattern,twikiheader)
                print msg
                for anode in dictarr:
                    msg = ('TWIKI | %s | %s | %s | %s  |') % (anode['nodeFullpath'], anode['isobsolete'], anode['versioning'],anode['iovbase'])
                    print msg
                
        elif (self.action=='MIGSCRIPT'):
            print 'Migration script creator: '
            jbcmt = JBCoolMigTools('LISTSCHEMAS.json','LISTNODES.json','TRACETAGS.json',self.dbinst)
            if len(self.args) >= 1:
                mergesqlite = (self.args[0] == 'True')
                print 'Apply merging conditions is ',mergesqlite
                jbcmt.mergeOutput(mergesqlite)
            if len(self.args) >=2:
                forcegtag=(self.args[1] == 'force')
                jbcmt.forceGtagOnly(forcegtag)
            
            jbcmt.createInstanceBySchema('LISTSCHEMAS.json')

            minRun2=222222
            mintsobjr2 = dbcool.findNemoRange(minRun2, 'Inf', 'run')
            jbcmt.setMinRun2(mintsobjr2)
            
            since = self.t0
            until = self.tMax
            iovspan = self.iovspan
            timestampobj = dbcool.findNemoRange(since, until, iovspan)
            timestampobj['nocut']=0
            timestampobj['nountilcut']=0
            if self.t0 == 0:
            	timestampobj['nocut']=1
            if self.tMax == 'Inf':
                timestampobj['nountilcut']=1
            if self.skipObsoleteCheck:
            	jbcmt.skipObsoleteCheck(self.skipObsoleteCheck)
            jbcmt.copyInstanceBySchema(timestampobj, 'LISTSCHEMAS.json');
            jbcmt.printlog()

        elif (self.action=='MIGSCRIPT_SINGLESCHEMAFOLDER'):
            print 'Migration script creator: '
            jbcmt = JBCoolMigTools('LISTSCHEMAS.json','LISTNODES.json','LISTTAGS.json',self.dbinst)
            jbcmt.skipGtagCheck(True)
            jbcmt.skipObsoleteCheck(True)
            jbcmt.skipAthenaCheck(True)
            jbcmt.createInstanceBySchema('LISTSCHEMAS.json')

            minRun2=222222
            mintsobjr2 = dbcool.findNemoRange(minRun2, 'Inf', 'run')
            jbcmt.setMinRun2(mintsobjr2)
            
            since = self.t0
            until = self.tMax
            iovspan = self.iovspan
            timestampobj = dbcool.findNemoRange(since, until, iovspan)
            timestampobj['nocut']=0
            timestampobj['nountilcut']=0
            if self.t0 == 0:
            	timestampobj['nocut']=1
            if self.tMax == 'Inf':
                timestampobj['nountilcut']=1
            if self.skipObsoleteCheck:
            	jbcmt.skipObsoleteCheck(self.skipObsoleteCheck)
            jbcmt.copyInstanceBySchema(timestampobj, 'LISTSCHEMAS.json');
            jbcmt.printlog()
            
                        
        elif (self.action=='NEMORANGE' and len(self.args) == 3):
            print 'NEMO time range script: '
            rmin = self.args[0]
            rmax = self.args[1]
            tspan = self.args[2]
            print 'Search time range for ',rmin,rmax,tspan
            timestampobj = dbcool.findNemoRange(rmin, rmax, tspan)
            print timestampobj
            
        elif (self.action=='LISTSCHEMAS'):
            print 'Schemas: '
            schemaList = dbcool.getSchemas()
            dictkeys = ['row','shortName','nfolders']
            msg = ('%4s >>>> %20s %10s') % ('row','shortName','nfolders')
            print colored.blue(msg)
            i = 0
            for aschema in schemaList:
                i+=1
                schemaname = aschema['schemaName']
                shortName = schemaname[len('ATLAS_'):]
                dictvalues = [i,shortName, aschema['nfolders']]
                msg = ('%4d >>>> %20s %5d') % (i,shortName, aschema['nfolders'])
                print colored.green(msg)
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)

            if self.jsondump:
                _dict = { 'db' : self.dbinst, 'schemas' : dictarr}
              
        elif (self.action=='LISTCHANNELS'):
            print 'Channels: '
            chid = None
            if len(self.args)==1:
                chid = self.args[0]
            channelList = dbcool.getChannels(self.schemapattern, self.dbinst, self.folderpattern, chid)
            dictkeys = ['row','channelId','channelName','channelDescription']
            msg = ('%4s >>>> %10s %20s %30s') % ('row','channelId','channelName','channelDescription')
            print colored.blue(msg)
            i = 0
            for achan in channelList:
                i+=1
                dictvalues = [i,achan['channelId'], achan['channelName'],achan['channelDescription']]
                msg = ('%4d >>>> %10d %20s %30s') % (i,achan['channelId'], achan['channelName'],achan['channelDescription'])
                print colored.green(msg)
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)

            if self.jsondump:
                _dict = { 'db' : self.dbinst, 'folder': self.folderpattern, 'schema': self.schemapattern, 'channels' : dictarr}
              
        elif (self.action=='LISTTAGS'):
            print 'Tags: '
            tagList = dbcool.getTags(self.schemapattern, self.dbinst, self.folderpattern)
            dictkeys = ['row','shortName','nodeFullpath','tagName']
            msg = ('%4s >>>> %20s %40s %80s') % ('row','shortName','nodeFullpath','tagName')
            print colored.blue(msg)
            i=0
            for atag in tagList:
                #if i==0:
                #    print atag
                i += 1
                dictvalues = [i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"]]
                msg = ('%4d >>>> %20s %40s %80s') % (i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"])
                print colored.green(msg)
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)
            if self.jsondump:
                _dict = { 'db' : self.dbinst, 'folder': self.folderpattern, 'schema': self.schemapattern, 'tags' : dictarr}
            

        elif (self.action=='IOVSTATS' and len(self.args) == 1):
            print 'Iov stats in folder and tag: '
            tagList = dbcool.getIovStatPerChannel(self.schemapattern, self.dbinst, self.folderpattern,self.args[0])
            dictkeys = ['row','channelId','channelName','niovs','min','max']
            msg = ('%4s >>>> %10s %20s %5s %5s %5s') % ('row','channelId','channelName','niovs','min','max')
            print colored.blue(msg)
            i=0
            for atag in tagList:
                #if i==0:
                #    print atag
                i += 1
                dictvalues = [i,atag['channelId'], atag['channelName'],atag["niovs"],atag["coolIovMinSince"],atag["coolIovMaxUntil"]]
                msg = ('%4d >>>> %10d %20s %5s %10s %10s') % (i,atag['channelId'], atag['channelName'],atag["niovs"],atag["coolIovMinSince"],atag["coolIovMaxUntil"])
                print colored.green(msg)

        elif (self.action=='LISTSTATES'):
            print 'Global Tag State: '
            # dbinst contains the state now
            stateList = dbcool.findGtagState(self.dbinst)
            msg = ('%4s >>>> %20s %20s %20s %30s %20s') % ('row','gtagState','startTime','endTime','tagName','createUser')
            print colored.blue(msg)
            i=0
            for astate in stateList:
                if i==0:
                    print astate
                i += 1
                if 'endTime' not in astate:
		    astate['endTime']='Inf'
                msg = ('%4d >>>> %20s %20s %20s %30s %20s') % (i,astate['gtagState'], astate['startTime'], astate['endTime'], astate["tagName"],astate["createUser"])
                print colored.green(msg)

        elif (self.action=='LISTGTAGS' and len(self.args)==1):
            print 'Global Tags: '
            tagList = dbcool.findGtagList(self.schemapattern, self.dbinst, self.args[0])
            msg = ('%4s >>>> %20s %60s %12s %8s ') % ('row','gtagName','gtagDescription','Lock','nschemas')
            print colored.blue(msg)
            i=0
            for atag in tagList:
                if i==0:
                    print atag
                i += 1
                if not 'gtagDescription' in atag:
                   atag['gtagDescription']='No description'
                msg = ('%4d >>>> %20s %60s %12s %5d ') % (i,atag['gtagName'], atag["gtagDescription"], atag['gtagLockStatus'],atag['nschemas'])
                print colored.green(msg)

        elif (self.action=='TRACETAGS' and len(self.args)>=1):
            trace = 'simple'
            if (len(self.args)>1):
                trace = self.args[1]
            
            if (len(self.args)>2):
                secondgtag = self.args[2]
                schemap=self.schemapattern
                fldpattern = self.folderpattern
                if "ATLAS" in schemap:
                    print 'Removing ATLAS_ from schema pattern'
                    schemap = self.schemapattern[len('ATLAS_'):] 
                if self.folderpattern == "/":
                    fldpattern = "all"
                folderclass=dbcool.findClassification(schemap,fldpattern)

            print 'Trace Tags using global tag association: '
            if trace == "simple":
                tagList = dbcool.getTraceTags(self.args[0], self.schemapattern, self.dbinst)
            elif trace == "full":
                tagList = dbcool.getFullTraceTags(self.args[0], self.schemapattern, self.dbinst)
            elif trace == "diff":
                tagList = dbcool.getTraceTags(self.args[0], self.schemapattern, self.dbinst)
                print 'Loaded list of tags of size ',len(tagList), ' using tag ', self.args[0]
                secondtagList = dbcool.getTraceTags(secondgtag, self.schemapattern, self.dbinst)
                print 'Loaded list of second tags of size ',len(secondtagList), ' using tag ', secondgtag
                for atag in tagList:
                    nodefullpath = atag["nodeFullpath"]
                    origtag = atag["tagName"]
                    msg = 'CHECKDIFF Analyze %s %s ' % (nodefullpath,origtag)
                    print msg
                    atag['class']='tobefilled'
                    for sectag in secondtagList:
#                        print '.'
                        if str(nodefullpath) == str(sectag["nodeFullpath"]):
                            for afld in folderclass:
                                if afld["nodeFullpath"] == nodefullpath:
                                    atag['class']=afld['folderClass']
                            print 'FOUND same path with tag ',sectag["tagName"]
                            if str(origtag) == str(sectag["tagName"]):
                                print 'FOUND same tag ... REMOVE FROM TAGLIST'
                                atag['removeit']=1
                            break
                
            dictkeys = ['row','shortName','nodeFullpath','tagName','gtagName']
            msg = ('%4s >>>> %20s %50s %70s %30s') % ('row','shortName','nodeFullpath','tagName','gtagName')
            print colored.blue(msg)
            i=0
            gtaglist = []
            print 'Dump summary information on a list of size ',len(tagList)
            for atag in tagList:
                if i==0:
                    print atag
                if 'removeit' in atag:
                    urldbclust='\"oracle://%s;schema=%s;dbname=CONDBR2;user=%s_W;password=$OPASS\"' % ('$DBINST',atag["schemaName"],atag["schemaName"])
                    print 'SKIP ', atag["nodeFullpath"], atag["tagName"]
                    if atag['class'].startswith('AllRun'):
                        print 'SETTAG: python coolutils.py --dbtech=oracle --cool=%s --folder=%s --gtag=$gtagname creategtag CONDBR2 None ' % (urldbclust, atag["nodeFullpath"])
                    else:
                        print 'SETTAG NOT ALLRUNS: python coolutils.py --dbtech=oracle --cool=%s --folder=%s --gtag=$gtagname creategtag CONDBR2 None ' % (urldbclust, atag["nodeFullpath"])
                        
                    continue
                else:
                    if trace == 'diff':
                        if atag['class'].startswith('AllRun'):
                            print 'ADD NEW TAG ',atag["nodeFullpath"], atag["tagName"]
                        else:
                            print 'ADD NOT ALLRUNS',atag["nodeFullpath"], atag["tagName"]
                i += 1
                if atag["gtagName"] not in gtaglist:
                    gtaglist.append(atag["gtagName"])
                dictvalues = [i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"], atag["gtagName"]]
                msg = ('%4d >>>> %20s %50s %70s %30s') % (i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"], atag["gtagName"])
                print colored.green(msg)
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)

            if self.jsondump:
                _dict = { 'db' : self.dbinst, self.args[0] : dictarr, 'gtags' : gtaglist}
            
            if os.path.isfile(self.action+'.json') and self.jsondump:
                db = None
                print 'Opening file '+self.action+'.json'
                inpf = open(self.action+'.json','r').read()
                data = json.loads(inpf)
                if 'db' in data.keys():
                    db = data['db']
                    if _dict['db'] == db:
                        # add to tags only if the DB instance is the same
                        data[self.args[0]] = dictarr
                        _dict = data
                if 'gtags' in data.keys():
                    gtags = data['gtags']
                    # Use the gtag in the keys...not the present one
                    if self.args[0] not in gtags:
                        gtags.append(self.args[0])            

        elif (self.action=='BACKTRACETAGS' and len(self.args)==1):
            print 'BackTrace Tags using tag to global tag association: '
            tagList = dbcool.getBackTraceTags(self.args[0], self.schemapattern, self.dbinst)
            dictkeys = ['row','shortName','nodeFullpath','tagName','gtagName','gtagDescription']
            msg = ('%4s %4s >>>> %20s %40s %80s %30s') % ('row','ntag','shortName','nodeFullpath','tagName','gtagName')
            print colored.blue(msg)
            i=0
            prevtagname=None
            itag=0
            for atag in tagList:
                if i==0:
                    print atag
                i += 1
                if prevtagname is None or prevtagname != atag["tagName"]:
                    itag += 1
                    prevtagname = atag["tagName"]
                dictvalues = [i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"], atag["gtagName"], atag["gtagDescription"]]
                msg = ('%4d %4s >>>> %20s %40s %80s %30s') % (i,itag,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"], atag["gtagName"])
                print colored.green(msg)
                if self.jsondump:
                    dictentry = dict(zip(dictkeys, dictvalues))
                    dictarr.append(dictentry)

            if self.jsondump:
                _dict = { 'db' : self.dbinst, self.args[0] : dictarr, 'tag' : [ self.args[0] ]}
            
            if os.path.isfile(self.action+'.json') and self.jsondump:
                db = None
                print 'Opening file '+self.action+'.json'
                inpf = open(self.action+'.json','r').read()
                data = json.loads(inpf)
                if 'db' in data.keys():
                    db = data['db']
                    if _dict['db'] == db:
                        # add to tags only if the DB instance is the same
                        data[self.args[0]] = dictarr
                        _dict = data
                if 'tag' in data.keys():
                    tags = data['tag']
                    # Use the gtag in the keys...not the present one
                    if self.args[0] not in tags:
                        tags.append(self.args[0])            

        
        elif (self.action=='GLOBALTAGREPORT' and len(self.args)==1):
            print 'Global Tags coverage report: '
            gtagsummary = dbcool.findCoverage(self.args[0])
            i=0
            msg = ('%4s >>>> %20s %12s %12s') % ('row','gtagName', "nUpdatedSchemas", "nUpdatedFolders")
            print colored.blue(msg)
            msg = ('%4d >>>> %20s %5d %5d') % (i,gtagsummary["globalTagName"], gtagsummary["nUpdatedSchemas"], gtagsummary["nUpdatedFolders"])
            print colored.green(msg)

        elif (self.action=='GLOBALTAGCOVERAGE' and len(self.args)==1):

            tagList = dbcool.getTraceTags(self.args[0], self.schemapattern, self.dbinst)
            since = self.t0
            until = self.tMax
            iovspan = self.iovspan
            i=0
            for atag in tagList:
                row = ''
                i += 1
                tag = atag["tagName"]
                self.schemapattern = atag["schemaName"]
                selnode = atag["nodeFullpath"]
                
                # Verify with obsolete folder list...
                if self.skipObsolete and self.checkObsolete(self.schemapattern, self.dbinst, selnode):
                    continue
                
                if self.folderpattern == '/':
                    #take all folders
                    selnode = atag["nodeFullpath"]
                    
                elif atag["nodeFullpath"][:len(self.folderpattern)] == self.folderpattern:
                    # use selnode
                    selnode = atag["nodeFullpath"]
                else:
                    selnode = None
                    
                if selnode is None:
                    continue
                
                msg = ('%4d >>>> %20s %40s %80s') % (i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"])
                print colored.green(msg)
                iovbase = ''
                for anode in nodeList:
                    if (anode['schemaName']==self.schemapattern and anode['nodeFullpath']==atag["nodeFullpath"]):
                        iovbase = anode['nodeIovBase']
                        
                if iovbase[:3] == 'run' and iovspan[:4] == 'date':
                    iovspan = 'daterun'
                elif iovbase[:3] != 'run' and iovspan[:3] == 'run':
                    iovspan = 'runtime'

                iovstatchannelList = dbcool.getIovStatPerChannel(self.schemapattern, self.dbinst, selnode, tag)
                nchans = iovstatchannelList.__len__()
                if nchans > 0:
                    achan = iovstatchannelList[0]
                    niovs = achan['niovs']
                    maxsince = achan['maxiovSince']
                    maxuntil = achan['maxiovUntil']
#                    print achan
                else:
                    niovs=-1
                    maxsince='none'
                    maxuntil='none'

                if self.dumpCoverageReport  or self.jsondump:
                    dictkeys = ['db','schema','node','iovbase','tag','nchans','niovs','maxsince','maxuntil']
                    dictvalues = [self.dbinst, self.schemapattern, selnode, iovbase, tag, nchans, niovs, maxsince, maxuntil]
                    row = ("REP: %s %s %s %s %s %d %d %s %s >>> ") % (self.dbinst, self.schemapattern, selnode, iovbase, tag, nchans, niovs, maxsince, maxuntil)
                                    
                iovsummchannelList = dbcool.listIovsSummaryInNodesSchemaTagRangeAsList(self.schemapattern, self.dbinst, selnode, tag,since,until,iovspan)
                msg = ('%4s >>>> %40s %6s %10s %15s %15s') % ('row','node','chanid','totaliovs','minuntil','maxuntil')
                print colored.blue(msg)
                i=0
                channeltotIovs = {}
                for iovchan in iovsummchannelList:
                    i += 1
                    msg = (' %4d >>>> %40s %4d %7d %15s %15s') % (i,iovchan['node'], iovchan['chanId'],iovchan['totalIovs'],iovchan['minuntil'],iovchan['maxuntil'])
                    print colored.blue(msg)
                    iovlist = iovchan['iovlist']
                    chansumm = { 'chanid' : iovchan['chanId'], 'totalIovs' : iovchan['totalIovs'], 'minuntil' : iovchan['minuntil'], 'maxuntil' : iovchan['maxuntil'], 'nchan' : 1 }
                    for aniov in iovlist:
                        msg = ('       --  %22s %22s %7s %10s') % (aniov['sinceCoolStr'], aniov['untilCoolStr'],aniov['ishole'],aniov['niovs'])
                        rangesumm = { 'since' : aniov['sinceCoolStr'], 'until' : aniov['untilCoolStr'], 'ishole' : aniov['ishole'], 'niovs' : aniov['niovs']}
                        if aniov['ishole'] == True:
                            print colored.red(msg)
                        else:
                            print colored.green(msg)
                    chansumm['lastrange'] = rangesumm
                    if not channeltotIovs.has_key(chansumm['totalIovs']) :
                        channeltotIovs[chansumm['totalIovs']] = chansumm
                    else:
                        #increment number of channels with this number of totalIovs
                        channeltotIovs[chansumm['totalIovs']]['nchan'] += 1
                            
                        
                if self.dumpCoverageReport or self.jsondump:
                    allrangeinfo = ''
        
                    for totaliovs in channeltotIovs:
                        chansumm = channeltotIovs[totaliovs]
                        rangeinfo = ('%d %d [%s] [%s] %d ;') % (totaliovs, chansumm['nchan'], chansumm['lastrange']['since'], chansumm['lastrange']['until'], chansumm['lastrange']['niovs'])
                        allrangeinfo += rangeinfo
                    row = (" %s %s") % (row, allrangeinfo)
                    
                    if self.jsondump:
                        dictentry = dict(zip(dictkeys, dictvalues))
                        dictarr.append(dictentry)
                        _dict = { 'db' : self.dbinst, self.args[0] : dictarr, 'gtags' : [ self.args[0] ] }

                    print row

            if os.path.isfile(self.action+'.json'):
                db = None
                inpf = open(self.action+'.json','r').read()
                data = json.loads(inpf)
                db = data['db']
                gtags = data['gtags']
                            
                # Use the gtag in the keys...not the present one
                if self.args[0] not in gtags:
                    gtags.append(self.args[0])
            
                if _dict['db'] == db:
                # add to tags only if the DB instance is the same
                    data[self.args[0]] = dictarr
                    _dict = data


        elif (self.action=='TAGCOVERAGE' and len(self.args)>=1):
            print 'Tag coverage report: '
            tag = self.args[0]
            if len(self.args)==4:
                since = self.args[1]
                until = self.args[2]
                iovspan = self.args[3]
            else:
                since = self.t0
                until = self.tMax
                iovspan = self.iovspan
            iovsummchannelList = dbcool.listIovsSummaryInNodesSchemaTagRangeAsList(self.schemapattern, self.dbinst, self.folderpattern, tag,since,until,iovspan)
            msg = ('%4s >>>> %40s %6s %10s %15s %15s') % ('row','node','chanid','totaliovs','minuntil','maxuntil')
            print colored.blue(msg)
            i=0
            for iovchan in iovsummchannelList:
                i += 1
                msg = (' %4d >>>> %40s %4d %7d %15s %15s') % (i,iovchan['node'], iovchan['chanId'],iovchan['totalIovs'],iovchan['minuntil'],iovchan['maxuntil'])
                print colored.blue(msg)
                iovlist = iovchan['iovlist']
                for aniov in iovlist:
                    msg = ('       --  %22s %22s %7s %10s') % (aniov['sinceCoolStr'], aniov['untilCoolStr'],aniov['ishole'],aniov['niovs'])
                    if aniov['ishole'] == True:
                        print colored.red(msg)
                    else:
                        print colored.green(msg)

        elif (self.action=='LISTIOVS' and len(self.args)>=1):
            print 'List Iovs report: '
            tag = self.args[0]
            if len(self.args)==4:
                since = self.args[1]
                until = self.args[2]
                iovspan = self.args[3]
            else:
                since = self.t0
                until = self.tMax
                iovspan = self.iovspan
            resultset = dbcool.listIovsInNodesSchemaTagRangeAsList(self.schemapattern, self.dbinst, self.folderpattern, tag,since,until,iovspan)
            msg = ('%4s >>>>  %6s %20s %20s %20s') % ('row','chanid','channelname','since','until')
            print colored.blue(msg)
            i=0
            msg='none'
            print resultset
            #iovlist = resultset['iovList']
            iovlist = resultset['iov']
            msg = (' %4d >>>> %40s ') % (i,resultset['nodeFullpath'])
            print colored.blue(msg)
            for aniov in iovlist:
                i += 1
                channelname='none'
                if 'channelName' in aniov:
                    channelname= aniov['channelName']
                msg = ('    --  %22s %22s %20s %20s') % (aniov['channelId'],channelname,aniov['sinceCoolStr'],aniov['untilCoolStr'])
                print colored.green(msg)                

        elif (self.action=='LISTPAYLOAD' and len(self.args)==6):
            print 'Payload report: '
            tag = self.args[0]
            chan = self.args[1]
            chansel = self.args[2]
            since = self.args[3]
            until = self.args[4]
            iovspan = self.args[5]
            iovdataList = dbcool.listPayloadInNodesSchemaTagRangeAsList(self.schemapattern, self.dbinst, self.folderpattern, tag, chan, chansel, since,until,iovspan)
            #msg = ('%4s >>>> %40s %6s %10s %15s %15s') % ('row','node','chanid','totaliovs','minuntil','maxuntil')
            #print colored.blue(msg)
            i=0
            print iovdataList
            iovlist = iovdataList['iovList']
            for iov in iovlist:
                print iov

        elif (self.action=='LISTFOLDERCLASS'):
            print 'Folder classification from COMA: '
            schemap = self.schemapattern 
            print 'Analyse string ',schemap
            if "ATLAS" in schemap:
                print 'Removing ATLAS_ from schema pattern'
                schemap = self.schemapattern[len('ATLAS_'):] 
            if self.folderpattern == "/":
                self.folderpattern = "all"
            fldList = dbcool.findClassification(schemap, self.folderpattern)
            msg = ('%4s >>>> %20s %60s %12s %12s %10s ') % ('row','schemaName','nodeFullpath','folderClass','gtagClass','type')
            print colored.blue(msg)
            i=0
            for afld in fldList:
                if i==0:
                    print afld
                i += 1
                msg = ('%4d >>>> %20s %60s %12s %12s %10s') % (i,afld['ownerName'], afld['nodeFullpath'], afld['folderClass'],afld['gtagClass'],afld['cbcDataMc'])
                print colored.green(msg)

        elif (self.action=='COPY' and len(self.args)>4):
            destschema = self.args[0]
            tagpattern = self.args[1]
            destfldstring = self.args[2]
            dbtech = self.args[3]
            passwd = self.args[4]
            
            print 'Copy Tags: '
            tagList = dbcool.getTags(self.schemapattern, self.dbinst, self.folderpattern)
            msg = ('%4s >>>> %20s %40s %80s') % ('row','shortName','nodeFullpath','tagName')
            print colored.blue(msg)
            i=0
            taglistforcopy = []
            for atag in tagList:
                #if i==0:
                #    print atag
                i += 1
                msg = ('%4d >>>> %20s %40s %80s') % (i,atag["schemaName"][len('ATLAS_'):], atag["nodeFullpath"], atag["tagName"])
                print colored.green(msg)
                if re.search(tagpattern,atag["tagName"]):
                    tagList = dbcool.getBackTraceTags(atag["tagName"], atag["schemaName"], self.dbinst)
                    if len(tagList) == 0:
                        print colored.red(("Skip tag %s because it is not associated to global tags ") % (atag["tagName"]))
                    else:
                        print colored.blue(("Add tag %s to list for copy ") % (atag["tagName"]))
                        taglistforcopy.append(atag)
                else:
                    print colored.red(("%s does not match %s")%(atag["tagName"], tagpattern))

            jbcmt = JBCoolCopyTools(self.schemapattern, destschema, taglistforcopy, destfldstring)
            try:
                jbcmt.createScript(dbtech,passwd)
            except NameError as e:
                print e
        else:
            # unknown action
            self.usage()
            sys.exit(0)
            

        if self.dump:
            outfile.close()
        if self.jsondump:
                
            j = json.dumps(_dict, indent=4)
            f = open(self.action+'.json','w')
            print >> f, j
            
#            if olddata is not None:
#                print >> f, olddata
            f.close()

        end = datetime.now()
        timedelta = end-start
        print start, end, timedelta

if __name__ == '__main__':

    JBCoolDriver()
    

