package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;

import javax.ejb.Local;

import atlas.cool.exceptions.CoolIOException;

/**
 * @author formica
 *
 */
@Local
public interface CoolResultSetDAO {

	 /**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelId
	 * @return
	 * @throws CoolIOException
	 */
	ResultSet getPayload(String schemaname, String dbname,
            String folder, String tagname, BigDecimal time, Long channelId) throws CoolIOException;

	 /**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param time
	 * @param channelName
	 * @return
	 * @throws CoolIOException
	 */
	ResultSet getPayload(String schemaname, String dbname,
            String folder, String tagname, BigDecimal time, String channelName) throws CoolIOException;

	 /**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelId
	 * @return
	 * @throws CoolIOException
	 */
	ResultSet getPayloads(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, Long channelId) throws CoolIOException;

	 /**
	 * @param schemaname
	 * @param dbname
	 * @param folder
	 * @param tagname
	 * @param stime
	 * @param etime
	 * @param channelName
	 * @return
	 * @throws CoolIOException
	 */
	ResultSet getPayloads(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, String channelName) throws CoolIOException;

	 /**
	 * 
	 */
	void closeConnection();
}
