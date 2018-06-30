package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Forum implements Parcelable {

    private String id;
    private String name;
    private int topicsCount;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTopicsCount() {
        return topicsCount;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.topicsCount);
    }

    public Forum() {
    }

    protected Forum(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.topicsCount = in.readInt();
    }

    public static final Parcelable.Creator<Forum> CREATOR = new Parcelable.Creator<Forum>() {
        @Override
        public Forum createFromParcel(Parcel source) {
            return new Forum(source);
        }

        @Override
        public Forum[] newArray(int size) {
            return new Forum[size];
        }
    };
}
