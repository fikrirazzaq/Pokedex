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
    private static final int PAGE_START = 0;
    PokemonAdapter adapter;
    LinearLayoutManager linearLayoutManager;
    RecyclerView rv;
    ProgressBar progressBar;
    private boolean isLoading = false;
    private boolean isLastPage = false;
    private int TOTAL_PAGES = 3;
    private int currentPage = PAGE_START;

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
                currentPage += 1;

                // mocking network delay for API call
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loadNextPage();
                    }
                }, 1000);
            }

            @Override
            public int getTotalPageCount() {
                return TOTAL_PAGES;
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

        Call<Pokemon> call = pokemonService.getPokemon(20, 0);
        call.enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {
                Log.d("AKSDJKLASJDKLJDKLA", " KLASJDLJLASD");
                Log.d(TAG, "loadFirstPage: onResponse: " + response.body().toString());

                List<Result> results = response.body().getResults();
                progressBar.setVisibility(View.GONE);
                adapter.addAll(results);

                if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
                else isLastPage = true;

//                for (Result r : adapter.getResults()) {
//                    Log.d("Pokemon ", r.getName() + " " + r.getUrl());
//                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                Log.e("ATEP ", "ASDKJALKSDJKLASD");
                t.printStackTrace(); // for now
            }
        });

    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        adapter.removeLoadingFooter();
        isLoading = false;

        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

}
