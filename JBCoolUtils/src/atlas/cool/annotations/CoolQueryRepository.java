/**
 * 
 */
package atlas.cool.annotations;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.Entity;

import org.jboss.vfs.VirtualFile;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.scanners.ResourcesScanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.vfs.SystemDir;
import org.reflections.vfs.Vfs;
import org.reflections.vfs.ZipDir;

import atlas.cool.exceptions.CoolQueryException;

/**
 * @author formica
 * 
 */
@ApplicationScoped
public class CoolQueryRepository {

	// Build a map containing queries for every class in package xxx.model.
	// The map will contain as key the query name
	//
	/**
	 * 
	 */
	private final Map<String, QueryParams> queryMap 
		= new HashMap<String, QueryParams>();

	// private final Map<String, Query> queryEmMap = new HashMap<String,
	// Query>();

	/**
	 * 
	 */
	public CoolQueryRepository() {

	}

	/**
	 * 
	 */
	@PostConstruct
	protected void init() {
		try {

			// log.info("Init CoolQueryRepository for package atlas.cool.rest.model");
			Vfs.addDefaultURLTypes(new Vfs.UrlType() {
				public boolean matches(final URL url) {
					return url.getProtocol().equals("vfs");
				}

				public Vfs.Dir createDir(final URL url) {
					VirtualFile content;
					try {
						content = (VirtualFile) url.openConnection()
								.getContent();
					} catch (Throwable e) {
						throw new ReflectionsException(
								"could not open url connection as VirtualFile ["
										+ url + "]", e);
					}

					Vfs.Dir dir = null;
					try {
						dir = createDir(new java.io.File(content
								.getPhysicalFile().getParentFile(), content
								.getName()));
					} catch (IOException e) {
						;
					}
					if (dir == null) {
						try {
							dir = createDir(content.getPhysicalFile());
						} catch (IOException e) {
							;
						}
					}
					return dir;
				}

				Vfs.Dir createDir(final java.io.File file) {
					try {
						return file.exists() && file.canRead() ? file
								.isDirectory() ? new SystemDir(file)
								: new ZipDir(new JarFile(file)) : null;
					} catch (IOException e) {
						e.printStackTrace();
					}
					return null;
				}
			});

			findAnnotatedClasses("atlas");
			// findAnnotatedClasses("atlas.coma.model");
			// System.out.println("Address for coolqueryrepository "+this.toString());
		} catch (CoolQueryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @param qryname
	 *            The name of the query to search.
	 * @return QueryParams The object which contains all the parameters needed
	 *         for the query.
	 */
	public final synchronized QueryParams getQueryParams(final String qryname)
			throws CoolQueryException {
		try {
			// System.out.println("Searching for "+qryname+" in map "+queryMap+" inside coolqueryrep "+this.toString());
			QueryParams query = queryMap.get(qryname);
			return query;
		} catch (Exception e) {
			throw new CoolQueryException(e.getMessage());
		}
	}

	/**
	 * Jar files URLs [ vfs:/content/JBCoolEjbs.jar vfs:/content/JBComaEjbs.jar] .
	 * 
	 * @param pckgname
	 * @throws CoolQueryException
	 */
	private void findAnnotatedClasses(final String pckgname)
			throws CoolQueryException {

		// System.out.println("Fill annotation for "+pckgname+" from query repository "+this.toString());
		// log.info("Package name for reflections "+pckgname);
		// Set<URL> packageurls = ClasspathHelper.forPackage("atlas");
		// for (URL url : packageurls) {
		// System.out.println("Found url "+url);
		// }

		Reflections reflections = new Reflections(new ConfigurationBuilder()
				.addUrls(ClasspathHelper.forPackage(pckgname)).setScanners(
						new ResourcesScanner(), new TypeAnnotationsScanner(),
						new SubTypesScanner()));

		// System.out.println("Reflections for "+pckgname+"  - "+reflections.toString());
		Set<Class<?>> jpaEntities = reflections
				.getTypesAnnotatedWith(Entity.class);
		// System.out.println("jpaEntities size "+jpaEntities.size());
		for (Class<?> jpaclass : jpaEntities) {
			// System.out.println("Analyse class "+jpaclass.getName());
			Field[] fields = jpaclass.getDeclaredFields();
			for (int ifield = 0; ifield < fields.length; ifield++) {
				if (fields[ifield].isAnnotationPresent(CoolQuery.class)) {
					CoolQuery ann = (CoolQuery) fields[ifield]
							.getAnnotation(CoolQuery.class);
					// System.out.println("Got field annotated "+fields[ifield].getName());
					String name = ann.name();
					String paramstr = ann.params();
					String[] paramsarr = paramstr.split(";");
					QueryParams params = new QueryParams(name, paramsarr);
					// System.out.println("Update map "+queryMap+" with query "+params);
					queryMap.put(name, params);
				}
			}
		}
	}

	/**
	 * @param pckgname
	 * 	The package name.
	 * @return
	 * 	List of classes.
	 * @throws ClassNotFoundException
	 * 	Class not found.
	 */
	@SuppressWarnings("rawtypes")
	public final List<Class> getClassesForPackage(final String pckgname)
			throws ClassNotFoundException {
		// This will hold a list of directories matching the pckgname.
		// There may be more than one if a package is split over multiple
		// jars/paths
		List<Class> classes = new ArrayList<Class>();
		ArrayList<File> directories = new ArrayList<File>();
		System.out.println("Look for package " + pckgname);
		try {
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null) {
				throw new ClassNotFoundException("Can't get class loader.");
			}
			// Ask for all resources for the path
			System.out.println("Class loader path "
					+ cld.getResource("screen.css") + " for screen.css");
			Enumeration<URL> resources = cld.getResources(pckgname.replace('.',
					'/'));
			System.out.println("Resources found has elements "
					+ resources.hasMoreElements());
			while (resources.hasMoreElements()) {
				URL res = resources.nextElement();
				// System.out.println("URL element "+res);
				if (res.getProtocol().equalsIgnoreCase("jar")) {
					JarURLConnection conn = (JarURLConnection) res
							.openConnection();
					JarFile jar = conn.getJarFile();
					for (JarEntry e : Collections.list(jar.entries())) {

						if (e.getName().startsWith(pckgname.replace('.', '/'))
								&& e.getName().endsWith(".class")
								&& !e.getName().contains("$")) {
							String className = e.getName().replace("/", ".")
									.substring(0, e.getName().length() - 6);
							// System.out.println(className);
							classes.add(Class.forName(className));
						}
					}

				} else if (res.getProtocol().equalsIgnoreCase("vfs")) {
					// use virtual file
					// System.out.println("Protocol is VFS...");
					Vfs.Dir dir = Vfs.fromURL(res);
					Iterable<Vfs.File> files = dir.getFiles();
					Vfs.File first = files.iterator().next();
					// System.out.println("Getting file "+first.getName()+" on path "+first.getRelativePath());
					first.getName();
					try {
						first.openInputStream();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}

					dir.close();

				} else {
					directories.add(new File(URLDecoder.decode(res.getPath(),
							"UTF-8")));
				}
			}
		} catch (NullPointerException x) {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be "
					+ "a valid package (Null pointer exception)");
		} catch (UnsupportedEncodingException encex) {
			throw new ClassNotFoundException(pckgname
					+ " does not appear to be "
					+ "a valid package (Unsupported encoding)");
		} catch (IOException ioex) {
			throw new ClassNotFoundException(
					"IOException was thrown when trying "
							+ "to get all resources for " + pckgname);
		}

		// For every directory identified capture all the .class files
		for (File directory : directories) {
			if (directory.exists()) {
				// Get the list of the files contained in the package
				String[] files = directory.list();
				for (String file : files) {
					// we are only interested in .class files
					if (file.endsWith(".class")) {
						// removes the .class extension
						classes.add(Class.forName(pckgname + '.'
								+ file.substring(0, file.length() - 6)));
					}
				}
			} else {
				throw new ClassNotFoundException(pckgname + " ("
						+ directory.getPath()
						+ ") does not appear to be a valid package");
			}
		}
		return classes;
	}

	/**
	 * @param thePackage
	 * 	The package name.
	 * @param theInterface
	 * 	The interface.
	 * @return
	 * 	List of classes implementing the interface.
	 */
	@SuppressWarnings("rawtypes")
	public final List<Class> getClassessOfInterface(final String thePackage,
			final Class theInterface) {
		List<Class> classList = new ArrayList<Class>();
		try {
			for (Class discovered : getClassesForPackage(thePackage)) {
				if (Arrays.asList(discovered.getInterfaces()).contains(
						theInterface)) {
					classList.add(discovered);
				}
			}
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}

		return classList;
	}

	/**
	 * @param key
	 * 	A key.
	 * @param value
	 * 	A value.
	 */
	public static void setDefaultSystemProperty(final String key,
			final String value) {
		if (System.getProperty(key) == null) {
			System.setProperty(key, value);
		}
	}

	/**
	 * @param args
	 * 	Input arguments.
	 */
	public static void main(final String[] args) {

		setDefaultSystemProperty("log4j.configuration", "/conf/log4j.xml");
		setDefaultSystemProperty("java.security.auth.login.config",
				"/conf/auth.conf");

		CoolQueryRepository main = new CoolQueryRepository();

	}
}
