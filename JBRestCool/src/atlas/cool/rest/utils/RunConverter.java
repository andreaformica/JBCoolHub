/**
 * 
 */
package atlas.cool.rest.utils;

import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.inject.Inject;
import javax.inject.Named;

import atlas.coma.model.RunSummary;
import atlas.cool.jsf.beans.CalendarBean;


/**
 * @author formica
 * 
 */
@Named("runconverter")
public class RunConverter implements Converter {

	@Inject
	private CalendarBean calendar;

	/**
	 * 
	 */
	public RunConverter() {
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
			List<RunSummary> runList = calendar.getRunList();
			if (runList == null)
				return null;
			System.out.println("Found run list in calendar "+runList.size());
			if (s.trim().equals("")) {
				return null;
			} else {
				System.out.println("Received string "+s);
				String[] chstr = s.split(":");
				System.out.println("try to convert to long "+chstr[0]);
				
				Long chid = new Long(chstr[0]);
				System.out.println("Loop using params "+calendar);
				for (RunSummary run : runList) {
					if (run.getRunNumber().compareTo(chid) == 0) {
						//System.out.println("Found mdthead "+ch.getChannelId());
						return run;
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
			return ((RunSummary) o).getRunNumber()+":"+((RunSummary)o).getType();
		}
	}

}
