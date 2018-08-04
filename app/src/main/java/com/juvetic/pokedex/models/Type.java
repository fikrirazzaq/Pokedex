package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Type {

    @SerializedName("slot")
    @Expose
    private int slot;
    @SerializedName("type")
    @Expose
    private Type_ type;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public Type_ getType() {
        return type;
    }

    public void setType(Type_ type) {
        this.type = type;
    }

}
