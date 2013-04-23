/**
 * 
 */
package atlas.cool.rest.utils;


import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

import atlas.frontier.fdo.FrontierBlobTypedEncoder;
import atlas.frontier.fdo.FrontierData;
import atlas.frontier.fdo.FrontierEncoder;
import atlas.frontier.fdo.FrontierResponseFormat;

/**
 * @author formica
 * 
 */
@Provider
@Produces("text/xml")
public class FrontierResponseProvider extends FrontierUtils implements
		MessageBodyWriter<FrontierData> {

	@Inject
	private Logger log;

	@Override
	public boolean isWriteable(final Class<?> type, final Type genericType,
			final Annotation[] annotations, final MediaType mediaType) {
		return true;
	}

	@Override
	public long getSize(final FrontierData t, final Class<?> type,
			final Type genericType, final Annotation[] annotations,
			final MediaType mediaType) {
		return -1;
	}

	@Override
	public void writeTo(final FrontierData t, final Class<?> type,
			final Type genericType, final Annotation[] annotations,
			final MediaType mediaType,
			final MultivaluedMap<String, Object> httpHeaders,
			final OutputStream entityStream) throws IOException,
			WebApplicationException {
//		final PrintWriter writer = new PrintWriter(entityStream);
		FrontierEncoder enc = null;
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			enc = new FrontierBlobTypedEncoder(entityStream, "zip");
			// PrintStream pos = new PrintStream(eos);
			FrontierResponseFormat.begin(entityStream, "3.29", "1.0");
			log.info("         response entity is " + t);
			if (t != null) {
				List<?> _objlist = t.getDataList();
				int ns = _objlist.size();
				FrontierResponseFormat.transaction_start(entityStream, 1);
				FrontierResponseFormat.payload_start(entityStream,
						"frontier_request", "1", "BLOBzip");
				if (ns > 0) {
					writeMetaData(_objlist.get(0), enc);
				}
				writeData(_objlist, enc);
				enc.flush();
				enc.close();
				String md5 = md5Digest(enc);
				long size = enc.getOutputSize();
				FrontierResponseFormat.payload_end(entityStream, 0, "", md5,
						ns, size);
				FrontierResponseFormat.transaction_end(entityStream);
				FrontierResponseFormat.close(entityStream);
				//enc.flush();
				//enc.close();
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//writer.println("Test");
	}
/*
	protected void writeMetaData(Object obj, FrontierEncoder enc)
			throws Exception {
		Method[] meth = obj.getClass().getDeclaredMethods();
		for (Method method : meth) {
			String name = method.getName();
			if (name.startsWith("get")) {
				log.info("Parsing metadata for " + name);
				String methname = name.substring(3);
				Class<?> rettype = method.getReturnType();
				log.info("writing column name and type " + methname + " "
						+ rettype.getSimpleName());
				enc.writeString(methname);
				enc.writeString(rettype.getSimpleName());
			}
		}
		enc.writeEOR();
	}

	protected void writeData(List<?> _objlist, FrontierEncoder enc)
			throws Exception {

		for (Object row : _objlist) {
			Method[] meth = row.getClass().getDeclaredMethods();
			for (Method method : meth) {
				String name = method.getName();
				if (name.startsWith("get")) {
					Class<?> rettype = method.getReturnType();
					Object column = method.invoke(row, (Object[]) null);
					//log.info("         column entity is " + column + " of type "+rettype); 
					if (rettype.equals(Integer.class)) {
						enc.writeInt((Integer) column);
					} else if (rettype.equals(Double.class)) {
						enc.writeDouble((Double) column);
					} else if (rettype.equals(Float.class)) {
						enc.writeFloat((Float) column);
					} else if (rettype.equals(Timestamp.class)) {
						enc.writeDate(new Date(((Timestamp) column).getTime()));
					} else if (rettype.equals(BigDecimal.class)) {
						enc.writeDouble(((BigDecimal) column).doubleValue());
					} else if (rettype.equals(String.class)) {
						enc.writeString((String) column);
					} else if (rettype.equals(Long.class)) {
						enc.writeLong((Long) column);
					}
				}
			}
			enc.writeEOR();
		}
	}
*/
}
