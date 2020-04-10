package com.soberg.dev.quickbuilder;

import com.intellij.openapi.project.Project;
import com.intellij.psi.*;

public class BuilderGenerator {

    private final PsiElementFactory elementFactory;

    public BuilderGenerator(Project project) {
        JavaPsiFacade psiFacade = JavaPsiFacade.getInstance(project);
        this.elementFactory = psiFacade.getElementFactory();
    }

    public PsiClass generateBuilderClass(PsiClass sourceClass) throws BuilderGenerationException {
        PsiClass builderClass = createBuilderClass();
        addBuilderFields(sourceClass, builderClass);
        return builderClass;
    }

    private PsiClass createBuilderClass() throws BuilderGenerationException {
        PsiClass builderClass = elementFactory.createClass("Builder");
        addClassModifiers(builderClass);
        return builderClass;
    }

    private void addClassModifiers(PsiClass builderClass) throws BuilderGenerationException {
        PsiModifierList modifiers = builderClass.getModifierList();
        if (modifiers == null) {
            throw new BuilderGenerationException("Problem finding modifier list for Builder class");
        }
        modifiers.setModifierProperty(PsiModifier.PUBLIC, true);
        modifiers.setModifierProperty(PsiModifier.STATIC, true);
        modifiers.setModifierProperty(PsiModifier.FINAL, true);
    }

    private void addBuilderFields(PsiClass sourceClass, PsiClass builderClass) {
        PsiField[] fields = sourceClass.getFields();
        for (PsiField field : fields) {
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
