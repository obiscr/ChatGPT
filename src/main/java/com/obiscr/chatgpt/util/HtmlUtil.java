package com.obiscr.chatgpt.util;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.ex.VirtualFileManagerEx;
import com.intellij.openapi.vfs.impl.VirtualFileManagerImpl;
import com.intellij.ui.jcef.JBCefOsrHandlerBrowser;
import com.obiscr.chatgpt.core.DataFactory;
import org.intellij.plugins.markdown.ui.preview.html.MarkdownUtil;

import java.util.Objects;

/**
 * @author Wuzi
 */
public class HtmlUtil {

    private static String html(String content) {
        content = content == null ? "" : content;
        return "<html>" + content + "</html>";
    }

    private static String head(String content) {
        content = content == null ? "" : content;
        return "<head>" + content + "</head>";
    }

    private static String body(String content) {
        content = content == null ? "" : content;
        return "<body>" + content + "</body>";
    }

    private static String h1(String content) {
        content = content == null ? "" : content;
        return "<h1>" + content + "</h1>";
    }

    private static String h2(String content) {
        content = content == null ? "" : content;
        return "<h2>" + content + "</h2>";
    }

    private static String h3(String content) {
        content = content == null ? "" : content;
        return "<h3>" + content + "</h3>";
    }

    private static String h4(String content) {
        content = content == null ? "" : content;
        return "<h4>" + content + "</h4>";
    }

    private static String h5(String content) {
        content = content == null ? "" : content;
        return "<h5>" + content + "</h5>";
    }

    public static String create(String content) {
        return html(head(null) + body(content));
    }


    public static String md2html(String source) {
        Project project = DataFactory.getInstance().getProject();
        if (project.getProjectFile() != null) {
            return MarkdownUtil.INSTANCE.generateMarkdownHtml(
                    project.getProjectFile(),
                    source, project);
        } else if (project.getWorkspaceFile() != null) {
            return MarkdownUtil.INSTANCE.generateMarkdownHtml(
                    project.getWorkspaceFile(),
                    source, project);
        } else {
            return create(source);
        }

    }
}
