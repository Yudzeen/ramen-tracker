package ebj.yujinkun.ramentracker.data.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "ramen", indices = {@Index("id")})
public class Ramen implements Parcelable {

    @NonNull
    @PrimaryKey
    private final String id;
    private final String name;
    private final String shop;
    private final String location;
    private final String date;
    private final String comments;
    private final boolean favorite;
    private final String photoUri;

    public Ramen(@NonNull String id, String name, String shop, String location, String date, String comments, boolean favorite, String photoUri) {
        this.id = id;
        this.name = name;
        this.shop = shop;
        this.location = location;
        this.date = date;
        this.comments = comments;
        this.favorite = favorite;
        this.photoUri = photoUri;
    }

    protected Ramen(Parcel in) {
        id = in.readString();
        name = in.readString();
        shop = in.readString();
        location = in.readString();
        date = in.readString();
        comments = in.readString();
        favorite = in.readInt() == 1;
        photoUri = in.readString();
    }

    public static final Creator<Ramen> CREATOR = new Creator<Ramen>() {
        @Override
        public Ramen createFromParcel(Parcel in) {
            return new Ramen(in);
        }

        @Override
        public Ramen[] newArray(int size) {
            return new Ramen[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShop() {
        return shop;
    }

    public String getLocation() {
        return location;
    }

    public String getDate() {
        return date;
    }

    public String getComments() {
        return comments;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ramen ramen = (Ramen) o;
        return favorite == ramen.favorite &&
                id.equals(ramen.id) &&
                Objects.equals(name, ramen.name) &&
                Objects.equals(shop, ramen.shop) &&
                Objects.equals(location, ramen.location) &&
                Objects.equals(date, ramen.date) &&
                Objects.equals(comments, ramen.comments) &&
                Objects.equals(photoUri, ramen.photoUri);

    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, shop, location, date, comments, favorite, photoUri);
    }

    @NonNull
    @Override
    public String toString() {
        return "Ramen{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", shop='" + shop + '\'' +
                ", location='" + location + '\'' +
                ", date='" + date + '\'' +
                ", comments='" + comments + '\'' +
                ", favorite=" + favorite +
                ", photoUri='" + photoUri + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(shop);
        dest.writeString(location);
        dest.writeString(date);
        dest.writeInt(favorite ? 1 : 0);
        dest.writeString(photoUri);
    }

    public static class Builder {
        private String id;
        private String name;
        private String shop;
        private String location;
        private String date;
        private String comments;
        private boolean favorite;
        private String photoUri;

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setShop(String shop) {
            this.shop = shop;
            return this;
        }

        public Builder setLocation(String location) {
            this.location = location;
            return this;
        }

        public Builder setDate(String date) {
            this.date = date;
            return this;
        }

        public Builder setComments(String comments) {
            this.comments = comments;
            return this;
        }

        public Builder setFavorite(boolean favorite) {
            this.favorite = favorite;
            return this;
        }

        public Builder setPhotoUri(String photoUri) {
            this.photoUri = photoUri;
            return this;
        }

        public Ramen build() {
            return new Ramen(id, name, shop, location, date, comments, favorite, photoUri);
        }
    }
}
