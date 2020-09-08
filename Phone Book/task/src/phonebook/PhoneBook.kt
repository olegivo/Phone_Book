package phonebook

import java.io.File

class PhoneBook(
        val directoryFilePath: String,
        val findFilePath: String
) {
    private lateinit var findItems: List<String>
    private lateinit var directoryLines: List<String>

    fun load() {
        val directoryFile = File(directoryFilePath)
        val findFile = File(findFilePath)
        directoryLines = directoryFile.readLines()
        findItems = findFile.readLines()
    }

    fun linearSearch() {
        val directory = directoryLines
                .asSequence()
                .map {
                    val separatorPosition = it.indexOf(' ')
                    val phone = it.substring(startIndex = 0, endIndex = separatorPosition)
                    val name = it.substring(startIndex = separatorPosition + 1)
                    phone to name
                }
                .associate { it }
//        val foundCount = findItems.count { directory.containsKey(it) }
        //"Found $foundCount / ${directoryLines.count()} entries"
    }

}