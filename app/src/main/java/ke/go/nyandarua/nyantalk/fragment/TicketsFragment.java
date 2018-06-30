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
import ke.go.nyandarua.nyantalk.network.BackEnd;


/**
 * A simple {@link Fragment} subclass.
 */
public class TicketsFragment extends ModelListFragment<Ticket> {


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
