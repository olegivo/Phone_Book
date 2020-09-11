package phonebook

sealed class TimedResult<T : Any>(val start: Long, val end: Long) {
    val timeSpent = end - start

    abstract val isSuccess: Boolean
    abstract fun getSuccess(): Success<T>?
    abstract fun getSuccessResult(): T?

    class Success<T : Any>(start: Long, end: Long, val result: T) : TimedResult<T>(start, end) {
        override val isSuccess: Boolean = true
        override fun getSuccess(): Success<T>? = this
        override fun getSuccessResult(): T = result
    }

    class Timeout<T : Any>(start: Long, end: Long) : TimedResult<T>(start, end) {
        override val isSuccess: Boolean = false
        override fun getSuccess(): Success<T>? = null
        override fun getSuccessResult(): T? = null
    }
}

sealed class BubbleSortAndJumpSearchTime {
    abstract val timeSpent: Long
    abstract val isSuccess: Boolean

    object None : BubbleSortAndJumpSearchTime() {
        override val timeSpent: Long = 0
        override val isSuccess: Boolean = false
    }

    data class BubbleSort(val bubbleSort: TimedResult<Unit>) : BubbleSortAndJumpSearchTime() {
        override val timeSpent get() = bubbleSort.timeSpent
        override val isSuccess: Boolean = bubbleSort.isSuccess
    }

    data class JumpSearch(val bubbleSort: BubbleSort, val jumpSearch: TimedResult<Int>) : BubbleSortAndJumpSearchTime() {
        override val timeSpent get() = bubbleSort.timeSpent + jumpSearch.timeSpent
        override val isSuccess: Boolean = jumpSearch.isSuccess
    }

    data class LinearSearch(val jumpSearch: BubbleSortAndJumpSearchTime, val linearSearch: TimedResult<Int>) : BubbleSortAndJumpSearchTime() {
        override val timeSpent get() = jumpSearch.timeSpent + linearSearch.timeSpent
        override val isSuccess: Boolean = true
    }
}

sealed class BubbleSortAndJumpSearchPhase {
    object BubbleSort : BubbleSortAndJumpSearchPhase()
    object JumpSearch : BubbleSortAndJumpSearchPhase()
    object LinearSearch : BubbleSortAndJumpSearchPhase()
}