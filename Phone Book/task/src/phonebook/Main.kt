package phonebook


fun main() {
    val phoneBook = PhoneBook(
            directoryFilePath = "/Users/olegivo/Projects/Learn/Kotlin/directory.txt",
            findFilePath = "/Users/olegivo/Projects/Learn/Kotlin/find.txt"
    )
    phoneBook.load()

    val linearSearchTime = measureLinearSearch(phoneBook)

    println()

    measureBubbleSortAndJumpSearch(phoneBook, linearSearchTime.timeSpent)
}

private fun measureLinearSearch(phoneBook: PhoneBook): TimedResult<Int> {
    println("Start searching (linear search)...")
    return measureTime { phoneBook.linearSearch() }
            .apply {
                println("Found 500 / 500 entries. Time taken: ${timeSpent.toTimePeriod()}")
            }
}

private fun measureBubbleSortAndJumpSearch(phoneBook: PhoneBook, linearSearchTime: Long) {
    println("Start searching (bubble sort + jump search)...")

    val result = timeLimited(timeLimit = 1) { cancellationToken ->
        val bubbleSortResult = BubbleSortAndJumpSearchTime.BubbleSort(
                measureTime { phoneBook.bubbleSort(cancellationToken) }
        )
        if (bubbleSortResult.isSuccess) {
            val jumpSearch = measureTime { phoneBook.jumpSearch(cancellationToken) }
            BubbleSortAndJumpSearchTime.JumpSearch(bubbleSortResult, jumpSearch)
        } else {
            bubbleSortResult
        }
    }.let { bubbleSortAndJumpSearch ->
        if (bubbleSortAndJumpSearch.isSuccess) {
            bubbleSortAndJumpSearch
        } else {
            BubbleSortAndJumpSearchTime.LinearSearch(bubbleSortAndJumpSearch, measureTime { phoneBook.linearSearch() })
        }
    }

    println("Found 500 / 500 entries. Time taken: ${result.timeSpent.toTimePeriod()}")
    (result as? BubbleSortAndJumpSearchTime.JumpSearch)?.let { jumpSearch ->
        println("Sorting time: ${jumpSearch.bubbleSort.timeSpent.toTimePeriod()}")
        println("Searching time: ${jumpSearch.jumpSearch.timeSpent.toTimePeriod()}")
    } ?: run {
        val linearSearch = result as BubbleSortAndJumpSearchTime.LinearSearch
        println("Sorting time: ${linearSearch.jumpSearch.timeSpent.toTimePeriod()} - STOPPED, moved to linear search")
        println("Searching time: ${linearSearch.linearSearch.timeSpent.toTimePeriod()}")
    }
}

class TimeoutException(val currentTimeMillis: Long) : RuntimeException()

fun <T : Any> timeLimited(timeLimit: Long, action: (cancellationToken: () -> Unit) -> T): T {
    val start = System.currentTimeMillis()
    val cancellationToken: () -> Unit = {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - start > timeLimit)
            throw TimeoutException(currentTimeMillis)
    }

    return action(cancellationToken)
}

fun <T : Any> measureTime(block: () -> T): TimedResult<T> {
    val start = System.currentTimeMillis()
    return try {
        val result = block()
        val end = System.currentTimeMillis()

        TimedResult.Success(start, end, result)
    } catch (ex: TimeoutException) {
        TimedResult.Timeout(start, ex.currentTimeMillis)
    }
}

private fun LongRange.toTimePeriod(): String = (last - first).toTimePeriod()

private fun Long.toTimePeriod(): String {
    return buildString {
        var millis = this@toTimePeriod
        val minutes = millis / 1000 / 60
        /*if (minutes > 0) */append("$minutes min. ")
        millis -= minutes * 1000 * 60

        val seconds = millis / 1000
        /*if (seconds > 0) */append("$seconds sec. ")
        millis -= seconds * 1000

        val intRange = 0..1
        append("$millis ms.")
    }
}