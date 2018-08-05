package com.juvetic.pokedex;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.juvetic.pokedex.api.PokemonApi;
import com.juvetic.pokedex.api.PokemonService;
import com.juvetic.pokedex.models.PokemonDetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";
    private PokemonService pokemonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        pokemonService = PokemonApi.getClient().create(PokemonService.class);

        receiveIntentData();
    }

    private void receiveIntentData() {
        if (getIntent().hasExtra("pokemon_id") && getIntent().hasExtra("pokemon_image_url")) {
            String pokemonId = getIntent().getStringExtra("pokemon_id");
            String pokemonName = getIntent().getStringExtra("pokemon_name");
            String pokemonImage = getIntent().getStringExtra("pokemon_image_url");

            setTitle(pokemonName);
            setPokemonImage(pokemonImage);
            setPokemonId(pokemonId);
            setPokemonName(pokemonName);
            loadPokemonDetail(pokemonId);
        }
    }

    private void setPokemonId(String pokemonId) {

        TextView textPokemonId = findViewById(R.id.text_pokemon_id);
        textPokemonId.setText(new StringBuilder().append(getString(R.string.text_pokemon_id))
                .append(String.format(getString(R.string.pokemon_id_format_image), Integer.parseInt(pokemonId))).toString());
    }

    private void setPokemonName(String pokemonName) {

        TextView textPokemonName = findViewById(R.id.text_pokemon_name);
        textPokemonName.setText(pokemonName);
    }

    private void setPokemonImage(String url) {

        ImageView pokemonImage = findViewById(R.id.pokemon_image);
        final ProgressBar progressBar = findViewById(R.id.pokemon_progress);

        /**
         * Using Glide to handle image loading.
         * Learn more about Glide here:
         * <a href="http://blog.grafixartist.com/image-gallery-app-android-studio-1-4-glide/" />
         */
        Glide
                .with(this)
                .load(url)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(pokemonImage);
    }

    private void loadPokemonDetail(final String pokemonId) {

        final TextView textHeight = findViewById(R.id.text_pokemon_height);
        final TextView textWeight = findViewById(R.id.text_pokemon_weight);
        final TextView textExp = findViewById(R.id.text_pokemon_base_experience);
        final TextView textTypes = findViewById(R.id.text_pokemon_types);

        Call<PokemonDetail> call = pokemonService.getPokemonDetail(Integer.parseInt(pokemonId));
        call.enqueue(new Callback<PokemonDetail>() {
            @Override
            public void onResponse(Call<PokemonDetail> call, Response<PokemonDetail> response) {

                PokemonDetail pokemonDetail = response.body();
                textHeight.setText(String.format(getString(R.string.pokemon_height), pokemonDetail.getHeight()));
                textWeight.setText(String.format(getString(R.string.pokemon_weight), pokemonDetail.getWeight()));
                textExp.setText(String.format(getString(R.string.pokemon_exp), pokemonDetail.getBaseExperience()));

                List<String> typesList = new ArrayList<>();

                for (int i = 0; i < pokemonDetail.getTypes().size(); i++) {
                    typesList.add(pokemonDetail.getTypes().get(i).getType().getName());
                }

                String s = Arrays.toString(typesList.toArray());
                textTypes.setText(s.substring(1, s.length() - 1));
            }

            @Override
            public void onFailure(Call<PokemonDetail> call, Throwable t) {

            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
