/**
 * 
 */
package atlas.cool.dao;

import java.math.BigDecimal;

import atlas.cool.exceptions.CoolIOException;
import atlas.cool.payload.model.CoolPayload;


/**
 * @author formica
 *
 */
public interface CoolPayloadDAO {

//	public ResultSet getPayload(String schemaname, String dbname,
//            String folder, String tagname, BigDecimal time, Long channelId) throws CoolIOException;
//
//	public ResultSet getPayload(String schemaname, String dbname,
//            String folder, String tagname, BigDecimal time, String channelName) throws CoolIOException;
//
//	public ResultSet getPayloads(String schemaname, String dbname,
//            String folder, String tagname, BigDecimal stime, BigDecimal etime, Long channelId) throws CoolIOException;
//
//	public ResultSet getPayloads(String schemaname, String dbname,
//            String folder, String tagname, BigDecimal stime, BigDecimal etime, String channelName) throws CoolIOException;

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
    CoolPayload getPayloadsObj(String schemaname, String dbname,
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
    CoolPayload getPayloadsObj(String schemaname, String dbname,
            String folder, String tagname, BigDecimal stime, BigDecimal etime, String channelName) throws CoolIOException;

}
