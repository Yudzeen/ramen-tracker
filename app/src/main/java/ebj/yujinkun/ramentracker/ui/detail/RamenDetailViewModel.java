package ebj.yujinkun.ramentracker.ui.detail;

import android.graphics.Bitmap;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.UUID;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.models.Photo;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel;
import ebj.yujinkun.ramentracker.util.DateUtils;
import ebj.yujinkun.ramentracker.util.Resource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class RamenDetailViewModel extends BaseViewModel {

    private final RamenRepository ramenRepository;

    private Ramen initialRamen;
    private String id;
    private String ramenName;
    private String shop;
    private String location;
    private String date;
    private String comments;
    private boolean favorite;

    private String initialPhotoLocation;
    private final MutableLiveData<String> photoLocationLiveData = new MutableLiveData<>();
    private Bitmap bitmap;

    private final MutableLiveData<Resource<Ramen>> saveRamenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> deleteRamenLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> unsavedChangesLiveData = new MutableLiveData<>();

    public RamenDetailViewModel(RamenRepository ramenRepository) {
        this.ramenRepository = ramenRepository;
    }

    public void initValues(Ramen ramen) {
        if (ramen != null) {
            this.initialRamen = ramen;
            id = ramen.getId();
            ramenName = ramen.getName();
            shop = ramen.getShop();
            location = ramen.getLocation();
            date = ramen.getDate();
            comments = ramen.getComments();
            favorite = ramen.isFavorite();
            loadPhotosForRamen(ramen);
        } else {
            id = UUID.randomUUID().toString();
            ramenName = "";
            shop = "";
            location = "";
            date = DateUtils.getCurrentDate();
            comments = "";
            favorite = false;
            initialPhotoLocation = "";
        }
        unsavedChangesLiveData.setValue(false);
    }

    private void loadPhotosForRamen(Ramen ramen) {
        bind(ramenRepository.getPhotosForRamen(ramen.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(photos -> {
                   if (photos.size() > 0) {
                       photoLocationLiveData.setValue(photos.get(0).getLocation());
                   } else {
                       Timber.i("No photos for ramen: %s", ramen);
                   }
                }));
    }

    public Ramen getInitialRamen() {
        return initialRamen;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        onDataChanged();
    }

    public String getRamenName() {
        return ramenName;
    }

    public void setRamenName(String ramenName) {
        this.ramenName = ramenName;
        onDataChanged();
    }

    public String getShop() {
        return shop;
    }

    public void setShop(String shop) {
        this.shop = shop;
        onDataChanged();
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
        onDataChanged();
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        onDataChanged();
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
        onDataChanged();
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
        onDataChanged();
    }

    public LiveData<String> getPhotoLocationLiveData() {
        return photoLocationLiveData;
    }

    public void saveRamen() {
        Ramen ramen = new Ramen.Builder()
                .setId(id)
                .setName(ramenName)
                .setShop(shop)
                .setLocation(location)
                .setDate(date)
                .setComments(comments)
                .setFavorite(favorite)
                .build();

        if (bitmap != null) {
            String id = UUID.randomUUID().toString();
            bind(ramenRepository.save(ramen)
                    .andThen(ramenRepository.saveBitmap(id, bitmap))
                    .map(location -> new Photo(id, ramen.getId(), location))
                    .flatMap(photo -> ramenRepository.save(photo).toSingleDefault(photo))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(photo -> {
                        updateInitialValues(ramen, photo);
                        saveRamenLiveData.setValue(Resource.success(ramen));
                    }, throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
        } else {
            bind(ramenRepository.save(ramen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> saveRamenLiveData.setValue(Resource.loading()))
                    .subscribe(() -> {
                                updateInitialValues(ramen, null);
                                saveRamenLiveData.setValue(Resource.success(ramen));
                            },
                            throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));

        }
    }

    private void updateInitialValues(Ramen ramen, Photo photo) {
        if (ramen != null) {
            this.initialRamen = ramen;
            id = ramen.getId();
            ramenName = ramen.getName();
            shop = ramen.getShop();
            location = ramen.getLocation();
            date = ramen.getDate();
            comments = ramen.getComments();
            favorite = ramen.isFavorite();
        }

        if (photo != null) {
            initialPhotoLocation = photo.getLocation();
            photoLocationLiveData.setValue(initialPhotoLocation);
        }
        unsavedChangesLiveData.setValue(false);
    }

    public LiveData<Resource<Ramen>> getSaveRamenLiveData() {
        return saveRamenLiveData;
    }

    public void deleteRamen() {
        bind(ramenRepository.delete(initialRamen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> deleteRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> deleteRamenLiveData.setValue(Resource.success(initialRamen)),
                        throwable -> deleteRamenLiveData.setValue(Resource.error(throwable))));
    }

    public LiveData<Resource<Ramen>> getDeleteRamenLiveData() {
        return deleteRamenLiveData;
    }

    private void onDataChanged() {
        boolean contentsUpdated;
        if (initialRamen == null) {
            contentsUpdated = !TextUtils.isEmpty(ramenName) || !TextUtils.isEmpty(shop) ||
                    !TextUtils.isEmpty(location) || !TextUtils.isEmpty(comments) ||
                    bitmap != null;
        } else {
            contentsUpdated = !Objects.equals(initialRamen.getName(), ramenName) ||
                    !Objects.equals(initialRamen.getShop(), shop) ||
                    !Objects.equals(initialRamen.getLocation(), location) ||
                    !Objects.equals(DateUtils.formatDate(initialRamen.getDate(), DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY),
                            DateUtils.formatDate(date, DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY)) ||
                    !Objects.equals(initialRamen.getComments(), comments) ||
                    !Objects.equals(initialRamen.isFavorite(), favorite) ||
                    !Objects.equals(initialPhotoLocation, photoLocationLiveData.getValue());
        }
        unsavedChangesLiveData.setValue(contentsUpdated);
    }

    public MutableLiveData<Boolean> getUnsavedChangesLiveData() {
        return unsavedChangesLiveData;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final RamenRepository ramenRepository;

        public Factory(RamenRepository ramenRepository) {
            this.ramenRepository = ramenRepository;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new RamenDetailViewModel(ramenRepository);
        }
    }

}
