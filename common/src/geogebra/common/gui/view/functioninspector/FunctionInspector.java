/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package geogebra.common.gui.view.functioninspector;

import geogebra.common.gui.SetLabels;
import geogebra.common.gui.UpdateFonts;
import geogebra.common.gui.view.functioninspector.FunctionInspectorModel.IFunctionInspectorListener;
import geogebra.common.kernel.Kernel;
import geogebra.common.kernel.ModeSetter;
import geogebra.common.kernel.View;
import geogebra.common.kernel.geos.GeoElement;
import geogebra.common.kernel.geos.GeoFunction;
import geogebra.common.main.App;
import geogebra.common.main.GeoElementSelectionListener;
import geogebra.common.main.Localization;

import java.awt.event.WindowEvent;

/**
 * View for inspecting selected GeoFunctions
 * 
 * @author G. Sturr, 2011-2-12
 * 
 */

public abstract class FunctionInspector implements View, UpdateFonts, SetLabels,
		IFunctionInspectorListener {

	private static final long serialVersionUID = 1L;
	private FunctionInspectorModel model;
	// ggb fields
	private Kernel kernel;
	private static final int minRows = 12;

		private boolean isIniting;

	private boolean isChangingValue;
	private int pointCount = 9;

	private GeoElementSelectionListener sl;
	private App app;
	private Localization loc;

	/***************************************************************
	 * Constructs a FunctionInspecor
	 * 
	 * @param app
	 * @param selectedGeo
	 */
	public FunctionInspector(App app, GeoFunction selectedGeo) {

		this.app = app;
		loc = app.getLocalization();
		kernel = app.getKernel();
		app.getKernel().attach(this);

		model = new FunctionInspectorModel(app, selectedGeo, this);
		// create the GUI
		createGeoElementSlectionListener();
		createGUI();

		// load selected function
		insertGeoElement(selectedGeo);

		isIniting = false;

	}

	// ======================================================
	// GUI
	// ======================================================

	private void createGUI() {

		// create the GUI components
		createGUIElements();
		createHeaderPanel();
		createTabPanel();
		updateFonts();
		setLabels();
	}

	
	protected void createTabPanel() {

		createTabPointPanel();
		createTabIntervalPanel();
		buildTabPanel();
		
	}

	protected abstract void buildTabPanel();
	protected abstract void buildHelpPanel();
	protected abstract void buildHeaderPanel();

	protected void createHeaderPanel() {

		createHelpPanel();
		buildHelpPanel();
	}

	private void createHelpPanel() {

		createOptionsButton();
		buildHelpPanel();
	}

	protected abstract void createTabIntervalPanel();
	protected abstract void createTabPointPanel();
	protected abstract void createGUIElements();
	protected void updateIntervalTab() {
		updateIntervalTable();
		model.updateIntervalGeoVisiblity();
	}
	
	protected abstract void updatePointsTab();
	
	protected abstract boolean isIntervalTabSelected();
	// =====================================
	// Update
	// =====================================

	private void updateGUI() {

		if (isIntervalTabSelected()) {
			updateIntervalTab();
	
		} else {
			updatePointsTab();
		}
	}

	/**
	 * Updates the tab panels and thus the entire GUI. Also updates the active
	 * EV to hide/show temporary GeoElements associated with the
	 * FunctionInspector (e.g. points, integral)
	 */
	private void updateTabPanels() {

		updateIntervalFields();
		model.updateGeos(isIntervalTabSelected());
		updateGUI();

	}

	protected abstract void updateIntervalFields();
	
	/**
	 * Updates the interval table. The max, min, roots, area etc. for the
	 * current interval are calculated and put into the IntervalTable model.
	 */
	private void updateIntervalTable() {

		isChangingValue = true;
		model.updateIntervalTable();
		isChangingValue = false;

	}

	/**
	 * Updates the XYTable with the coordinates of the current sample points and
	 * any related values (e.g. derivative, difference)
	 */
	protected abstract void updateXYTable();
	protected abstract void removeColumn();
	
	protected void setInspectorVisible(boolean isVisible) {
		if (isVisible) {
			app.getKernel().attach(this);
		} else {
			app.getKernel().detach(this);
			model.clearGeoList();
		}
	}

	// ====================================================
	// View Implementation
	// ====================================================

	public void update(GeoElement geo) {

		if (!model.isValid() || isChangingValue || isIniting) {
			return;
		}

		model.update(geo, !isIntervalTabSelected());

	}

	final public void updateVisualStyle(GeoElement geo) {
		update(geo);
	}

	public void add(GeoElement geo) {
	}

	public void remove(GeoElement geo) {
	}

	public void rename(GeoElement geo) {
	}

	public void updateAuxiliaryObject(GeoElement geo) {
	}

	public void repaintView() {
	}

	
	public void clearView() {
	}

	public void setMode(int mode, ModeSetter m) {
	}

	// ====================================================
	// Geo Selection Listener
	// ====================================================

	private void createGeoElementSlectionListener() {
		sl = new GeoElementSelectionListener() {
			public void geoElementSelected(GeoElement geo,
					boolean addToSelection) {
				insertGeoElement(geo);
			}
		};
	}

	/**
	 * Sets the function to be inspected and updates the entire GUI
	 * 
	 * @param geo
	 *            The function to be inspected
	 */
	public void insertGeoElement(GeoElement geo) {
		if (geo == null || !geo.isGeoFunction()) {
			return;
		}
		model.insertGeoElement(geo);

		updateTabPanels();
	}

	private void updateTestPoint() {

		if (isIniting) {
			return;
		}

		isChangingValue = true;

		model.updateTestPoint();

		isChangingValue = false;

	}


	public void windowLostFocus(WindowEvent e) {
		// do nothing
	}

	protected void setStart(double x) {
		try {
			model.setStart(x);
			// Application.debug("" + start);
			updateXYTable();
			updateTestPoint();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
	}
	
	protected abstract void changeStart(double x);
	
	
	public void changedNumberFormat() {
		this.updateGUI();
		this.updateIntervalFields();
		this.updateTestPoint();

	}

	protected abstract void createOptionsButton();
	protected abstract void doCopyToSpreadsheet();
	public int getViewID() {
		return App.VIEW_FUNCTION_INSPECTOR;
	}

	public void repaint() {
		App.debug("unimplemented");
	}

	public boolean isShowing() {
		App.debug("unimplemented");
		return false;
	}

	public void startBatchUpdate() {
		// TODO Auto-generated method stub

	}

	public void endBatchUpdate() {
		// TODO Auto-generated method stub

	}

}
