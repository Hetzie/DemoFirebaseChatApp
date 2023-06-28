package com.demo.demofirebasechat.base


import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.RecyclerView
import com.demo.demofirebasechat.datastore.MyDataStore
import com.demo.demofirebasechat.extentions.hideKeyboard
import com.google.gson.Gson
import javax.inject.Inject


abstract class BaseActivity<T : ViewDataBinding, V : ViewModel> : AppCompatActivity() {
    abstract val layoutId: Int
    abstract val bindingVariable: Int

    @Inject
    lateinit var mViewModel: V
    lateinit var binding: T
    lateinit var fragManager: FragmentManager

    lateinit var activity: Activity
    lateinit var baseActivity: BaseActivity<T, V>

    @Inject
    lateinit var gson: Gson
    @Inject
    lateinit var myDataStore: MyDataStore

    var applyPaddingBottomToWindow = true
    var isShown = false
    var flag = true
    var liveActivity = false

    abstract fun setupObservable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        baseActivity = this
        fragManager = supportFragmentManager
        activity = this
        myDataStore = MyDataStore(this)
        performDataBinding()

    }

    private fun performDataBinding() {
        activity = this
        binding = DataBindingUtil.setContentView(this, layoutId)
        binding.setVariable(bindingVariable, mViewModel)
        binding.executePendingBindings()
        setupObservable()
        if (!liveActivity) {
            setupUI(binding.root)
        }
        binding.root.rootView.clearFocus()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun setupUI(view: View) {
        // Set up touch listener for non-text box views to hide keyboard.

        if (view !is EditText && view !is AppCompatImageButton && view !is RecyclerView) {
            view.setOnTouchListener { _, _ ->
                hideKeyboard()
                binding.root.rootView.clearFocus()
                false
            }
        }

        //If a layout container, iterate over children and seed recursion.
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) {
                val innerView: View = view.getChildAt(i)
                setupUI(innerView)
            }
        }
    }

}