package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.CommonBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.EditorSettings;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.editor.impl.DocumentImpl;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextField;
import com.intellij.ui.components.panels.NonOpaquePanel;
import com.intellij.ui.components.panels.VerticalLayout;
import com.intellij.ui.scale.JBUIScale;
import com.intellij.util.ui.JBUI;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import com.obiscr.chatgpt.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import static com.obiscr.chatgpt.ui.action.editor.CustomAction.ACTIVE_PREFIX;
import static com.obiscr.chatgpt.ui.action.editor.CustomAction.ACTIVE_PROMPT;

/**
 * @author Wuzi
 */
public class AddCustomAction extends AnAction {

    private final Runnable runnable;
    public AddCustomAction(Runnable runnable) {
        super(() -> "Add Custom Action", AllIcons.Actions.AddList);
        this.runnable = runnable;
    }
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // Set the file type
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        VirtualFile virtualFile = e.getData(CommonDataKeys.VIRTUAL_FILE);
        e.getProject().putUserData(CustomAction.ACTIVE_FILE_TYPE, virtualFile.getFileType());

        // Set the file prompt
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();
        e.getProject().putUserData(ACTIVE_PROMPT, selectedText);

        new CustomActionDialog(e.getProject(),runnable).show();
    }

    static class CustomActionDialog extends DialogWrapper {
        private final Runnable runnable;
        private JPanel panel;
        private final JBTextField question = new JBTextField();
        private final Project project;
        private Editor editor;

        public CustomActionDialog(@Nullable Project project, Runnable runnable) {
            super(project);
            this.project = project;
            setTitle("New Custom Action");
            setResizable(false);
            init();
            setOKActionEnabled(true);
            this.runnable = runnable;
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            panel = new JPanel();
            panel.setLayout(new VerticalLayout(JBUIScale.scale(8)));
            panel.setBorder(JBUI.Borders.empty(10));
            panel.add(createItemPanel());
            return panel;
        }

        @Override
        public JComponent getPreferredFocusedComponent() {
            return panel;
        }

        @Override
        protected @NotNull DialogStyle getStyle() {
            return DialogStyle.COMPACT;
        }

        @Override
        protected Action @NotNull [] createActions() {
            myOKAction = new DialogWrapperAction("Send") {
                @Override
                protected void doAction(ActionEvent e) {
                    project.putUserData(ACTIVE_PREFIX, question.getText());
                    project.putUserData(ACTIVE_PROMPT, editor.getDocument().getText());
                    runnable.run();
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            DialogWrapperAction mySendAndSaveAction = new DialogWrapperAction("Send And Save") {
                @Override
                protected void doAction(ActionEvent e) {
                    project.putUserData(ACTIVE_PREFIX, question.getText());
                    project.putUserData(ACTIVE_PROMPT, editor.getDocument().getText());
                    runnable.run();
                    if (!StringUtil.isEmpty(question.getText())) {
                        List<String> customActionsPrefix = OpenAISettingsState.getInstance().customActionsPrefix;
                        customActionsPrefix.add(question.getText());
                    }
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            myCancelAction = new DialogWrapperAction(CommonBundle.getCancelButtonText()) {
                @Override
                protected void doAction(ActionEvent e) {
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };
            ArrayList<Action> actions = new ArrayList<>();
            actions.add(myOKAction);
            actions.add(mySendAndSaveAction);
            actions.add(myCancelAction);
            return actions.toArray(new Action[0]);
        }

        private JPanel createItemPanel() {
            JPanel basePanel = new JPanel(new BorderLayout());
            JPanel prefixPanel = new NonOpaquePanel(new BorderLayout());
            JBLabel prefixLabel = new JBLabel("Prefix: ");
            prefixLabel.setBorder(JBUI.Borders.emptyBottom(5));
            prefixPanel.add(prefixLabel, BorderLayout.NORTH);
            question.getEmptyText().setText("Type new custom action here");
            prefixPanel.add(question,BorderLayout.CENTER);
            prefixPanel.setBorder(JBUI.Borders.empty(5,0));
            basePanel.add(prefixPanel,BorderLayout.NORTH);

            JPanel codePanel = new NonOpaquePanel(new BorderLayout());
            JBLabel codeLabel = new JBLabel("Code block:");
            codeLabel.setBorder(JBUI.Borders.empty(10,0,5,0));
            codePanel.add(codeLabel,BorderLayout.NORTH);
            EditorFactory editorFactory = EditorFactory.getInstance();
            editor = editorFactory.createEditor(new DocumentImpl((String) project.
                    getUserData(ACTIVE_PROMPT)),project,
                    (FileType) project.getUserData(CustomAction.ACTIVE_FILE_TYPE),false);
            editor.getDocument().addDocumentListener(new DocumentListener() {
                @Override
                public void documentChanged(@NotNull DocumentEvent event) {
                    editor.getComponent().repaint();
                }
            });
            EditorSettings editorSettings = editor.getSettings();
            editorSettings.setVirtualSpace(false);
            editorSettings.setLineMarkerAreaShown(false);
            editorSettings.setIndentGuidesShown(true);
            editorSettings.setLineNumbersShown(true);
            editorSettings.setFoldingOutlineShown(false);
            editorSettings.setAdditionalColumnsCount(3);
            editorSettings.setAdditionalLinesCount(3);
            editorSettings.setCaretRowShown(false);
            editorSettings.setAnimatedScrolling(true);
            codePanel.setPreferredSize(new Dimension(600,400));
            codePanel.add(editor.getComponent(),BorderLayout.CENTER);
            basePanel.add(codePanel,BorderLayout.CENTER);
            return basePanel;
        }
    }
}
