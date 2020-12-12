package ebj.yujinkun.ramentracker.data.models;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Objects;
import java.util.UUID;

@Entity(tableName = "photos")
public class Photo {

    @NonNull
    @PrimaryKey
    private final String id;
    private final String uri;

    public static Photo create(String uri) {
        return new Photo(UUID.randomUUID().toString(), uri);
    }

    public Photo(@NonNull String id, String uri) {
        this.id = id;
        this.uri = uri;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Photo photo = (Photo) o;
        return id.equals(photo.id) &&
                Objects.equals(uri, photo.uri);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uri);
    }

    @NonNull
    @Override
    public String toString() {
        return "Photo{" +
                "id='" + id + '\'' +
                ", link='" + uri + '\'' +
                '}';
    }

}
