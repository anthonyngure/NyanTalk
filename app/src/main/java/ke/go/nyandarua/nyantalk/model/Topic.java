package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.toshngure.basecode.utils.DatesHelper;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.co.toshngure.views.NetworkImage;
import ke.co.toshngure.views.ToshTextView;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.network.BackEnd;

public class Topic extends AbstractItem<Topic, Topic.ViewHolder> implements Parcelable {


    private long id;
    private String title;
    private String description;
    private String createdAt;
    private int contributionsCount;
    private User author;
    private Forum forum;

    public Topic() {
    }

    public long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }

    public Forum getForum() {
        return forum;
    }

    public int getContributionsCount() {
        return contributionsCount;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_type_topic;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_topic;
    }

    static class ViewHolder extends FastItemAdapter.ViewHolder<Topic> {
        @BindView(R.id.avatarTV)
        NetworkImage avatarTV;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.createdAtTV)
        ToshTextView createdAtTV;
        @BindView(R.id.titleTV)
        TextView titleTV;
        @BindView(R.id.descriptionTV)
        TextView descriptionTV;
        @BindView(R.id.forumTV)
        TextView forumTV;
        @BindView(R.id.contributionsCountTV)
        TextView contributionsCountTV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(Topic item, List<Object> payloads) {
            avatarTV.loadImageFromNetwork(BackEnd.image(item.author.getAvatar()));
            nameTV.setText(item.author.getName());
            createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(item.getCreatedAt()) - 30000);
            descriptionTV.setText(item.description);
            titleTV.setText(item.title);
            forumTV.setText(item.forum.getName());
            contributionsCountTV.setText(new Spanny(String.valueOf(item.contributionsCount)).append(" contributions"));
        }

        @Override
        public void unbindView(Topic item) {

        }
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.title);
        dest.writeString(this.description);
        dest.writeString(this.createdAt);
        dest.writeInt(this.contributionsCount);
        dest.writeParcelable(this.author, flags);
        dest.writeParcelable(this.forum, flags);
    }

    protected Topic(Parcel in) {
        this.id = in.readLong();
        this.title = in.readString();
        this.description = in.readString();
        this.createdAt = in.readString();
        this.contributionsCount = in.readInt();
        this.author = in.readParcelable(User.class.getClassLoader());
        this.forum = in.readParcelable(Forum.class.getClassLoader());
    }

    public static final Creator<Topic> CREATOR = new Creator<Topic>() {
        @Override
        public Topic createFromParcel(Parcel source) {
            return new Topic(source);
        }

        @Override
        public Topic[] newArray(int size) {
            return new Topic[size];
        }
    };
}
