package net.interaxia.haxer.api;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 14, 2009
 * Time: 9:58:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class ObjectType {
    private String typePackage;
    private String typeName;
    private String normalizedTypeName;

    public ObjectType(String pkg, String name) {
        this.typePackage = pkg;
        this.typeName = name;
        normalizeName();
    }

    private void normalizeName() {
        char c = typeName.charAt(0);
        String temp = new String(new char[] {c});
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
        return typePackage + "."+ normalizedTypeName;
    }

}
