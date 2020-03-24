package com.mrt.box.sample

import com.mrt.box.core.BoxSideEffect

/**
 * Created by jaehochoe on 2020-01-02.
 */
sealed class ExampleSideEffect : BoxSideEffect {
    data class AutoCountUp(val count: Int) : ExampleSideEffect()
    data class Finish(val action: () -> Unit) : ExampleSideEffect()
}