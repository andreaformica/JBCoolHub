package atlas.cool.rest.utils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimestampStringFormatter {

	protected static final SimpleDateFormat df = new SimpleDateFormat();

	protected static final String _fmt = "yyyy/MM/dd hh:mm:ss";
	
	public static String format(String format, Date adate) {
		try {
			if (format == null)
				format = _fmt;
			df.applyPattern(format);
			return df.format(adate);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static String formatTs(String format, Timestamp adate) {
		try {
			if (format == null)
				format = _fmt;
			df.applyPattern(format);
			return df.format(new Date(adate.getTime()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
