package ebj.yujinkun.ramentracker.ui.detail;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.Objects;
import java.util.UUID;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.models.Ramen;
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel;
import ebj.yujinkun.ramentracker.util.DateUtils;
import ebj.yujinkun.ramentracker.util.Resource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RamenDetailViewModel extends BaseViewModel {

    private final RamenRepository ramenRepository;
    private Ramen ramen;
    private String id;
    private String ramenName;
    private String shop;
    private String location;
    private String date;
    private String comments;
    private boolean favorite;

    private final MutableLiveData<Resource<Ramen>> saveRamenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> deleteRamenLiveData = new MutableLiveData<>();

    private final MutableLiveData<Boolean> contentsUpdatedLiveData = new MutableLiveData<>();

    public RamenDetailViewModel(RamenRepository ramenRepository) {
        this.ramenRepository = ramenRepository;
    }

    public void initValues(Ramen ramen) {
        if (ramen != null) {
            this.ramen = ramen;
            id = ramen.getId();
            ramenName = ramen.getName();
            shop = ramen.getShop();
            location = ramen.getLocation();
            date = ramen.getDate();
            comments = ramen.getComments();
            favorite = ramen.isFavorite();
        } else {
            id = UUID.randomUUID().toString();
            ramenName = "";
            shop = "";
            location = "";
            date = DateUtils.getCurrentDate();
            comments = "";
            favorite = false;
        }
        contentsUpdatedLiveData.setValue(false);
    }

    public Ramen getRamen() {
        return ramen;
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

    public void save() {
        Ramen ramen = new Ramen.Builder()
                .setId(id)
                .setName(ramenName)
                .setShop(shop)
                .setLocation(location)
                .setDate(date)
                .setComments(comments)
                .setFavorite(favorite)
                .build();
        bind(ramenRepository.save(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> saveRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> saveRamenLiveData.setValue(Resource.success(ramen)),
                        throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
    }

    public LiveData<Resource<Ramen>> getSaveRamenLiveData() {
        return saveRamenLiveData;
    }

    public void delete() {
        bind(ramenRepository.delete(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> deleteRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> deleteRamenLiveData.setValue(Resource.success(ramen)),
                        throwable -> deleteRamenLiveData.setValue(Resource.error(throwable))));
    }

    public LiveData<Resource<Ramen>> getDeleteRamenLiveData() {
        return deleteRamenLiveData;
    }

    private void onDataChanged() {
        if (ramen == null) {
            boolean contentsUpdated = !TextUtils.isEmpty(ramenName) || !TextUtils.isEmpty(shop) ||
                    !TextUtils.isEmpty(location) || !TextUtils.isEmpty(comments);
            contentsUpdatedLiveData.setValue(contentsUpdated);
        } else {
            boolean contentsUpdated = !Objects.equals(ramen.getName(), ramenName) ||
                    !Objects.equals(ramen.getShop(), shop) ||
                    !Objects.equals(ramen.getLocation(), location) ||
                    !Objects.equals(DateUtils.formatDate(ramen.getDate(), DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY),
                            DateUtils.formatDate(date, DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_ONLY)) ||
                    !Objects.equals(ramen.getComments(), comments) ||
                    !Objects.equals(ramen.isFavorite(), favorite);
            contentsUpdatedLiveData.setValue(contentsUpdated);
        }
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
