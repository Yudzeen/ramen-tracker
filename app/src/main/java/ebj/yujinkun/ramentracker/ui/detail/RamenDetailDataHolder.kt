package ebj.yujinkun.ramentracker.ui.detail

import android.graphics.Bitmap
import android.text.TextUtils
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.util.DateUtils
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.subjects.BehaviorSubject
import java.util.*

class RamenDetailDataHolder {
    private val dataUpdatedSubject = BehaviorSubject.create<Boolean>()
    var initialRamen: Ramen? = null
        private set
    var id = UUID.randomUUID().toString()
        private set
    var name = ""
        private set
    var shop = ""
        private set
    var location = ""
        private set
    var date = DateUtils.currentDate
        private set
    var comments = ""
        private set
    var isFavorite = false
        private set
    var photoUri = ""
        private set
    private var bitmap: Bitmap? = null
    fun setInitialRamen(initialRamen: Ramen?): RamenDetailDataHolder {
        this.initialRamen = initialRamen
        onDataChanged()
        return this
    }

    fun setId(id: String): RamenDetailDataHolder {
        this.id = id
        onDataChanged()
        return this
    }

    fun setName(name: String): RamenDetailDataHolder {
        this.name = name
        onDataChanged()
        return this
    }

    fun setShop(shop: String): RamenDetailDataHolder {
        this.shop = shop
        onDataChanged()
        return this
    }

    fun setLocation(location: String): RamenDetailDataHolder {
        this.location = location
        onDataChanged()
        return this
    }

    fun setDate(date: String): RamenDetailDataHolder {
        this.date = date
        onDataChanged()
        return this
    }

    fun setComments(comments: String): RamenDetailDataHolder {
        this.comments = comments
        onDataChanged()
        return this
    }

    fun setFavorite(favorite: Boolean): RamenDetailDataHolder {
        isFavorite = favorite
        onDataChanged()
        return this
    }

    fun setPhotoUri(photoUri: String): RamenDetailDataHolder {
        this.photoUri = photoUri
        onDataChanged()
        return this
    }

    fun getBitmap(): Bitmap? {
        return bitmap
    }

    fun setBitmap(bitmap: Bitmap?) {
        this.bitmap = bitmap
        onDataChanged()
    }

    fun build(): Ramen {
        return Ramen(
            id = id,
            name = name,
            shop = shop,
            location = location,
            date = date,
            comments = comments,
            favorite = isFavorite,
            photoUri = photoUri
        )
    }

    val dataUpdatedObservable: Flowable<Boolean>
        get() = dataUpdatedSubject.toFlowable(BackpressureStrategy.LATEST)

    private fun onDataChanged() {
        val dataUpdated: Boolean = if (initialRamen == null) {
            !TextUtils.isEmpty(name) || !TextUtils.isEmpty(shop) ||
                    !TextUtils.isEmpty(location) || !TextUtils.isEmpty(comments) || bitmap != null
        } else {
            initialRamen!!.name != name ||
                    initialRamen!!.shop != shop ||
                    initialRamen!!.location != location ||
                    DateUtils.formatDate(
                        initialRamen!!.date,
                        DateUtils.DATE_FORMAT_DEFAULT,
                        DateUtils.DATE_FORMAT_DATE_ONLY
                    ) != DateUtils.formatDate(
                date,
                DateUtils.DATE_FORMAT_DEFAULT,
                DateUtils.DATE_FORMAT_DATE_ONLY
            ) ||
                    initialRamen!!.comments != comments ||
                    initialRamen!!.favorite != isFavorite ||
                    initialRamen!!.photoUri != photoUri ||
                    TextUtils.isEmpty(photoUri) && bitmap != null
        }
        dataUpdatedSubject.onNext(dataUpdated)
    }
}