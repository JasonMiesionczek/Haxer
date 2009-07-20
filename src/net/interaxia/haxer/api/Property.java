package net.interaxia.haxer.api;

/**
 * Created by IntelliJ IDEA.
 * User: Atmospherian
 * Date: Jul 20, 2009
 * Time: 7:38:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class Property {
    private String getterFunction;
    private String setterFunction;
    private String objectType;
    private String propertyName;

    public Property(String name) {
        getterFunction = null;
        setterFunction = null;
        objectType = null;
        propertyName = name;
    }

    public String getGetterFunction() {
        return getterFunction;
    }

    public void setGetterFunction(String getterFunction) {
        this.getterFunction = getterFunction;
    }

    public String getSetterFunction() {
        return setterFunction;
    }

    public void setSetterFunction(String setterFunction) {
        this.setterFunction = setterFunction;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public String getPropertyName() {
        return propertyName;
    }

    public void setPropertyName(String propertyName) {
        this.propertyName = propertyName;
    }

    @Override
    public String toString() {

        return "public var " + propertyName + "(" + getterFunction + "," + setterFunction + "):" + objectType + ";";
    }

}
