package ke.go.nyandarua.nyantalk.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Anthony Ngure on 30/12/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class FacebookUser {


    @SerializedName("picture")
    private Picture picture;
    @SerializedName("id")
    private String id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("email")
    private String email;
    @SerializedName("name")
    private String name;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("gender")
    private String gender;
    @SerializedName("age_range")
    private AgeRange ageRange;

    public Picture getPicture() {
        return picture;
    }

    public String getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getLastName() {
        return lastName;
    }

    public String getGender() {
        return gender;
    }

    public AgeRange getAgeRange() {
        return ageRange;
    }

    public static class Data {
        @SerializedName("url")
        private String url;
        @SerializedName("is_silhouette")
        private boolean isSilhouette;
        @SerializedName("width")
        private int width;
        @SerializedName("height")
        private int height;

        public String getUrl() {
            return url;
        }

        public boolean getIsSilhouette() {
            return isSilhouette;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }

    public static class Picture {
        @SerializedName("data")
        private Data data;

        public Data getData() {
            return data;
        }
    }

    public static class AgeRange {
        @SerializedName("min")
        private int min;

        public int getMin() {
            return min;
        }
    }
}
