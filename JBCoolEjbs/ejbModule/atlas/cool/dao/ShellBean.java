package atlas.cool.dao;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import javax.ejb.Local;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Session Bean implementation class ShellBean
 */
@Stateless
@Local(ShellDAO.class)
@LocalBean
public class ShellBean implements ShellDAO {

	@Inject
	protected Logger log;

    /**
     * Default constructor. 
     */
    public ShellBean() {
        // TODO Auto-generated constructor stub
    }

	/**
     * @see ShellDAO#executeCommand(String)
     */
    public String executeCommand(String command) {
    	StringBuffer output = new StringBuffer();
    	 
		Process p;
		try {
			log.info("execute command "+command);
			p = Runtime.getRuntime().exec(command);
			p.waitFor();
			BufferedReader reader = 
                            new BufferedReader(new InputStreamReader(p.getInputStream()));
 
                        String line = "";			
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
			}
			log.info("output  \n"+output.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
 
		return output.toString();
	}

}
