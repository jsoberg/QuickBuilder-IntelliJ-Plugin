package com.soberg.dev.quickbuilder.ui.settings;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

@State(name = "QuickBuilderSettings", storages = {@Storage("quickbuilder_settings.xml")})
public class SettingsPreferences implements PersistentStateComponent<SettingsPreferences.State> {

    private SettingsPreferences.State pendingState = new State();
    private SettingsPreferences.State state = new State();

    public SettingsPreferences.State getPendingState() {
        return pendingState;
    }

    @Nullable
    @Override
    public SettingsPreferences.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull SettingsPreferences.State state) {
        this.pendingState = state;
        this.state = state;
    }

    @Override
    public void noStateLoaded() {
        this.state = new State();
        this.pendingState = state;
    }

    public void setPendingFieldModifier(FieldModifier modifier) {
        this.pendingState = new State(modifier);
    }

    public boolean isModified() {
        return !pendingState.equals(state);
    }

    public void applyChanges() {
        this.state = pendingState;
    }

    public enum FieldModifier {
        PRIVATE("private"),
        PACKAGE_PRIVATE("package-private"),
        PUBLIC("public");

        public final String displayName;

        FieldModifier(String displayName) {
            this.displayName = displayName;
        }
    }

    public static class State {
        public final FieldModifier fieldModifier;

        public State() {
            this.fieldModifier = FieldModifier.PACKAGE_PRIVATE;
        }

        State(FieldModifier fieldModifier) {
            this.fieldModifier = fieldModifier;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            State state = (State) o;
            return fieldModifier == state.fieldModifier;
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldModifier);
        }
    }
}
