package ebj.yujinkun.ramentracker.data.models;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Objects;

@Entity(tableName = "ramen_photo_join", primaryKeys = {"ramen_id", "photo_id"},
        foreignKeys = {
                @ForeignKey(entity = Ramen.class, parentColumns = "id", childColumns = "ramen_id"),
                @ForeignKey(entity = Photo.class, parentColumns = "id", childColumns = "photo_id")},
        indices = {@Index("ramen_id"), @Index("photo_id")})
public class RamenPhotoJoin {

    @NonNull
    @ColumnInfo(name = "ramen_id")
    private final String ramenId;

    @NonNull
    @ColumnInfo(name = "photo_id")
    private final String photoId;

    public RamenPhotoJoin(@NonNull String ramenId, @NonNull String photoId) {
        this.ramenId = ramenId;
        this.photoId = photoId;
    }

    public static RamenPhotoJoin from(Ramen ramen, Photo photo) {
        return new RamenPhotoJoin(ramen.getId(), photo.getId());
    }

    @NonNull
    public String getRamenId() {
        return ramenId;
    }

    @NonNull
    public String getPhotoId() {
        return photoId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RamenPhotoJoin that = (RamenPhotoJoin) o;
        return ramenId.equals(that.ramenId) &&
                photoId.equals(that.photoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ramenId, photoId);
    }

    @NonNull
    @Override
    public String toString() {
        return "RamenPhotoJoin{" +
                "ramenId='" + ramenId + '\'' +
                ", photoId='" + photoId + '\'' +
                '}';
    }

}
