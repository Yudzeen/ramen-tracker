package ebj.yujinkun.ramentracker.ui.detail;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.LiveDataReactiveStreams;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.UUID;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.files.FileStorage;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel;
import ebj.yujinkun.ramentracker.util.DateUtils;
import ebj.yujinkun.ramentracker.util.Resource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RamenDetailViewModel extends BaseViewModel {

    private final RamenRepository ramenRepository;
    private final FileStorage fileStorage;

    private final RamenDetailDataHolder ramenDetailDataHolder = new RamenDetailDataHolder();

    private final MutableLiveData<Resource<Ramen>> saveRamenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> deleteRamenLiveData = new MutableLiveData<>();

    private final LiveData<Boolean> dataUpdatedLiveData =
            LiveDataReactiveStreams.fromPublisher(ramenDetailDataHolder.getDataUpdatedObservable());

    public RamenDetailViewModel(RamenRepository ramenRepository, FileStorage fileStorage) {
        this.ramenRepository = ramenRepository;
        this.fileStorage = fileStorage;
    }

    public void initValues(Ramen ramen) {
        if (ramen != null) {
            ramenDetailDataHolder
                    .setInitialRamen(ramen)
                    .setId(ramen.getId())
                    .setName(ramen.getName())
                    .setShop(ramen.getShop())
                    .setLocation(ramen.getLocation())
                    .setDate(ramen.getDate())
                    .setComments(ramen.getComments())
                    .setFavorite(ramen.isFavorite())
                    .setPhotoUri(ramen.getPhotoUri());
        } else {
            ramenDetailDataHolder
                    .setInitialRamen(null)
                    .setId(UUID.randomUUID().toString())
                    .setName("")
                    .setShop("")
                    .setLocation("")
                    .setDate(DateUtils.getCurrentDate())
                    .setComments("")
                    .setFavorite(false)
                    .setPhotoUri("");
        }
    }

    public void saveRamen() {
        Ramen.Builder ramenBuilder = ramenDetailDataHolder.toRamenBuilder();
        Bitmap bitmap = ramenDetailDataHolder.getBitmap();
        if (bitmap != null) {
            saveRamenLiveData.setValue(Resource.loading());
            bind(fileStorage.saveBitmap(ramenDetailDataHolder.getId(), bitmap)
                    .map(ramenBuilder::setPhotoUri)
                    .flatMap(builder -> {
                        Ramen ramen = ramenBuilder.build();
                        return ramenRepository.save(ramen).toSingleDefault(ramen);
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onSaveSuccess,
                            throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
        } else {
            Ramen ramen = ramenBuilder.build();
            bind(ramenRepository.save(ramen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe(disposable -> saveRamenLiveData.setValue(Resource.loading()))
                    .subscribe(() -> onSaveSuccess(ramen),
                            throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
        }
    }

    private void onSaveSuccess(Ramen ramen) {
        saveRamenLiveData.setValue(Resource.success(ramen));
    }

    public LiveData<Resource<Ramen>> getSaveRamenLiveData() {
        return saveRamenLiveData;
    }

    public void deleteRamen() {
        bind(ramenRepository.delete(ramenDetailDataHolder.getInitialRamen())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> deleteRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> deleteRamenLiveData.setValue(Resource.success(ramenDetailDataHolder.getInitialRamen())),
                        throwable -> deleteRamenLiveData.setValue(Resource.error(throwable))));
    }

    public LiveData<Resource<Ramen>> getDeleteRamenLiveData() {
        return deleteRamenLiveData;
    }

    public LiveData<Boolean> getDataUpdatedLiveData() {
        return dataUpdatedLiveData;
    }

    public boolean isFavorite() {
        return ramenDetailDataHolder.isFavorite();
    }

    public void setFavorite(boolean favorite) {
        ramenDetailDataHolder.setFavorite(favorite);
    }

    public boolean isDeletable() {
        return ramenDetailDataHolder.getInitialRamen() != null;
    }

    public String getShop() {
        return ramenDetailDataHolder.getShop();
    }

    public void setShop(String shop) {
        ramenDetailDataHolder.setShop(shop);
    }

    public String getLocation() {
        return ramenDetailDataHolder.getLocation();
    }

    public void setLocation(String location) {
        ramenDetailDataHolder.setLocation(location);
    }

    public String getRamenName() {
        return ramenDetailDataHolder.getName();
    }

    public void setRamenName(String ramenName) {
        ramenDetailDataHolder.setName(ramenName);
    }

    public String getComments() {
        return ramenDetailDataHolder.getComments();
    }

    public void setComments(String comments) {
        ramenDetailDataHolder.setComments(comments);
    }

    public String getDate() {
        return ramenDetailDataHolder.getDate();
    }

    public void setDate(String date) {
        ramenDetailDataHolder.setDate(date);
    }

    public String getPhotoUri() { return ramenDetailDataHolder.getPhotoUri(); }

    public void updateSelectedPhoto(Bitmap bitmap) {
        ramenDetailDataHolder.setBitmap(bitmap);
        ramenDetailDataHolder.setPhotoUri("");  // set to empty to mark as data changed
    }

    public static class Factory implements ViewModelProvider.Factory {

        private final RamenRepository ramenRepository;
        private final FileStorage fileStorage;

        public Factory(RamenRepository ramenRepository, FileStorage fileStorage) {
            this.ramenRepository = ramenRepository;
            this.fileStorage = fileStorage;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            //noinspection unchecked
            return (T) new RamenDetailViewModel(ramenRepository, fileStorage);
        }
    }

}
