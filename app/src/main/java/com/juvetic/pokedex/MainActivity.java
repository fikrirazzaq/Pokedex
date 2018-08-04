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
import com.juvetic.pokedex.models.Pokemon;
import com.juvetic.pokedex.models.Result;
import com.juvetic.pokedex.util.PaginationScrollListener;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.main_progress);

        adapter = new PokemonAdapter(this);

        linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rv = findViewById(R.id.main_recycler);
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

        // mocking network delay for API call
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadFirstPage();
            }
        }, 1000);

    }

    private void loadFirstPage() {
        Log.d(TAG, "loadFirstPage: ");

        List<Result> results = new ArrayList<>();
        int itemCount = adapter.getItemCount();
        for (int i = 0; i < 20; i++) {
            Result resultPokemon = new Result("Pokemon " + (itemCount == 0 ? (itemCount + 1 + i) : (itemCount + i)));
            results.add(resultPokemon);
        }

        List<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Pokemon pokemon = new Pokemon(results);
            pokemons.add(pokemon);
        }

        progressBar.setVisibility(View.GONE);
        adapter.addAll(pokemons);

        if (currentPage <= TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

    private void loadNextPage() {
        Log.d(TAG, "loadNextPage: " + currentPage);

        List<Result> results = new ArrayList<>();
        int itemCount = adapter.getItemCount();
        Log.d("ITEM CONT ", itemCount + "");
        for (int i = 0; i < 20; i++) {
            Result resultPokemon = new Result("Pokemon " + (itemCount == 0 ? (itemCount + 1 + i) : (itemCount + i)));
            results.add(resultPokemon);
        }

        List<Pokemon> pokemons = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Pokemon pokemon = new Pokemon(results);
            pokemons.add(pokemon);
        }

        adapter.removeLoadingFooter();

        isLoading = false;
        adapter.addAll(pokemons);

        if (currentPage != TOTAL_PAGES) adapter.addLoadingFooter();
        else isLastPage = true;
    }

}
