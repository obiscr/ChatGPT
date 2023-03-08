package com.obiscr.chatgpt.settings;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.cellvalidators.*;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.fields.ExtendableTextField;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.JBInsets;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuzi
 */
public class CustomActionsPanel implements Configurable, Disposable {

    private JPanel myMainPanel;

    private JPanel customActionsTitledBorderBox;
    private final Disposable myDisposable = Disposer.newDisposable();
    private final ListTableModel<String> myModel = new ListTableModel<>() {
        @Override
        public void addRow() {
            addRow("");
        }
    };

    private final JBTable myTable = new JBTable(myModel) {
        @Override
        public void editingCanceled(ChangeEvent e) {
            int row = getEditingRow();
            super.editingCanceled(e);
            if (row >= 0 && row < myModel.getRowCount() && StringUtil.isEmpty(myModel.getRowValue(row))) {
                myModel.removeRow(row);
            }
        }
    };

    public CustomActionsPanel() {
        init();
    }

    private void init() {
        myMainPanel.add(new JBLabel("Double click to edit it."), BorderLayout.NORTH);
        myMainPanel.add(createComponent(), BorderLayout.CENTER);
    }

    @Override
    public void dispose() {

    }

    @Override
    public String getDisplayName() {
        return "Custom Actions";
    }

    @Override
    public @Nullable JComponent createComponent() {
        myModel.setColumnInfos(new ColumnInfo[]{new ColumnInfo<String, String>(""){

            @Override
            public @Nullable String valueOf(String s) {
                return s;
            }

            @Override
            public boolean isCellEditable(String s) {
                return true;
            }

            @Override
            public void setValue(String s, String value) {
                int row = myTable.getSelectedRow();
                if (StringUtil.isEmpty(value) && row >= 0 && row < myModel.getRowCount()) {
                    myModel.removeRow(row);
                }

                List<String> items = new ArrayList<>(myModel.getItems());
                items.set(row, value);
                myModel.setItems(items);
                myModel.fireTableCellUpdated(row, TableModelEvent.ALL_COLUMNS);

                myTable.repaint();
            }
        }});
        myTable.getColumnModel().setColumnMargin(0);
        myTable.setShowColumns(false);
        myTable.setShowGrid(false);
        myTable.getEmptyText().setText("No prefix configured");
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTable.setToolTipText("Double click to edit it");

        ExtendableTextField cellEditor = new ExtendableTextField();
        DefaultCellEditor editor = new StatefulValidatingCellEditor(cellEditor, myDisposable).
                withStateUpdater(vi -> ValidationUtils.setExtension(cellEditor, vi));
        editor.setClickCountToStart(2);
        myTable.setDefaultEditor(Object.class, editor);

        myTable.setDefaultRenderer(Object.class, new ValidatingTableCellRendererWrapper(new ColoredTableCellRenderer() {
            {
                setIpad(new JBInsets(0, 0, 0, 0));}

            @Override
            protected void customizeCellRenderer(@NotNull JTable table, @Nullable Object value, boolean selected, boolean hasFocus, int row, int column) {
                if (row >= 0 && row < myModel.getRowCount()) {
                    String prefix = myModel.getRowValue(row);
                    setForeground(selected ? table.getSelectionForeground() : table.getForeground());
                    setBackground(selected ? table.getSelectionBackground() : table.getBackground());
                    append(prefix, SimpleTextAttributes.REGULAR_ATTRIBUTES);
                    setToolTipText("Double click to edit it.");
                }
            }

            @Override
            protected SimpleTextAttributes modifyAttributes(SimpleTextAttributes attributes) {
                return attributes;
            }
        }).bindToEditorSize(cellEditor::getPreferredSize));

        return ToolbarDecorator.createDecorator(myTable).disableUpDownActions().createPanel();
    }

    @Override
    public boolean isModified() {
        List<String> prefix = new ArrayList<>(myModel.getItems());
        return !OpenAISettingsState.getInstance().customActionsPrefix.equals(prefix);
    }

    @Override
    public void apply() {
        myTable.editingStopped(null);

        List<String> list = OpenAISettingsState.getInstance().customActionsPrefix;
        list.clear();
        list.addAll(myModel.getItems());
    }

    @Override
    public void reset() {
        List<String> prefix = new ArrayList<>(OpenAISettingsState.
                getInstance().customActionsPrefix);
        myModel.setItems(prefix);
    }

    private void createUIComponents() {
        customActionsTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("Custom Actions Settings");
        customActionsTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        myMainPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(myDisposable);
    }
}
