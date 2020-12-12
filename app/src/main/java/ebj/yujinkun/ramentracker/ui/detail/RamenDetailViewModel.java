package ebj.yujinkun.ramentracker.ui.detail;

import android.net.Uri;
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
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

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

    private String initialPhotoUri;
    private final MutableLiveData<String> photoUriLiveData = new MutableLiveData<>();

    private final MutableLiveData<Resource<Ramen>> saveRamenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> deleteRamenLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> contentsUpdatedLiveData = new MutableLiveData<>();

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
            initialPhotoUri = "";
        }
        contentsUpdatedLiveData.setValue(false);
    }

    private void loadPhotosForRamen(Ramen ramen) {
        bind(ramenRepository.getPhotosForRamen(ramen.getId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(photos -> photos.get(0).getUri())
                .subscribe(photoUriLiveData::setValue));
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

    public LiveData<String> getPhotoUriLiveData() {
        return photoUriLiveData;
    }

    public void setPhotoUri(Uri uri) {
        photoUriLiveData.setValue(uri.toString());
        onDataChanged();
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

        Completable saveOperation;
        Photo photo = photoUriLiveData.getValue() != null ?
                Photo.create(photoUriLiveData.getValue()) : null;
        if (photo != null) {
            saveOperation = ramenRepository.save(ramen)
                    .andThen(ramenRepository.save(photo))
                    .andThen(ramenRepository.addPhotoToRamen(photo, ramen));
        } else {
            saveOperation = ramenRepository.save(ramen);
        }
        bind(saveOperation
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> saveRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> {
                        updateInitialValues(ramen, photo);
                        saveRamenLiveData.setValue(Resource.success(ramen));
                    },
                        throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
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
            initialPhotoUri = photo.getUri();
        }
        contentsUpdatedLiveData.setValue(false);
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
                    !TextUtils.isEmpty(photoUriLiveData.getValue());
        } else {
            contentsUpdated = !Objects.equals(initialRamen.getName(), ramenName) ||
                    !Objects.equals(initialRamen.getShop(), shop) ||
                    !Objects.equals(initialRamen.getLocation(), location) ||
                    !Objects.equals(DateUtils.formatDate(initialRamen.getDate(), DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY),
                            DateUtils.formatDate(date, DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY)) ||
                    !Objects.equals(initialRamen.getComments(), comments) ||
                    !Objects.equals(initialRamen.isFavorite(), favorite) ||
                    !Objects.equals(initialPhotoUri, photoUriLiveData.getValue());
        }
        contentsUpdatedLiveData.setValue(contentsUpdated);
    }

    public MutableLiveData<Boolean> getContentsUpdatedLiveData() {
        return contentsUpdatedLiveData;
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
