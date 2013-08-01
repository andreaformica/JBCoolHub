/**
 * 
 */
package atlas.cool.summary.model;

import java.io.Serializable;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author formica
 * 
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class CondSchema implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -506802595238395582L;

	private String name;
	private String dbName;

	private Set<CondNodeStats> children;

	/**
	 * 
	 */
	public CondSchema() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param schemaName
	 * @param dbName
	 * @param schemaNodeStats
	 */
	public CondSchema(final String schemaName, final String dbName,
			final Set<CondNodeStats> schemaNodeStats) {
		super();
		this.name = schemaName;
		this.dbName = dbName;
		this.children = schemaNodeStats;
	}

	/**
	 * @return the schemaName
	 */
	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public void setName(final String schemaName) {
		this.name = schemaName;
	}

	/**
	 * @return the dbName
	 */
	@XmlElement(name = "db")
	public String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public void setDbName(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the schemaNodeStats
	 */
	@XmlElement(name = "children")
	public Set<CondNodeStats> getChildren() {
		return children;
	}

	/**
	 * @param schemaNodeStats
	 *            the schemaNodeStats to set
	 */
	public void setChildren(final Set<CondNodeStats> schemaNodeStats) {
		this.children = schemaNodeStats;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CondSchema) {
			CondSchema theo = (CondSchema) obj;
			if (theo.getDbName().equals(this.getDbName())
					&& theo.getName().equals(this.getName())) {
				return true;
			}
		}
		return super.equals(obj);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return (name + ":" + dbName).hashCode();
	}

}
