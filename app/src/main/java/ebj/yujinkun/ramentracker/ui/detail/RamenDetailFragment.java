package ebj.yujinkun.ramentracker.ui.detail;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import androidx.navigation.ui.NavigationUI;

import java.util.Calendar;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.databinding.FragmentRamenDetailBinding;
import ebj.yujinkun.ramentracker.di.Injection;
import ebj.yujinkun.ramentracker.models.Ramen;
import ebj.yujinkun.ramentracker.util.DateUtils;
import timber.log.Timber;

public class RamenDetailFragment extends Fragment {

    private FragmentRamenDetailBinding binding;
    private RamenDetailViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRamenDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RamenRepository ramenRepository = Injection.provideRamenRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, new RamenDetailViewModel.Factory(ramenRepository))
                .get(RamenDetailViewModel.class);
        parseArguments(getArguments());
        initViews();

        viewModel.getSaveRamenLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleSaveLoading);
            resource.doOnSuccess(this::handleSaveSuccess);
            resource.doOnError(this::handleSaveError);
        });

        viewModel.getDeleteRamenLiveData().observe(getViewLifecycleOwner(), resource -> {
            resource.doOnLoading(this::handleDeleteLoading);
            resource.doOnSuccess(this::handleDeleteSuccess);
            resource.doOnError(this::handleDeleteError);
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_ramen_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_favorite).setIcon(viewModel.isFavorite() ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        menu.findItem(R.id.action_delete).setVisible(viewModel.getRamen() != null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_favorite) {
            handleFavoriteAction();
            return true;
        }

        if (id == R.id.action_delete) {
            handleDeleteAction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void parseArguments(Bundle args) {
        Ramen ramen = RamenDetailFragmentArgs.fromBundle(args).getRamen();
        viewModel.initValues(ramen);
    }

    private void setupToolbar() {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        binding.toolbar.setTitle("");
        activity.setSupportActionBar(binding.toolbar);
        NavigationUI.setupWithNavController(binding.toolbar, NavHostFragment.findNavController(this));
    }

    private void initViews() {
        initShopEditText();
        initLocationEditText();
        initRamenNameEditText();
        initCommentsEditText();
        initDateField();
        binding.fab.setOnClickListener(view -> viewModel.save());
    }

    private void initShopEditText() {
        binding.shop.getEditText().setText(viewModel.getShop());
        binding.shop.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setShop(s.toString());
            }
        });
    }

    private void initLocationEditText() {
        binding.location.getEditText().setText(viewModel.getLocation());
        binding.location.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setLocation(s.toString());
            }
        });
    }

    private void initRamenNameEditText() {
        binding.ramenName.getEditText().setText(viewModel.getRamenName());
        binding.ramenName.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setRamenName(s.toString());
            }
        });
    }

    private void initCommentsEditText() {
        binding.comments.getEditText().setText(viewModel.getComments());
        binding.comments.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                viewModel.setComments(s.toString());
            }
        });
    }

    private void initDateField() {
        binding.date.getEditText().setText(DateUtils.formatDate(viewModel.getDate(),
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY));
        binding.date.getEditText().setInputType(InputType.TYPE_NULL);
        binding.date.getEditText().setOnClickListener(v -> handleDateClicked());
    }

    private void handleDateClicked() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(DateUtils.getDate(viewModel.getDate()));
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                (view, year, month, dayOfMonth) -> handleDatePicked(year, month, dayOfMonth),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void handleDatePicked(int year, int month, int dayOfMonth) {
        viewModel.setDate(DateUtils.formatDate(year, month, dayOfMonth));
        binding.date.getEditText().setText(DateUtils.formatDate(viewModel.getDate(),
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY));
    }

    private void handleFavoriteAction() {
        viewModel.setFavorite(!viewModel.isFavorite());
        requireActivity().invalidateOptionsMenu();
    }

    private void handleDeleteAction() {
        viewModel.delete();
    }

    private void handleDeleteLoading() {
        Timber.i("Deleting");
    }

    private void handleDeleteSuccess(Ramen ramen) {
        Timber.i("Delete success: %s", ramen);
        NavHostFragment.findNavController(this).navigateUp();
    }

    private void handleDeleteError(Throwable error) {
        Timber.e(error, "Delete error");
        Toast.makeText(requireContext(), "Delete error", Toast.LENGTH_SHORT).show();
    }

    private void handleSaveLoading() {
        Timber.i("Saving...");
    }

    private void handleSaveSuccess(Ramen ramen) {
        Timber.i("Save success: %s", ramen);
        Toast.makeText(requireContext(), "Saved", Toast.LENGTH_SHORT).show();
    }

    private void handleSaveError(Throwable error) {
        Timber.e(error, "Save error");
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show();
    }

}