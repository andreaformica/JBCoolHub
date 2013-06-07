/**
 * 
 */
package atlas.cool.rest.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import atlas.cool.annotations.CoolQuery;

/**
 * @author formica
 * 
 */
@Entity
@NamedNativeQueries({ @NamedNativeQuery(name = GtagTagDiffType.QUERY_FINDGTAGS_TAGS_DIFF, query = "select   "
		+ " gtag1.schema_name, "
		+ " gtag1.db_name, "
		+ " gtag1.node_fullpath ,"
		+ " gtag1.gtag_name as gtaga, "
		+ " gtag2.gtag_name as gtagb, "
		+ " gtag1.tag_name tag_name_a, "
		+ " gtag2.tag_name tag_name_b, "
		+ " rownum "
		+ " from (select * "
		+ " from table(cool_select_pkg.f_getall_tagsforgtag(:schema,:db,:gtaga)) ) gtag1 "
		+ " left join (select * "
		+ " from table(cool_select_pkg.f_getall_tagsforgtag(:schema,:db,:gtagb)) ) gtag2 "
		+ " on gtag1.schema_name=gtag2.schema_name and gtag1.node_fullpath=gtag2.node_fullpath "
		+ " where gtag1.tag_name != gtag2.tag_name ", resultClass = GtagTagDiffType.class) })
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class GtagTagDiffType implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2990371156354454615L;

	@Id
	@Column(name = "ROWNUM")
	private Long rowid;

	@Column(name = "SCHEMA_NAME", length = 30)
	private String schemaName;
	@Column(name = "DB_NAME", length = 30)
	private String dbName;
	@Column(name = "NODE_FULLPATH", length = 255)
	private String nodeFullpath;

	@Column(name = "GTAGA", length = 255)
	private String gtagNameA;
	@Column(name = "GTAGB", length = 255)
	private String gtagNameB;

	@Column(name = "TAG_NAME_A", length = 255)
	private String tagNameA;
	@Column(name = "TAG_NAME_B", length = 255)
	private String tagNameB;

	@CoolQuery(name = "cool.findgtagstagsdiff", params = "schema;db;gtaga;gtagb")
	public static final String QUERY_FINDGTAGS_TAGS_DIFF = "cool.findgtagstagsdiff";

	/**
	 * @return the schemaName
	 */
	public final  String getSchemaName() {
		return schemaName;
	}

	/**
	 * @param schemaName
	 *            the schemaName to set
	 */
	public final  void setSchemaName(final String schemaName) {
		this.schemaName = schemaName;
	}

	/**
	 * @return the dbName
	 */
	public final  String getDbName() {
		return dbName;
	}

	/**
	 * @param dbName
	 *            the dbName to set
	 */
	public final  void setDbName(final String dbName) {
		this.dbName = dbName;
	}

	/**
	 * @return the nodeFullpath
	 */
	public final  String getNodeFullpath() {
		return nodeFullpath;
	}

	/**
	 * @param nodeFullpath
	 *            the nodeFullpath to set
	 */
	public final  void setNodeFullpath(final String nodeFullpath) {
		this.nodeFullpath = nodeFullpath;
	}

	/**
	 * @return the gtagNameA
	 */
	public final  String getGtagNameA() {
		return gtagNameA;
	}

	/**
	 * @param gtagNameA
	 *            the gtagNameA to set
	 */
	public final  void setGtagNameA(final String gtagNameA) {
		this.gtagNameA = gtagNameA;
	}

	/**
	 * @return the gtagNameB
	 */
	public final  String getGtagNameB() {
		return gtagNameB;
	}

	/**
	 * @param gtagNameB
	 *            the gtagNameB to set
	 */
	public final  void setGtagNameB(final String gtagNameB) {
		this.gtagNameB = gtagNameB;
	}

	/**
	 * @return the tagNameA
	 */
	public final  String getTagNameA() {
		return tagNameA;
	}

	/**
	 * @param tagNameA
	 *            the tagNameA to set
	 */
	public final  void setTagNameA(final String tagNameA) {
		this.tagNameA = tagNameA;
	}

	/**
	 * @return the tagNameB
	 */
	public final  String getTagNameB() {
		return tagNameB;
	}

	/**
	 * @param tagNameB
	 *            the tagNameB to set
	 */
	public final  void setTagNameB(final String tagNameB) {
		this.tagNameB = tagNameB;
	}

}
