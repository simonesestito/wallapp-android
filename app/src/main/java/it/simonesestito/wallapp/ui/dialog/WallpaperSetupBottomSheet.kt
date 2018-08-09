package it.simonesestito.wallapp.ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import it.simonesestito.wallapp.R
import it.simonesestito.wallapp.annotations.FORMAT_16_9
import it.simonesestito.wallapp.annotations.FORMAT_18_9
import it.simonesestito.wallapp.annotations.FORMAT_IPHONE
import it.simonesestito.wallapp.annotations.FORMAT_IPHONE_X
import it.simonesestito.wallapp.utils.getSuggestedWallpaperFormat
import kotlinx.android.synthetic.main.wallpaper_setup_bottom_sheet.view.*


class WallpaperSetupBottomSheet : BottomSheetDialogFragment() {
    override fun getTheme() = R.style.AppTheme_BottomSheet_Dialog

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View =
            inflater.inflate(R.layout.wallpaper_setup_bottom_sheet, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Apply default selections
        val defaultFormatId = when (getSuggestedWallpaperFormat(resources.displayMetrics)) {
            FORMAT_16_9 -> R.id.wallpaperFormatChip16_9
            FORMAT_18_9 -> R.id.wallpaperFormatChip18_9
            FORMAT_IPHONE -> R.id.wallpaperFormatChipIphone
            FORMAT_IPHONE_X -> R.id.wallpaperFormatChipIphoneX
            else -> R.id.wallpaperFormatChip18_9 // Fallback
        }
        view.wallpaperFormatChipGroup.check(defaultFormatId)
        view.wallpaperLocationChipGroup.check(R.id.wallpaperLocationChipBoth)
    }
}