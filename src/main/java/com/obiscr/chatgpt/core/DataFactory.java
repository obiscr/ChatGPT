package com.obiscr.chatgpt.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @author Wuzi
 */
public class DataFactory {

    private static final DataFactory FACTORY = new DataFactory();

    private Project project;
    private ToolWindow toolWindow;
    private String conversationId;
    private final List<Map<String,String>> conversations = new LinkedList<>();


    private DataFactory() {

    }

    public static DataFactory getInstance() {
        return FACTORY;
    }

    public Project getProject() {
        return project;
    }

    public ToolWindow getToolWindow() {
        return toolWindow;
    }

    public void setData(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        this.toolWindow = toolWindow;
        this.project = project;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
    public void addConversation(Map<String,String> conversation) {
        conversations.add(conversation);
    }

    public void clearConversation() {
        conversations.clear();
    }

    public String buildConversations() {
        StringBuilder sb = new StringBuilder();
        for (Map<String,String> map : conversations) {
            for (Map.Entry<String,String> entry: map.entrySet()) {
                sb.append(entry.getKey()).append(entry.getValue());
            }
        }
        return sb.toString();
    }
}
