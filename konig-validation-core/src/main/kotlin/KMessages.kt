package  com.konigsoftware.validation

import com.konigsoftware.validation.kasts.Int32Kast
import com.konigsoftware.validation.kasts.Kast
import com.konigsoftware.validation.kasts.StringKast

class KMessages() {

    companion object {
        private val DEFAULT_KASTS: Map<String, Kast<*, *>> = listOf(
            Int32Kast,
            StringKast
        ).associateBy { it.id() }

        private val DEFAULT = KMessages()

        fun default() = DEFAULT
    }

    fun getKast(kastId: String): Kast<*, *> = DEFAULT_KASTS.getOrElse(kastId) { throw IllegalArgumentException() }
}
