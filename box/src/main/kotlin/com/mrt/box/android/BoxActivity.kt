package com.mrt.box.android

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import com.mrt.box.android.event.BoxInAppEvent
import com.mrt.box.android.event.InAppEvent
import com.mrt.box.core.Box
import com.mrt.box.core.BoxEvent
import com.mrt.box.core.BoxSideEffect
import com.mrt.box.core.BoxState
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.launch

/**
 * Created by jaehochoe on 2020-01-03.
 */
abstract class BoxActivity<S : BoxState, E : BoxEvent, SE : BoxSideEffect> : AppCompatActivity(),
    BoxAndroidView {

    private val rendererList: List<BoxRenderer> by lazy {
        val list = (extraRenderer() ?: mutableListOf())
        renderer?.let {
            list.add(0, it)
        }
        list
    }
    abstract val renderer: BoxRenderer?

    abstract val viewInitializer: BoxViewInitializer?

    abstract val vm: BoxVm<S, E, SE>?

    override val binding: ViewDataBinding? by lazy {
        if (layout > 0) DataBindingUtil.setContentView<ViewDataBinding>(this, layout) else null
    }

    open fun preOnCreate(savedInstanceState: Bundle?) {
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preOnCreate(savedInstanceState)
        vm?.let {
            binding?.lifecycleOwner = this
            it.bind(this@BoxActivity)
            it.launch {
                subscribe(BoxInAppEvent.asChannel())
            }
        }
        viewInitializer?.initializeView(this, vm)
    }

    override fun render(state: BoxState) {
        rendererList.forEach { renderer ->
            renderer.renderView(this, state, vm)
        }
    }

    override fun intent(event: BoxEvent) {
        vm?.intent(event)
    }

    private suspend fun subscribe(channel: ReceiveChannel<InAppEvent>) {
        var isNeedSkipFirstEvent = channel.isEmpty.not()
        for (inAppEvent in channel) {
            if (isNeedSkipFirstEvent.not()) {
                Box.log { "InAppEvent = $inAppEvent in $this" }
                onSubscribe(inAppEvent)
            } else
                isNeedSkipFirstEvent = false
        }
    }

    override fun onDestroy() {
        viewInitializer?.onCleared()
        vm?.cancel()
        super.onDestroy()
    }

    @Suppress("UNUSED")
    fun extraRenderer(): MutableList<BoxRenderer>? {
        return null
    }

    override fun activity(): AppCompatActivity {
        return this
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        vm?.onNewIntent(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        vm?.onActivityResult(this, requestCode, resultCode, data)
    }

    private fun onSubscribe(inAppEvent: InAppEvent) {
        vm?.onSubscribe(inAppEvent)
    }

    override fun fragment(): Fragment {
        throw NullPointerException()
    }

    override fun pendingState(): S? {
        return vm?.currentState?.value
    }
}