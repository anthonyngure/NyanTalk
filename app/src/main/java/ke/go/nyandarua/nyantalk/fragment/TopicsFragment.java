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
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.RequestParams;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.IAdapter;
import com.mikepenz.fastadapter.adapters.ItemAdapter;
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.OnClickListener;

import java.util.Objects;

import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.dataloading2.DataLoadingConfig;
import ke.co.toshngure.dataloading2.DefaultCursorImpl;
import ke.co.toshngure.dataloading2.ModelListFragment;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.model.Ticket;
import ke.go.nyandarua.nyantalk.model.Topic;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;


/**
 * A simple {@link Fragment} subclass.
 */
public class TopicsFragment extends ModelListFragment<Topic> {


    public static final String ACTION_NEW_TOPIC = "ke.go.nyandarua.nyantalk.ACTION_NEW_TOPIC";

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!TextUtils.isEmpty(intent.getAction()) && intent.getAction().equals(ACTION_NEW_TOPIC)) {
                Topic topic = intent.getParcelableExtra(Extras.TOPIC);
                if (mItemAdapter.getAdapterItemCount() != 0) {
                    mItemAdapter.add(0, topic);
                    mRecyclerView.smoothScrollToPosition(0);
                } else {
                    refresh();
                }
                //Toast.makeText(context, intent.getAction(), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public TopicsFragment() {
        // Required empty public constructor
    }

    public static TopicsFragment newInstance() {

        Bundle args = new Bundle();

        TopicsFragment fragment = new TopicsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter intentFilter = new IntentFilter(ACTION_NEW_TOPIC);
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
    public DataLoadingConfig<Topic> getDataLoadingConfig() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TOPICS);
        return super.getDataLoadingConfig()
                .withDebugEnabled()
                .withCursors(new DefaultCursorImpl(), false, true)
                .withEmptyDataMessage(R.string.topics_message_empty_data, android.R.color.black)
                .withPerPage(10)
                .withUrl(url, Client.getInstance().getClient(), Topic.class, true);
    }


    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.FILTER, BackEnd.Params.ALL);
        params.put(BackEnd.Params.INCLUDE, "author,forum");
        return params;
    }

    @Override
    public void onSetUpAdapter(ItemAdapter<Topic> itemAdapter, FastAdapter<Topic> fastAdapter) {
        super.onSetUpAdapter(itemAdapter, fastAdapter);
        mFastAdapter.withOnClickListener((v, adapter, item, position) -> {
            ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                    ContributionsFragment.newInstance(item), getString(R.string.contributions));
            return false;
        });
    }
}
