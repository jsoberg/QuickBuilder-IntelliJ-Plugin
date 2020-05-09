package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifier;
import com.intellij.psi.PsiModifierList;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences;
import com.soberg.dev.quickbuilder.ui.settings.SettingsPreferences.FieldModifier;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FieldGenerator {

    private final PsiElementFactory elementFactory;
    private final SettingsPreferences settings;

    @Inject
    FieldGenerator(PsiElementFactory elementFactory, SettingsPreferences settings) {
        this.elementFactory = elementFactory;
        this.settings = settings;
    }

    /**
     * @return A collection of valid builder fields (obtained from the specified {@link PsiField[]} fields)
     */
    Collection<PsiField> generateBuilderFields(PsiField[] sourceFields) throws BuilderGenerationException {
        List<PsiField> validFields = new ArrayList<>();
        for (PsiField field : sourceFields) {
            PsiField builderField = generateFieldForBuilder(field);
            if (builderField != null) {
                validFields.add(builderField);
            }
        }
        return validFields;
    }

    @Nullable
    private PsiField generateFieldForBuilder(PsiField sourceField) throws BuilderGenerationException {
        if (isValidBuilderField(sourceField)) {
            return createFieldForBuilder(sourceField);
        }
        return null;
    }

    private boolean isValidBuilderField(PsiField field) {
        PsiModifierList modifiers = field.getModifierList();
        if (modifiers == null) {
            return false;
        }
        // We want to add builder fields for all non-static class fields.
        return !modifiers.hasExplicitModifier(PsiModifier.STATIC);
    }

    private PsiField createFieldForBuilder(PsiField field) throws BuilderGenerationException {
        String fieldName = field.getName();
        if (fieldName == null) {
            throw new BuilderGenerationException("Field " + field + " has no name");
        }
        String modifier = getModifier();
        return elementFactory.createFieldFromText(modifier + field.getType().getPresentableText() + " " + field.getName() + ";", field);
    }

    private String getModifier() {
        FieldModifier modifier = settings.getPendingState().fieldModifier;
        switch (modifier) {
            case PUBLIC:
                return "public ";
            case PRIVATE:
                return "private ";
            case PACKAGE_PRIVATE:
            default:
                return "";
        }
    }
}
