package com.obiscr.chatgpt.ui.notifier;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import com.obiscr.chatgpt.message.ChatGPTBundle;
import com.obiscr.chatgpt.ui.notifier.action.AutoRefreshAction;
import com.obiscr.chatgpt.ui.notifier.action.ManuallyRefreshAction;
import org.jetbrains.annotations.Nullable;

public class MyNotifier {

    public static void notifyError(@Nullable Project project, String title,
                                   String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(ChatGPTBundle.message("group.id"))
                .createNotification(content, NotificationType.WARNING)
                .setTitle(title)
//                .addAction(new AutoRefreshAction(ChatGPTBundle.message("action.refresh.automatic.text")))
                .addAction(new ManuallyRefreshAction(ChatGPTBundle.message("action.refresh.manually.text")))
                .notify(project);
    }
}
