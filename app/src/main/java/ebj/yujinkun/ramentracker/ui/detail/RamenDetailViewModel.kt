package ebj.yujinkun.ramentracker.ui.detail

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import ebj.yujinkun.ramentracker.data.RamenRepository
import ebj.yujinkun.ramentracker.data.files.FileStorage
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.ui.common.BaseViewModel
import ebj.yujinkun.ramentracker.util.DateUtils
import ebj.yujinkun.ramentracker.util.Resource
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*
import javax.inject.Inject

@HiltViewModel
class RamenDetailViewModel
    @Inject internal constructor(
    private val ramenRepository: RamenRepository,
    private val fileStorage: FileStorage
) : BaseViewModel() {
    private val ramenDetailDataHolder = RamenDetailDataHolder()
    private val saveRamenLiveData = MutableLiveData<Resource<Ramen>>()
    private val deleteRamenLiveData = MutableLiveData<Resource<Ramen>>()
    val dataUpdatedLiveData =
        LiveDataReactiveStreams.fromPublisher(ramenDetailDataHolder.dataUpdatedObservable)

    fun initValues(ramen: Ramen?) {
        if (ramen != null) {
            ramenDetailDataHolder
                .setInitialRamen(ramen)
                .setId(ramen.id)
                .setName(ramen.name ?: "")
                .setShop(ramen.shop ?: "")
                .setLocation(ramen.location ?: "")
                .setDate(ramen.date ?: "")
                .setComments(ramen.comments ?: "")
                .setFavorite(ramen.favorite)
                .setPhotoUri(ramen.photoUri ?: "")
        } else {
            ramenDetailDataHolder
                .setInitialRamen(null)
                .setId(UUID.randomUUID().toString())
                .setName("")
                .setShop("")
                .setLocation("")
                .setDate(DateUtils.currentDate)
                .setComments("")
                .setFavorite(false)
                .setPhotoUri("")
        }
    }

    fun saveRamen() {
        val bitmap = ramenDetailDataHolder.getBitmap()
        if (bitmap != null) {
            saveRamenLiveData.setValue(Resource.Loading())
            bind(
                fileStorage.saveBitmap(ramenDetailDataHolder.id, bitmap)
                    .map { photoUri -> ramenDetailDataHolder.setPhotoUri(photoUri)}
                    .flatMap {
                        val ramen = ramenDetailDataHolder.build()
                        ramenRepository.saveRamen(ramen).toSingleDefault(ramen)
                    }
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                        { ramen -> onSaveSuccess(ramen) }
                    ) { throwable: Throwable -> saveRamenLiveData.setValue(Resource.Error(throwable)) })
        } else {
            val ramen = ramenDetailDataHolder.build()
            bind(
                ramenRepository.saveRamen(ramen)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { saveRamenLiveData.setValue(Resource.Loading()) }
                    .subscribe(
                        { onSaveSuccess(ramen) }
                    ) { throwable: Throwable -> saveRamenLiveData.setValue(Resource.Error(throwable)) })
        }
    }

    private fun onSaveSuccess(ramen: Ramen) {
        saveRamenLiveData.value = Resource.Success(ramen)
    }

    fun getSaveRamenLiveData(): LiveData<Resource<Ramen>> {
        return saveRamenLiveData
    }

    fun deleteRamen() {
        bind(
            ramenRepository.deleteRamen(ramenDetailDataHolder.initialRamen!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { disposable: Disposable? -> deleteRamenLiveData.setValue(Resource.Loading()) }
                .subscribe(
                    { deleteRamenLiveData.setValue(Resource.Success(ramenDetailDataHolder.initialRamen!!)) }
                ) { throwable: Throwable -> deleteRamenLiveData.setValue(Resource.Error(throwable)) })
    }

    fun getDeleteRamenLiveData(): LiveData<Resource<Ramen>> {
        return deleteRamenLiveData
    }

    var isFavorite: Boolean
        get() = ramenDetailDataHolder.isFavorite
        set(favorite) {
            ramenDetailDataHolder.setFavorite(favorite)
        }
    val isDeletable: Boolean
        get() = ramenDetailDataHolder.initialRamen != null
    var shop: String?
        get() = ramenDetailDataHolder.shop
        set(shop) {
            ramenDetailDataHolder.setShop(shop!!)
        }
    var location: String?
        get() = ramenDetailDataHolder.location
        set(location) {
            ramenDetailDataHolder.setLocation(location!!)
        }
    var ramenName: String?
        get() = ramenDetailDataHolder.name
        set(ramenName) {
            ramenDetailDataHolder.setName(ramenName!!)
        }
    var comments: String?
        get() = ramenDetailDataHolder.comments
        set(comments) {
            ramenDetailDataHolder.setComments(comments!!)
        }
    var date: String?
        get() = ramenDetailDataHolder.date
        set(date) {
            ramenDetailDataHolder.setDate(date!!)
        }
    val photoUri: String
        get() = ramenDetailDataHolder.photoUri

    fun updateSelectedPhoto(bitmap: Bitmap?) {
        ramenDetailDataHolder.setBitmap(bitmap)
        ramenDetailDataHolder.setPhotoUri("") // set to empty to mark as data changed
    }
}