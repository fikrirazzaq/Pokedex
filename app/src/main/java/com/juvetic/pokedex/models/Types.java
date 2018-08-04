package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Types {

    @SerializedName("slot")
    @Expose
    private Integer slot;
    @SerializedName("type")
    @Expose
    private List<Type> type = new ArrayList<>();

    /**
     * @return The slot
     */
    public Integer getSlot() {
        return slot;
    }

    /**
     * @param slot The slot
     */
    public void setSlot(Integer slot) {
        this.slot = slot;
    }

    /**
     * @return The type
     */
    public List<Type> getType() {
        return type;
    }

    /**
     * @param type The type
     */
    public void setTypeList(List<Type> type) {
        this.type = type;
    }
}
