package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.*;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

class FieldGenerator {

    private final PsiElementFactory elementFactory;

    FieldGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /** @return A collection of valid builder fields (obtained from the specified {@link PsiField[]} fields) */
    Collection<PsiField> generateBuilderFields(PsiField[] sourceFields) {
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
    private PsiField generateFieldForBuilder(PsiField sourceField) {
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

    private PsiField createFieldForBuilder(PsiField field) {
        String fieldName = field.getName();
        if (fieldName == null) {
            throw new IllegalStateException("Field " + field + " has no name");
        }
        // We just want a private field of the same name, without any of the other modifiers.
        return elementFactory.createField(fieldName, field.getType());
    }
}
