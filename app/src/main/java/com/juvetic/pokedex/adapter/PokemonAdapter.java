package com.juvetic.pokedex.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.juvetic.pokedex.R;
import com.juvetic.pokedex.api.PokemonService;
import com.juvetic.pokedex.models.PokemonDetail;
import com.juvetic.pokedex.models.Result;

import java.util.ArrayList;
import java.util.List;

public class PokemonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://assets.pokemon.com/assets/cms2/img/pokedex/detail/";

    private List<Result> results;
    private List<PokemonDetail> pokemonDetails;
    private Context context;

    private PokemonService pokemonService;

    private boolean isLoadingAdded = false;

    public PokemonAdapter(Context context) {
        this.context = context;
        results = new ArrayList<>();
        pokemonDetails = new ArrayList<>();
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
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

        Result result = results.get(position);
        String pokemonImage = BASE_URL_IMG + String.format("%03d", position + 1) + ".png";


        switch (getItemViewType(position)) {
            case ITEM:
                final PokemonVH pokemonVH = (PokemonVH) holder;
                pokemonVH.textPokemonName.setText(textCapWords(result.getName()));
                pokemonVH.textPokemonId.setText(String.format("#%03d", position + 1));

                /**
                 * Using Glide to handle image loading.
                 * Learn more about Glide here:
                 * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
                 */
                Glide
                        .with(context)
                        .load(pokemonImage)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                pokemonVH.progressBarImage.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                pokemonVH.progressBarImage.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(pokemonVH.imagePokemon);

                break;
            case LOADING:
//                Do nothing
                break;
        }

    }

    @Override
    public int getItemCount() {
        return results == null ? 0 : results.size();
    }

    @Override
    public int getItemViewType(int position) {
        return (position == results.size() - 1 && isLoadingAdded) ? LOADING : ITEM;
    }

    /*
  Helpers
  _________________________________________________________________________________________________
   */

    public void add(Result r) {
        results.add(r);
        notifyItemInserted(results.size() - 1);
    }

    public void addAll(List<Result> results) {
        for (Result result : results) {
            add(result);
        }
    }

    public void remove(Result r) {
        int position = results.indexOf(r);
        if (position > -1) {
            results.remove(position);
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
        add(new Result());
    }

    public void removeLoadingFooter() {
        isLoadingAdded = false;

        int position = results.size() - 1;
        Result result = getItem(position);

        if (result != null) {
            results.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Result getItem(int position) {
        return results.get(position);
    }

    public String textCapWords(String str) {
        String[] strArray = str.split(" ");
        StringBuilder builder = new StringBuilder();
        for (String s : strArray) {
            String cap = s.substring(0, 1).toUpperCase() + s.substring(1);
            builder.append(cap).append(" ");
        }
        return builder.toString();
    }

    /*
   View Holders
   _________________________________________________________________________________________________
    */

    /**
     * Main list's content ViewHolder
     */
    protected class PokemonVH extends RecyclerView.ViewHolder {
        private TextView textPokemonName, textPokemonId;
        private ProgressBar progressBarImage;
        private ImageView imagePokemon;

        public PokemonVH(View itemView) {
            super(itemView);
            textPokemonName = itemView.findViewById(R.id.text_pokemon_name_item);
            textPokemonId = itemView.findViewById(R.id.text_pokemon_id_item);
            imagePokemon = itemView.findViewById(R.id.pokemon_image);
            progressBarImage = itemView.findViewById(R.id.pokemon_progress);
        }

    }

    protected class LoadingVH extends RecyclerView.ViewHolder {

        public LoadingVH(View itemView) {
            super(itemView);
        }

    }
}
