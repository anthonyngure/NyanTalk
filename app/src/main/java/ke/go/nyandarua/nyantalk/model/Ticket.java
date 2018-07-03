package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.items.AbstractItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ke.co.toshngure.basecode.utils.DatesHelper;
import ke.co.toshngure.basecode.utils.Spanny;
import ke.co.toshngure.logging.BeeLog;
import ke.co.toshngure.views.NetworkImage;
import ke.co.toshngure.views.ToshTextView;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.network.BackEnd;

public class Ticket extends AbstractItem<Ticket, Ticket.ViewHolder> implements Parcelable {

    private static final String TAG = "Ticket";

    public boolean isComplete() {
        return this.status.equals(STATUS.COMPLETED);
    }

    public static final class STATUS {

        public static final String COMPLETED = "COMPLETED";
    }

    private boolean isRated;
    private int responsesCount;
    private String updatedAt;
    private String createdAt;
    private String status;
    private String image;
    private String details;
    private String subject;
    private String assignedOfficialAt;
    private String number;
    private Department department;
    private Ward ward;
    private User official;
    private long id;
    private Rating rating;

    public Ticket() {
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_type_ticket;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_ticket;
    }

    public boolean getIsRated() {
        return isRated;
    }

    public int getResponsesCount() {
        return responsesCount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getStatus() {
        return status;
    }


    public String getDetails() {
        return details;
    }

    public String getSubject() {
        return subject;
    }

    public String getAssignedOfficialAt() {
        return assignedOfficialAt;
    }

    public String getNumber() {
        return number;
    }

    public long getId() {
        return id;
    }

    public void setIsRated(boolean isRated) {
        this.isRated = isRated;
    }

    public void setResponsesCount(int responsesCount) {
        this.responsesCount = responsesCount;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAssignedOfficialAt(String assignedOfficialAt) {
        this.assignedOfficialAt = assignedOfficialAt;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public void setId(long id) {
        this.id = id;
    }

    static class ViewHolder extends FastItemAdapter.ViewHolder<Ticket> {

        @BindView(R.id.subjectTV)
        TextView subjectTV;
        @BindView(R.id.detailsTV)
        TextView detailsTV;
        @BindView(R.id.departmentTV)
        ToshTextView departmentTV;
        @BindView(R.id.statusTV)
        ToshTextView statusTV;
        @BindView(R.id.responsesCountTV)
        ToshTextView responsesCountTV;
        @BindView(R.id.createdAtTV)
        ToshTextView createdAtTV;
        @BindView(R.id.wardTV)
        ToshTextView wardTV;
        @BindView(R.id.numberTV)
        TextView numberTV;
        @BindView(R.id.imageNI)
        NetworkImage imageNI;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(Ticket item, List<Object> payloads) {
            subjectTV.setText(item.getSubject());
            detailsTV.setText(item.getDetails());
            if (item.official == null) {
                statusTV.setText(item.getStatus());
            } else {
                statusTV.setText(new Spanny(item.getStatus()).append(" | ").append(item.official.getName()));
            }
            numberTV.setText(new Spanny("Number: ").append(item.getNumber()));
            departmentTV.setText(item.getDepartment().getName());
            responsesCountTV.setText(new Spanny(String.valueOf(item.getResponsesCount())).append(" responses"));
            createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(item.getCreatedAt()));
            wardTV.setText(new Spanny(item.getWard().getName()).append(", ").append(item.getWard().getSubCounty().getName()));
            if (!TextUtils.isEmpty(item.image)) {
                BeeLog.i(TAG, BackEnd.image(item.image));
                imageNI.loadImageFromNetwork(BackEnd.image(item.image));
                imageNI.setVisibility(View.VISIBLE);
            } else {
                imageNI.setVisibility(View.GONE);
            }
        }

        @Override
        public void unbindView(Ticket item) {

        }
    }

    public String getImage() {
        return image;
    }

    public boolean isRated() {
        return isRated;
    }

    public Department getDepartment() {
        return department;
    }

    public Ward getWard() {
        return ward;
    }


    public User getOfficial() {
        return official;
    }


    public Rating getRating() {
        return rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeByte(this.isRated ? (byte) 1 : (byte) 0);
        dest.writeInt(this.responsesCount);
        dest.writeString(this.updatedAt);
        dest.writeString(this.createdAt);
        dest.writeString(this.status);
        dest.writeString(this.image);
        dest.writeString(this.details);
        dest.writeString(this.subject);
        dest.writeString(this.assignedOfficialAt);
        dest.writeString(this.number);
        dest.writeParcelable(this.department, flags);
        dest.writeParcelable(this.ward, flags);
        dest.writeParcelable(this.official, flags);
        dest.writeLong(this.id);
        dest.writeParcelable(this.rating, flags);
    }

    protected Ticket(Parcel in) {
        this.isRated = in.readByte() != 0;
        this.responsesCount = in.readInt();
        this.updatedAt = in.readString();
        this.createdAt = in.readString();
        this.status = in.readString();
        this.image = in.readString();
        this.details = in.readString();
        this.subject = in.readString();
        this.assignedOfficialAt = in.readString();
        this.number = in.readString();
        this.department = in.readParcelable(Department.class.getClassLoader());
        this.ward = in.readParcelable(Ward.class.getClassLoader());
        this.official = in.readParcelable(User.class.getClassLoader());
        this.id = in.readLong();
        this.rating = in.readParcelable(Rating.class.getClassLoader());
    }

    public static final Creator<Ticket> CREATOR = new Creator<Ticket>() {
        @Override
        public Ticket createFromParcel(Parcel source) {
            return new Ticket(source);
        }

        @Override
        public Ticket[] newArray(int size) {
            return new Ticket[size];
        }
    };
}
