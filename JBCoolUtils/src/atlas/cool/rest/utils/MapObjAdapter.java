/**
 * 
 */
package atlas.cool.rest.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import atlas.cool.meta.MapEntry;
import atlas.cool.meta.MapWrapper;

/**
 * @author formica
 * 
 */
public class MapObjAdapter extends XmlAdapter<MapWrapper, Map<String, Object>> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#marshal(java.lang.Object)
	 */
	@Override
	public final MapWrapper marshal(final Map<String, Object> amap)
			throws Exception {
		MapWrapper mrw = new MapWrapper();
		Set<String> keys = amap.keySet();
		List<MapEntry> entrylist = new ArrayList<MapEntry>();
		for (String akey : keys) {
			String objval = "";
			Object value = amap.get(akey);
			if (value == null) {
				objval="null";
			} else {
				objval = value.toString();
			}
			MapEntry mpe = new MapEntry(akey, objval);
			entrylist.add(mpe);
		}
		mrw.column = entrylist;
		return mrw;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.xml.bind.annotation.adapters.XmlAdapter#unmarshal(java.lang.Object)
	 */
	@Override
	public final Map<String, Object> unmarshal(
			final MapWrapper arg0) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

}
