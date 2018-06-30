package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;


public class User implements Parcelable {


    private int closedTickets;
    private int pendingTickets;
    private int queuedTickets;
    private int allTickets;
    private String facebookPictureUrl;
    private String facebookId;
    private String token;
    private int ticketsCount;
    private String updatedAt;
    private String createdAt;
    private boolean phoneVerified;
    private String phone;
    private String email;
    private String type;
    private String avatar;
    private String name;
    private long id;
    private Ward ward;
    private Department department;
    private boolean smsNotifiable;

    public User() {
    }

    public int getClosedTickets() {
        return closedTickets;
    }

    public int getPendingTickets() {
        return pendingTickets;
    }

    public int getQueuedTickets() {
        return queuedTickets;
    }

    public int getAllTickets() {
        return allTickets;
    }

    public String getToken() {
        return token;
    }

    public int getTicketsCount() {
        return ticketsCount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public boolean getPhoneVerified() {
        return phoneVerified;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public String getAvatar() {
        return avatar;
    }

    public String getName() {
        return name;
    }

    public long getId() {
        return id;
    }

    public String getFacebookPictureUrl() {
        return facebookPictureUrl;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public boolean isPhoneVerified() {
        return phoneVerified;
    }


    public Ward getWard() {
        return ward;
    }

    public boolean isSmsNotifiable() {
        return smsNotifiable;
    }


    public void setClosedTickets(int closedTickets) {
        this.closedTickets = closedTickets;
    }

    public void setPendingTickets(int pendingTickets) {
        this.pendingTickets = pendingTickets;
    }

    public void setQueuedTickets(int queuedTickets) {
        this.queuedTickets = queuedTickets;
    }

    public void setAllTickets(int allTickets) {
        this.allTickets = allTickets;
    }

    public void setFacebookPictureUrl(String facebookPictureUrl) {
        this.facebookPictureUrl = facebookPictureUrl;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTicketsCount(int ticketsCount) {
        this.ticketsCount = ticketsCount;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setPhoneVerified(boolean phoneVerified) {
        this.phoneVerified = phoneVerified;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Generated(hash = 1561130978)
    public User(int closedTickets, int pendingTickets, int queuedTickets, int allTickets,
            String facebookPictureUrl, String facebookId, String token, int ticketsCount,
            String updatedAt, String createdAt, boolean phoneVerified, String phone,
            String email, String type, String avatar, String name, long id) {
        this.closedTickets = closedTickets;
        this.pendingTickets = pendingTickets;
        this.queuedTickets = queuedTickets;
        this.allTickets = allTickets;
        this.facebookPictureUrl = facebookPictureUrl;
        this.facebookId = facebookId;
        this.token = token;
        this.ticketsCount = ticketsCount;
        this.updatedAt = updatedAt;
        this.createdAt = createdAt;
        this.phoneVerified = phoneVerified;
        this.phone = phone;
        this.email = email;
        this.type = type;
        this.avatar = avatar;
        this.name = name;
        this.id = id;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.closedTickets);
        dest.writeInt(this.pendingTickets);
        dest.writeInt(this.queuedTickets);
        dest.writeInt(this.allTickets);
        dest.writeString(this.facebookPictureUrl);
        dest.writeString(this.facebookId);
        dest.writeString(this.token);
        dest.writeInt(this.ticketsCount);
        dest.writeString(this.updatedAt);
        dest.writeString(this.createdAt);
        dest.writeByte(this.phoneVerified ? (byte) 1 : (byte) 0);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.type);
        dest.writeString(this.avatar);
        dest.writeString(this.name);
        dest.writeLong(this.id);
        dest.writeParcelable(this.ward, flags);
        dest.writeByte(this.smsNotifiable ? (byte) 1 : (byte) 0);
    }

    protected User(Parcel in) {
        this.closedTickets = in.readInt();
        this.pendingTickets = in.readInt();
        this.queuedTickets = in.readInt();
        this.allTickets = in.readInt();
        this.facebookPictureUrl = in.readString();
        this.facebookId = in.readString();
        this.token = in.readString();
        this.ticketsCount = in.readInt();
        this.updatedAt = in.readString();
        this.createdAt = in.readString();
        this.phoneVerified = in.readByte() != 0;
        this.phone = in.readString();
        this.email = in.readString();
        this.type = in.readString();
        this.avatar = in.readString();
        this.name = in.readString();
        this.id = in.readLong();
        this.ward = in.readParcelable(Ward.class.getClassLoader());
        this.smsNotifiable = in.readByte() != 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel source) {
            return new User(source);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public Department getDepartment() {
        return department;
    }

    @Override
    public String toString() {
        return "User{" +
                "closedTickets=" + closedTickets +
                ", pendingTickets=" + pendingTickets +
                ", queuedTickets=" + queuedTickets +
                ", allTickets=" + allTickets +
                ", facebookPictureUrl='" + facebookPictureUrl + '\'' +
                ", facebookId='" + facebookId + '\'' +
                ", token='" + token + '\'' +
                ", ticketsCount=" + ticketsCount +
                ", updatedAt='" + updatedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", phoneVerified=" + phoneVerified +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", type='" + type + '\'' +
                ", avatar='" + avatar + '\'' +
                ", name='" + name + '\'' +
                ", id=" + id +
                ", ward=" + ward +
                ", department=" + department +
                ", smsNotifiable=" + smsNotifiable +
                '}';
    }
}
