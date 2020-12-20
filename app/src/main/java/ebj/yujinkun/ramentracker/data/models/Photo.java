package ebj.yujinkun.ramentracker.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;

@Entity(tableName = "photos")
public class Photo {

    @NonNull
    @PrimaryKey
    private final String id;
    private final String location;

    public Photo(@NonNull String id, String location) {
        this.id = id;
        this.location = location;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id.equals(photo.id) &&
                Objects.equals(location, photo.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, location);
    }

    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", location='" + location + '\'' +
                '}';
    }

}
