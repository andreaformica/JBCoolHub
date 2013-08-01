/**
 * 
 */
package atlas.cool.payload.plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import atlas.cool.annotations.CoolPayloadParser;
import atlas.cool.payload.plugin.rpc.RpcPanelRes;
import atlas.cool.payload.plugin.rpc.RpcStripStatus;

/**
 * @author formica
 * 
 */
@CoolPayloadParser(schema = "ATLAS_COOLOFL_RPC", folder = "/RPC/DQMF/ELEMENT_STATUS")
public class RpcClobParser implements ClobParser {

	private final String[] stripstatusvars = { "status", "time", "timeRes" };

	private final String[] panelresvars = { "dBversion", "nTracks", "nStrips",
			"eff", "effErr", "effGap", "effGapErr", "resCs1", "cs1Err",
			"resCs2", "cs2Err", "resCsOther", "csOtherErr", "noise",
			"noiseErr", "noiseCor", "noiseCorErr", "clusterSize",
			"clusterSizeErr", "fracCs1", "fracCs2", "fracCs38", "averCs38",
			"fracCs9up", "averCs9up" };

	/*
	 * (non-Javadoc)
	 * 
	 * @see atlas.cool.payload.plugin.ClobParser#parseClob(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public Object parseClob(final String payloadColumn, final String content) {
		if (content == null) {
			return null;
		}
		if (payloadColumn.equals("PanelRes")) {
			//System.out.println("Calling transformation for panelres");
			return parsePanelResObj(content);
		} else if (payloadColumn.equals("StripStatus")) {
			//System.out.println("Calling transformation for stripstatus");
			return parseStripStatusObj(content);
		}
		return null;
	}

	/**
	 * @param content
	 * @return
	 */
	protected Map<Integer, Map<String, Float>> parsePanelRes(
			final String content) {

		final Map<Integer, Map<String, Float>> panelresMap = new HashMap<Integer, Map<String, Float>>();
		final Integer rowindex = 0;

		final String[] rowarr = content.split(" ");

		final Map<String, Float> panelresmapContent = new HashMap<String, Float>();
		for (int i = 0; i < rowarr.length; i++) {
			panelresmapContent
					.put(panelresvars[i], new Float(rowarr[i].trim()));
		}
		panelresMap.put(rowindex, panelresmapContent);
		return panelresMap;
	}

	/**
	 * @param content
	 * @return
	 */
	protected RpcPanelRes parsePanelResObj(final String content) {

		final String[] rowarr = content.split(" ");

		final RpcPanelRes panelres = new RpcPanelRes();
		for (int i = 0; i < rowarr.length; i++) {
			//System.out.println("Parsing panelres " + rowarr[i]);
			if (rowarr[i].length() == 0) {
				continue;
			}
			final Float val = new Float(rowarr[i].trim());
			final String name = panelresvars[i];
			try {
				final Method setter = searchSetterMethod(name,
						RpcPanelRes.class, Float.class);
				setter.invoke(panelres, val);
			} catch (final NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return panelres;
	}

	/**
	 * @param content
	 * @return
	 */
	protected Map<Integer, Map<String, Float>> parseStripStatus(
			final String content) {

		final Map<Integer, Map<String, Float>> stripstatusMap = new HashMap<Integer, Map<String, Float>>();

		final String[] striparr = content.split("\\|");
		// System.out.println("Parsing clob "+content+" in array of "+striparr.length);

		for (int i = 0; i < striparr.length; i++) {
			final String astrip = striparr[i].trim();
			final String[] astriparr = astrip.split("\\s+");
			final Integer key = new Integer(i);
			final Map<String, Float> stripstatusmapContent = new HashMap<String, Float>();
			for (int j = 0; j < astriparr.length; j++) {
				final String val = astriparr[j].trim();
				stripstatusmapContent.put(stripstatusvars[j], new Float(val));
			}
			stripstatusMap.put(key, stripstatusmapContent);
		}
		return stripstatusMap;
	}

	/**
	 * @param content
	 * @return
	 */
	protected Collection<RpcStripStatus> parseStripStatusObj(
			final String content) {

		final Collection<RpcStripStatus> stripstatusList = new ArrayList<RpcStripStatus>();

		final String[] striparr = content.split("\\|");
		//System.out.println("Parsing clob " + content + " in array of "
		//		+ striparr.length);

		for (int i = 0; i < striparr.length; i++) {
			final String astrip = striparr[i].trim();
			final String[] astriparr = astrip.split("\\s+");
			final RpcStripStatus stripstatus = new RpcStripStatus();
			for (int j = 0; j < astriparr.length; j++) {
				//System.out.println("Parsing stripstatus " + astriparr[j]);
				final String val = astriparr[j].trim();
				final String name = stripstatusvars[j];
				try {
					final Method setter = searchSetterMethod(name,
							RpcStripStatus.class, Float.class);
					setter.invoke(stripstatus, new Float(val));
				} catch (final NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			stripstatusList.add(stripstatus);
		}
		return stripstatusList;
	}

	/**
	 * @param name
	 * @param clazz
	 * @param argclazz
	 * @return
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	protected Method searchSetterMethod(final String name,
			final Class<?> clazz, final Class<?> argclazz)
			throws NoSuchMethodException, SecurityException {
		final String settername = "set" + name.substring(0, 1).toUpperCase()
				+ name.substring(1);
		final Method mth = clazz.getDeclaredMethod(settername, argclazz);
		return mth;
	}
}
