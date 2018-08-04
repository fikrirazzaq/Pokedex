package com.juvetic.pokedex.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class PokemonDetail {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("weight")
    @Expose
    private Integer weight;
    @SerializedName("height")
    @Expose
    private Integer height;
    @SerializedName("base_experience")
    @Expose
    private Integer base_experience;
    @SerializedName("types")
    @Expose
    private List<Types> types = new ArrayList<>();

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return The weight
     */
    public Integer getWeight() {
        return weight;
    }

    /**
     * @param weight The weight
     */
    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    /**
     * @return The height
     */
    public Integer getHeight() {
        return height;
    }

    /**
     * @param height The height
     */
    public void setHeight(Integer height) {
        this.height = height;
    }

    /**
     * @return The base_experience
     */
    public Integer getBase_experience() {
        return base_experience;
    }

    /**
     * @param base_experience The base_experience
     */
    public void setBase_experience(Integer base_experience) {
        this.base_experience = base_experience;
    }

    /**
     * @return The types
     */
    public List<Types> getTypes() {
        return types;
    }

    /**
     * @param types The types
     */
    public void setTypes(List<Types> types) {
        this.types = types;
    }
}
