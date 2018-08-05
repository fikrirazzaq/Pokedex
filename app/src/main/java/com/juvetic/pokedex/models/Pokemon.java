package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Pokemon {

    @SerializedName("count")
    @Expose
    private Integer count;
    @SerializedName("results")
    @Expose
    private List<Result> results = new ArrayList<>();
    @SerializedName("previous")
    @Expose
    private String previous;
    @SerializedName("next")
    @Expose
    private String next;

    /**
     * @return The count of pokemon
     */
    public Integer getCount() {
        return count;
    }

    /**
     * @param count The count of pokemon
     */
    public void setCount(Integer count) {
        this.count = count;
    }

    /**
     * @return The results of pokemon
     */
    public List<Result> getResults() {
        return results;
    }

    /**
     * @param results The results of pokemon
     */
    public void setResults(List<Result> results) {
        this.results = results;
    }

    /**
     * @return The previous url
     */
    public String getPrevious() {
        return previous;
    }

    /**
     * @param previous The previous url
     */
    public void setPrevious(String previous) {
        this.previous = previous;
    }

    /**
     * @return The next url
     */
    public String getNext() {
        return next;
    }

    /**
     * @param next The next url
     */
    public void setNext(String next) {
        this.next = next;
    }
}
