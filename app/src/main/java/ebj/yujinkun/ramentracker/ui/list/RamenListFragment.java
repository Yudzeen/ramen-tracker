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

import java.util.List;

import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.databinding.FragmentRamenListBinding;
import ebj.yujinkun.ramentracker.di.Injection;
import ebj.yujinkun.ramentracker.models.Ramen;
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

        viewModel.getRamenListLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleRamenListLoading);
            resource.doOnSuccess(this::handleLoadRamenListSuccess);
            resource.doOnError(this::handleLoadRamenListError);
        });

        viewModel.loadAllRamen();
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

    private void navigateToRamenDetailScreen(@Nullable Ramen ramen) {
        RamenListFragmentDirections.ActionRamenListToRamenDetail action =
                RamenListFragmentDirections.actionRamenListToRamenDetail();
        action.setRamen(ramen);
        NavHostFragment.findNavController(this).navigate(action);
    }
}