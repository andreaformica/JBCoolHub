/**
 * 
 */
package atlas.cool.rest.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.logging.Logger;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.cool.jsf.beans.CoolSchemaParamsBean;
import atlas.cool.rest.model.ChannelType;


/**
 * @author formica
 * 
 */
@Named("channel")
public class ChannelConverter implements Converter {

	@Inject
	private CoolSchemaParamsBean params;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public ChannelConverter() {
		// TODO Auto-generated constructor stub
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsObject(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.String)
	 */
	@Override
	public Object getAsObject(FacesContext facesCtx, UIComponent uiComponent,
			String s) {
		try {
			List<ChannelType> chList = params.getChannelList();
			if (chList == null)
				return null;

			log.info("Found channel list in params "+chList.size());
			if (s.trim().equals("") || s.trim().equals("none")) {
				return null;
			} else {
				log.info("Received string "+s);
				String[] chstr = s.split(":");
				log.info("try to convert to bigdecimal "+chstr[0]);
				
				BigDecimal chid = new BigDecimal(chstr[0]);
				log.info("Loop using params "+params);
				for (ChannelType ch : chList) {
					if (ch.getChannelId().compareTo(chid) == 0) {
						//System.out.println("Found mdthead "+ch.getChannelId());
						return ch;
					}
				}
			}
		} catch (NumberFormatException exception) {
			throw new ConverterException(new FacesMessage(
					FacesMessage.SEVERITY_ERROR, "Conversion Error",
					"Not a valid converter"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.faces.convert.Converter#getAsString(javax.faces.context.FacesContext
	 * , javax.faces.component.UIComponent, java.lang.Object)
	 */
	@Override
	public String getAsString(FacesContext facesCtx, UIComponent uiComponent,
			Object o) {
		if (o == null || o.equals("")) {
			return "none";
		} else {
			return ((ChannelType) o).getChannelId().toString()+":"+((ChannelType)o).getChannelName();
		}
	}

}
