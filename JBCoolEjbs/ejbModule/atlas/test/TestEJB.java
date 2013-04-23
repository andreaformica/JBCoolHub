/**
 * 
 */
package atlas.test;

import java.lang.reflect.Method;

import javax.naming.NamingException;

import atlas.cool.dao.remote.CoolDAORemote;

/**
 * @author formica
 *
 */
public class TestEJB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
