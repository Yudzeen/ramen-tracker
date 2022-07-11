package ebj.yujinkun.ramentracker.ui.list

import ebj.yujinkun.ramentracker.data.RamenRepository
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel
import androidx.lifecycle.MutableLiveData
import ebj.yujinkun.ramentracker.data.room.RamenEntity
import androidx.lifecycle.LiveData
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.util.Resource
import javax.inject.Inject

@HiltViewModel
class RamenListViewModel
    @Inject internal constructor(private val ramenRepository: RamenRepository) : BaseViewModel() {
    private val ramenListLiveData = MutableLiveData<Resource<List<Ramen>>>()
    private val saveRamenLiveData = MutableLiveData<Resource<Ramen>>()
    private val deleteRamenLiveData = MutableLiveData<Resource<Ramen>>()
    fun getRamenListLiveData(): LiveData<Resource<List<Ramen>>> {
        return ramenListLiveData
    }

    fun getSaveRamenLiveData(): LiveData<Resource<Ramen>> {
        return saveRamenLiveData
    }

    fun getDeleteRamenLiveData(): LiveData<Resource<Ramen>> {
        return deleteRamenLiveData
    }

    fun loadAllRamen() {
        bind(
            ramenRepository.getAllRamen()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext{ ramenListLiveData.value = Resource.Success(it) }
                .doOnError { ramenListLiveData.value = Resource.Error(it) }
                .doOnSubscribe { ramenListLiveData.value = Resource.Loading() }
                .subscribe())
    }

    fun save(ramen: Ramen) {
        bind(
            ramenRepository.saveRamen(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { saveRamenLiveData.value = Resource.Loading() }
                .subscribe(
                    {
                        saveRamenLiveData.setValue(Resource.Success(ramen))
                    }
                ) { throwable: Throwable ->
                    saveRamenLiveData.setValue(Resource.Error(throwable))
                })
    }

    fun delete(ramen: Ramen) {
        bind(
            ramenRepository.deleteRamen(ramen)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { deleteRamenLiveData.setValue(Resource.Loading()) }
                .subscribe(
                    {
                        deleteRamenLiveData.setValue(Resource.Success(ramen))
                    }
                ) { throwable: Throwable ->
                    deleteRamenLiveData.setValue(Resource.Error(throwable))
                })
    }
}