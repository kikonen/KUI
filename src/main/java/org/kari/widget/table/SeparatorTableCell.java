package org.kari.widget.table;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.AbstractCellEditor;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import org.kari.util.Icons;

import ca.odell.glazedlists.SeparatorList;

/**
 * Defualt renderer for separator cells
 *  
 * @author kari
 */
public class SeparatorTableCell
    extends AbstractCellEditor
    implements
        TableCellRenderer,
        TableCellEditor,
        ActionListener
{
    /** application appearance */
    public static final Color GLAZED_LISTS_DARK_BROWN = new Color(36, 23, 10);
    public static final Color GLAZED_LISTS_MEDIUM_BROWN = new Color(69, 64, 56);
    public static final Color GLAZED_LISTS_MEDIUM_LIGHT_BROWN = new Color(150, 140, 130);
    public static final Color GLAZED_LISTS_LIGHT_BROWN = new Color(246, 237, 220);
    public static final Color GLAZED_LISTS_LIGHT_BROWN_DARKER = new Color(231, 222, 205);
    //public static final Icon THROBBER_ACTIVE = loadIcon("resources/throbber-active.gif");
    //public static final Icon THROBBER_STATIC = loadIcon("resources/throbber-static.gif");
    public static final Icon EXPANDED_ICON = Icons.triangle(9, SwingConstants.EAST, GLAZED_LISTS_MEDIUM_LIGHT_BROWN);
    public static final Icon COLLAPSED_ICON = Icons.triangle(9, SwingConstants.SOUTH, GLAZED_LISTS_MEDIUM_LIGHT_BROWN);
    public static final Icon X_ICON = Icons.x(10, 5, GLAZED_LISTS_MEDIUM_LIGHT_BROWN);
    public static final Border EMPTY_ONE_PIXEL_BORDER = BorderFactory.createEmptyBorder(1, 1, 1, 1);
    public static final Border EMPTY_TWO_PIXEL_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);

    
    private final MessageFormat nameFormat = new MessageFormat("{0} ({1})");

    /** the separator list to lock */
    private final SeparatorList separatorList;

    private final JPanel panel = new JPanel(new BorderLayout());
    private final JButton expandButton;
    private final JLabel nameLabel = new JLabel();

    private SeparatorList.Separator<Object> separator;

    public SeparatorTableCell(SeparatorList separatorList) {
        this.separatorList = separatorList;

        this.expandButton = new JButton(EXPANDED_ICON);
        this.expandButton.setOpaque(false);
        this.expandButton.setBorder(EMPTY_TWO_PIXEL_BORDER);
        this.expandButton.setIcon(EXPANDED_ICON);
        this.expandButton.setContentAreaFilled(false);

//        this.nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        this.nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

        this.expandButton.addActionListener(this);

        this.panel.setBackground(GLAZED_LISTS_LIGHT_BROWN);
        this.panel.add(expandButton, BorderLayout.WEST);
        this.panel.add(nameLabel, BorderLayout.CENTER);
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
        configure(value);
        return panel;
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
        configure(value);
        return panel;
    }

    @Override
    public Object getCellEditorValue() {
        return this.separator;
    }

    private void configure(Object value) {
        this.separator = (SeparatorList.Separator<Object>)value;
        Object object = separator.first();
        if (object == null) {
            // handle 'late' rendering calls after this separator is invalid
            return; 
        }
        expandButton.setIcon(separator.getLimit() == 0 ? EXPANDED_ICON : COLLAPSED_ICON);
        nameLabel.setText(nameFormat.format(new Object[] {
            getObjectName(object), 
            new Integer(separator.size())}));
    }

    public String getObjectName(Object pObject) {
        return pObject != null
            ? pObject.toString()
            : null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        separatorList.getReadWriteLock().writeLock().lock();
        boolean collapsed;
        try {
            collapsed = separator.getLimit() == 0;
            separator.setLimit(collapsed ? Integer.MAX_VALUE : 0);
        } finally {
            separatorList.getReadWriteLock().writeLock().unlock();
        }
        expandButton.setIcon(collapsed ? COLLAPSED_ICON : EXPANDED_ICON);
    }
}
