package c.m.aurainteriorproject.util.base

interface BasePresenter<T : BaseView> {
    fun onAttach(view: T)

    fun onDetach()
}