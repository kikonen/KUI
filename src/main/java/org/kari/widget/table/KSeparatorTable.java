package org.kari.widget.table;

import java.awt.Rectangle;

import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import ca.odell.glazedlists.SeparatorList;
import ca.odell.glazedlists.swing.EventTableModel;

/**
 * @author <a href="mailto:jesse@swank.ca">Jesse Wilson</a>
 */
public class KSeparatorTable extends JTable {

    /** working with separator cells */
    private TableCellRenderer separatorRenderer;
    private TableCellEditor separatorEditor;

    public KSeparatorTable(EventTableModel tableModel) {
        this(tableModel, null);
    }

    public KSeparatorTable(EventTableModel tableModel, TableColumnModel tableColumnModel) {
        super(tableModel, tableColumnModel);
        setUI(new SpanTableUI());

        // use a toString() renderer for the separator
        this.separatorRenderer = getDefaultRenderer(Object.class);
    }

    @Override
    public void setModel(TableModel tableModel) {
        if(!(tableModel instanceof EventTableModel))
            throw new IllegalArgumentException("tableModel is expected to be an EventTableModel");
        super.setModel(tableModel);
    }

    /**
     * A convenience method to cast the TableModel to the expected
     * EventTableModel implementation.
     *
     * @return the EventTableModel that backs this table
     */
    private EventTableModel getEventTableModel() {
        return (EventTableModel) getModel();
    }

    @Override
    public Rectangle getCellRect(int row, int column, boolean includeSpacing) {
        final EventTableModel eventTableModel = getEventTableModel();

        // sometimes JTable asks for a cellrect that doesn't exist anymore, due
        // to an editor being installed before a bunch of rows were removed.
        // In this case, just return an empty rectangle, since it's going to
        // be discarded anyway
        if(row >= eventTableModel.getRowCount()) {
            return new Rectangle();
        }

        // if it's the separator row, return the entire row as one big rectangle
        Object rowValue = eventTableModel.getElementAt(row);
        if(rowValue instanceof SeparatorList.Separator) {
            Rectangle firstColumn = super.getCellRect(row, 0, includeSpacing);
            Rectangle lastColumn = super.getCellRect(row, getColumnCount() - 1, includeSpacing);
            return firstColumn.union(lastColumn);

        // otherwise it's business as usual
        } else {
            return super.getCellRect(row, column, includeSpacing);
        }
    }

    public Rectangle getCellRectWithoutSpanning(int row, int column, boolean includeSpacing) {
        return super.getCellRect(row, column, includeSpacing);
    }

    @Override
    public Object getValueAt(int row, int column) {
        final Object rowValue = getEventTableModel().getElementAt(row);

        // if it's the separator row, return the value directly
        if(rowValue instanceof SeparatorList.Separator)
            return rowValue;

        // otherwise it's business as usual
        return super.getValueAt(row, column);
    }

    @Override
    public TableCellRenderer getCellRenderer(int row, int column) {
        // if it's the separator row, use the separator renderer
        if(getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator)
            return separatorRenderer;

        // otherwise it's business as usual
        return super.getCellRenderer(row, column);
    }

    @Override
    public TableCellEditor getCellEditor(int row, int column) {
        // if it's the separator row, use the separator editor
        if(getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator)
            return separatorEditor;

        // otherwise it's business as usual
        return super.getCellEditor(row, column);
    }

    @Override
    public boolean isCellEditable(int row, int column) {
        // if it's the separator row, it is always editable (so that the separator can be collapsed/expanded)
        if(getEventTableModel().getElementAt(row) instanceof SeparatorList.Separator)
            return true;

        // otherwise it's business as usual
        return super.isCellEditable(row, column);
    }

    /**
     * Get the renderer for separator rows.
     */
    public TableCellRenderer getSeparatorRenderer() { return separatorRenderer; }
    public void setSeparatorRenderer(TableCellRenderer separatorRenderer) { this.separatorRenderer = separatorRenderer; }

    /**
     * Get the editor for separator rows.
     */
    public TableCellEditor getSeparatorEditor() { return separatorEditor; }
    public void setSeparatorEditor(TableCellEditor separatorEditor) { this.separatorEditor = separatorEditor; }

    @Override
    public void tableChanged(TableModelEvent e) {
        // stop edits when the table changes, or else we might
        // get a relocated edit in the wrong cell!
        if(isEditing()) {
            super.getCellEditor().cancelCellEditing();
        }

        // handle the change event
        super.tableChanged(e);
    }
}