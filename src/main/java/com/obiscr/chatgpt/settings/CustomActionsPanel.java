package com.obiscr.chatgpt.settings;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import com.intellij.ui.*;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.table.JBTable;
import com.intellij.util.ui.ColumnInfo;
import com.intellij.util.ui.ListTableModel;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author Wuzi
 */
public class CustomActionsPanel implements Configurable, Disposable {

    private JPanel myMainPanel;
    private JPanel customActionsTitledBorderBox;
    private final Disposable myDisposable = Disposer.newDisposable();
    private final List<MyPrompt> promptList = new ArrayList<>();
    private final MyTableModel myModel = new MyTableModel(promptList);
    private final JBTable myTable = new JBTable(myModel);

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
        return "Custom Prompt";
    }

    @Override
    public @Nullable JComponent createComponent() {
        myTable.getColumnModel().setColumnMargin(0);
        myTable.setShowColumns(true);
        myTable.setShowGrid(true);
        myTable.getEmptyText().setText("No prompt configured");
        myTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        myTable.setToolTipText("Double click to edit it");
        myTable.getTableHeader().setDefaultRenderer(new MyTableCellRenderer());
        myTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    doEditAction();
                }
            }
        });
        return ToolbarDecorator.createDecorator(myTable)
                .setAddAction(anActionButton -> {
                    doAddAction();
                })
                .setEditAction(anActionButton -> {
                    doEditAction();
                })
                .createPanel();
    }

    public void doAddAction() {
        final CustomPromptEditor macroEditor = new CustomPromptEditor("Add Custom Prompt", "", "", new AddValidator("Add Custom Prompt",myModel));
        if (macroEditor.showAndGet()) {
            final String key = macroEditor.getKey();
            final String value = macroEditor.getValue();
            myModel.addRow(new MyPrompt(key,value,myModel.getRowCount()));
        }
    }

    public void doEditAction() {
        int selectedRow = myTable.getSelectedRow();
        if (selectedRow < 0) {
            return;
        }
        MyPrompt rowValue = myModel.getRowValue(selectedRow);
        final CustomPromptEditor macroEditor = new CustomPromptEditor("Edit Custom Prompt", rowValue.name, rowValue.value, new EditValidator());
        if (macroEditor.showAndGet()) {
            myModel.removeRow(selectedRow);
            final String key = macroEditor.getKey();
            final String value = macroEditor.getValue();
            myModel.addRow(new MyPrompt(key,value,myModel.getRowCount()));
        }
    }

    @Override
    public boolean isModified() {
        List<MyPrompt> prompts = new ArrayList<>(myModel.getItems());
        Map<String, String> customPrompts = OpenAISettingsState.getInstance().customPrompts;
        for (MyPrompt prompt : prompts) {
            if (!customPrompts.containsKey(prompt.name)) {
                return true;
            }
            String value = customPrompts.get(prompt.name);
            if (!prompt.value.equals(value)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void apply() {
        myTable.editingStopped(null);

        Map<String, String> customPrompts = OpenAISettingsState.getInstance().customPrompts;
        customPrompts.clear();
        List<MyPrompt> prompts = new ArrayList<>(myModel.getItems());
        for (MyPrompt prompt : prompts) {
            customPrompts.put(prompt.name, prompt.value);
        }
    }

    @Override
    public void reset() {
        List<MyPrompt> prompts = new ArrayList<>();
        Map<String, String> customPrompts = OpenAISettingsState.getInstance().customPrompts;
        for (Map.Entry<String, String> prompt : customPrompts.entrySet()) {
            prompts.add(new MyPrompt(prompt.getKey(), prompt.getValue(), myModel.getRowCount()));
        }
        myModel.setItems(prompts);
    }

    private void createUIComponents() {
        customActionsTitledBorderBox = new JPanel(new BorderLayout());
        TitledSeparator tsUrl = new TitledSeparator("Custom Prompt Settings");
        customActionsTitledBorderBox.add(tsUrl,BorderLayout.CENTER);

        myMainPanel = new JPanel(new BorderLayout());
    }

    @Override
    public void disposeUIResources() {
        Disposer.dispose(myDisposable);
    }

    static class MyColumnInfo extends ColumnInfo<MyPrompt, String> {

        public MyColumnInfo(String name) {
            super(name);
        }

        @Override
        public @Nullable String valueOf(MyPrompt prompt) {
            return getName().equals("Prompt Name") ? prompt.name : prompt.value;
        }
    }

    static class MyPrompt {
        private final String name;
        private final String value;
        private final int order;

        public MyPrompt(String value, int order) {
            this(value,value,order);
        }
        public MyPrompt(String name, String value, int order) {
            this.name = name;
            this.value = value;
            this.order = order;
        }
    }

    private static final class AddValidator implements CustomPromptEditor.Validator {
        private final String myTitle;
        private final MyTableModel myModel;
        AddValidator(String title, MyTableModel model) {
            myTitle = title;
            myModel = model;
        }

        @Override
        public boolean checkName(String name) {
            return name.length() != 0;
        }

        @Override
        public boolean isOK(String name, String value) {
            if(name.length() == 0) {
                return false;
            }
            if (myModel.containsName(name)) {
                Messages.showErrorDialog(
                        "Prompt with name " + name + " already exists.", myTitle);
                return false;
            }
            return true;
        }
    }

    private static final class EditValidator implements CustomPromptEditor.Validator {

        @Override
        public boolean checkName(String name) {
            return name.length() != 0;
        }

        @Override
        public boolean isOK(String name, String value) {
            return checkName(name);
        }
    }

    static class MyTableModel extends AbstractTableModel {

        private List<MyPrompt> prompts;

        public MyTableModel(List<MyPrompt> prompts) {
            this.prompts = new ArrayList<>(prompts);
        }

        @Override
        public int getRowCount() {
            return prompts.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            MyPrompt prompt = prompts.get(rowIndex);
            return columnIndex == 0 ? prompt.name : prompt.value;
        }

        public List<MyPrompt> getItems() {
            return prompts;
        }

        public boolean containsName(String name) {
            for (MyPrompt prompt : prompts) {
                if (prompt.name.equals(name)) {
                    return true;
                }
            }
            return false;
        }

        public void addRow(MyPrompt prompt) {
            prompts.add(prompt);
            fireTableDataChanged();
        }

        public MyPrompt getRowValue(int selectedIndex) {
            return prompts.get(selectedIndex);
        }

        public void removeRow(int selectedIndex) {
            prompts.remove(selectedIndex);
            fireTableDataChanged();
        }

        public void setItems(List<MyPrompt> prompts) {
            this.prompts = new ArrayList<>(prompts);
            fireTableDataChanged();
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Prompt Name" : "Prompt Value";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return String.class;
        }
    }

    static class MyTableCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            setHorizontalTextPosition(SwingConstants.LEFT);
            if (column == 0) {
                setToolTipText("<html>The name displayed in the menu, and it should be as short as possible.");
            } else {
                setToolTipText("<html>When asking a question, the prompt content sent to AI.");
            }
            setIcon(AllIcons.General.ContextHelp);
            return component;
        }
    }
}

