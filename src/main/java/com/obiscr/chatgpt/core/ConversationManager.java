package com.obiscr.chatgpt.core;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;


/**
 * @author Wuzi
 */
public class ConversationManager {

    public static ConversationManager getInstance(@NotNull Project project) {
        return project.getService(ConversationManager.class);
    }

    private String parentMessageId = UUID.randomUUID().toString();
    private String conversationId = "";

    public String getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(String parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
