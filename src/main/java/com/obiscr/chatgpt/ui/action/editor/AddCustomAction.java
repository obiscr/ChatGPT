package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.CommonBundle;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.components.JBLabel;
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
       new CustomActionDialog(e.getProject(),runnable).show();
    }

    static class CustomActionDialog extends DialogWrapper {
        private final Runnable runnable;
        private JPanel panel;
        private final JBTextField question = new JBTextField();
        private final Project project;

        public CustomActionDialog(@Nullable Project project, Runnable runnable) {
            super(project);
            setTitle("New Custom Action");
            setResizable(false);
            init();
            setOKActionEnabled(true);
            this.runnable = runnable;
            this.project = project;
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
                    runnable.run();
                    dispose();
                    close(OK_EXIT_CODE);
                }
            };

            DialogWrapperAction mySendAndSaveAction = new DialogWrapperAction("Send & Save") {
                @Override
                protected void doAction(ActionEvent e) {
                    project.putUserData(ACTIVE_PREFIX, question.getText());
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
            JPanel jPanel = new NonOpaquePanel(new GridLayout(4,1));
            jPanel.add(new JBLabel("Prefix:"));
            question.getEmptyText().setText("Type new custom action here");
            jPanel.add(question);

            jPanel.add(new JBLabel("Code block:"));
            JBTextField codeBlock = new JBTextField();
            codeBlock.setEditable(false);
            codeBlock.setText("CodeBlock will auto-fill the text content you select");

            jPanel.add(codeBlock);
            return jPanel;
        }
    }
}
