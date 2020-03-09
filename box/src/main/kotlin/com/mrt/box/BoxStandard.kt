package com.mrt.box

import androidx.databinding.ViewDataBinding
import com.mrt.box.android.BoxVm
import com.mrt.box.core.Box
import com.mrt.box.core.BoxEvent
import com.mrt.box.core.BoxState
import com.mrt.box.core.BoxSideEffect

/**
 * Created by jaehochoe on 2020-01-03.
 */
fun <B : ViewDataBinding> ViewDataBinding?.be(): B {
    return this as B
}

fun <S : BoxState, E : BoxEvent, SE : BoxSideEffect> BoxVm<S, E, SE>.isValidEvent(event: Any) : Boolean {
    return kotlin.runCatching {
        event as E
    }.isSuccess
}