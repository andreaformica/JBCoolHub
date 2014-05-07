/**
 * 
 */
package atlas.cool.rest.post;

import java.util.logging.Logger;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.Session;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.annotations.Form;
import org.jfree.util.Log;

import atlas.cool.dao.CoolDAO;
import atlas.cool.dao.ShellDAO;
import atlas.cool.dao.mdb.GtagCoverage;

/**
 * @author formica
 * 
 */
@Path("/post")
public class CoolRestPostService {

	@Resource(mappedName = "queue/gtagCoverageQueue")
	private Queue queueCoverage;

	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory cf;

	@Inject
	protected Logger log;

	@Inject
	protected ShellDAO shell;

	/**
	 * Call via JMS asynchronous method for global tag coverage
	 * @param input
	 * @return
	 */
	@POST
	@Path("/gtag/coverage")
	public Response checkGlobalTagCoverage(@Form final GlobalTagForm form) {

		String globaltagname = form.getGlobalTagName();
		Connection connection = null;
		Session session = null;
		try {
			connection = cf.createConnection();
			session = connection.createSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
			ObjectMessage mess = session.createObjectMessage();
			mess.setObject(new GtagCoverage(globaltagname, null));
			mess.setStringProperty("checkType", form.getCheckType());
			mess.setStringProperty("db", form.getDb());
			MessageProducer producer = session.createProducer(queueCoverage);
			producer.send(mess);

			return Response.status(200)
					.entity("Sumbitted request for coverage check on : " + globaltagname)
					.build();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				session.close();
				connection.close();
			} catch (JMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return Response.status(401)
				.entity("Error occurred when submitting global tag : " + globaltagname + " for coverage checks ")
				.build();

	}

	@POST
	@Path("/tree")
	public Response getRootTreeForPayload(@Form final RootTreeForm form) {
		String url = form.getUrl();
		String outputfname = "test.root";
		try {
			String command = ("/tmp/start.sh "+url+" "+outputfname);
			log.info("Launch shell command "+command);
			shell.executeCommand(command);

			return Response.status(200)
					.entity("Sumbitted request for tree producer using url : " + url)
					.build();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Response.status(401)
				.entity("Error occurred when submitting tree producer for : " + url + " !")
				.build();
		
	}
}
