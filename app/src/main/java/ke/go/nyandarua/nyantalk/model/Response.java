package ke.go.nyandarua.nyantalk.model;

import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import com.mikepenz.fastadapter.FastAdapter;
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

public class Response extends AbstractItem<Response, Response.ViewHolder> {

    private String updatedAt;
    private String createdAt;
    private String details;
    private String id;
    private long userId;
    private long officialId;
    private User official;
    private User citizen;

    public Response() {
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getDetails() {
        return details;
    }

    public long getUserId() {
        return userId;
    }

    public long getOfficialId() {
        return officialId;
    }

    public String getId() {
        return id;
    }

    @NonNull
    @Override
    public ViewHolder getViewHolder(View v) {
        return new ViewHolder(v);
    }

    @Override
    public int getType() {
        return R.id.item_type_response;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.item_response;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setOfficialId(long officialId) {
        this.officialId = officialId;
    }

    static class ViewHolder extends FastItemAdapter.ViewHolder<Response> {

        @BindView(R.id.avatarTV)
        NetworkImage avatarTV;
        @BindView(R.id.nameTV)
        TextView nameTV;
        @BindView(R.id.departmentTV)
        TextView departmentTV;
        @BindView(R.id.detailsTV)
        TextView detailsTV;
        @BindView(R.id.createdAtTV)
        ToshTextView createdAtTV;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @Override
        public void bindView(Response item, List<Object> payloads) {
            if (item.citizen != null){
                avatarTV.loadImageFromNetwork(BackEnd.avatarUrl(item.citizen.getAvatar()));
                nameTV.setText(new Spanny("(Me) ").append(item.citizen.getName()));
                departmentTV.setVisibility(View.GONE);
            } else {
                avatarTV.loadImageFromNetwork(BackEnd.avatarUrl(item.official.getAvatar()));
                nameTV.setText(item.official.getName());
                if (item.official.getDepartment() != null){
                    departmentTV.setVisibility(View.VISIBLE);
                    departmentTV.setText(item.official.getDepartment().getName());
                } else {
                    departmentTV.setVisibility(View.GONE);
                }
            }
            createdAtTV.setReferenceTime(DatesHelper.formatSqlTimestamp(item.getCreatedAt())-30000);
            detailsTV.setText(item.details);

        }

        @Override
        public void unbindView(Response item) {

        }
    }

    public User getOfficial() {
        return official;
    }

    public User getCitizen() {
        return citizen;
    }

    @Override
    public String toString() {
        return "Response{" +
                "updatedAt='" + updatedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", details='" + details + '\'' +
                ", id='" + id + '\'' +
                ", userId=" + userId +
                ", officialId=" + officialId +
                ", official=" + official +
                ", citizen=" + citizen +
                '}';
    }
}
