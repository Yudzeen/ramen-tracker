package ebj.yujinkun.ramentracker.ui.list;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.ItemTouchHelper;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.databinding.FragmentRamenListBinding;
import ebj.yujinkun.ramentracker.di.Injection;
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

        RamenRepository ramenRepository = Injection.getRamenRepository(requireActivity().getApplication());
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

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_ramen_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        final int id = item.getItemId();

        if (id == R.id.action_about) {
            navigateToAboutScreen();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupToolbar() {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);
    }

    private void handleRamenItemSwiped(int position) {
        Ramen ramen = ramenAdapter.getItemAt(position);
        viewModel.delete(ramen);
    }

    private void handleRamenListLoading() {
        Timber.i("Loading ramen list");
        showLoadingView();
    }

    private void handleLoadRamenListSuccess(List<Ramen> ramenList) {
        Timber.i("Load ramen list success: %s", ramenList);
        hideLoadingView();
        ramenAdapter.submitList(ramenList);
    }

    private void handleLoadRamenListError(Throwable error) {
        Timber.e(error, "Load ramen list error");
        hideLoadingView();
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

    private void showLoadingView() {
        binding.progressBarContainer.getRoot().setVisibility(View.VISIBLE);
    }

    private void hideLoadingView() {
        binding.progressBarContainer.getRoot().setVisibility(View.GONE);
    }

    private void navigateToRamenDetailScreen(@Nullable Ramen ramen) {
        RamenListFragmentDirections.ActionRamenListToRamenDetail action =
                RamenListFragmentDirections.actionRamenListToRamenDetail();
        action.setRamen(ramen);
        NavHostFragment.findNavController(this).navigate(action);
    }

    private void navigateToAboutScreen() {
        NavHostFragment.findNavController(this)
                .navigate(RamenListFragmentDirections.actionRamenListFragmentToAboutFragment());
    }
}