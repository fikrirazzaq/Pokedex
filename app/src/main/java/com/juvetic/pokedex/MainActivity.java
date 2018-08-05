package com.juvetic.pokedex;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.juvetic.pokedex.adapter.PokemonAdapter;
import com.juvetic.pokedex.api.PokemonApi;
import com.juvetic.pokedex.api.PokemonService;
import com.juvetic.pokedex.models.Pokemon;
import com.juvetic.pokedex.models.Result;
import com.juvetic.pokedex.util.PaginationScrollListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LIMIT = 20;
    private static final int OFFSET_START = 0;
    PokemonAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_OFFSET = 940;
    private int currentOffset = OFFSET_START;

    private PokemonService pokemonService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rv = findViewById(R.id.main_recycler);
        progressBar = findViewById(R.id.main_progress);

        adapter = new PokemonAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv.setLayoutManager(linearLayoutManager);

        rv.setItemAnimator(new DefaultItemAnimator());

        rv.setAdapter(adapter);

        rv.addOnScrollListener(new PaginationScrollListener(linearLayoutManager) {
            @Override
            protected void loadMoreItems() {
                isLoading = true;
                currentOffset += 20;
                Log.v("OFFSET NAMBAH WA ", "" + currentOffset);
//                 mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalOffsetCount() {
                return TOTAL_OFFSET;
            }

            @Override
            public boolean isLastPage() {
                return isLastPage;
            }

            @Override
            public boolean isLoading() {
                return isLoading;
            }
        });

        //init service and load data
        pokemonService = PokemonApi.getClient(this).create(PokemonService.class);

        loadFirstPage();
    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        Call<Pokemon> call = pokemonService.getPokemon(LIMIT, currentOffset);
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                Log.d("AKSDJKLASJDKLJDKLA", " KLASJDLJLASD");

                List<Result> results = response.body().getResults();

                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentOffset <= TOTAL_OFFSET) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("ATEP ", "ASDKJALKSDJKLASD");
                t.printStackTrace(); // for now
            }
        });

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentOffset);

        Call<Pokemon> call = pokemonService.getPokemon(LIMIT, currentOffset);
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                Log.d("APETTTTTTT", " KLASJDLJLASD");

                List<Result> results = response.body().getResults();
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentOffset <= TOTAL_OFFSET) adapter.addLoadingFooter();
                else isLastPage = true;
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("ATEASDasdasdP ", "ASDKJALKSDJKLASD");
                t.printStackTrace(); // for now
            }
        });
        adapter.removeLoadingFooter();
        isLoading = false;

        if (currentOffset != TOTAL_OFFSET) adapter.addLoadingFooter();
        else isLastPage = true;
    }

}
