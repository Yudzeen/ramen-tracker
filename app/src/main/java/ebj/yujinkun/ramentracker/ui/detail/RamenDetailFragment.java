package ebj.yujinkun.ramentracker.ui.detail;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import java.io.IOException;
import java.util.Calendar;
import java.util.Objects;

import ebj.yujinkun.ramentracker.R;
import ebj.yujinkun.ramentracker.data.RamenRepository;
import ebj.yujinkun.ramentracker.data.models.Ramen;
import ebj.yujinkun.ramentracker.databinding.FragmentRamenDetailBinding;
import ebj.yujinkun.ramentracker.di.Injection;
import ebj.yujinkun.ramentracker.util.DateUtils;
import ebj.yujinkun.ramentracker.util.SoftKeyboardUtils;
import timber.log.Timber;

public class RamenDetailFragment extends Fragment {

    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1;

    private static final int SELECT_IMAGE_REQUEST_CODE = 100;
    private static final int TAKE_PHOTO_REQUEST_CODE = 200;

    private FragmentRamenDetailBinding binding;
    private RamenDetailViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                onBackPressed();
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRamenDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RamenRepository ramenRepository = Injection.getRamenRepository(requireActivity().getApplication());
        viewModel = new ViewModelProvider(this, new RamenDetailViewModel.Factory(ramenRepository))
                .get(RamenDetailViewModel.class);
        parseArguments(getArguments());
        initViews();

        viewModel.getPhotoLocationLiveData().observe(getViewLifecycleOwner(), location -> updateRamenPhoto(BitmapFactory.decodeFile(location)));

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

        viewModel.getUnsavedChangesLiveData().observe(getViewLifecycleOwner(), hasUnsavedChanges -> {
            if (hasUnsavedChanges) {
                showSaveChangesButton();
            } else {
                hideSaveChangesButton();
            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupToolbar();
    }

    @Override
    public void onDestroyView() {
        SoftKeyboardUtils.hideSoftKeyboard(requireActivity());
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.action_bar_ramen_detail, menu);
    }

    @Override
    public void onPrepareOptionsMenu(@NonNull Menu menu) {
        menu.findItem(R.id.action_favorite).setIcon(viewModel.isFavorite() ?
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
        menu.findItem(R.id.action_delete).setVisible(viewModel.getInitialRamen() != null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == SELECT_IMAGE_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri uri = data.getData();
                    Timber.i("Received uri: %s", uri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                        updateRamenPhoto(bitmap);
                    } catch (IOException e) {
                        Timber.e(e, "Get bitmap error");
                    }
                } else {
                    Timber.e("data is null");
                }
            }
        } else if (requestCode == TAKE_PHOTO_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Bundle extras = data.getExtras();
                Bitmap bitmap = (Bitmap) extras.get("data");
                updateRamenPhoto(bitmap);
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case STORAGE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    handleSelectImage();
                } else {
                    Toast.makeText(requireContext(), "Please allow storage permission to proceed", Toast.LENGTH_SHORT).show();
                }
        }
    }

    private void parseArguments(Bundle args) {
        Ramen ramen = RamenDetailFragmentArgs.fromBundle(args).getRamen();
        viewModel.initValues(ramen);
    }

    private void setupToolbar() {
        setHasOptionsMenu(true);
        AppCompatActivity activity = (AppCompatActivity) requireActivity();
        activity.setSupportActionBar(binding.toolbar);
        ActionBar actionBar = Objects.requireNonNull(activity.getSupportActionBar());
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle("");
    }

    private void initViews() {
        initShopEditText();
        initLocationEditText();
        initRamenNameEditText();
        initCommentsEditText();
        initDateField();
        binding.ramenImagePlaceholder.setOnClickListener(view -> showUpdatePhotoDialog());
        binding.ramenImage.setOnClickListener(view -> showUpdatePhotoDialog());
        binding.fab.setOnClickListener(view -> viewModel.saveRamen());
    }

    private void initShopEditText() {
        EditText editText = binding.shop.getEditText();
        editText.setText(viewModel.getShop());
        editText.addTextChangedListener(new TextWatcher() {
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
        EditText editText = binding.location.getEditText();
        editText.setText(viewModel.getLocation());
        editText.addTextChangedListener(new TextWatcher() {
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
        EditText editText = binding.ramenName.getEditText();
        editText.setText(viewModel.getRamenName());
        editText.addTextChangedListener(new TextWatcher() {
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
        EditText editText = binding.comments.getEditText();
        editText.setText(viewModel.getComments());
        editText.addTextChangedListener(new TextWatcher() {
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
        binding.date.getEditText().setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                handleDateClicked();
            }
        });
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
        viewModel.deleteRamen();
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

    private void onBackPressed() {
        boolean contentsUpdated = Objects.requireNonNull(viewModel.getUnsavedChangesLiveData().getValue());
        if (contentsUpdated) {
            showConfirmationDialog();
        } else {
            navigateUp();
        }
    }

    private void showConfirmationDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                .setMessage("Are you sure you want to discard your changes?")
                .setPositiveButton("Yes", (dialog, which) ->
                        navigateUp())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void showUpdatePhotoDialog() {
        AlertDialog alertDialog = new AlertDialog.Builder(requireActivity())
                .setTitle(R.string.update_image)
                .setItems(R.array.update_photo, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            handleTakePhoto();
                            break;
                        case 1:
                            handleSelectImage();
                            break;
                        default:
                            Timber.e("Unknown item selected");
                    }
                })
                .setNegativeButton(R.string.cancel, (dialog, which) -> dialog.dismiss())
                .create();
        alertDialog.show();
    }

    private void handleTakePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, TAKE_PHOTO_REQUEST_CODE);
    }

    private void handleSelectImage() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) ==
                PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Image"), SELECT_IMAGE_REQUEST_CODE);
        }  else {
            requestPermissions(new String[] {Manifest.permission.READ_EXTERNAL_STORAGE}, STORAGE_PERMISSION_REQUEST_CODE);
        }
    }

    private void updateRamenPhoto(Bitmap bitmap) {
        Timber.i("Update ramen photo");
        viewModel.setBitmap(bitmap);
        binding.ramenImagePlaceholder.setVisibility(View.GONE);
        binding.addPhotoIcon.setVisibility(View.GONE);
        binding.ramenImage.setImageBitmap(bitmap);
        binding.ramenImage.setVisibility(View.VISIBLE);
    }

    private void showSaveChangesButton() {
        binding.fab.setVisibility(View.VISIBLE);
    }

    private void hideSaveChangesButton() {
        binding.fab.setVisibility(View.GONE);
    }

    private void navigateUp() {
        NavHostFragment.findNavController(this).navigateUp();
    }

}