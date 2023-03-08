package com.obiscr.chatgpt.ui.action.editor;

import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.Key;
import com.obiscr.chatgpt.icons.ChatGPTIcons;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.settings.OpenAISettingsState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Wuzi
 */
public class CustomAction extends AbstractEditorAction {

    public static final Key ACTIVE_PREFIX = Key.create("ActivePrefix");
    public static final Key ACTIVE_PROMPT = Key.create("ActivePrompt");
    public static final Key ACTIVE_FILE_TYPE = Key.create("ActiveFileType");

    public CustomAction() {
        super(() -> ChatGPTBundle.message("action.code.custom.action"));
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Runnable runnable = () -> {
            doActionPerformed(e);
        };

        ListPopup actionGroupPopup = JBPopupFactory.getInstance().createActionGroupPopup("Custom Action Popups",
                new CustomPrefixActionGroup(runnable), e.getDataContext(), true, null, Integer.MAX_VALUE);
        actionGroupPopup.showInBestPositionFor(e.getData(CommonDataKeys.EDITOR));
    }

    static class CustomPrefixActionGroup extends ActionGroup {
        private final Runnable runnable;
        public CustomPrefixActionGroup(Runnable runnable) {
            initialization();
            this.runnable = runnable;
        }

        private List<AnAction> initialization(){
            List<AnAction> anActionList = new ArrayList<>();
            anActionList.add(new AddCustomAction(runnable));
            anActionList.add(new Separator());
            return anActionList;
        }

        @Override
        public AnAction @NotNull [] getChildren(@Nullable AnActionEvent e) {
            List<AnAction> anActionList = initialization();
            for (String customActionsPrefix : OpenAISettingsState.getInstance().customActionsPrefix) {
                anActionList.add(new CustomActionItem(customActionsPrefix, this.runnable));
            }
            return anActionList.toArray(AnAction[]::new);
        }
    }

    static class CustomActionItem extends AnAction {

        private final Runnable runnable;
        private final String prefix;
        public CustomActionItem(String prefix, Runnable runnable) {
            super(() -> prefix, ChatGPTIcons.TOOL_WINDOW);
            this.runnable = runnable;
            this.prefix = prefix;
        }
        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            e.getProject().putUserData(ACTIVE_PREFIX, prefix);
            runnable.run();
        }
    }
}
