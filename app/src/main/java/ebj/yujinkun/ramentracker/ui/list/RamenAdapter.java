package ebj.yujinkun.ramentracker.ui.list;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.databinding.ListItemRamenBinding;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.util.DateUtils;
import timber.log.Timber;

public class RamenAdapter extends RecyclerView.Adapter<RamenAdapter.ViewHolder> {

    private final List<Ramen> ramenList = new ArrayList<>();
    private OnItemClickListener onItemClickListener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_ramen, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ramen ramen = ramenList.get(position);
        holder.setRamen(ramen);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClicked(ramen);
            } else {
                Timber.w("onItemClickListener is null");
            }
        });
    }

    @Override
    public int getItemCount() {
        return ramenList.size();
    }

    public Ramen getItemAt(int position) {
        return ramenList.get(position);
    }

    public void submitList(List<Ramen> newList) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new DiffCallback(ramenList, newList));
        diffResult.dispatchUpdatesTo(this);
        ramenList.clear();
        ramenList.addAll(newList);
    }

    public interface OnItemClickListener {
        void onItemClicked(Ramen ramen);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ListItemRamenBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ListItemRamenBinding.bind(itemView);
        }

        public void setRamen(Ramen ramen) {
            binding.ramenName.setText(ramen.getName());
            binding.shop.setText(ramen.getShop());
            binding.date.setText(DateUtils.formatDate(ramen.getDate(),
                    DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY));
            binding.favorite.setVisibility(ramen.isFavorite() ? View.VISIBLE : View.GONE);

            if (!TextUtils.isEmpty(ramen.getPhotoUri())) {
                updateRamenPhoto(BitmapFactory.decodeFile(ramen.getPhotoUri()));
            }

        }

        private void updateRamenPhoto(Bitmap bitmap) {
            binding.ramenImagePlaceholder.setVisibility(View.GONE);
            binding.ramenImage.setImageBitmap(bitmap);
            binding.ramenImage.setVisibility(View.VISIBLE);
        }
    }

    public static class DiffCallback extends DiffUtil.Callback {

        private final List<Ramen> oldList;
        private final List<Ramen> newList;

        public DiffCallback(List<Ramen> oldList, List<Ramen> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId().equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }
    }
}
