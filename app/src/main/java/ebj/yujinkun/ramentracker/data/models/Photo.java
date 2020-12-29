package ebj.yujinkun.ramentracker.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "photos")
public class Photo {

    @NonNull
    @PrimaryKey
    @ColumnInfo(name = "photo_id")
    private final String photoId;

    @ColumnInfo(name = "ramen_id")
    private final String ramenId;

    private final String location;

    public Photo(@NonNull String photoId, String ramenId, String location) {
        this.photoId = photoId;
        this.ramenId = ramenId;
        this.location = location;
    }

    @NonNull
    public String getPhotoId() {
        return photoId;
    }

    public String getRamenId() {
        return ramenId;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return photoId.equals(photo.photoId) &&
                Objects.equals(ramenId, photo.ramenId) &&
                Objects.equals(location, photo.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(photoId, ramenId, location);
    }

    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
                "photoId='" + photoId + '\'' +
                ", ramenId='" + ramenId + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
