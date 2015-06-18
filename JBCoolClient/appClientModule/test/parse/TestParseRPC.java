/**
 * 
 */
package test.parse;

import java.util.Map;

import atlas.cool.payload.plugin.ClobParser;
import atlas.cool.payload.plugin.RpcClobParser;

/**
 * @author formica
 *
 */
public class TestParseRPC {

	
	public String rpcPanelResclob = "2 14291 64 0.886 0.002 0.897 0.002 10.33 1.469 -8.93 2.420 21.72 7.209 0.259 0.001 0.105 0.001 1.505 0.006 0.629 0.293";
	public String rpcStripStatusclob = "5 93.8 21.0|5 93.8 19.1|5 93.8 20.4|5 93.8 21.8|5 93.8 19.6|5 96.9 17.7|5 96.9 21.9|5 96.9 20.5|5 93.8 18.4|5 96.9 20.5|5 93.8 20.3|5 93.8 22.2|5 93.8 21.4|5 93.8 19.4|5 93.8 21.6|5 93.8 18.1|5 93.8 20.0|5 93.8 19.2|5 93.8 16.4|5 93.8 19.8|5 93.8 19.5|5 96.9 18.5|5 96.9 22.1|5 96.9 20.8|5 96.9 21.4|5 96.9 21.3|5 96.9 20.5|5 93.8 23.2|5 93.8 19.0|5 96.9 21.4|5 96.9 21.4|5 93.8 20.1|5 90.6 18.5|5 90.6 19.5|5 90.6 18.5|5 90.6 19.2|5 90.6 20.3|5 90.6 20.3|5 90.6 20.2|5 90.6 19.6|5 90.6 20.6|5 90.6 21.8|5 90.6 19.8|5 90.6 21.4|5 93.8 20.1|5 93.8 19.1|5 93.8 20.9|5 90.6 18.4|5 90.6 18.8|5 90.6 19.5|5 90.6 19.1|5 90.6 21.5|5 90.6 18.7|5 93.8 21.1|5 93.8 19.6|5 90.6 20.6|5 93.8 21.5|5 93.8 19.8|5 93.8 20.0|5 93.8 20.8|5 90.6 19.5|5 93.8 20.2|5 90.6 21.0|5 90.6 20.5|";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		TestParseRPC rpcparsetest = new TestParseRPC();
		ClobParser parser = new RpcClobParser();
		
		Map<Integer, Map<String,Float>> parsedMap = (Map<Integer, Map<String, Float>>) parser.parseClob("PanelRes", rpcparsetest.rpcPanelResclob);
		for (Integer row : parsedMap.keySet()) {
			Map<String,Float> content = parsedMap.get(row);
			System.out.println("Parsed line "+row+" content "+content.get("DBversion")+" "+content.get("nTracks"));		
		}
		Map<Integer, Map<String,Float>> parsedStripMap = (Map<Integer, Map<String, Float>>) parser.parseClob("StripStatus", rpcparsetest.rpcStripStatusclob);
		for (Integer row : parsedStripMap.keySet()) {
			Map<String,Float> content = parsedStripMap.get(row);
			System.out.println("Parsed line "+row+" content "+content.get("Status")+" "+content.get("Time"));		
		}
	}

}
