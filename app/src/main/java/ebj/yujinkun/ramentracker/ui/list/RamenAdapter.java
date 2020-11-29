package ebj.yujinkun.ramentracker.ui.list;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.databinding.ListItemRamenBinding;
import ebj.yujinkun.ramentracker.models.Ramen;
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

    public void submitList(List<Ramen> newList) {
        ramenList.clear();
        ramenList.addAll(newList);
        notifyDataSetChanged();
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
            binding.location.setText(ramen.getLocation());
            binding.favorite.setImageResource(ramen.isFavorite() ? R.drawable.ic_favorite :
                    R.drawable.ic_favorite_border);
        }
    }
}
