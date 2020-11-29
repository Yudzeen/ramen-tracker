package ebj.yujinkun.ramentracker.ui.detail;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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

    private void initViews() {
        initShopEditText();
        initLocationEditText();
        initRamenNameEditText();

        binding.date.setText(DateUtils.formatDate(viewModel.getDate(),
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY));
        binding.date.setOnClickListener(v -> handleDateClicked());

        binding.favorite.setImageResource(viewModel.isFavorite() ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        binding.favorite.setOnClickListener(v -> handleFavoriteClicked());

        binding.fab.setOnClickListener(view -> viewModel.save());
    }

    private void initShopEditText() {
        binding.shop.setText(viewModel.getShop());
        binding.shop.addTextChangedListener(new TextWatcher() {
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
        binding.location.setText(viewModel.getLocation());
        binding.location.addTextChangedListener(new TextWatcher() {
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
        binding.ramenName.setText(viewModel.getRamenName());
        binding.ramenName.addTextChangedListener(new TextWatcher() {
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
        binding.date.setText(DateUtils.formatDate(viewModel.getDate(),
                DateUtils.DATE_FORMAT_DEFAULT, DateUtils.DATE_FORMAT_DATE_PRETTY));
    }

    private void handleFavoriteClicked() {
        viewModel.setFavorite(!viewModel.isFavorite());
        binding.favorite.setImageResource(viewModel.isFavorite() ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    private void parseArguments(Bundle args) {
        Ramen ramen = RamenDetailFragmentArgs.fromBundle(args).getRamen();
        viewModel.initValues(ramen);
    }
}