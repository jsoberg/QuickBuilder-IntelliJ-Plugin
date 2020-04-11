package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.*;

class FieldGenerator {

    private final PsiElementFactory elementFactory;

    FieldGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /** Adds builder fields (obtained from the specified {@link PsiField[]} fields) to the specified
     * {@link PsiClass} builder class.*/
    void addBuilderFields(PsiClass builderClass, PsiField[] sourceFields) {
        for (PsiField field : sourceFields) {
            addFieldToBuilder(builderClass, field);
        }
    }

    private void addFieldToBuilder(PsiClass builderClass, PsiField field) {
        if (isValidBuilderField(field)) {
            builderClass.add(createFieldForBuilder(field));
        }
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
