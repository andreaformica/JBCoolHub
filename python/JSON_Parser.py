###############################################
#author Ivan Yeletskikh;  ivaneleckih@jinr.ru
###############################################

import ROOT
import array
import numpy
#from ctypes import addressof
from array import array
#from ROOT import *
from ROOT import vector
#from ROOT import gROOT, TCanvas, TF1, TFile
import logging 

#logging.basicConfig(filename='log.out', filemode='w', level=logging.DEBUG, format='%(asctime)s %(message)s')
#logging.basicConfig(filemode='w')
#logging.basicConfig(level=logging.DEBUG)
#logging.basicConfig(format='%(asctime)s %(message)s')


def Draw_var(ttree, var_name):  
	canv1 = ROOT.TCanvas(var_name, var_name, 800, 600);
	ttree.Draw(var_name+"[0]")
	#canv1.Print(var_name + ".gif")
	#ttree.Print()
	return canv1


###################################################################################################
###################################################################################################
class Parser:

	
	def GetKeyTypeValueLists(self, indict, name_prefix, allkeys_list, allvalues_list, alltypes_list):
		keys_list = self.Keys(indict)
		values_list = self.Values(indict)		
		type_list = self.Types(indict)
		name_prefix2 = name_prefix + '_'
		for k in range(len(keys_list)):
			if type_list[k]=='dict': 
				exec ('self.GetKeyTypeValueLists(indict["' +  keys_list[k] + '"]' +', name_prefix2 +' + '"' + keys_list[k] + '", ' + 'allkeys_list, allvalues_list, alltypes_list)' )
				continue
			if type_list[k]=='list':
				for j in range(len(values_list[k])):				
					exec ('self.GetKeyTypeValueLists(indict["' +  keys_list[k] + '"][j]' +', name_prefix2 +' + '"' + keys_list[k] + '_' + str(j) + '", ' + 'allkeys_list, allvalues_list, alltypes_list)' )
				continue
			allkeys_list.append(name_prefix + '_' + keys_list[k])
			allvalues_list.append(values_list[k])
			alltypes_list.append(type_list[k])


	def GetKeyTypeValuePatterns(self, indict, prefix_list, allkeys_list, alltypes_list):
	#### this functions reads the JSON string header and saves pathes to variables in 'allkeys_list',
	#### which is 2-dimensional list - first dimension counts variables, second dimesion counts path keys, e,g. ['payloadObj', 'PanelRes', 'fracCs1'] is the n-th element in allkeys_list;
	#### the alltypes_list reads values from the header that contain types of variables in main JSON string;
		keys_list = self.Keys(indict)
		values_list = self.Values(indict)		
		type_list = self.Types(indict)
		for k in range(len(keys_list)):
			prefix_list2 = prefix_list[:]
			prefix_list2.append(keys_list[k])
			if type_list[k]=='dict':
				exec ('self.GetKeyTypeValuePatterns(indict["' +  keys_list[k] + '"]' +', prefix_list2, ' + 'allkeys_list, alltypes_list)' )
				continue
			if type_list[k]=='list':
				prefix_list2.append('0')
				for j in range(len(values_list[k])):				
					exec ('self.GetKeyTypeValuePatterns(indict["' +  keys_list[k] + '"][j]' +', prefix_list2, ' + 'allkeys_list, alltypes_list)' )
				continue
			#prefix_list2.append(keys_list[k])
			allkeys_list.append(prefix_list2)
			alltypes_list.append(values_list[k])
		

	def GetValueViaPath(self, indict, path_list, n):
		path_str = ''
		for j in range(len(path_list)):
			if path_list[j] == '0': path_str += '[n]'
			else: path_str += '[' + '"' + path_list[j] + '"' + ']'
		try: 
			exec('out = indict' + path_str)
		except KeyError:
			out = 'KeyError'
			logging.error("There is no " + path_str + " in main JSON, though it is declared in header!")
		return out

	def GetListSizeViaPath(self, indict, path_list, flag):
		path_str = ''
		level = 0
		for j in range(len(path_list)):
			if path_list[j] == '0': 
				level += 1
				if level == flag: break
			else: path_str += '[' + '"' + path_list[j] + '"' + ']'
		exec('out = len(indict' + path_str + ')' )
		return out

	def GetROOTbranchName(self, prefix, path_list):
		path_str = prefix[:]
		for j in range(len(path_list)):
			if path_list[j] != '0': path_str += "_" + path_list[j]
		return path_str

	
	def GetValueLists(self, indict, name_prefix, allkeys_list, allvalues_list, alltypes_list):
		keys_list = self.Keys(indict)
		values_list = self.Values(indict)		
		type_list = self.Types(indict)
		name_prefix2 = name_prefix + '_'
		for k in range(len(keys_list)):
			if type_list[k]=='dict': 
				exec ('self.GetValueLists(indict["' +  keys_list[k] + '"]' +', name_prefix2 +' + '"' + keys_list[k] + '", ' + 'allkeys_list, allvalues_list, alltypes_list)' )
				continue
			if type_list[k]=='list':
				for j in range(len(values_list[k])):				
					exec ('self.GetValueLists(indict["' +  keys_list[k] + '"][j]' +', name_prefix2 +' + '"' + keys_list[k] + '_' + str(j) + '", ' + 'allkeys_list, allvalues_list, alltypes_list)' )
				continue
			#allkeys_list.append(name_prefix + '_' + keys_list[k])
			allvalues_list.append(values_list[k])
			#alltypes_list.append(type_list[k])


	def GetRootTree(self, newtree, instring, name_prefix, header_pass):
		logging.info("Launching root constructing function 'GetRootTree'")
		#logging.warning("WARNING")
		allkeys_list = []
		allvalues_list = []
		alltypes_list = []
		name_prefix = []
		try: 
			header = instring['parserHeader']['iovList'][0]
			#exec("header = instring['parserHeader']['" +  header_pass + "'][0]")
		except KeyError:
			logging.error("ERROR! No header string in the input data! exit.")
			return 0
		self.GetKeyTypeValuePatterns(header, name_prefix, allkeys_list, alltypes_list)
		
		type_ptrn = ''
		logging.info("List of branches created and their types:\n")
		for m in range(len(allkeys_list)):
			varname = self.GetROOTbranchName("iovList", allkeys_list[m])
			flag = 0
			for k in range(len(allkeys_list[m])):
				if allkeys_list[m][k] == '0': flag += 1
			if flag == 0:
				logging.info(varname + ': ' + alltypes_list[m])
				if alltypes_list[m]=='Float' or alltypes_list[m]=='float': 
					exec (varname + " = array('d',[0.0])")
					type_ptrn = '/D'
					#exec('newtree.Branch("' + varname +'", ' + varname + ')'  )
					exec('newtree.Branch("' + varname +'", ' + varname + ', "' + varname + type_ptrn + '")'  )
				if alltypes_list[m]=='Integer' or alltypes_list[m]=='int' or alltypes_list[m]=='integer': 
					exec (varname + " = array('l',[0])")
					type_ptrn = '/I'
					exec('newtree.Branch("' + varname +'", ' + varname + ', "' + varname + type_ptrn + '")'  )
				if alltypes_list[m]=='Long' or alltypes_list[m]=='long': 
					exec (varname + " = array('d',[0.0])")
					type_ptrn = '/D'
					exec('newtree.Branch("' + varname +'", ' + varname + ', "' + varname + type_ptrn + '")'  )
				if alltypes_list[m]=='String' or alltypes_list[m]=='unicode' or alltypes_list[m]=='string': 
					exec (varname + " = ROOT.std.string()")
					exec('newtree.Branch("' + varname +'", ' + varname + ')'  )
				#exec('newtree.Branch("' + varname +'", ' + varname + ')'  )
			if flag == 1:
				logging.info(varname + ': vector<' + alltypes_list[m] +'>')
				if alltypes_list[m]=='Float' or alltypes_list[m]=='float': 
					exec (varname + " = ROOT.vector(float)()")
				if alltypes_list[m]=='Integer' or alltypes_list[m]=='int' or alltypes_list[m]=='integer': 
					exec (varname + " = ROOT.vector(int)()")
				if alltypes_list[m]=='Long' or alltypes_list[m]=='long': 
					exec (varname + " = ROOT.vector(long)()")
#				#if alltypes_list[m]=='String' or alltypes_list[m]=='unicode' or alltypes_list[m]=='string': 
#				#	exec (varname + " = ROOT.vector(float)()")
				exec('newtree.Branch("' + varname +'", ' + varname + ')'  )

		for n in range(len(instring['iovList'])):
			#print n  
			for m in range(len(allkeys_list)):
				flag = 0
				for k in range(len(allkeys_list[m])): 
					if allkeys_list[m][k] == '0': flag += 1
				varname = self.GetROOTbranchName("iovList", allkeys_list[m])
				if flag == 0:
					varvalue = self.GetValueViaPath( instring['iovList'][n], allkeys_list[m], 0)
					if varvalue == 'KeyError': continue
					if(alltypes_list[m] == 'unicode' or alltypes_list[m] == 'String' or alltypes_list[m] == 'String'):
						if(varvalue == None): exec(varname + '.replace(0, ROOT.std.string.npos, "None")')
						else: 
							exec(varname + '.replace(0, ROOT.std.string.npos, "' + str(varvalue) + '")')
							#exec('print ' + varname )
					if(alltypes_list[m] == 'int' or alltypes_list[m] == 'long' or alltypes_list[m] == 'Integer' or alltypes_list[m] == 'Long' or alltypes_list[m] == 'integer'):
						if(varvalue == None): exec(varname + '[0] = 0' )
						else: exec(varname + '[0] = ' + str(varvalue))
					if(alltypes_list[m] == 'float' or alltypes_list[m] == 'Float'):
						if(varvalue == None): exec(varname + '[0] = 0.0' )
						else: exec(varname + '[0] = ' + str(varvalue))
				if flag == 1:
					exec(varname + '.clear()')
					vec_size = self.GetListSizeViaPath( instring['iovList'][n], allkeys_list[m], 1)
					#exec( 'print varname, type(' + varname +')' )
					for j in range(vec_size):
						varvalue = self.GetValueViaPath( instring['iovList'][n], allkeys_list[m], j)
						if varvalue == 'KeyError': break
						#print varvalue
						#if(alltypes_list[m] == 'unicode' or alltypes_list[m] == 'String'):
						#	if(varvalue == None): exec(varname + '.push_back(u"None")' )
						#	else: exec(varname + '.push_back(u"' + str(varvalue) + '")' )
						if(alltypes_list[m] == 'int' or alltypes_list[m] == 'long' or alltypes_list[m] == 'Integer' or alltypes_list[m] == 'Long' or alltypes_list[m] == 'integer'):
							if(varvalue == None): exec(varname + '.push_back(0)' )
							else: exec(varname + '.push_back(' + str(varvalue) + ')')
						if(alltypes_list[m] == 'float' or alltypes_list[m] == 'Float'):
							if(varvalue == None): exec(varname + '.push_back(0.0)' )
							else: exec(varname + '.push_back(' + str(varvalue) + ')')
				#print allkeys_list[m], " = ", allvalues_list[m], "type = ", alltypes_list[m]
				#if m == (len(allkeys_list)-1): newtree.Fill()  ## filling in the last loop due to variables visibility behavior
			newtree.Fill()
		logging.info("'GetRootTree' is completed")



	def GetHistograms(self, histo_list, instring, name_prefix, header_pass):
		allkeys_list = []
		allvalues_list = []
		alltypes_list = []
		name_prefix = []
		logging.info("'GetHistograms' starts")
		try: 
			header = instring['parserHeader']['iovList'][0]
		except KeyError:
			logging.error("ERROR! No header string in the input data! exit.")
			return 0
		self.GetKeyTypeValuePatterns(header, name_prefix, allkeys_list, alltypes_list)
		
		type_ptrn = ''
		for m in range(len(allkeys_list)):
			varname = self.GetROOTbranchName("iovList", allkeys_list[m])
			flag = 0
			for k in range(len(allkeys_list[m])): 
				if allkeys_list[m][k] == '0': flag += 1
			if flag == 0:
				if alltypes_list[m]=='float' or alltypes_list[m]=='Float': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
				elif alltypes_list[m]=='int' or alltypes_list[m]=='Integer' or alltypes_list[m]=='integer': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
				elif alltypes_list[m]=='long' or alltypes_list[m]=='Long': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
				else: continue
			if flag == 1:
				if alltypes_list[m]=='float' or alltypes_list[m]=='Float': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
					#print 'hist_'+varname
				elif alltypes_list[m]=='int' or alltypes_list[m]=='Integer' or alltypes_list[m]=='integer': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
				elif alltypes_list[m]=='long' or alltypes_list[m]=='Long': 
					exec ('hist_' + varname + ' = ROOT.TH1F("'+ varname + '", "' + varname +  '", 100, 0, 0)' )
				else: continue
			print varname, "start filling!"
			for n in range(len(instring['iovList'])):
				if flag == 0:
					if alltypes_list[m]=='float' or alltypes_list[m]=='int' or alltypes_list[m]=='long' or alltypes_list[m]=='Float' or alltypes_list[m]=='Integer' or alltypes_list[m]=='Long' or alltypes_list[m]=='integer':
						varvalue = self.GetValueViaPath( instring['iovList'][n], allkeys_list[m], 0)
						if varvalue == 'KeyError': continue
						if(varvalue != None): exec('hist_' + varname + '.Fill(' + str(varvalue) + ')' )
				if flag == 1:
					vec_size = self.GetListSizeViaPath( instring['iovList'][n], allkeys_list[m], 1)
					for j in range(vec_size):
						if alltypes_list[m]=='float' or alltypes_list[m]=='int' or alltypes_list[m]=='long' or alltypes_list[m]=='Float' or alltypes_list[m]=='Integer' or alltypes_list[m]=='Long' or alltypes_list[m]=='integer':
							varvalue = self.GetValueViaPath( instring['iovList'][n], allkeys_list[m], j)
							if varvalue == 'KeyError': break
							if(varvalue != None): exec('hist_' + varname + '.Fill(' + str(varvalue) + ')' )
			exec('histo_list.append(hist_' + varname +')')
		logging.info("'GetHistograms' is completed")


	def GetRootTree_old(self, newtree, inlist, name_prefix):
		allkeys_list = []
		allvalues_list = []
		alltypes_list = []
		#self.GetKeyTypeValueLists(inlist, name_prefix, allkeys_list, allvalues_list, alltypes_list)		
		self.GetKeyTypeValueLists(inlist[0], name_prefix, allkeys_list, allvalues_list, alltypes_list)
		print allkeys_list
		#print allkeys_list
		type_ptrn = ''
		for m in range(len(allkeys_list)):
			if alltypes_list[m]=='float' or alltypes_list[m]=='none': 
				exec (allkeys_list[m] + " = array('f',[0.0])")
				type_ptrn = '/F'
			if alltypes_list[m]=='int': 
				exec (allkeys_list[m] + " = array('l',[0])")
				type_ptrn = '/I'
			if alltypes_list[m]=='long': 
				exec (allkeys_list[m] + " = array('f',[0.0])")
				type_ptrn = '/F'
			if alltypes_list[m]=='unicode' or alltypes_list[m]=='str': 
				exec (allkeys_list[m] + " = u'str0'")
				#exec (allkeys_list[m] + " = array('u')")
				type_ptrn = '/C'
			exec('newtree.Branch("' + allkeys_list[m] +'", ' + allkeys_list[m] + ', "' + allkeys_list[m] + type_ptrn + '")'  )
		for n in range(200):  
		#for n in range(1000, len(inlist)):
			del allvalues_list[:]
			self.GetValueLists(inlist[n], name_prefix, allkeys_list, allvalues_list, alltypes_list)
			for m in range(len(allkeys_list)):
				exec ('global ' + allkeys_list[m])
				if(alltypes_list[m] == 'none'): exec(allkeys_list[m] + '[0] = 0.0')
				elif(alltypes_list[m] == 'unicode' or alltypes_list[m] == 'str'):
					print n, allkeys_list[m], " = ", allvalues_list[m]
					try: exec(allkeys_list[m] + '= u"' + str(allvalues_list[m]) + '"' )
					except TypeError: print ("Type error when assigning value to key")
				else: exec(allkeys_list[m] + '[0] = ' + str(allvalues_list[m]))
				#print allkeys_list[m], " = ", allvalues_list[m], "type = ", alltypes_list[m]
				#if m == (len(allkeys_list)-1): newtree.Fill()  ## filling in the last loop due to variables visibility behavior
			newtree.Fill()
		

	def Keys(self, indict):
		keys_list = indict.keys()
		return keys_list
	
		
	def Values(self, indict):
		values_list = indict.values()
		return values_list


			
	def Types(self, indict):
		t_list = []
		v_list = self.Values(indict)		
		for i in range(len(v_list)):
			#print type(values_list[i])			
			if type(v_list[i]) is float: st = "float"			
			#elif isinstance(list[i], double): st = "/D"
			elif type(v_list[i]) is int: st = "int"
			elif type(v_list[i]) is long: st = "long"
			elif type(v_list[i]) is str: st = "str"
			elif type(v_list[i]) is unicode: st = "unicode"
			elif type(v_list[i]) is dict: st = "dict"
			elif isinstance(v_list[i], list): st = "list"
			elif type(v_list[i]) is list: st = "list"
			else:  st = "none" #continue
			t_list.append(st)	
		
		return t_list

