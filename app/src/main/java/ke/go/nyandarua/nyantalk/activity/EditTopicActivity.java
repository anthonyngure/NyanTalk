package ke.go.nyandarua.nyantalk.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.model.Forum;
import ke.go.nyandarua.nyantalk.model.Topic;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;

public class EditTopicActivity extends BaseActivity {

    @BindView(R.id.titleMET)
    MaterialEditText titleMET;
    @BindView(R.id.descriptionMET)
    MaterialEditText descriptionMET;
    @BindView(R.id.forumMS)
    MaterialSpinner forumMS;
    private List<Forum> mForums;
    private Forum mSelectedForum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_topic);
        ButterKnife.bind(this);
        getData();
    }

    private void getData() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.FORUMS);
        Client.getInstance().getClient().get(url, new ResponseHandler(new ForumsCallback()));
    }

    @OnClick(R.id.submitBtn)
    public void onSubmitBtnClicked() {
        String title = titleMET.getText().toString();
        String description = descriptionMET.getText().toString();
        if (mSelectedForum == null) {
            toast(R.string.error_forum);
        } else if (TextUtils.isEmpty(title)) {
            toast(R.string.error_topic_title);
        } else if (TextUtils.isEmpty(description)) {
            toast(R.string.error_topic_description);
        } else {
            RequestParams requestParams = new RequestParams();
            requestParams.put(BackEnd.Params.TITLE, title);
            requestParams.put(BackEnd.Params.DESCRIPTION, description);
            requestParams.put(BackEnd.Params.FORUM_ID, mSelectedForum.getId());
            String url = Client.absoluteUrl(BackEnd.EndPoints.TOPICS);
            Client.getInstance().getClient().post(url, requestParams, new ResponseHandler(new SubmitCallback()));
        }
    }

    private class ForumsCallback extends Callback<Forum> {

        ForumsCallback() {
            super(EditTopicActivity.this, Forum.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            getData();
        }

        @Override
        protected void onResponse(List<Forum> items) {
            super.onResponse(items);
            mForums = items;
            initSpinners();
        }
    }

    private void initSpinners() {
        List<String> forumNames = new ArrayList<>();
        for (Forum forum : mForums) {
            forumNames.add(forum.getName());
        }
        forumMS.setItems(forumNames);
        forumMS.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item)
                -> mSelectedForum = mForums.get(position));
    }

    private class SubmitCallback extends Callback<Topic> {

        SubmitCallback() {
            super(EditTopicActivity.this, Topic.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onSubmitBtnClicked();
        }

        @Override
        protected void onResponse(Topic item) {
            super.onResponse(item);
            Intent intent = new Intent();
            intent.putExtra(Extras.TOPIC, item);
            setResult(Activity.RESULT_OK, intent);
            EditTopicActivity.this.finish();
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, EditTopicActivity.class);
        context.startActivity(starter);
    }

}
