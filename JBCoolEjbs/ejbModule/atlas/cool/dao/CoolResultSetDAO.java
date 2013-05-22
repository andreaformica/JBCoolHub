package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;

import javax.ejb.Local;

@Local
public interface CoolResultSetDAO {

	public ResultSet getPayload(String schemaname, String dbname,
            String folder, String tagname, BigDecimal time, Long channelId) throws CoolIOException;

	public ResultSet getPayload(String schemaname, String dbname,
            String folder, String tagname, BigDecimal time, String channelName) throws CoolIOException;

	public ResultSet getPayloads(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, Long channelId) throws CoolIOException;

	public ResultSet getPayloads(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, String channelName) throws CoolIOException;

	public void closeConnection();
}
