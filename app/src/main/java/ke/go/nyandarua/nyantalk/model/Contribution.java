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
import ke.co.toshngure.views.NetworkImage;
import ke.co.toshngure.views.ToshTextView;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.network.BackEnd;

public class Contribution extends AbstractItem<Contribution, Contribution.ViewHolder> implements Parcelable {


    private long id;
    private String text;
    private String createdAt;
    private User author;


    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_type_contribution;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_contribution;
    }

    static class ViewHolder extends FastItemAdapter.ViewHolder<Contribution> {

        @BindView(R.id.avatarTV)
        NetworkImage avatarTV;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.textTV)
        TextView textTV;
        @BindView(R.id.createdAtTV)
        ToshTextView createdAtTV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(Contribution item, List<Object> payloads) {
            avatarTV.loadImageFromNetwork(BackEnd.image(item.author.getAvatar()));
            nameTV.setText(item.author.getName());
            textTV.setText(item.getText());
            createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(item.getCreatedAt()) - 30000);
        }

        @Override
        public void unbindView(Contribution item) {

        }
    }


    public long getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getAuthor() {
        return author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.text);
        dest.writeString(this.createdAt);
        dest.writeParcelable(this.author, flags);
    }

    public Contribution() {
    }

    protected Contribution(Parcel in) {
        this.id = in.readLong();
        this.text = in.readString();
        this.createdAt = in.readString();
        this.author = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Contribution> CREATOR = new Creator<Contribution>() {
        @Override
        public Contribution createFromParcel(Parcel source) {
            return new Contribution(source);
        }

        @Override
        public Contribution[] newArray(int size) {
            return new Contribution[size];
        }
    };
}
