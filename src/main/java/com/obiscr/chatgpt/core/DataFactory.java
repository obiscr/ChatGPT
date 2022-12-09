package com.obiscr.chatgpt.core;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import org.jetbrains.annotations.NotNull;

/**
 * @author Wuzi
 */
public class DataFactory {

    private static final DataFactory FACTORY = new DataFactory();

    private Project project;
    private ToolWindow toolWindow;

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
}
