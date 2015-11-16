package wash.rocket.xor.rocketwash.ui;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;

import java.util.ArrayList;

import wash.rocket.xor.rocketwash.R;
import wash.rocket.xor.rocketwash.adapters.DialogRecyclerViewAdapter;
import wash.rocket.xor.rocketwash.model.DialogItem;
import wash.rocket.xor.rocketwash.services.JacksonGoogleHttpClientSpiceServiceEx;
import wash.rocket.xor.rocketwash.widgets.DividerItemDecoration;


public class Dialoglist extends DialogFragment implements OnItemClickListener {
    protected static final String LIST_PARAM = "dialog.list.param";
    protected Bundle params;
    protected ArrayList<DialogItem> list = null;
    protected RecyclerView recyclerView;
    protected DialogRecyclerViewAdapter adapter;
    protected ProgressBar progressBar;
    protected Toolbar toolbar;
    protected SearchView searchView;

    private SpiceManager spiceManager = new SpiceManager(JacksonGoogleHttpClientSpiceServiceEx.class);

    public static Dialoglist newInstance() {
        Dialoglist d = new Dialoglist();
        d.setStyle(DialogFragment.STYLE_NO_FRAME, 0);
        /*
        Bundle data = new Bundle();
        data.putStringArrayList(LIST_PARAM, list);
        d.setArguments(data);*/
        return d;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setRetainInstance(true);
        params = getArguments();
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_list, container, true);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar1);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        adapter = new DialogRecyclerViewAdapter(list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setHasFixedSize(true);
        //recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);

        recyclerView.setFocusableInTouchMode(true);
        recyclerView.requestFocus();
        toolbar = (Toolbar) v.findViewById(R.id.toolbar1);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        super.onCancel(dialog);
        dismissAllowingStateLoss();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_CANCELED, getActivity().getIntent());
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //dismissAllowingStateLoss();
        //Intent i = new Intent();
        //i.putExtra("position", position);
        //getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
    }

    public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.left = space;
            outRect.right = space;
            outRect.bottom = space;

            if (parent.getChildPosition(view) == 0)
                outRect.top = space;
        }
    }

    protected SpiceManager getSpiceManager() {
        return spiceManager;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (spiceManager.isStarted()) {
            spiceManager.shouldStop();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        spiceManager.start(getActivity());
    }

    public void shwToast(String text, int resback, int length) {

        LayoutInflater mInflater = LayoutInflater.from(getActivity());
        Toast t = Toast.makeText(getActivity(), text, length);
        View v = mInflater.inflate(resback, null);
        TextView tv = (TextView) v.findViewById(R.id.text);
        tv.setText(text);
        t.setView(v);
        t.show();
    }

    public void shwToastOk(String text) {
        shwToast(text, R.layout.toast_ok, Toast.LENGTH_SHORT);
    }

    public void shwToastOk(int res) {
        shwToastOk(getActivity().getString(res));
    }

    public void shwToastError(String text) {
        shwToast(text, R.layout.toast_error, Toast.LENGTH_LONG);
    }

    public void shwToastError(int res) {
        shwToastError(getActivity().getString(res));
    }
}
