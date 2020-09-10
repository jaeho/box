package com.mrt.box.sample

import com.mrt.box.android.BoxAndroidView
import com.mrt.box.android.BoxStateRenderer
import com.mrt.box.core.Vm
import com.mrt.box.sample.databinding.ActivityExampleBinding

/**
 * Created by jaehochoe on 2020-01-03.
 */
<<<<<<< HEAD
object ExampleRenderer : BoxRenderer<ExampleState, ExampleEvent> {
    override fun render(v: BoxAndroidView<ExampleState, ExampleEvent>, s: ExampleState, vm: Vm?) : Boolean {
=======
object ExampleRenderer : BoxStateRenderer<ExampleState> {
    override fun render(v: BoxAndroidView, s: ExampleState, vm: Vm?) {
        println("ExampleRenderer.render() $s")
>>>>>>> upstream/master
        val binding = v.binding<ActivityExampleBinding>()
        binding.vm = vm
        binding.count = s.count
        return super.render(v, s, vm)
    }
}