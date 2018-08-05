package com.juvetic.pokedex;

import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.juvetic.pokedex.adapter.PokemonAdapter;
import com.juvetic.pokedex.api.PokemonApi;
import com.juvetic.pokedex.api.PokemonService;
import com.juvetic.pokedex.models.Pokemon;
import com.juvetic.pokedex.models.Result;
import com.juvetic.pokedex.util.PaginationAdapterCallback;
import com.juvetic.pokedex.util.PaginationScrollListener;

import java.util.List;
import java.util.concurrent.TimeoutException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements PaginationAdapterCallback {

    private static final String TAG = "MainActivity";
    private static final int LIMIT = 20;
    private static final int OFFSET_START = 0;

    PokemonAdapter adapter;
    LinearLayoutManager linearLayoutManager;

    RecyclerView rv;
    ProgressBar progressBar;
    LinearLayout errorLayout;
    Button btnRetry;
    TextView txtError, txtErrorInfo1, txtErrorInfo2, txtErrorInfo3, txtErrorInfo4;

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
        errorLayout = findViewById(R.id.error_layout);
        btnRetry = findViewById(R.id.error_btn_retry);
        txtError = findViewById(R.id.error_txt_cause);
        txtErrorInfo1 = findViewById(R.id.error_txt_cause_info1);
        txtErrorInfo2 = findViewById(R.id.error_txt_cause_info2);
        txtErrorInfo3 = findViewById(R.id.error_txt_cause_info3);
        txtErrorInfo4 = findViewById(R.id.error_txt_cause_info4);

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

                loadNextPage();
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
        pokemonService = PokemonApi.getClient().create(PokemonService.class);

        loadFirstPage();

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadFirstPage();
            }
        });
    }

    private void loadFirstPage() {

        hideErrorView();

        callPokemon().enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {

                hideErrorView();

                Log.d(TAG, "onResponse: " + response.raw().toString());
                if (response.code() != 403 || response.code() != 504) {

                    List<Result> results = fetchResults(response);
                    progressBar.setVisibility(View.GONE);
                    adapter.addAll(results);

                    if (currentOffset <= TOTAL_OFFSET) adapter.addLoadingFooter();
                    else isLastPage = true;

                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtErrorInfo1.setVisibility(View.VISIBLE);
                    txtErrorInfo2.setVisibility(View.VISIBLE);
                    txtErrorInfo3.setVisibility(View.VISIBLE);
                    txtErrorInfo4.setVisibility(View.VISIBLE);
                    String errorMsg = getResources().getString(R.string.error_msg_unknown);
                    txtError.setText(errorMsg);
                }
            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                t.printStackTrace(); // for now
                showErrorView(t);
            }
        });

    }

    private void loadNextPage() {

        callPokemon().enqueue(new Callback<Pokemon>() {
            @Override
            public void onResponse(Call<Pokemon> call, Response<Pokemon> response) {

                adapter.removeLoadingFooter();
                isLoading = false;

                if (response.code() != 403 || response.code() != 504) {
                    List<Result> results = fetchResults(response);
                    adapter.addAll(results);

                    if (currentOffset != TOTAL_OFFSET) adapter.addLoadingFooter();
                    else isLastPage = true;
                } else {
                    errorLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                    txtErrorInfo1.setVisibility(View.VISIBLE);
                    txtErrorInfo2.setVisibility(View.VISIBLE);
                    txtErrorInfo3.setVisibility(View.VISIBLE);
                    txtErrorInfo4.setVisibility(View.VISIBLE);
                    String errorMsg = getResources().getString(R.string.error_msg_unknown);
                    txtError.setText(errorMsg);
                }

            }

            @Override
            public void onFailure(Call<Pokemon> call, Throwable t) {
                t.printStackTrace(); // for now
                adapter.showRetry(true, fetchErrorMessage(t));
            }
        });
    }

    @Override
    public void retryPageLoad() {
        loadNextPage();
    }

    /**
     * Performs a Retrofit call to pokemon API.
     * Same API call for Pagination.
     * As {@link #currentOffset} will be incremented automatically
     * by @{@link PaginationScrollListener} to load next page.
     */
    private Call<Pokemon> callPokemon() {
        return pokemonService.getPokemon(
                LIMIT,
                currentOffset
        );
    }

    /**
     * @param response extracts List<{@link Result>} from response
     * @return
     */
    private List<Result> fetchResults(Response<Pokemon> response) {
        Pokemon pokemon = response.body();
        return pokemon.getResults();
    }

    /**
     * @param throwable required for {@link #fetchErrorMessage(Throwable)}
     * @return
     */
    private void showErrorView(Throwable throwable) {

        if (errorLayout.getVisibility() == View.GONE) {
            errorLayout.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);

            txtError.setText(fetchErrorMessage(throwable));
        }
    }

    /**
     * @param throwable to identify the type of error
     * @return appropriate error message
     */
    private String fetchErrorMessage(Throwable throwable) {
        String errorMsg = getResources().getString(R.string.error_msg_unknown);

        if (!isNetworkConnected()) {
            errorMsg = getResources().getString(R.string.error_msg_no_internet);
        } else if (throwable instanceof TimeoutException) {
            errorMsg = getResources().getString(R.string.error_msg_timeout);
        }

        return errorMsg;
    }

    // Helpers -------------------------------------------------------------------------------------


    private void hideErrorView() {
        if (errorLayout.getVisibility() == View.VISIBLE) {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remember to add android.permission.ACCESS_NETWORK_STATE permission.
     *
     * @return
     */
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }


}
