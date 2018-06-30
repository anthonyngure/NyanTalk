package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;

public class SubCounty implements Parcelable {

    private long id;
    private String name;
    public List<Ward> wards;
    public SubCounty() {
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.wards);
    }

    protected SubCounty(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.wards = in.createTypedArrayList(Ward.CREATOR);
    }

    public static final Parcelable.Creator<SubCounty> CREATOR = new Parcelable.Creator<SubCounty>() {
        @Override
        public SubCounty createFromParcel(Parcel source) {
            return new SubCounty(source);
        }

        @Override
        public SubCounty[] newArray(int size) {
            return new SubCounty[size];
        }
    };
}
