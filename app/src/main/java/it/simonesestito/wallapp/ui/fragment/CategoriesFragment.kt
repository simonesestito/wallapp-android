package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import it.simonesestito.wallapp.MainActivity
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.onScrollListener
import it.simonesestito.wallapp.ui.adapter.CategoriesAdapter
import it.simonesestito.wallapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.categories_fragment.*
import kotlinx.android.synthetic.main.categories_fragment.view.*

class CategoriesFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var categoriesAdapter: CategoriesAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        categoriesAdapter = CategoriesAdapter(inflater.context)
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Show loading spinner
        view.categoriesLoadingBar.show()

        // Set initial RecyclerView status, without any data
        view.categoriesRecyclerView.adapter = categoriesAdapter
        view.categoriesRecyclerView.layoutManager = LinearLayoutManager(context)

        view.categoriesRecyclerView.onScrollListener { recyclerView ->
            val mainActivity = activity
            if (mainActivity == null || mainActivity !is MainActivity)
                return@onScrollListener

            val layoutManager = recyclerView.layoutManager
            if (layoutManager == null || layoutManager !is LinearLayoutManager)
                return@onScrollListener

            // Find the first completely visible item
            // If it's the first one, hide the elevation
            // Else show it
            // Show the elevation only if the RecyclerView is scrolled
            val firstIndex = layoutManager.findFirstCompletelyVisibleItemPosition()
            if (firstIndex == 0)
                mainActivity.hideAppbarElevation()
            else
                mainActivity.showAppbarElevation()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getCategories().observe(this, Observer { list ->
            // Hide loading spinner
            categoriesLoadingBar.hide()

            if (list.isNotEmpty()) {
                // Update Adapter dataset
                categoriesAdapter.updateDataSet(list)

                // Hide Empty View
                categoriesEmptyView.visibility = View.GONE

                // Show RecyclerView
                categoriesRecyclerView.visibility = View.VISIBLE
            } else {
                // Hide RecyclerView
                categoriesRecyclerView.visibility = View.GONE

                // Show Empty View
                categoriesEmptyView.visibility = View.VISIBLE
            }
        })
    }

    override fun onDetach() {
        super.onDetach()
        // Reset default appbar elevation on fragment detach
        (activity as MainActivity?)?.hideAppbarElevation()
    }
}
