package com.juvetic.pokedex.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.juvetic.pokedex.R;
import com.juvetic.pokedex.models.Pokemon;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;

    private List<Pokemon> pokemons;
    private Context context;

    private boolean isLoadingAdded = false;

    public PokemonAdapter(Context context) {
        this.context = context;
        pokemons = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case ITEM:
                viewHolder = getViewHolder(parent, inflater);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress_pokemon, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }
        return viewHolder;
    }

    @NonNull
    private RecyclerView.ViewHolder getViewHolder(ViewGroup parent, LayoutInflater inflater) {
        RecyclerView.ViewHolder viewHolder;
        View v1 = inflater.inflate(R.layout.item_list_pokemon, parent, false);
        viewHolder = new PokemonVH(v1);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Pokemon pokemon = pokemons.get(position);
        switch (getItemViewType(position)) {
            case ITEM:
                PokemonVH pokemonVH = (PokemonVH) holder;
                pokemonVH.textView.setText(pokemon.getResults().get(position).getName());
                break;
            case LOADING:
//                Do nothing
                break;
        }
    }

    @Override
    public int getItemCount() {
        return pokemons == null ? 0 : pokemons.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == pokemons.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
  Helpers
  _________________________________________________________________________________________________
   */

    public void add(Pokemon pokemon) {
        pokemons.add(pokemon);
        notifyItemInserted(pokemons.size() - 1);
    }

    public void addAll(List<Pokemon> pokemons) {
        for (Pokemon pokemon : pokemons) {
            add(pokemon);
        }
    }

    public void remove(Pokemon pokemon) {
        int position = pokemons.indexOf(pokemon);
        if (position > -1) {
            pokemons.remove(position);
            notifyItemRemoved(position);
        }
    }

    public void clear() {
        isLoadingAdded = false;
        while (getItemCount() > 0) {
            remove(getItem(0));
        }
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void addLoadingFooter() {
        isLoadingAdded = true;
        add(new Pokemon());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;
        int position = pokemons.size() - 1;
        Pokemon item = getItem(position);
        if (item != null) {
            pokemons.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Pokemon getItem(int position) {
        return pokemons.get(position);
    }

    /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PokemonVH extends RecyclerView.ViewHolder {
        private TextView textView;

        public PokemonVH(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.text_pokemon_name_item);
        }

    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }

    }
}
