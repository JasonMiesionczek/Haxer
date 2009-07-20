package net.interaxia.haxer.api;

public class ObjectType {
    private String typePackage;
    private String typeName;
    private String normalizedTypeName;
    private boolean basicType;

    public void setNormalizedTypeName(String normalizedTypeName) {
        this.normalizedTypeName = normalizedTypeName;
    }

    public boolean isBasicType() {
        return basicType;
    }

    public void setBasicType(boolean basicType) {
        this.basicType = basicType;
    }

    public ObjectType(String pkg, String name) {
        this.typePackage = pkg;
        this.typeName = name;
        normalizeName();
    }

    private void normalizeName() {
        char c = typeName.charAt(0);
        String temp = new String(new char[]{c});
        normalizedTypeName = temp.toUpperCase() + typeName.substring(1);
    }

    public String getTypePackage() {
        return typePackage;
    }

    public String getTypeName() {
        return typeName;
    }

    public String getNormalizedTypeName() {
        return normalizedTypeName;
    }

    @Override
    public String toString() {
        return basicType ? normalizedTypeName : typePackage + "." + normalizedTypeName;
    }

}
