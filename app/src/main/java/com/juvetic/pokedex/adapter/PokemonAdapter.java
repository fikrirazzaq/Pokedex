package com.juvetic.pokedex.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.juvetic.pokedex.DetailActivity;
import com.juvetic.pokedex.R;
import com.juvetic.pokedex.models.Result;
import com.juvetic.pokedex.util.PaginationAdapterCallback;

import java.util.ArrayList;
import java.util.List;

import static android.support.constraint.Constraints.TAG;

public class PokemonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int ITEM = 0;
    private static final int LOADING = 1;
    private static final String BASE_URL_IMG = "https://assets.pokemon.com/assets/cms2/img/pokedex/detail/";

    private List<Result> results;
    private Context context;

    private boolean isLoadingAdded = false;
    private boolean retryPageLoad = false;

    private PaginationAdapterCallback mCallback;
    private String errorMsg;

    public PokemonAdapter(Context context) {
        this.context = context;
        this.mCallback = (PaginationAdapterCallback) context;
        results = new ArrayList<>();
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
                View v1 = inflater.inflate(R.layout.item_list_pokemon, parent, false);
                viewHolder = new PokemonVH(v1);
                break;
            case LOADING:
                View v2 = inflater.inflate(R.layout.item_progress_pokemon, parent, false);
                viewHolder = new LoadingVH(v2);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {

        Result result = results.get(position);
        Log.d(TAG, "onBindViewHolder: " + result.getName());

        final String pokemonImage = String.format("%s%s.png", BASE_URL_IMG,
                String.format(context.getString(R.string.pokemon_id_format_image), position + 1));
        final String pokemonName = result.getName().substring(0, 1).toUpperCase() + result.getName().substring(1);
        final String pokemonId = String.format(context.getString(R.string.pokemon_id_format), position + 1);
        final String pokemonIdNumber = String.valueOf(position + 1);

        switch (getItemViewType(position)) {
            case ITEM:
                final PokemonVH pokemonVH = (PokemonVH) holder;
                pokemonVH.textPokemonName.setText(pokemonName);
                pokemonVH.textPokemonId.setText(pokemonId);

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
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                pokemonVH.progressBarImage.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model,
                                                           Target<Drawable> target, DataSource dataSource,
                                                           boolean isFirstResource) {
                                pokemonVH.progressBarImage.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(pokemonVH.imagePokemon);

                pokemonVH.parentLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, DetailActivity.class);
                        intent.putExtra("pokemon_id", pokemonIdNumber);
                        intent.putExtra("pokemon_name", pokemonName);
                        intent.putExtra("pokemon_image_url", pokemonImage);
                        context.startActivity(intent);
                    }
                });

                break;
            case LOADING:
                LoadingVH loadingVH = (LoadingVH) holder;

                if (retryPageLoad) {
                    loadingVH.mErrorLayout.setVisibility(View.VISIBLE);
                    loadingVH.mProgressBar.setVisibility(View.GONE);

                    loadingVH.mErrorTxt.setText(
                            errorMsg != null ?
                                    errorMsg :
                                    context.getString(R.string.error_msg_unknown));

                } else {
                    loadingVH.mErrorLayout.setVisibility(View.GONE);
                    loadingVH.mProgressBar.setVisibility(View.VISIBLE);
                }

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

    /**
     * Displays Pagination retry footer view along with appropriate errorMsg
     *
     * @param show
     * @param errorMsg to display if page load fails
     */
    public void showRetry(boolean show, @Nullable String errorMsg) {
        retryPageLoad = show;
        notifyItemChanged(results.size() - 1);

        if (errorMsg != null) this.errorMsg = errorMsg;
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
        private FrameLayout parentLayout;

        public PokemonVH(View itemView) {
            super(itemView);
            textPokemonName = itemView.findViewById(R.id.text_pokemon_name_item);
            textPokemonId = itemView.findViewById(R.id.text_pokemon_id_item);
            imagePokemon = itemView.findViewById(R.id.pokemon_image_item);
            progressBarImage = itemView.findViewById(R.id.pokemon_progress_item);
            parentLayout = itemView.findViewById(R.id.frame_item_list_pokemon);
        }

    }

    protected class LoadingVH extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ProgressBar mProgressBar;
        private ImageButton mRetryBtn;
        private TextView mErrorTxt;
        private LinearLayout mErrorLayout;

        public LoadingVH(View itemView) {
            super(itemView);

            mProgressBar = itemView.findViewById(R.id.loadmore_progress);
            mRetryBtn = itemView.findViewById(R.id.loadmore_retry);
            mErrorTxt = itemView.findViewById(R.id.loadmore_errortxt);
            mErrorLayout = itemView.findViewById(R.id.loadmore_errorlayout);

            mRetryBtn.setOnClickListener(this);
            mErrorLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.loadmore_retry:
                case R.id.loadmore_errorlayout:

                    showRetry(false, null);
                    mCallback.retryPageLoad();

                    break;
            }
        }
    }
}
