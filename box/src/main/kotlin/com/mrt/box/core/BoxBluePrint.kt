package com.mrt.box.core

/**
 * Created by jaehochoe on 2020-01-01.
 */
class BoxBlueprint<S : BoxState, E : BoxEvent, SE : BoxSideEffect> internal constructor(
    val initialState: S,
    private val outputs: Map<BoxKey<E, E>, (S, E) -> To<S, SE>>,
    private val ioWorks: Map<BoxKey<SE, SE>, IOWork<S, E, SE>>,
    private val heavyWorks: Map<BoxKey<SE, SE>, HeavyWork<S, E, SE>>,
    private val lightWorks: Map<BoxKey<SE, SE>, LightWork<S, E, SE>>,
    private val asyncWorks: Map<BoxKey<SE, SE>, AsyncWork<S, E, SE>>
) {
    fun reduce(state: S, event: E): BoxOutput<S, E, SE> {
        return synchronized(this) {
            state.toOutput(event)
        }
    }

    fun asyncWorkOrNull(sideEffect: SE): AsyncWork<S, E, SE>? {
        return synchronized(this) {
            asyncWorks
                .filter { it.key.check(sideEffect) }
                .map { it.value }
                .firstOrNull()
        }
    }

    fun ioWorkOrNull(sideEffect: SE): IOWork<S, E, SE>? {
        return synchronized(this) {
            ioWorks
                .filter { it.key.check(sideEffect) }
                .map { it.value }
                .firstOrNull()
        }
    }

    fun heavyWorkOrNull(sideEffect: SE): HeavyWork<S, E, SE>? {
        return synchronized(this) {
            heavyWorks
                .filter { it.key.check(sideEffect) }
                .map { it.value }
                .firstOrNull()
        }
    }

    fun workOrNull(sideEffect: SE): LightWork<S, E, SE>? {
        return synchronized(this) {
            lightWorks
                .filter { it.key.check(sideEffect) }
                .map { it.value }
                .firstOrNull()
        }
    }

    private fun S.toOutput(event: E): BoxOutput<S, E, SE> {
        for ((key, stateTo) in outputs) {
            if (key.check(event)) {
                val (toState, sideEffect) = stateTo(this, event)
                return BoxOutput.Valid(this, event, toState, sideEffect)
            }
        }
        return BoxOutput.Void(this, event)
    }

    data class To<out STATE : BoxState, out SIDE_EFFECT : BoxSideEffect> internal constructor(
        val toState: STATE,
        val sideEffect: SIDE_EFFECT
    )
}