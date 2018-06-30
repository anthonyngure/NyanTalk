package ke.go.nyandarua.nyantalk.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.loopj.android.http.RequestParams;
import com.mikepenz.fastadapter.IAdapter;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class TopicsFragment extends ModelListFragment<Topic> {


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
    public void onSetUpAdapter(FastItemAdapter<Topic> fastItemAdapter) {
        super.onSetUpAdapter(fastItemAdapter);
        fastItemAdapter.withOnClickListener((v, adapter, item, position) -> {
            ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                    ContributionsFragment.newInstance(item), getString(R.string.contributions));
            return false;
        });
    }
}
