package ebj.yujinkun.ramentracker.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.databinding.FragmentRamenListBinding;
import ebj.yujinkun.ramentracker.di.Injection;
import ebj.yujinkun.ramentracker.models.Ramen;
import ebj.yujinkun.ramentracker.ui.common.ItemSwipeCallback;
import timber.log.Timber;

public class RamenListFragment extends Fragment {

    private FragmentRamenListBinding binding;
    private RamenListViewModel viewModel;
    private RamenAdapter ramenAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRamenListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.fab.setOnClickListener(v -> navigateToRamenDetailScreen(null));

        RamenRepository ramenRepository = Injection.provideRamenRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, new RamenListViewModel.Factory(ramenRepository))
                .get(RamenListViewModel.class);

        ramenAdapter = new RamenAdapter();
        ramenAdapter.setOnItemClickListener(this::navigateToRamenDetailScreen);
        binding.ramenListView.setAdapter(ramenAdapter);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemSwipeCallback(this::handleRamenItemSwiped));
        itemTouchHelper.attachToRecyclerView(binding.ramenListView);

        viewModel.getRamenListLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleRamenListLoading);
            resource.doOnSuccess(this::handleLoadRamenListSuccess);
            resource.doOnError(this::handleLoadRamenListError);
        });

        viewModel.getDeleteRamenLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleDeleteRamenLoading);
            resource.doOnSuccess(this::handleDeleteRamenSuccess);
            resource.doOnError(this::handleDeleteRamenError);
        });

        viewModel.getSaveRamenLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleSaveRamenLoading);
            resource.doOnSuccess(this::handleSaveRamenSuccess);
            resource.doOnError(this::handleSaveRamenError);
        });

        viewModel.loadAllRamen();
    }

    private void handleRamenItemSwiped(int position) {
        Ramen ramen = ramenAdapter.getItemAt(position);
        viewModel.delete(ramen);
    }

    private void handleRamenListLoading() {
        Timber.i("Loading ramen list");
    }

    private void handleLoadRamenListSuccess(List<Ramen> ramenList) {
        Timber.i("Load ramen list success: %s", ramenList);
        ramenAdapter.submitList(ramenList);
    }

    private void handleLoadRamenListError(Throwable error) {
        Timber.e(error, "Load ramen list error");
        Toast.makeText(requireContext(), "An error occurred.", Toast.LENGTH_SHORT).show();
    }

    private void handleDeleteRamenLoading() {
        Timber.i("Deleting ramen...");
    }

    private void handleDeleteRamenSuccess(Ramen ramen) {
        Timber.i("Ramen deleted: %s", ramen);
        Snackbar.make(requireView(), R.string.entry_deleted, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> viewModel.save(ramen))
                .show();
    }

    private void handleDeleteRamenError(Throwable error) {
        Timber.e(error, "Ramen delete error");
        Toast.makeText(requireContext(), "Delete error", Toast.LENGTH_SHORT).show();
    }

    private void handleSaveRamenLoading() {
        Timber.i("Saving...");
    }

    private void handleSaveRamenSuccess(Ramen ramen) {
        Timber.i("Save ramen success: %s", ramen);
    }

    private void handleSaveRamenError(Throwable error) {
        Timber.e(error, "Save ramen error");
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show();
    }

    private void navigateToRamenDetailScreen(@Nullable Ramen ramen) {
        RamenListFragmentDirections.ActionRamenListToRamenDetail action =
                RamenListFragmentDirections.actionRamenListToRamenDetail();
        action.setRamen(ramen);
        NavHostFragment.findNavController(this).navigate(action);
    }
}