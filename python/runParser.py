###############################################
#author Ivan Yeletskikh;  ivaneleckih@jinr.ru
###############################################

#import encoding.aliases
import json
import ROOT
import JSON_Parser
import array
import urllib
from optparse import OptionParser
import logging


if __name__ == '__main__':
	optparser = OptionParser()
	optparser.add_option("--url", action="store", type="string", dest="restful_url", default="http://voatlas135.cern.ch:8080/JBRestCool/rest/plsqlcooljson/ATLAS_COOLOFL_RPC/COMP200/RPC/DQMF/ELEMENT_STATUS/fld/RPCDQMFElementStatus_2012_Jaunuary_26/tag/1650186752/chanid/0/Inf/time/data/list")
	optparser.add_option("--tree_name", action="store", type="string", dest="tree_name", default="out_tree")
	optparser.add_option("--file_name", action="store", type="string", dest="file_name", default="out_file.root")
	optparser.add_option("--header_pass", action="store", type="string", dest="header_pass", default="iovList")
	optparser.add_option("--tree", action="store_true", dest="save_tree", default=False)
	optparser.add_option("--hist", action="store_true", dest="save_histograms", default=False)
	(options, args) = optparser.parse_args()


def main():
	logging.basicConfig(filename='log.out', filemode='w', level=logging.DEBUG, format='%(asctime)s %(message)s')

if __name__ == '__main__':
	main()

#print args.RESTful_URL
#print args.RESTful_URL[0]

logging.info("Start loading data via RESTful")
logging.info(options.restful_url)
print "Using RESTful URL: ", options.restful_url
#f = open('rpcClobOneChannel_new.json', 'r')
#f = open('test01.json', 'r')
f = urllib.urlopen(options.restful_url)      ####### uncomment this to use data from URL
#f = urllib.urlopen('http://voatlas135.cern.ch:8080/JBRestCool/rest/plsqlcooljson/ATLAS_COOLOFL_RPC/COMP200/RPC/DQMF/ELEMENT_STATUS/fld/RPCDQMFElementStatus_Run1_UPD4-01/tag/1650186752/chanid/0/Inf/time/data/list')
#f = open('rpcClobAll_new.json', 'r')
#f = open('DCS.json', 'r')
data01 = f.read()
#print data01
logging.info("Loading completed")
s01 = json.loads(data01)
#print s01
f.close()
logging.info("Parsing completed")


#f2 = urllib.urlopen('http://voatlas135.cern.ch:8080/JBRestCool/rest/plsqlcooljson/ATLAS_COOLOFL_RPC/COMP200/RPC/DQMF/ELEMENT_STATUS/fld/RPCDQMFElementStatus_Run1_UPD4-01/tag/1650186752/chanid/0/Inf/time/data/list')
#data01 = f.read()
#s03 = json.loads(data01)
#print s03
#f2.close()


#print data01
#print s01


logging.info("Create output root file...")
logging.info(options.file_name)
print "Using output file name: ", options.file_name
file00 = ROOT.TFile(options.file_name, "recreate")
file00.cd()
PRP = JSON_Parser.Parser()

if(options.save_tree):
	newtree = ROOT.TTree(options.tree_name, options.tree_name)
	logging.info("Start creating and filling a root tree...")
	PRP.GetRootTree(newtree, s01, options.tree_name, options.header_pass)
	newtree.Write()
	logging.info("Writing tree to file...")

if(options.save_histograms):
	hist_list = []
	PRP.GetHistograms(hist_list, s01, "RPC", options.header_pass)
	logging.info("Writing histograms to file...")
	for it in range(len(hist_list)):
		hist_list[it].Write()

#c1 = JSON_Parser.Draw_var(newtree, "iovList_payloadObj_PanelRes_fracCs2")
#c1.Write()
file00.Close()
logging.info("Writing to file completed.")
print "Completed."
