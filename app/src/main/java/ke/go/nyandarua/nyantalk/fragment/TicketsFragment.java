package ke.go.nyandarua.nyantalk.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;

import java.util.Objects;

import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.dataloading2.DataLoadingConfig;
import ke.co.toshngure.dataloading2.DefaultCursorImpl;
import ke.co.toshngure.dataloading2.ModelListFragment;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.model.Ticket;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsFragment extends ModelListFragment<Ticket> {

    public static final String ACTION_NEW_TICKET = "ke.go.nyandarua.nyantalk.ACTION_NEW_TICKET";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(ACTION_NEW_TICKET)) {
                Ticket ticket = intent.getParcelableExtra(Extras.TICKET);
                if (mFastItemAdapter.getItemCount() != 0) {
                    mFastItemAdapter.add(0, ticket);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    refresh();
                }
                //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public TicketsFragment() {
        // Required empty public constructor
    }

    public static TicketsFragment newInstance() {

        Bundle args = new Bundle();

        TicketsFragment fragment = new TicketsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public DataLoadingConfig<Ticket> getDataLoadingConfig() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS);
        return super.getDataLoadingConfig()
                .withDebugEnabled()
                .withCursors(new DefaultCursorImpl(), false, true)
                .withPerPage(10)
                .withEmptyDataMessage(R.string.tickets_message_empty_data, android.R.color.black)
                .withUrl(url, Client.getInstance().getClient(), Ticket.class, true);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ACTION_NEW_TICKET);
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()))
                .registerReceiver(mBroadcastReceiver, intentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(Objects.requireNonNull(getActivity()))
                .unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onSaveItem(Ticket item) {
        super.onSaveItem(item);
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.INCLUDE, "rating,department,ward.sub-county,official");
        return params;
    }

    @Override
    public void onSetUpAdapter(FastItemAdapter<Ticket> fastItemAdapter) {
        super.onSetUpAdapter(fastItemAdapter);
        fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
            ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                    ResponsesFragment.newInstance(item), getString(R.string.responses));
            return false;
        });
    }
}
