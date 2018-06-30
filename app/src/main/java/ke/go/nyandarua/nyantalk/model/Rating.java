package ke.go.nyandarua.nyantalk.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Rating implements Parcelable {

    private String id;
    private String text;
    private int stars;

    public Rating() {
    }

    public String getId() {
        return id;
    }

    public String getText() {
        return text;
    }

    public int getStars() {
        return stars;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.text);
        dest.writeInt(this.stars);
    }

    protected Rating(Parcel in) {
        this.id = in.readString();
        this.text = in.readString();
        this.stars = in.readInt();
    }

    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() {
        @Override
        public Rating createFromParcel(Parcel source) {
            return new Rating(source);
        }

        @Override
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };
}
