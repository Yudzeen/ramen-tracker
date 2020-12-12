package ebj.yujinkun.ramentracker.ui.list;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.List;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel;
import ebj.yujinkun.ramentracker.util.Resource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class RamenListViewModel extends BaseViewModel {

    private final RamenRepository ramenRepository;

    private final MutableLiveData<Resource<List<Ramen>>> ramenListLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> saveRamenLiveData = new MutableLiveData<>();
    private final MutableLiveData<Resource<Ramen>> deleteRamenLiveData = new MutableLiveData<>();

    public RamenListViewModel(RamenRepository ramenRepository) {
        this.ramenRepository = ramenRepository;
    }

    public MutableLiveData<Resource<List<Ramen>>> getRamenListLiveData() {
        return ramenListLiveData;
    }

    public MutableLiveData<Resource<Ramen>> getSaveRamenLiveData() {
        return saveRamenLiveData;
    }

    public MutableLiveData<Resource<Ramen>> getDeleteRamenLiveData() {
        return deleteRamenLiveData;
    }

    public void loadAllRamen() {
        bind(ramenRepository.getAllRamen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(Resource::success)
                .onErrorReturn(Resource::error)
                .startWith(Resource.loading())
                .subscribe(ramenListLiveData::setValue));
    }

    public void save(Ramen ramen) {
        bind(ramenRepository.save(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> saveRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> saveRamenLiveData.setValue(Resource.success(ramen)),
                        throwable -> saveRamenLiveData.setValue(Resource.error(throwable))));
    }

    public void delete(Ramen ramen) {
        bind(ramenRepository.delete(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> deleteRamenLiveData.setValue(Resource.loading()))
                .subscribe(() -> deleteRamenLiveData.setValue(Resource.success(ramen)),
                        throwable -> deleteRamenLiveData.setValue(Resource.error(throwable))));
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
            return (T) new RamenListViewModel(ramenRepository);
        }
    }

}
