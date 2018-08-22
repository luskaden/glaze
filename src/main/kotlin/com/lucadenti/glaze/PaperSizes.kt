package com.lucadenti.glaze

object PaperSizes {
    /**
     * ISO Paper Sizes (very few of them).
     * It returns an array of mm
     */
    fun getSize(type: String): Array<Int> {
        return when (type)  {
            "A3" -> arrayOf(297, 420)
            "A4" -> arrayOf(210, 297)
            "B3" -> arrayOf(353, 500)
            "B4" -> arrayOf(250, 353)
            "LETTER" -> arrayOf(216, 279)
            else -> arrayOf(210, 297)//A4 is the default one
        }
    }
}