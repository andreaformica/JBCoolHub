package atlas.client.main;
import java.lang.reflect.Method;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import atlas.cool.dao.CondToolsBean;
import atlas.cool.dao.CoolBean;
import atlas.cool.dao.remote.CondToolsDAORemote;
import atlas.cool.dao.remote.CoolDAORemote;
import atlas.cool.exceptions.CoolIOException;


public class TestClient {
	

	  public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			Method im[] = atlas.cool.dao.remote.CoolDAORemote.class.getDeclaredMethods();
			System.out.println("number of methods in cooldaoremote "+im.length);
			for (int i=0; i< im.length ; i++) {
				System.out.println("interface method "+im[i].getName());
			}
			im = atlas.cool.dao.CoolDAO.class.getDeclaredMethods();
			System.out.println("number of methods in cooldao "+im.length);
			for (int i=0; i< im.length ; i++) {
				System.out.println("interface method "+im[i].getName());
			}
			System.out.println("Loading dao class...");
			CoolDAORemote cooldao = lookupCoolDAO();
			System.out.println("Using cooldaoremote ... "+cooldao.toString());
			Method mths[] = cooldao.getClass().getDeclaredMethods();
			for (int i=0; i< mths.length ; i++) {
				System.out.println("method "+mths[i].getName());
			}
			String msg = cooldao.getInfo("caller is JBCoolClient.Main");
			System.out.println("Returned message \n"+msg);
			
			CondToolsDAORemote condtoolsdao = lookupCondToolsDAO();
			System.out.println("Using CondToolsDAORemote ... "+condtoolsdao.toString());
			Method mths1[] = condtoolsdao.getClass().getDeclaredMethods();
			for (int i=0; i< mths1.length ; i++) {
				System.out.println("method "+mths1[i].getName());
			}
//			condtoolsdao.insertCoolIovRanges("ATLAS_COOLOFL_MUONALIGN", "COMP200", "/MUONALIGN/MDT/ENDCAP/SIDEC", "MuonAlignMDTEndCapCAlign-ECC_ROLLING_2012_03_01-UPD4-03");
//			condtoolsdao.insertCoolIovRanges("ATLAS_COOLOFL_MUONALIGN", "COMP200", "/MUONALIGN/MDT/ENDCAP/SIDEA", "MuonAlignMDTEndCapAAlign-ECA_ROLLING_2012_03_01-UPD4-03");
//			condtoolsdao.checkGlobalTagForSchemaDB("COMCOND-BLKPA-RUN1-01","ATLAS_COOLOFL_FWD", "COMP200");
//			System.out.println("done FWD");
//			condtoolsdao.checkGlobalTagForSchemaDB("COMCOND-BLKPA-RUN1-01","ATLAS_COOLOFL_GLOBAL", "COMP200");
//			System.out.println("done GLOBAL");
//			condtoolsdao.checkGlobalTagForSchemaDB("COMCOND-BLKPA-RUN1-01","ATLAS_COOLOFL_CALO", "COMP200");
//			System.out.println("done CALO");
//			condtoolsdao.checkGlobalTagForSchemaDB("COMCOND-BLKPA-RUN1-01","ATLAS_COOLOFL_TRIGGER", "COMP200");
//			System.out.println("done TRIGGER");
			condtoolsdao.checkGlobalTagForSchemaDB("COMCOND-BLKPA-RUN1-01","ATLAS_COOLONL%", "COMP200");
			System.out.println("done ONLINE schemas");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CoolIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static CoolDAORemote lookupCoolDAO() throws NamingException {
    	
		  String JBOSS_CONTEXT="org.jboss.naming.remote.client.InitialContextFactory";
		  Properties props = new Properties();
		  props.put(Context.INITIAL_CONTEXT_FACTORY, JBOSS_CONTEXT);
		  props.put(Context.PROVIDER_URL, "remote://localhost:4447");
		  props.put(Context.SECURITY_PRINCIPAL, "testuser");
		  props.put(Context.SECURITY_CREDENTIALS, "testpwd");

//        final Hashtable jndiProperties = new Hashtable();
//        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(props);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "JBCoolEAR/";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = "JBCoolEjbs/";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = CoolBean.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = CoolDAORemote.class.getName();
        System.out.println("look up for appName/beanName : "+"java:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        // let's do the lookup
        return (CoolDAORemote) context.lookup(appName + moduleName + distinctName + beanName + "!" + viewClassName);
//        return (CoolDAO) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        //CallerRemote remote=(CallerRemote)context.lookup("TestRemoteEJBEAR/remoteEJB//CallerBean!remote.CallerRemote");
// jboss/exported/JBCoolEjbs/CoolBean!atlas.cool.dao.remote.CoolDAORemote
    }
    
    
	/**
     * Looks up and returns the proxy to remote stateless calculator bean
     *
     * @return
     * @throws NamingException
     */
    private static CondToolsDAORemote lookupCondToolsDAO() throws NamingException {
    	
		  String JBOSS_CONTEXT="org.jboss.naming.remote.client.InitialContextFactory";
		  Properties props = new Properties();
		  props.put(Context.INITIAL_CONTEXT_FACTORY, JBOSS_CONTEXT);
		  props.put(Context.PROVIDER_URL, "remote://localhost:4447");
		  props.put(Context.SECURITY_PRINCIPAL, "testuser");
		  props.put(Context.SECURITY_CREDENTIALS, "testpwd");

//        final Hashtable jndiProperties = new Hashtable();
//        jndiProperties.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
        final Context context = new InitialContext(props);
        // The app name is the application name of the deployed EJBs. This is typically the ear name
        // without the .ear suffix. However, the application name could be overridden in the application.xml of the
        // EJB deployment on the server.
        // Since we haven't deployed the application as a .ear, the app name for us will be an empty string
        final String appName = "JBCoolEAR/";
        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
        // jboss-as-ejb-remote-app
        final String moduleName = "JBCoolEjbs/";
        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
        // our EJB deployment, so this is an empty string
        final String distinctName = "";
        // The EJB name which by default is the simple class name of the bean implementation class
        final String beanName = CondToolsBean.class.getSimpleName();
        // the remote view fully qualified class name
        final String viewClassName = CondToolsDAORemote.class.getName();
        System.out.println("look up for appName/beanName : "+"java:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        // let's do the lookup
        return (CondToolsDAORemote) context.lookup(appName + moduleName + distinctName + beanName + "!" + viewClassName);
//        return (CoolDAO) context.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
        //CallerRemote remote=(CallerRemote)context.lookup("TestRemoteEJBEAR/remoteEJB//CallerBean!remote.CallerRemote");
// jboss/exported/JBCoolEjbs/CoolBean!atlas.cool.dao.remote.CoolDAORemote
    }
    
    
	/* (non-Java-doc)
	 * @see java.lang.Object#Object()
	 */
	public TestClient() {
		super();
	}

}