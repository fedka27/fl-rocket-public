package wash.rocket.xor.rocketwash.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.Reservation;
import wash.rocket.xor.rocketwash.model.ReservedResult;
import wash.rocket.xor.rocketwash.requests.ReserveCompletedRequest;
import wash.rocket.xor.rocketwash.ui.BaseActivity;

public class HistoryActivity extends BaseActivity {
    private Profile profile;

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ViewGroup contentLoading;
    private ViewGroup contentEmpty;

    private HistoryAdapter historyAdapter = new HistoryAdapter();

    public static void start(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        spiceManager.start(this);

        profile = preferences.getProfile();

        initViews();
        initToolbar();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setAdapter(historyAdapter);

        contentLoading = findViewById(R.id.contentLoading);
        contentEmpty = findViewById(R.id.contentEmpty);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_history_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (historyAdapter.isEmpty()) {
            loadHistory();
        }
    }

    private void loadHistory() {
        showProgress();
        spiceManager.execute(new ReserveCompletedRequest(preferences.getSessionID()),
                null,
                DurationInMillis.ALWAYS_EXPIRED,
                new RequestListener<ReservedResult>() {
                    @Override
                    public void onRequestFailure(SpiceException spiceException) {
                        spiceException.printStackTrace();
                        Toast.makeText(HistoryActivity.this, R.string.activity_history_error_loading, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onRequestSuccess(ReservedResult reservedResult) {
                        hideProgress();
                        List<Reservation> reservationList = reservedResult.getData();
                        if (reservationList.isEmpty()) {
                            showEmptyResult();
                        }
                        historyAdapter.setReservations(reservationList);
                    }
                });
    }

    private void showProgress() {
        contentLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        contentEmpty.setVisibility(View.GONE);
    }

    private void hideProgress() {
        recyclerView.setVisibility(View.VISIBLE);
        contentLoading.setVisibility(View.GONE);
        contentEmpty.setVisibility(View.GONE);
    }

    private void showEmptyResult() {
        contentEmpty.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        contentLoading.setVisibility(View.GONE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }
}
