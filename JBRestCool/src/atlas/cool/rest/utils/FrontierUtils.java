/**
 * 
 */
package atlas.cool.rest.utils;


import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import atlas.frontier.fdo.FrontierEncoder;


/**
 * @author formica
 *
 */
public abstract class FrontierUtils {

	
	protected void writeMetaData(Object obj, FrontierEncoder enc) throws Exception {
		Method[] meth = obj.getClass().getDeclaredMethods();
		for (Method method : meth) {
			String name = method.getName();
			if (name.startsWith("get")) {
				String methname = name.substring(3);
				Class<?> rettype = method.getReturnType();
				enc.writeString(methname);
				enc.writeString(rettype.getSimpleName());
			}
		}
		enc.writeEOR();
	}
	
	protected void writeData(List<?> _objlist, FrontierEncoder enc) throws Exception {

		for (Object row : _objlist) {
			Method[] meth = row.getClass().getDeclaredMethods();
			for (Method method : meth) {
				String name = method.getName();
				if (name.startsWith("get")) {
					Class<?> rettype = method.getReturnType();
					Object column = method.invoke(row, (Object[]) null);
					if (rettype.equals(Integer.class)) {
						enc.writeInt((Integer) column);
					} else if (rettype.equals(Double.class)) {
						enc.writeDouble((Double) column);
					} else if (rettype.equals(Float.class)) {
						enc.writeFloat((Float) column);
					} else if (rettype.equals(Timestamp.class)) {
						enc.writeDate(new Date(((Timestamp)column).getTime()));
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

	protected String md5Digest(FrontierEncoder encoder) throws Exception
	   {
	    StringBuffer md5_ascii=new StringBuffer("");
	    byte[] md5_digest=encoder.getMD5Digest();
	    for(int i=0;i<md5_digest.length;i++) 
	     {
	      int v=(int)md5_digest[i];
	      if(v<0)v=256+v;
	      String str=Integer.toString(v,16);
	      if(str.length()==1)md5_ascii.append("0");
	      md5_ascii.append(str);
	     }
	    return md5_ascii.toString();
	 }  

}
