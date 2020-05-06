package com.soberg.dev.quickbuilder.ui.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(name = "QuickBuilderSettings", storages = {@Storage("quickbuilder_settings.xml")})
public class SettingsComponent implements PersistentStateComponent<SettingsComponent.State> {

    private SettingsComponent.State state;

    @Nullable
    @Override
    public SettingsComponent.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull SettingsComponent.State state) {
        this.state = state;
    }

    @Override
    public void noStateLoaded() {
        /* Do nothing. */
    }

    public static class State {
        public final boolean usePrivateBuilderFields;

        State(boolean usePrivateBuilderFields) {
            this.usePrivateBuilderFields = usePrivateBuilderFields;
        }
    }
}
