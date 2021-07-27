package me.senseiju.sennetmazes


enum class Cardinal(val bit: Int, val dx: Int, val dy: Int) {
    N(1, 0, -1),
    S(2, 0, 1),
    E(4, 1, 0),
    W(8, -1, 0);

    lateinit var opposite: Cardinal

    companion object {
        init {
            N.opposite = S
            S.opposite = N
            E.opposite = W
            W.opposite = E
        }

        fun shuffled(): ArrayList<Cardinal> {
            with (values().toMutableList() as ArrayList) {
                shuffle()

                return this
            }
        }
    }
}