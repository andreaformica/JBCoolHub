/**
 * 
 */
package atlas.cool.jsf.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.inject.Inject;

import atlas.cool.jsf.utils.ColumnsGenerator.ColumnModel;

/**
 * @author formica
 * 
 * @param <T>
 */
public abstract class AbstractColumnHelper<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2159796556170554725L;

	@Inject
	private Logger alog;

	protected List<ColumnModel> columns = null;
	protected List<String> allcolumns = null;
	protected List<String> selectedColumns = null;
	protected String exclusionPattern = "none";
	protected Map<String, String> columnsDisplayMask = null;
	protected Map<String, ColumnModel> columnsDisplayList = null;

	protected boolean columnSelectionChanged = false;

	ColumnsGenerator<T> generator = null;

	/**
	 * 
	 */
	public AbstractColumnHelper() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the columns
	 */
	public List<ColumnModel> getColumns() {
		if (selectedColumns == null) {
			applyExclusionPattern();
			createSelection();
		}
		if (columnSelectionChanged) {
			fillColumns();
		}
		return columns;
	}

	/**
	 * 
	 */
	protected void applyExclusionPattern() {
		for (final String acol : allcolumns) {
			if (!acol.matches(exclusionPattern)) {
				setDisplayMask(acol, "selected");
			} else {
				setDisplayMask(acol, "masked");
			}
		}
	}

	/*
	 * protected void selColumns() { selectedColumns = new ArrayList<String>();
	 * columns = new ArrayList<ColumnModel>(); for (String acol :
	 * columnsDisplayMask.keySet()) { String value =
	 * columnsDisplayMask.get(acol); if (value.equals("selected")) { ColumnModel
	 * model = columnsDisplayList.get(acol); columns.add(model);
	 * selectedColumns.add(acol);
	 * alog.info("selColumns init selection..."+acol+" is "+value); } } }
	 */

	/**
	 * @param pattern
	 */
	public void defaultSelectionExclusion(final String pattern) {
		exclusionPattern = pattern;
		applyExclusionPattern();
		createSelection();
	}

	/**
	 * 
	 */
	protected void initAllColumns() {
		if (allcolumns == null) {
			columns = generator.getColumns();
			allcolumns = new ArrayList<String>();
			columnsDisplayList = new HashMap<String, ColumnModel>();
			columnsDisplayMask = new HashMap<String, String>();
			for (final ColumnModel acol : columns) {
				String fieldname = acol.getProperty();
				if (fieldname.endsWith("Str")) {
					fieldname = fieldname.replaceAll("Str$", "");
				}
				// alog.info("initAllColumns init masks..."+fieldname+" as selected");
				columnsDisplayList.put(fieldname, acol);
				columnsDisplayMask.put(fieldname, "selected");
				allcolumns.add(fieldname);
			}
		}
	}

	/**
	 * 
	 */
	protected void createSelection() {
		columnSelectionChanged = true;
		selectedColumns = new ArrayList<String>();
		for (final String acol : allcolumns) {
			final String value = columnsDisplayMask.get(acol);
			if (value.equals("selected")) {
				selectedColumns.add(acol);
				// alog.info("createSelection init selection..."+acol+" is "+value);
			}
			// alog.info(" column "+acol+" has been masked as "+columnsDisplayMask.get(acol));
		}
	}

	/**
	 * 
	 */
	protected void fillColumns() {
		columns = new ArrayList<ColumnModel>();
		alog.info("fillColumns looping over " + selectedColumns.size()
				+ " list of selected columns...");
		for (final String acol : selectedColumns) {
			final String key = columnsDisplayMask.get(acol);
			if (key.equals("selected")) {
				// alog.info("Adding column "+acol+" to columns for class "+generator.getObj());
				final ColumnModel model = columnsDisplayList.get(acol);
				columns.add(model);
			}
		}
		columnSelectionChanged = false;
	}

	/**
	 * @return the selectedColumns
	 */
	public List<String> getSelectedColumns() {
		if (selectedColumns == null || selectedColumns.size() == 0) {
			alog.info("getSelectedColumns null or empty....");
			applyExclusionPattern();
			createSelection();
		}
		return selectedColumns;
	}

	/**
	 * @param selectedColumns
	 *            the selectedColumns to set
	 */
	public void setSelectedColumns(final List<String> selectedColumns) {
		alog.info("Changed columns selection..." + selectedColumns);
		columnSelectionChanged = true;
		this.selectedColumns = selectedColumns;
		if (selectedColumns == null || selectedColumns.size() == 0) {
			alog.info("setSelectedColumns: set default columns selection...");
			applyExclusionPattern();
			createSelection();
			alog.info("  ....columns selection has size " + this.selectedColumns.size());
			return;
		}
		// Now make the display mask to match the selection
		fillDisplayMask();
	}

	/**
	 * 
	 */
	protected void fillDisplayMask() {
		for (final String selection : allcolumns) {
			if (this.selectedColumns.indexOf(selection) < 0) {
				setDisplayMask(selection, "masked");
			} else {
				setDisplayMask(selection, "selected");
			}
			// alog.info(" column "+selection+" has been masked as "+columnsDisplayMask.get(selection));
		}
	}

	/**
	 * @return the allcolumns
	 */
	public List<String> getAllcolumns() {
		return allcolumns;
	}

	/**
	 * @param property
	 * @param display
	 */
	protected void setDisplayMask(final String property, final String display) {
		if (columnsDisplayMask.containsKey(property)) {
			columnsDisplayMask.remove(property);
		}
		columnsDisplayMask.put(property, display);
	}
}
