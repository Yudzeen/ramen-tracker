package ebj.yujinkun.ramentracker.ui.detail;

import android.graphics.Bitmap;
import android.text.TextUtils;

import java.util.Objects;
import java.util.UUID;

import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.util.DateUtils;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.subjects.BehaviorSubject;

public class RamenDetailDataHolder {

    private final BehaviorSubject<Boolean> dataUpdatedSubject = BehaviorSubject.create();

    private Ramen initialRamen = null;

    private String id = UUID.randomUUID().toString();
    private String name = "";
    private String shop = "";
    private String location = "";
    private String date = DateUtils.getCurrentDate();
    private String comments = "";
    private boolean favorite = false;
    private String photoUri = "";

    private Bitmap bitmap;

    public Ramen getInitialRamen() {
        return initialRamen;
    }

    public RamenDetailDataHolder setInitialRamen(Ramen initialRamen) {
        this.initialRamen = initialRamen;
        onDataChanged();
        return this;
    }

    public String getId() {
        return id;
    }

    public RamenDetailDataHolder setId(String id) {
        this.id = id;
        onDataChanged();
        return this;
    }

    public String getName() {
        return name;
    }

    public RamenDetailDataHolder setName(String name) {
        this.name = name;
        onDataChanged();
        return this;
    }

    public String getShop() {
        return shop;
    }

    public RamenDetailDataHolder setShop(String shop) {
        this.shop = shop;
        onDataChanged();
        return this;
    }

    public String getLocation() {
        return location;
    }

    public RamenDetailDataHolder setLocation(String location) {
        this.location = location;
        onDataChanged();
        return this;
    }

    public String getDate() {
        return date;
    }

    public RamenDetailDataHolder setDate(String date) {
        this.date = date;
        onDataChanged();
        return this;
    }

    public String getComments() {
        return comments;
    }

    public RamenDetailDataHolder setComments(String comments) {
        this.comments = comments;
        onDataChanged();
        return this;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public RamenDetailDataHolder setFavorite(boolean favorite) {
        this.favorite = favorite;
        onDataChanged();
        return this;
    }

    public String getPhotoUri() {
        return photoUri;
    }

    public RamenDetailDataHolder setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
        onDataChanged();
        return this;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        onDataChanged();
    }

    public Ramen.Builder toRamenBuilder() {
        return new Ramen.Builder()
                .setId(id)
                .setName(name)
                .setShop(shop)
                .setLocation(location)
                .setDate(date)
                .setComments(comments)
                .setFavorite(favorite)
                .setPhotoUri(photoUri);
    }

    public Flowable<Boolean> getDataUpdatedObservable() {
        return dataUpdatedSubject.toFlowable(BackpressureStrategy.LATEST);
    }

    private void onDataChanged() {
        boolean dataUpdated;
        if (initialRamen == null) {
            dataUpdated = !TextUtils.isEmpty(name) || !TextUtils.isEmpty(shop) ||
                    !TextUtils.isEmpty(location) || !TextUtils.isEmpty(comments) ||
                    bitmap != null;
        } else {
            dataUpdated = !Objects.equals(initialRamen.getName(), name) ||
                    !Objects.equals(initialRamen.getShop(), shop) ||
                    !Objects.equals(initialRamen.getLocation(), location) ||
                    !Objects.equals(DateUtils.formatDate(initialRamen.getDate(), DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY),
                            DateUtils.formatDate(date, DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY)) ||
                    !Objects.equals(initialRamen.getComments(), comments) ||
                    !Objects.equals(initialRamen.isFavorite(), favorite) ||
                    !Objects.equals(initialRamen.getPhotoUri(), photoUri) ||
                    (TextUtils.isEmpty(photoUri) && bitmap != null);
        }
        dataUpdatedSubject.onNext(dataUpdated);
    }

}
