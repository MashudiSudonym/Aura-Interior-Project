package c.m.aurainteriorproject.ui.main

import c.m.aurainteriorproject.model.WallpaperResponse
import c.m.aurainteriorproject.util.base.BaseView

interface MainView : BaseView {
    fun showLoading()
    fun hideLoading()
    fun getWallpaper(wallpaperData: List<WallpaperResponse?>)
}