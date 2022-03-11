package com.michaelpohl.wifitool.ui.screens.mainscreen

// TODO move to better place
class StringBuffer {
    private val list = mutableListOf<String>()

    @Suppress("EqualsAlwaysReturnsTrueOrFalse")
    override fun equals(other: Any?): Boolean {
        return false
    }

    override fun hashCode(): Int {
        return super.hashCode()
    }

    fun addMessage(string: String) {
        if (list.size > BUFFER_SIZE) list.removeAt(0)
        list.add(string)
    }

    fun get(): List<String> {
        return list
    }

    companion object {
        const val BUFFER_SIZE = 24
    }
}
