package ke.go.nyandarua.nyantalk.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;

import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.DatesHelper;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.co.toshngure.dataloading2.DataLoadingConfig;
import ke.co.toshngure.dataloading2.DefaultCursorImpl;
import ke.co.toshngure.dataloading2.ModelListFragment;
import ke.co.toshngure.views.NetworkImage;
import ke.co.toshngure.views.ToshTextView;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.model.Contribution;
import ke.go.nyandarua.nyantalk.model.Topic;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;


/**
 * A simple {@link Fragment} subclass.
 */
public class ContributionsFragment extends ModelListFragment<Contribution> {

    private static final String TAG = "ContributionsFragment";
    private Topic mTopic;
    private ImageButton mSendBtn;
    private EditText mReplyET;

    public ContributionsFragment() {
        // Required empty public constructor
    }

    public static ContributionsFragment newInstance(Topic topic) {

        Bundle args = new Bundle();
        args.putParcelable(Extras.TICKET, topic);
        ContributionsFragment fragment = new ContributionsFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTopic = getArguments().getParcelable(Extras.TICKET);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public DataLoadingConfig<Contribution> getDataLoadingConfig() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TOPICS + "/"
                + mTopic.getId() + BackEnd.EndPoints.CONTRIBUTIONS);
        return super.getDataLoadingConfig()
                .withDebugEnabled()
                .withTopViewCollapsible()
                .withCursors(new DefaultCursorImpl(), false, true)
                .withEmptyDataMessage(R.string.contributions_message_empty_data, android.R.color.black)
                .withUrl(url, Client.getInstance().getClient(), Contribution.class, true);
    }

    @Override
    public void onSaveItem(Contribution item) {
        super.onSaveItem(item);
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.INCLUDE, "author");
        return params;
    }

    @Override
    public void setUpTopView(FrameLayout topViewContainer) {
        super.setUpTopView(topViewContainer);
        LayoutInflater.from(getActivity()).inflate(R.layout.item_topic, topViewContainer);
        topViewContainer.setBackgroundColor(Color.LTGRAY);
        NetworkImage avatarTV = topViewContainer.findViewById(R.id.avatarTV);
        TextView nameTV = topViewContainer.findViewById(R.id.nameTV);
        ToshTextView createdAtTV = topViewContainer.findViewById(R.id.createdAtTV);
        TextView titleTV = topViewContainer.findViewById(R.id.titleTV);
        TextView descriptionTV = topViewContainer.findViewById(R.id.descriptionTV);
        TextView forumTV = topViewContainer.findViewById(R.id.forumTV);
        TextView contributionsCountTV = topViewContainer.findViewById(R.id.contributionsCountTV);

        avatarTV.loadImageFromNetwork(BackEnd.image(mTopic.getAuthor().getAvatar()));
        nameTV.setText(mTopic.getAuthor().getName());
        createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(mTopic.getCreatedAt()));
        descriptionTV.setText(mTopic.getDescription());
        titleTV.setText(mTopic.getTitle());
        forumTV.setText(mTopic.getForum().getName());
        contributionsCountTV.setText(new Spanny(String.valueOf(mTopic.getContributionsCount())).append(" contributions"));
    }

    @Override
    public void setUpBottomView(FrameLayout bottomViewContainer) {
        super.setUpBottomView(bottomViewContainer);
        LayoutInflater.from(getActivity()).inflate(R.layout.fragment_response_bottom_view, bottomViewContainer);
        mSendBtn = bottomViewContainer.findViewById(R.id.sendBtn);
        mSendBtn.setOnClickListener(v -> submitReply());
        mReplyET = bottomViewContainer.findViewById(R.id.replyET);
        mReplyET.setHint(R.string.hint_contribution);
    }

    private void submitReply() {
        String reply = mReplyET.getText().toString();
        if (TextUtils.isEmpty(reply)) {
            Snackbar.make(mSendBtn, R.string.error_contribution, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> {
                    }).show();
        } else {
            String url = Client.absoluteUrl(BackEnd.EndPoints.TOPICS + "/" + mTopic.getId() + BackEnd.EndPoints.CONTRIBUTIONS);
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.TEXT, reply);
            url = url + "&include=author";
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new SubmitResponseCallback()));
        }
    }

    private class SubmitResponseCallback extends Callback<Contribution> {

        SubmitResponseCallback() {
            super((BaseAppActivity) getActivity(), Contribution.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            submitReply();
        }

        @Override
        protected void onResponse(Contribution item) {
            super.onResponse(item);
            if (mFastItemAdapter.getItemCount() == 0){
                refresh();
            } else {
                mFastItemAdapter.add(item);
                mRecyclerView.smoothScrollToPosition((mFastItemAdapter.getItemCount() - 1));
            }
            mReplyET.getText().clear();
        }
    }
}
