/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;
import java.sql.ResultSet;

//import atlas.cool.meta.CoolPayload;

/**
 * @author formica
 *
 */
public interface CoolPayloadDAO {

	public ResultSet getPayload(String schemaname, String dbname,
            String folder, String tagname, BigDecimal time, Integer channelId) throws CoolIOException;
    public ResultSet getPayloads(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, Integer channelId) throws CoolIOException;
    public void remove() throws CoolIOException;
}
