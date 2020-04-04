package c.m.aurainteriorproject.ui.detail

import c.m.aurainteriorproject.util.base.BaseView

interface DetailView : BaseView {
    fun getDetailWallpaper(
        type: String,
        imageWallpaper: String
    )
}