package ke.go.nyandarua.nyantalk.fragment;


import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import com.hsalf.smilerating.BaseRating;
import com.hsalf.smilerating.SmileRating;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Objects;

import butterknife.BindView;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.co.toshngure.basecode.utils.DatesHelper;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.co.toshngure.dataloading2.DataLoadingConfig;
import ke.co.toshngure.dataloading2.DefaultCursorImpl;
import ke.co.toshngure.dataloading2.ModelListBottomSheetFragment;
import ke.co.toshngure.dataloading2.ModelListFragment;
import ke.co.toshngure.logging.BeeLog;
import ke.co.toshngure.views.ToshTextView;
import ke.co.toshngure.views.utils.Utils;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.model.Rating;
import ke.go.nyandarua.nyantalk.model.Response;
import ke.go.nyandarua.nyantalk.model.Ticket;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResponsesFragment extends ModelListFragment<Response> {

    private static final String TAG = "ResponsesFragment";
    private Ticket mTicket;
    private ImageButton mSendBtn;
    private EditText mReplyET;
    private SmileRating smileRating;
    private MaterialEditText commentMET;
    private Button submitRatingBtn;
    private TextView ratingGuideTV;
    private TextView ratingGuideTitleTV;

    public ResponsesFragment() {
        // Required empty public constructor
    }

    public static ResponsesFragment newInstance(Ticket ticket) {

        Bundle args = new Bundle();
        args.putParcelable(Extras.TICKET, ticket);
        ResponsesFragment fragment = new ResponsesFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        if (getArguments() != null) {
            mTicket = getArguments().getParcelable(Extras.TICKET);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public DataLoadingConfig<Response> getDataLoadingConfig() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS + "/" + mTicket.getId() + BackEnd.EndPoints.RESPONSES);
        return super.getDataLoadingConfig()
                .withDebugEnabled()
                .withTopViewCollapsible()
                .withEmptyDataMessage(R.string.responses_message_empty_data, android.R.color.black)
                .withUrl(url, Client.getInstance().getClient(), Response.class, true);
    }

    @Override
    public void onSaveItem(Response item) {
        super.onSaveItem(item);
    }

    @Override
    public RequestParams getRequestParams() {
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.INCLUDE, "official.department,citizen");
        return params;
    }

    @Override
    public void setUpTopView(FrameLayout topViewContainer) {
        super.setUpTopView(topViewContainer);
        LayoutInflater.from(getActivity()).inflate(R.layout.item_ticket, topViewContainer);
        topViewContainer.setBackgroundColor(Color.LTGRAY);
        TextView subjectTV = topViewContainer.findViewById(R.id.subjectTV);
        subjectTV.setText(mTicket.getSubject());
        TextView detailsTV = topViewContainer.findViewById(R.id.detailsTV);
        detailsTV.setText(mTicket.getDetails());
        ToshTextView departmentTV = topViewContainer.findViewById(R.id.departmentTV);
        departmentTV.setText(mTicket.getDepartment().getName());
        ToshTextView statusTV = topViewContainer.findViewById(R.id.statusTV);
        if (mTicket.getOfficial() != null){
            statusTV.setText(new Spanny(mTicket.getStatus()).append(" | ").append(mTicket.getOfficial().getName()));
        } else {
            statusTV.setText(mTicket.getStatus());
        }
        ToshTextView responsesCountTV = topViewContainer.findViewById(R.id.responsesCountTV);
        responsesCountTV.setText(new Spanny(String.valueOf(mTicket.getResponsesCount())).append(" responses"));
        ToshTextView createdAtTV = topViewContainer.findViewById(R.id.createdAtTV);
        createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(mTicket.getCreatedAt()));
        ToshTextView wardTV = topViewContainer.findViewById(R.id.wardTV);
        wardTV.setText(new Spanny(mTicket.getWard().getName()).append(", ")
                .append(mTicket.getWard().getSubCounty().getName()));
    }

    @Override
    public void setUpBottomView(FrameLayout bottomViewContainer) {
        super.setUpBottomView(bottomViewContainer);
        if (mTicket.isComplete()) {
            LayoutInflater.from(getActivity()).inflate(R.layout.rating_view, bottomViewContainer);
            smileRating = bottomViewContainer.findViewById(R.id.smileRating);
            submitRatingBtn = bottomViewContainer.findViewById(R.id.submitRatingBtn);
            commentMET = bottomViewContainer.findViewById(R.id.commentMET);
            ratingGuideTV = bottomViewContainer.findViewById(R.id.ratingGuideTV);
            ratingGuideTitleTV = bottomViewContainer.findViewById(R.id.ratingGuideTitleTV);
            if (mTicket.isRated()) {
                smileRating.setIndicator(true);
                smileRating.setSelectedSmile(mTicket.getRating().getStars()-1);
                ratingGuideTitleTV.setText(R.string.heading_ticket_rated);
                ratingGuideTV.setVisibility(View.GONE);
            } else {
                bottomViewContainer.findViewById(R.id.ratingGuideTV).setVisibility(View.VISIBLE);
                smileRating.setOnRatingSelectedListener(new SmileRating.OnRatingSelectedListener() {
                    @Override
                    public void onRatingSelected(int level, boolean reselected) {
                        submitRatingBtn.setVisibility(View.VISIBLE);
                        commentMET.setVisibility(View.VISIBLE);
                    }
                });
                submitRatingBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        submitRating();
                    }
                });
            }
        } else {
            LayoutInflater.from(getActivity()).inflate(R.layout.fragment_response_bottom_view, bottomViewContainer);
            mSendBtn = bottomViewContainer.findViewById(R.id.sendBtn);
            mSendBtn.setOnClickListener(v -> submitReply());
            mReplyET = bottomViewContainer.findViewById(R.id.replyET);
        }
    }

    private void submitRating() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS + "/" + mTicket.getId() + BackEnd.EndPoints.RATINGS);
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.STARS, smileRating.getRating());
        params.put(BackEnd.Params.TEXT, commentMET.getText().toString());
        Client.getInstance().getClient().post(url, params, new ResponseHandler(new SubmitRatingCallback()));
    }

    private class SubmitRatingCallback extends Callback<Rating> {

        SubmitRatingCallback() {
            super((BaseAppActivity) getActivity(), Rating.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            submitRating();
        }

        @Override
        protected void onResponse(Rating item) {
            super.onResponse(item);
            commentMET.setVisibility(View.GONE);
            submitRatingBtn.setVisibility(View.GONE);
            ratingGuideTV.setVisibility(View.GONE);
            smileRating.setIndicator(true);
            smileRating.setSelectedSmile(item.getStars()-1);
            ratingGuideTitleTV.setText(R.string.heading_ticket_rated);


        }
    }

    private void submitReply() {
        String reply = mReplyET.getText().toString();
        if (TextUtils.isEmpty(reply)) {
            Snackbar.make(mSendBtn, R.string.error_reply, Snackbar.LENGTH_INDEFINITE)
                    .setAction(android.R.string.ok, v -> {
                    }).show();
        } else {
            String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS + "/" + mTicket.getId() + BackEnd.EndPoints.RESPONSES);
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.DETAILS, reply);
            //params.put(BackEnd.Params.INCLUDE, "citizen");
            url = url + "&include=citizen";
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new SubmitResponseCallback()));
        }
    }

    private class SubmitResponseCallback extends Callback<Response> {

        SubmitResponseCallback() {
            super((BaseAppActivity) getActivity(), Response.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            submitReply();
        }

        @Override
        protected void onResponse(Response item) {
            super.onResponse(item);
            //BeeLog.i(TAG, item);
            if (mItemAdapter.getAdapterItemCount() == 0){
                refresh();
            } else {
                mItemAdapter.add(item);
                mRecyclerView.smoothScrollToPosition((mItemAdapter.getAdapterItemCount() - 1));
            }
            mReplyET.getText().clear();
        }
    }
}
