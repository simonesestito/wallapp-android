package it.simonesestito.wallapp.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.firebase.storage.FirebaseStorage
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.model.Category
import it.simonesestito.wallapp.utils.setFirebaseImage
import it.simonesestito.wallapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.category_fragment.*
import kotlinx.android.synthetic.main.category_fragment.view.*

class SingleCategoryFragment : Fragment() {
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }
    private val args by lazy {
        SingleCategoryFragmentArgs.fromBundle(arguments)
    }

    // Used to save the current category ID on state
    private val categoryArgsKey = "categoryId"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.category_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Rounded ImageView with rounded_corners background
        view.categoryCoverImage.clipToOutline = true
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel.getCategoryById(args.categoryId).observe(this, Observer {
            activity?.title = it.displayName
            populateView(it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity?.title = args.categoryTitle
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(categoryArgsKey, args.categoryId)
        super.onSaveInstanceState(outState)
    }

    private fun populateView(category: Category) {
        val coverRef = FirebaseStorage
                .getInstance()
                .getReference(category.coverUrl)

        categoryDescription.text = category.description
        categoryCoverImage.setFirebaseImage(coverRef)
    }
}
