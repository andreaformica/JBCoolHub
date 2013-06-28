/**
 * 
 */
package atlas.client.main;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import atlas.cool.rest.model.CoolIovType;
import atlas.cool.rest.model.NodeType;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author formica
 *
 */
public class TestJsonClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		 try {
			 
				URL url = new URL("http://voatlas135.cern.ch:8080/JBRestCool/rest/plsqlcooljson/ATLAS_COOLOFL_RPC/OFLP200/RPC/DQMF/ELEMENT_STATUS/fld/RPCDQMFElementStatus_2012_Jaunuary_26/tag/1650186752/chanid/0/Inf/time/data/list");
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				conn.setRequestProperty("Accept", "application/json");
		 
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}
		 
/*				BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));
				String output;
				System.out.println("Output from Server .... \n");
				while ((output = br.readLine()) != null) {
					System.out.println(output);
				}

*/				ObjectMapper mapper = new ObjectMapper();
				NodeType node = mapper.readValue(conn.getInputStream(),NodeType.class);
				System.out.println("Found node "+node.getNodeFullpath());
				List<CoolIovType> cooliovs = node.getIovList();
				for (CoolIovType coolIovType : cooliovs) {
					System.out.println("Found iov "+coolIovType.getChannelName()+" "+coolIovType.getSinceCoolStr()+" "+coolIovType.getUntilCoolStr());
					Map<String,String> payloadmap = coolIovType.getPayload();
					for (String key : payloadmap.keySet()) {
						String val = payloadmap.get(key);
						System.out.println("Found "+key+" "+val);
					}
				}
				conn.disconnect();
		 
			  } catch (MalformedURLException e) {
		 
				e.printStackTrace();
		 
			  } catch (IOException e) {
		 
				e.printStackTrace();
		 
			  }
		 
		 	}

}
