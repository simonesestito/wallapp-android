package it.simonesestito.wallapp.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.TAG
import kotlinx.android.synthetic.main.categories_fragment.*

class CategoriesFragment : Fragment() {
    private lateinit var viewModel: MainViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.categories_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        viewModel.getCategories().observe(this, Observer { list ->
            // TODO: Implement real view logic
            list.forEach { item ->
                Log.d(TAG, item.toString())
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoriesFragmentDemoText.setOnClickListener { v ->
            val direction = CategoriesFragmentDirections.action_categoriesFragment_to_categoryFragment2("ciao")
            v.findNavController().navigate(direction)
        }
    }
}
