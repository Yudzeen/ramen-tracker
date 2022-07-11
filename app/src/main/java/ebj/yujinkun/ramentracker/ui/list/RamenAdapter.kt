package ebj.yujinkun.ramentracker.ui.list

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ebj.yujinkun.ramentracker.R
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.databinding.ListItemRamenBinding
import ebj.yujinkun.ramentracker.util.DateUtils
import timber.log.Timber
import java.util.*

class RamenAdapter : RecyclerView.Adapter<RamenAdapter.ViewHolder>() {
    private val ramenList: MutableList<Ramen> = ArrayList()
    private var onItemClickListener: OnItemClickListener? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_ramen, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ramen = ramenList[position]
        holder.setRamen(ramen)
        holder.itemView.setOnClickListener { v: View? ->
            if (onItemClickListener != null) {
                onItemClickListener!!.onItemClicked(ramen)
            } else {
                Timber.w("onItemClickListener is null")
            }
        }
    }

    override fun getItemCount(): Int {
        return ramenList.size
    }

    fun getItemAt(position: Int): Ramen {
        return ramenList[position]
    }

    fun submitList(newList: List<Ramen>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(ramenList, newList))
        diffResult.dispatchUpdatesTo(this)
        ramenList.clear()
        ramenList.addAll(newList)
    }

    interface OnItemClickListener {
        fun onItemClicked(ramen: Ramen)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener?) {
        this.onItemClickListener = onItemClickListener
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding: ListItemRamenBinding = ListItemRamenBinding.bind(itemView)

        fun setRamen(ramen: Ramen) {
            binding.ramenName.text = ramen.name
            binding.shop.text = ramen.shop
            binding.date.text = DateUtils.formatDate(
                ramen.date,
                DateUtils.DATE_FORMAT_DEFAULT,
                DateUtils.DATE_FORMAT_DATE_PRETTY
            )
            binding.favorite.visibility =
                if (ramen.favorite) View.VISIBLE else View.GONE
            if (!TextUtils.isEmpty(ramen.photoUri)) {
                updateRamenPhoto(itemView, ramen.photoUri)
            }
        }

        private fun updateRamenPhoto(itemView: View, uri: String?) {
            binding.ramenImagePlaceholder.visibility = View.GONE
            Glide.with(itemView)
                .load(uri)
                .into(binding.ramenImage)
            binding.ramenImage.visibility = View.VISIBLE
        }

    }

    class DiffCallback(
        private val oldList: List<Ramen>,
        private val newList: List<Ramen>
    ) : DiffUtil.Callback() {
        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}