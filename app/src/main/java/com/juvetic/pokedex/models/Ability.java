package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ability {

    @SerializedName("slot")
    @Expose
    private int slot;
    @SerializedName("is_hidden")
    @Expose
    private boolean isHidden;
    @SerializedName("ability")
    @Expose
    private Ability_ ability;

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public boolean isIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean isHidden) {
        this.isHidden = isHidden;
    }

    public Ability_ getAbility() {
        return ability;
    }

    public void setAbility(Ability_ ability) {
        this.ability = ability;
    }

}
