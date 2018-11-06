package org.geogebra.web.full.gui.toolbarpanel;

import org.geogebra.common.gui.SetLabels;
import org.geogebra.common.gui.view.table.TableValuesListener;
import org.geogebra.common.gui.view.table.TableValuesModel;
import org.geogebra.web.html5.main.AppW;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Widget;

/**
 * 
 * 
 */
public class TableTab extends ToolbarPanel.ToolbarTab implements SetLabels, TableValuesListener {

	private ToolbarPanel toolbarPanel;
	private AppW app;

	/**
	 * @param toolbarPanel
	 *            toolbar panel
	 */
	public TableTab(ToolbarPanel toolbarPanel) {
		this.toolbarPanel = toolbarPanel;
		this.app = toolbarPanel.app;
		buildGui();
		setLabels();
		getView().getTableValuesModel().registerListener(this);
	}

	@Override
	protected void onActive() {
		buildGui();
		getView().setHeight(getOffsetHeight());
	}

	private TableValuesViewW getView() {
		return (TableValuesViewW) app.getGuiManager().getTableValuesView();
	}

	private void buildGui() {
		Widget w = getView().getWidget();
		if (w == null) {
			return;
		}
		setWidget(w);
		if (getView().isEmpty()) {
			w.getElement().getParentElement().addClassName("tableViewParent");
		}
	}

	@Override
	public void setLabels() {
		getView().setLabels();
	}

	@Override
	public void onResize() {
		int w = this.toolbarPanel.getTabWidth();
		int h = toolbarPanel.getOffsetHeight();
		if (w < 0 || h < 0) {
			return;
		}
		setWidth(2 * w + "px");
		setHeight(h + "px");
		getElement().getStyle().setLeft(2 * w, Unit.PX);

		getView().setHeight(h);
	}

	@Override
	public void notifyColumnRemoved(TableValuesModel model, int column) {
		buildGui();
	}

	@Override
	public void notifyColumnChanged(TableValuesModel model, int column) {
		buildGui();
	}

	@Override
	public void notifyColumnAdded(TableValuesModel model, int column) {
		buildGui();
	}

	@Override
	public void notifyColumnHeaderChanged(TableValuesModel model, int column) {
		buildGui();
	}

	@Override
	public void notifyDatasetChanged(TableValuesModel model) {
		buildGui();
	}

}
