package com.beauney.injector.processor;

import javax.lang.model.type.TypeMirror;

/**
 * @author zengjiantao
 * @since 2020-08-20
 */
public class FieldViewBinding {
    private String name;
    private TypeMirror type;
    private int resId;

    public FieldViewBinding(String name, TypeMirror type, int resId) {
        this.name = name;
        this.type = type;
        this.resId = resId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public TypeMirror getType() {
        return type;
    }

    public void setType(TypeMirror type) {
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
