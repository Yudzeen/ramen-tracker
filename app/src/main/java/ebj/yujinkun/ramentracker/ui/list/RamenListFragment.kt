package ebj.yujinkun.ramentracker.ui.list

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ebj.yujinkun.ramentracker.R
import ebj.yujinkun.ramentracker.data.models.Ramen
import ebj.yujinkun.ramentracker.databinding.FragmentRamenListBinding
import ebj.yujinkun.ramentracker.ui.common.ItemSwipeCallback
import ebj.yujinkun.ramentracker.util.Resource
import timber.log.Timber

@AndroidEntryPoint
class RamenListFragment : Fragment() {
    private lateinit var binding: FragmentRamenListBinding
    private val viewModel: RamenListViewModel by viewModels()
    private var ramenAdapter: RamenAdapter? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRamenListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.fab.setOnClickListener { v: View? -> navigateToRamenDetailScreen(null) }
        ramenAdapter = RamenAdapter()
        ramenAdapter!!.setOnItemClickListener(object: RamenAdapter.OnItemClickListener {
            override fun onItemClicked(ramen: Ramen) {
                navigateToRamenDetailScreen(ramen)
            }
        })
        binding.ramenListView.adapter = ramenAdapter
        val itemTouchHelper =
            ItemTouchHelper(ItemSwipeCallback(object : ItemSwipeCallback.Listener {
                override fun onItemSwiped(position: Int) {
                    handleRamenItemSwiped(position)
                }

            }))
        itemTouchHelper.attachToRecyclerView(binding.ramenListView)
        viewModel.getRamenListLiveData().observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> handleLoadRamenListSuccess(resource.data)
                is Resource.Error -> handleLoadRamenListError(resource.error)
                is Resource.Loading -> handleRamenListLoading()
            }
        })
        viewModel.getDeleteRamenLiveData().observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> handleDeleteRamenLoading()
                is Resource.Error -> handleDeleteRamenError(resource.error)
                is Resource.Loading -> handleDeleteRamenLoading()
            }
        })
        viewModel.getSaveRamenLiveData().observe(viewLifecycleOwner, { resource ->
            when (resource) {
                is Resource.Success -> handleSaveRamenSuccess(resource.data)
                is Resource.Error -> handleSaveRamenError(resource.error)
                is Resource.Loading -> handleSaveRamenLoading()
            }
        })
        viewModel.loadAllRamen()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupToolbar()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.action_bar_ramen_list, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.action_about) {
            navigateToAboutScreen()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setupToolbar() {
        setHasOptionsMenu(true)
        val activity = requireActivity() as AppCompatActivity
        activity.setSupportActionBar(binding.toolbar)
    }

    private fun handleRamenItemSwiped(position: Int) {
        val ramen = ramenAdapter?.getItemAt(position)
        ramen?.let { viewModel.delete(it) }

    }

    private fun handleRamenListLoading() {
        Timber.i("Loading ramen list")
        showLoadingView()
    }

    private fun handleLoadRamenListSuccess(ramenList: List<Ramen>) {
        Timber.i("Load ramen list success: %s", ramenList)
        hideLoadingView()
        ramenAdapter!!.submitList(ramenList)
    }

    private fun handleLoadRamenListError(error: Throwable) {
        Timber.e(error, "Load ramen list error")
        hideLoadingView()
        Toast.makeText(requireContext(), "An error occurred.", Toast.LENGTH_SHORT).show()
    }

    private fun handleDeleteRamenLoading() {
        Timber.i("Deleting ramen...")
    }

    private fun handleDeleteRamenSuccess(ramen: Ramen) {
        Timber.i("Ramen deleted: %s", ramen)
        Snackbar.make(requireView(), R.string.entry_deleted, Snackbar.LENGTH_LONG)
            .setAction(R.string.undo) { v: View? -> viewModel.save(ramen) }
            .show()
    }

    private fun handleDeleteRamenError(error: Throwable?) {
        Timber.e(error, "Ramen delete error")
        Toast.makeText(requireContext(), "Delete error", Toast.LENGTH_SHORT).show()
    }

    private fun handleSaveRamenLoading() {
        Timber.i("Saving...")
    }

    private fun handleSaveRamenSuccess(ramen: Ramen) {
        Timber.i("Save ramen success: %s", ramen)
    }

    private fun handleSaveRamenError(error: Throwable?) {
        Timber.e(error, "Save ramen error")
        Toast.makeText(requireContext(), "Save error", Toast.LENGTH_SHORT).show()
    }

    private fun showLoadingView() {
        binding.progressBarContainer.root.visibility = View.VISIBLE
    }

    private fun hideLoadingView() {
        binding.progressBarContainer.root.visibility = View.GONE
    }

    private fun navigateToRamenDetailScreen(ramen: Ramen?) {
        val action = RamenListFragmentDirections.actionRamenListToRamenDetail()
        action.setRamen(ramen)
        NavHostFragment.findNavController(this).navigate(action)
    }

    private fun navigateToAboutScreen() {
        NavHostFragment.findNavController(this)
            .navigate(RamenListFragmentDirections.actionRamenListFragmentToAboutFragment())
    }
}