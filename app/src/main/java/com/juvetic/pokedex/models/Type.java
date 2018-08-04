package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Type {
    @SerializedName("url")
    @Expose
    private Integer url;
    @SerializedName("name")
    @Expose
    private Integer name;

    /**
     * @return The url
     */
    public Integer getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(Integer url) {
        this.url = url;
    }

    /**
     * @return The name
     */
    public Integer getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(Integer name) {
        this.name = name;
    }
}
