package com.obiscr.chatgpt.message;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * @author Wuzi
 */
public class ChatGPTBundle extends DynamicBundle {

    @NonNls
    private static final String BUNDLE = "messages.ChatGPTBundle";
    private static final ChatGPTBundle INSTANCE = new ChatGPTBundle();

    private ChatGPTBundle() {
        super(BUNDLE);
    }

    @NotNull
    public static @Nls String message(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    @NotNull
    public static Supplier<@Nls String> messagePointer(@NotNull @PropertyKey(resourceBundle = BUNDLE) String key, Object @NotNull ... params) {
        return INSTANCE.getLazyMessage(key, params);
    }

    @Override
    protected @NotNull ResourceBundle findBundle(@NotNull String pathToBundle,
                                                 @NotNull ClassLoader baseLoader,
                                                 ResourceBundle.@NotNull Control control) {
        Locale locale = DynamicBundle.getLocale();
        return ResourceBundle.getBundle(pathToBundle,locale, baseLoader, control);
    }
}
