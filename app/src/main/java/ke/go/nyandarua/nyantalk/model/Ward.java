package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.ToOne;

public class Ward implements Parcelable {

    @Id
    private long id;
    private String name;
    private long subCountyId;
    public SubCounty subCounty;

    public Ward() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getSubCountyId() {
        return this.subCountyId;
    }

    public void setSubCountyId(long subCountyId) {
        this.subCountyId = subCountyId;
    }

    @Override
    public String toString() {
        return "Ward{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", subCountyId=" + subCountyId +
                '}';
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeLong(this.subCountyId);
        dest.writeParcelable(this.subCounty, flags);
    }

    protected Ward(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.subCountyId = in.readLong();
        this.subCounty = in.readParcelable(SubCounty.class.getClassLoader());
    }

    public static final Parcelable.Creator<Ward> CREATOR = new Parcelable.Creator<Ward>() {
        @Override
        public Ward createFromParcel(Parcel source) {
            return new Ward(source);
        }

        @Override
        public Ward[] newArray(int size) {
            return new Ward[size];
        }
    };

    public SubCounty getSubCounty() {
        return subCounty;
    }
}
