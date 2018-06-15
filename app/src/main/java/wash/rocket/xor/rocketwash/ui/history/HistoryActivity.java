package wash.rocket.xor.rocketwash.ui.history;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Toast;

import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.model.Profile;
import wash.rocket.xor.rocketwash.model.ReservedResult;
import wash.rocket.xor.rocketwash.requests.ReservedRequest;
import wash.rocket.xor.rocketwash.ui.BaseActivity;

public class HistoryActivity extends BaseActivity {
    private Profile profile;

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private HistoryAdapter historyAdapter = new HistoryAdapter();

    public static void start(Context context) {
        Intent intent = new Intent(context, HistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

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
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(R.string.activity_history_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onStart() {
        super.onStart();
        spiceManager.start(this);
        if (historyAdapter.isEmpty()) {
            loadHistory();
        }
    }

    private void loadHistory() {
        spiceManager.execute(new ReservedRequest(preferences.getSessionID()), null, DurationInMillis.ALWAYS_EXPIRED, new RequestListener<ReservedResult>() {
            @Override
            public void onRequestFailure(SpiceException spiceException) {
                spiceException.printStackTrace();
                Toast.makeText(HistoryActivity.this, R.string.activity_history_error_loading, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onRequestSuccess(ReservedResult reservedResult) {
                historyAdapter.setReservations(reservedResult.getData());
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }
}
