/**
 * 
 */
package atlas.cool.rest.utils;

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
import atlas.cool.rest.model.SchemaType;


/**
 * @author formica
 * 
 */
@Named("schemaconverter")
public class SchemaConverter implements Converter {

	@Inject
	private CoolSchemaParamsBean params;

	@Inject
	private Logger log;

	/**
	 * 
	 */
	public SchemaConverter() {
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
			List<SchemaType> schList = params.getSchemaList();
			if (schList == null)
				return null;

			log.info("Found schema list in params "+schList.size()+", try to find "+s);
			if (s.trim().equals("")) {
				return null;
			} else {
				log.info("Received string "+s);
				
				log.info("Loop using params "+params);
				for (SchemaType sch : schList) {
					if (sch.getSchemaName().equals("ATLAS_"+s)) {
						log.info("Found schema "+sch.getSchemaName());
						return sch;
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
			return ((SchemaType) o).getShortName();
		}
	}

}
