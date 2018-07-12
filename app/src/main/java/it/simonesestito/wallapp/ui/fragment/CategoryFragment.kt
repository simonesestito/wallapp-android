package it.simonesestito.wallapp.ui.fragment


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.category_fragment.*

class CategoryFragment : Fragment() {
    private lateinit var viewModel: MainViewModel
    private lateinit var args: CategoryFragmentArgs

    private val categoryArgsKey = "categoryId"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(MainViewModel::class.java)
        args = CategoryFragmentArgs.fromBundle(arguments)
        viewModel.getCategoryById(args.categoryId).observe(this, Observer {
            singleCategoryDemoText.text = it.displayName
        })
    }


    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(categoryArgsKey, args.categoryId)
        super.onSaveInstanceState(outState)
    }
}
