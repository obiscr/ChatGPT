package com.obiscr.chatgpt.ui.action.editor;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.obiscr.chatgpt.core.TokenManager;
import com.obiscr.chatgpt.core.builder.OfficialBuilder;
import com.obiscr.chatgpt.core.parser.OfficialParser;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.obiscr.chatgpt.AbstractHandler.getProxy;

/**
 * @author Wuzi
 */
public class EditorAction extends AbstractEditorAction {

    private static final Logger LOG = LoggerFactory.getLogger(EditorAction.class);

    public EditorAction() {
        super(() -> "Generate in the Editor");
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Editor editor = e.getData(CommonDataKeys.EDITOR);
        assert editor != null;
        String selectedText = editor.getSelectionModel().getSelectedText();

        LOG.info("Editor Request: question={}",selectedText);

        try {
            ProgressManager.getInstance().run(new Task.WithResult<Boolean, Exception>(
                    ProjectManager.getInstance().getDefaultProject(),
                    "AI For Dev: Generating...", true) {
                @Override
                protected Boolean compute(@NotNull ProgressIndicator indicator) {
                    execute(selectedText, editor, e.getProject());
                    return true;
                }

                @Override
                public void onCancel() {
                    super.onCancel();
                }
            });
        } catch (Exception ex) {
            insertToEditor(editor, e.getProject(), "// Generate failed");
        }
    }

    private void execute(String question,Editor editor, Project project) {
        String url = "https://api.openai.com/v1/chat/completions";
        HttpResponse response = HttpUtil.createPost("https://api.openai.com/v1/chat/completions")
                .headerMap(TokenManager.getInstance().getGPT35TurboHeaders(),true)
                .setProxy(getProxy())
                .body(OfficialBuilder.buildGpt35Turbo(question).toString().getBytes(StandardCharsets.UTF_8)).execute();
        LOG.info("Editor Response: answer={}",response.body());
        if (response.getStatus() != 200) {
            LOG.info("ChatGPT: Request failure. Url={}, response={}",url, response.body());
        }
        OfficialParser.ParseResult parseResult = OfficialParser.
                parseGPT35Turbo(response.body());
        String source = parseResult.getSource();
        writeResult(project, source, editor);
    }

    private void writeResult(Project project, String source, Editor editor) {
        if (!source.contains("```")) {
            insertToEditor(editor, project, source);
        }

        // Remove the Markdown format
        // ```some language
        // some code
        // ```
        Pattern codePattern = Pattern.compile("```(?:[^`\n]+?\n)?(.*?)```", Pattern.DOTALL);
        Matcher codeMatcher = codePattern.matcher(source);
        while (codeMatcher.find()) {
            insertToEditor(editor, project, codeMatcher.group(1).trim());
        }
    }

    private void insertToEditor(Editor editor, Project project, String content) {
        String finalContent = "\n" + content;
        int offset = editor.getCaretModel().getVisualLineEnd();
        Runnable runnable = () -> editor.getDocument().insertString(offset, finalContent);
        WriteCommandAction.runWriteCommandAction(project, runnable);
    }
}
