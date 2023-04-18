//package com.obiscr.chatgpt.settings;
//
//import com.intellij.application.options.PathMacrosCollector;
//import com.intellij.application.options.pathMacros.CustomPromptEditor;
//import com.intellij.application.options.pathMacros.PathMacroTable;
//import com.intellij.openapi.application.ApplicationBundle;
//import com.intellij.openapi.application.PathMacros;
//import com.intellij.openapi.diagnostic.Logger;
//import com.intellij.openapi.ui.Messages;
//import com.intellij.openapi.util.Couple;
//import com.intellij.openapi.util.NlsContexts;
//import com.intellij.openapi.util.Pair;
//import com.intellij.openapi.util.io.FileUtilRt;
//import com.intellij.openapi.util.text.StringUtil;
//import com.intellij.ui.JBColor;
//import com.intellij.ui.table.JBTable;
//import org.jetbrains.annotations.NotNull;
//
//import javax.swing.*;
//import javax.swing.table.AbstractTableModel;
//import javax.swing.table.DefaultTableCellRenderer;
//import javax.swing.table.TableColumn;
//import java.awt.*;
//import java.io.File;
//import java.util.List;
//import java.util.*;
//import java.util.regex.Pattern;
//
//public final class CustomPromptTable extends JBTable {
//  private static final Logger LOG = Logger.getInstance(PathMacroTable.class);
//  private final PathMacros myPathMacros = PathMacros.getInstance();
//  private final MyTableModel myTableModel = new MyTableModel();
//  private static final int KEY_COLUMN = 0;
//  private static final int VALUE_COLUMN = 1;
//  private static final Pattern PROMPT_PATTERN = Pattern.compile("\\$([\\w\\-.]+?)\\$");
//
//  private final List<Couple<String>> myPrompts = new ArrayList<>();
//  private static final Comparator<Pair<String, String>> PROMPT_COMPARATOR = Pair.comparingByFirst();
//
//  private final Collection<String> myUndefinedMacroNames;
//
//  public CustomPromptTable() {
//    this(null);
//  }
//
//  public CustomPromptTable(Collection<String> undefinedMacroNames) {
//    myUndefinedMacroNames = undefinedMacroNames;
//    setShowGrid(false);
//    setModel(myTableModel);
//    TableColumn column = getColumnModel().getColumn(KEY_COLUMN);
//    column.setCellRenderer(new DefaultTableCellRenderer() {
//      @Override
//      public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
//        final Component component = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
//        final String macroValue = getMacroValueAt(row);
//        component.setForeground(macroValue.length() == 0
//                                ? JBColor.RED
//                                : isSelected ? table.getSelectionForeground() : table.getForeground());
//        return component;
//      }
//    });
//    setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
//    //obtainData();
//
//    getEmptyText().setText("No custom prompt");
//  }
//
//  public String getMacroValueAt(int row) {
//    return (String) getValueAt(row, VALUE_COLUMN);
//  }
//
//  public void addMacro() {
//    final String title = "Add Custom Prompt";
//    final CustomPromptEditor macroEditor = new CustomPromptEditor(title, "", "", new AddValidator(title));
//    if (macroEditor.showAndGet()) {
//      final String name = macroEditor.getKey();
//      myPrompts.add(Couple.of(name, macroEditor.getValue()));
//      myPrompts.sort(PROMPT_COMPARATOR);
//      final int index = indexOfMacroWithName(name);
//      LOG.assertTrue(index >= 0);
//      myTableModel.fireTableDataChanged();
//      setRowSelectionInterval(index, index);
//    }
//  }
//
//  private boolean isValidRow(int selectedRow) {
//    return selectedRow >= 0 && selectedRow < myPrompts.size();
//  }
//
//  public void removeSelectedMacros() {
//    final int[] selectedRows = getSelectedRows();
//    if(selectedRows.length == 0) return;
//    Arrays.sort(selectedRows);
//    final int originalRow = selectedRows[0];
//    for (int i = selectedRows.length - 1; i >= 0; i--) {
//      final int selectedRow = selectedRows[i];
//      if (isValidRow(selectedRow)) {
//        myPrompts.remove(selectedRow);
//      }
//    }
//    myTableModel.fireTableDataChanged();
//    if (originalRow < getRowCount()) {
//      setRowSelectionInterval(originalRow, originalRow);
//    } else if (getRowCount() > 0) {
//      final int index = getRowCount() - 1;
//      setRowSelectionInterval(index, index);
//    }
//  }
//
//  public void commit() {
//    myPathMacros.removeAllMacros();
//    for (Couple<String> pair : myPrompts) {
//      final String value = pair.getSecond();
//      if (!StringUtil.isEmptyOrSpaces(value)) {
//        String path = StringUtil.trimEnd(value.replace(File.separatorChar, '/'), "/");
//        myPathMacros.setMacro(pair.getFirst(), path);
//      }
//    }
//  }
//
//  public void reset() {
//    obtainData();
//  }
//
//  private boolean hasMacroWithName(String name) {
//    if (PathMacros.getInstance().getSystemMacroNames().contains(name)) {
//      return true;
//    }
//
//    for (Couple<String> macro : myPrompts) {
//      if (name.equals(macro.getFirst())) {
//        return true;
//      }
//    }
//    return false;
//  }
//
//  private int indexOfMacroWithName(String name) {
//    for (int i = 0; i < myPrompts.size(); i++) {
//      final Couple<String> pair = myPrompts.get(i);
//      if (name.equals(pair.getFirst())) {
//        return i;
//      }
//    }
//    return -1;
//  }
//
//  private void obtainData() {
//    obtainMacroPairs(myPrompts);
//    myTableModel.fireTableDataChanged();
//  }
//
//  private void obtainMacroPairs(@NotNull List<Couple<String>> macros) {
//    macros.clear();
//    final Map<String, String> macroNames = myPathMacros.getUserMacros();
//    for (String name : macroNames.keySet()) {
//      macros.add(Couple.of(name, FileUtilRt.toSystemDependentName(macroNames.get(name))));
//    }
//
//    if (myUndefinedMacroNames != null) {
//      for (String undefinedMacroName : myUndefinedMacroNames) {
//        macros.add(Couple.of(undefinedMacroName, ""));
//      }
//    }
//    macros.sort(PROMPT_COMPARATOR);
//  }
//
//  public void editMacro() {
//    if (getSelectedRowCount() != 1) {
//      return;
//    }
//    final int selectedRow = getSelectedRow();
//    final Couple<String> pair = myPrompts.get(selectedRow);
//    final String title = ApplicationBundle.message("title.edit.variable");
//    final String macroName = pair.getFirst();
//    final CustomPromptEditor macroEditor = new CustomPromptEditor(title, macroName, pair.getSecond(), new EditValidator());
//    if (macroEditor.showAndGet()) {
//      myPrompts.remove(selectedRow);
//      myPrompts.add(Couple.of(macroEditor.getKey(), macroEditor.getValue()));
//      myPrompts.sort(PROMPT_COMPARATOR);
//      myTableModel.fireTableDataChanged();
//    }
//  }
//
//  public boolean isModified() {
//    final ArrayList<Couple<String>> macros = new ArrayList<>();
//    obtainMacroPairs(macros);
//    return !macros.equals(myPrompts);
//  }
//
//  private final class MyTableModel extends AbstractTableModel{
//    @Override
//    public int getColumnCount() {
//      return 2;
//    }
//
//    @Override
//    public int getRowCount() {
//      return myPrompts.size();
//    }
//
//    @Override
//    public Class<?> getColumnClass(int columnIndex) {
//      return String.class;
//    }
//
//    @Override
//    public Object getValueAt(int rowIndex, int columnIndex) {
//      final Couple<String> pair = myPrompts.get(rowIndex);
//      return switch (columnIndex) {
//        case KEY_COLUMN -> pair.getFirst();
//        case VALUE_COLUMN -> pair.getSecond();
//        default -> {
//          LOG.error("Wrong indices");
//          yield null;
//        }
//      };
//    }
//
//    @Override
//    public String getColumnName(int columnIndex) {
//      return switch (columnIndex) {
//        case KEY_COLUMN -> ApplicationBundle.message("column.name");
//        case VALUE_COLUMN -> ApplicationBundle.message("column.value");
//        default -> null;
//      };
//    }
//  }
//
//  private List<String> getPrompts() {
//    Iterator<Couple<String>> iterator = myPrompts.stream().iterator();
//    List<String> promptKeys = new ArrayList<>();
//    while (iterator.hasNext()) {
//      promptKeys.add(iterator.next().getFirst());
//    }
//    return promptKeys;
//  }
//
//  private final class AddValidator implements CustomPromptEditor.Validator {
//    private final @NlsContexts.DialogTitle String myTitle;
//
//    AddValidator(@NlsContexts.DialogTitle String title) {
//      myTitle = title;
//    }
//
//    @Override
//    public boolean checkName(String name) {
//      if (name.length() == 0) return false;
//      return PathMacrosCollector.MACRO_PATTERN.matcher("$" + name + "$").matches();
//    }
//
//    @Override
//    public boolean isOK(String name, String value) {
//      if(name.length() == 0) return false;
//      if (hasMacroWithName(name)) {
//        Messages.showErrorDialog(CustomPromptTable.this,
//                                 "Prompt with name " + name + " already exists", myTitle);
//        return false;
//      }
//      return true;
//    }
//  }
//
//  private static class EditValidator implements CustomPromptEditor.Validator {
//    @Override
//    public boolean checkName(String name) {
//      if (name.isEmpty() || PathMacros.getInstance().getSystemMacroNames().contains(name)) {
//        myPrompts.get
//        return false;
//      }
//
//      return PROMPT_PATTERN.matcher("$" + name + "$").matches();
//    }
//
//    @Override
//    public boolean isOK(String name, String value) {
//      return checkName(name);
//    }
//  }
//}
