package atlas.cool.rest.model;
/**
 * 
 */


import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import atlas.cool.annotations.CoolQuery;


/**
 * @author formica
 *
 */
@Entity
@NamedNativeQueries( {
	@NamedNativeQuery(name = SchemaType.QUERY_FINDSCHEMAS, query = "select   "
			 + " schema_name, "
			 + " count(schema_name) as nfolders "
 + " from table(cool_select_pkg.f_getall_nodes(:schema,:dbname,:node)) "
 + " group by schema_name ", resultClass = SchemaType.class)
})
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SchemaType implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1312992055795112291L;

	@Id
	@Column(name = "SCHEMA_NAME", length = 30)
	String schemaName;

	@Column(name = "NFOLDERS", precision = 10, scale = 0)
	Long nfolders;
	
	@CoolQuery(name="cool.findschemas",params="schema;dbname;node")
	public static final String QUERY_FINDSCHEMAS = "cool.findschemas";

	/**
	 * @return the schemaName
	 */
	public String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName the schemaName to set
	 */
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the nfolders
	 */
	public Long getNfolders() {
		return nfolders;
	}

	/**
	 * @param nfolders the nfolders to set
	 */
	public void setNfolders(Long nfolders) {
		this.nfolders = nfolders;
	}

	public String getShortName() {
		if (schemaName == null)
			return null;
		String shortname = schemaName.replaceFirst("ATLAS_", "");
		return shortname;
	}

}
