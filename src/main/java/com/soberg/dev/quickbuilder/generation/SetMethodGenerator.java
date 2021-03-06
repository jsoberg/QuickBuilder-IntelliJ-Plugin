package com.soberg.dev.quickbuilder.generation;

import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiMethod;

import javax.inject.Inject;
import java.util.Collection;

class SetMethodGenerator {

    private final PsiElementFactory elementFactory;

    @Inject
    SetMethodGenerator(PsiElementFactory elementFactory) {
        this.elementFactory = elementFactory;
    }

    /**
     * Generates set methods for the specified {@link Collection<PsiField>} builder fields, and adds them to the
     * specified {@link PsiClass} builder class.
     */
    void addSetMethods(PsiClass builderClass, Collection<PsiField> builderFields) throws BuilderGenerationException {
        for (PsiField field : builderFields) {
            addSetMethodToBuilder(builderClass, field);
        }
    }

    private void addSetMethodToBuilder(PsiClass builderClass, PsiField field) throws BuilderGenerationException {
        PsiMethod setMethod = generateSetMethod(field);
        builderClass.add(setMethod);
    }

    private PsiMethod generateSetMethod(PsiField parentField) throws BuilderGenerationException {
        String fieldName = parentField.getName();
        if (fieldName == null) {
            throw new BuilderGenerationException("Could not find name for field " + parentField);
        }
        String methodName = getSetMethodName(fieldName);
        String fieldType = parentField.getType().getPresentableText();
        String methodText = "public Builder " + methodName + "(" + fieldType + " " + fieldName + ")"
                + " { this." + fieldName + " = " + fieldName + "; return this; }";
        return elementFactory.createMethodFromText(methodText, parentField);
    }

    private String getSetMethodName(String fieldName) {
        String methodAppend = toInitialUpperCase(fieldName);
        return "set" + methodAppend;
    }

    private String toInitialUpperCase(String name) {
        char initialChar = Character.toUpperCase(name.charAt(0));
        // Check that our field name isn't a single character.
        return initialChar + ((name.length() != 1) ? name.substring(1) : "");
    }
}
