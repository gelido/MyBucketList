package com.rafaelcarvalho.mybucketlist.util;

import com.rafaelcarvalho.mybucketlist.model.BucketListItem;

/**
 * Created by Rafael on 07/09/16.
 */

/**
 *
 * @param <T> This is the type of the new value on the modification
 */
public class Modification <T> {

    public enum Type
    {
        UPDATE, CREATE, REMOVE
    }

    public enum Field
    {
        SEEN,ALL
    }

    private Type type;

    private String id;

    private T newValue;

    private Field field = Field.ALL;


    public Modification(Type type, String id) {
        this.type = type;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public T getNewValue() {
        if (newValue == null)
            throw new UnsupportedOperationException();
        return newValue;
    }

    public void setNewValue(T newValue) {
        this.newValue = newValue;
    }

    public Type getType() {
        return type;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }
}
