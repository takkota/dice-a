package com.github.takkota.android.dice_a

import java.util.concurrent.ThreadLocalRandom

class DiceA<T> {

    private var pips: MutableList<PipData<T>> = mutableListOf()

    private data class PipData<out T> (
            val pip: Pair<Double, T>,
            var decisionCount: Int = 0
    )

    class Builder<T> {
        private var pipsBuilder: MutableList<PipData<T>> = mutableListOf()

        fun addPip(probability: Double, face: T): Builder<T> {
            val pip = PipData(Pair(probability, face), 0)
            pipsBuilder.add(pip)
            return this
        }

        fun addPips(pips: List<Pair<Double, T>>): Builder<T> {
            val pipdata = pips.map { PipData(it) }
            pipsBuilder.addAll(pipdata)
            return this
        }

        fun build(): DiceA<T> {
            return DiceA<T>().apply {
                this.pips = pipsBuilder
            }
        }
    }

    fun throwDice(): T? {
        val totalProb = pips.sumByDouble{ it.pip.first }

        val randomValue = ThreadLocalRandom.current().nextDouble(0.0, totalProb)
        var boundValue = 0.0
        val decision = pips.sortedBy { it.pip.first }.firstOrNull {
            boundValue += it.pip.first
            boundValue > randomValue
        }
        decision ?: return null

        ++decision.decisionCount
        return decision.pip.second
    }
}
