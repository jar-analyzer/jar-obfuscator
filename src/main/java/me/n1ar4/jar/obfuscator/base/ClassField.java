package me.n1ar4.jar.obfuscator.base;

import java.util.Objects;

public class ClassField {
    private String className;
    private String fieldName;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassField that = (ClassField) o;
        return Objects.equals(className, that.className) && Objects.equals(fieldName, that.fieldName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, fieldName);
    }
}
