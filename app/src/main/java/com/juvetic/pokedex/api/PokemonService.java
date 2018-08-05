package com.juvetic.pokedex.api;

import com.juvetic.pokedex.models.Pokemon;
import com.juvetic.pokedex.models.PokemonDetail;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PokemonService {

    @GET("pokemon/")
    Call<Pokemon> getPokemon(
            @Query("limit") int limit,
            @Query("offset") int offset
    );

    @GET("pokemon/{id}/")
    Call<PokemonDetail> getPokemonDetail(
            @Path("id") int id
    );

}
