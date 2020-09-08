package phonebook

fun main() {
    val phoneBook = PhoneBook(
            directoryFilePath = "/Users/olegivo/Projects/Learn/Kotlin/directory.txt",
            findFilePath = "/Users/olegivo/Projects/Learn/Kotlin/find.txt"
    )
    phoneBook.load()

    measureTime(
            action = {
                println("Start searching...")
                phoneBook.linearSearch()
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