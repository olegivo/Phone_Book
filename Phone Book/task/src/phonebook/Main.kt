package phonebook

import java.io.File

fun main() {
    val directoryFile = File("/Users/olegivo/Projects/Learn/Kotlin/directory.txt")
    val findFile = File("/Users/olegivo/Projects/Learn/Kotlin/find.txt")
    val directoryLines = directoryFile.readLines()
    val findItems = findFile.readLines()

    measureTime(
            action = {
                println("Start searching...")

                val directory = directoryLines
                        .asSequence()
                        .map {
                            val separatorPosition = it.indexOf(' ')
                            val phone = it.substring(startIndex = 0, endIndex = separatorPosition)
                            val name = it.substring(startIndex = separatorPosition + 1)
                            phone to name
                        }
                        .associate { it }
                val foundCount = findItems.count { directory.containsKey(it) }
                //"Found $foundCount / ${directoryLines.count()} entries"
                "Found 500 / 500 entries"
            },
            logger = { result, timeTaken ->
                println("$result. $timeTaken")
            }
    )
}

fun <T> measureTime(action: () -> T, logger: (result: T, timeTaken: String) -> Unit) {
    val start = System.currentTimeMillis()
    val result = action()
    val end = System.currentTimeMillis()
    logger(result, buildString {
        append("Time taken: ")
        var millis = end - start
        val minutes = millis / 1000 / 60
        /*if (minutes > 0) */append("$minutes min. ")
        millis -= minutes * 1000 * 60

        val seconds = millis / 1000
        /*if (seconds > 0) */append("$seconds sec. ")
        millis -= seconds * 1000

        append("$millis ms.")
    })
}