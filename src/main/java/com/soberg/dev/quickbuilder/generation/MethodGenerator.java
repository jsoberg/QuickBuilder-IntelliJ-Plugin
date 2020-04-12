package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import java.util.Collection;

class MethodGenerator {

    private final PsiElementFactory elementFactory;

    MethodGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    void addSetMethods(PsiClass builderClass, Collection<PsiField> builderFields) {
        for (PsiField field : builderFields) {
            addSetMethodToBuilder(builderClass, field);
        }
    }

    private void addSetMethodToBuilder(PsiClass builderClass, PsiField field) {
        String fieldName = field.getName();
        if (fieldName == null) {
            throw new IllegalStateException("Could not find name for field " + field);
        }
        String methodName = generateSetMethodName(fieldName);
        String fieldType = field.getType().getPresentableText();
        String methodText = "public Builder " + methodName + "(" + fieldType + " " + fieldName + ")"
                + " { this." + fieldName + " = " + fieldName + "; return this; }";
        PsiMethod setMethod = elementFactory.createMethodFromText(methodText, field);
        builderClass.add(setMethod);
    }

    private String generateSetMethodName(String fieldName) {
        String methodAppend = toInitialUpperCase(fieldName);
        return "set" + methodAppend;
    }

    private String toInitialUpperCase(String name) {
        char initialChar = Character.toUpperCase(name.charAt(0));
        // Check that our field name isn't a single character.
        return initialChar + ((name.length() != 1) ? name.substring(1) : "");
    }
}
