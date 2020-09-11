package phonebook

import java.io.File

class PhoneBook(
        val directoryFilePath: String,
        val findFilePath: String
) {
    private lateinit var findItems: List<String>
    private lateinit var directoryLines: MutableList<String>
    private lateinit var entries: MutableList<Entry>

    fun load() {
        val directoryFile = File(directoryFilePath)
        val findFile = File(findFilePath)
        directoryLines = directoryFile.readLines().toMutableList()
        findItems = findFile.readLines()
        entries = getDirectoryEntries().toMutableList()
    }

    fun linearSearch(): Int {
        val directory = getDirectoryEntries()
                .map { it.phone to it.name }
                .associate { it }
        val foundCount = findItems.count { directory.containsKey(it) }
        //"Found $foundCount / ${directoryLines.count()} entries"
        return foundCount
    }

    fun bubbleSort(cancellationToken: () -> Unit) {
        val swap = { i1: Int, i2: Int ->
            val tmp = entries[i1]
            entries[i1] = entries[i2]
            entries[i2] = tmp
        }
        for (i in entries.size - 1 downTo 1) {
            for (j in 1..i) {
                if (entries[j - 1].name > entries[j].name) {
                    swap(j - 1, j)
                    cancellationToken()
                }
            }
        }
    }

    fun jumpSearch(cancellationToken: () -> Unit): Int {
        cancellationToken()
        val n = entries.size
        val ns = Math.sqrt(n.toDouble()).toInt()
        val lastSegmentSize = n - ns * ns

        var foundCount = 0

        findItems.forEach { findItem ->
            var hasFound = false
            var needFindInLastSegment = false
            for (i in 0 until ns) {
                val nsPosition = i * ns
                val entry = entries[nsPosition]
                if (entry.name == findItem) {
                    hasFound = true
                } else if (entry.name > findItem) {
                    for (j in nsPosition - 1 downTo nsPosition - ns) {
                        if (entries[j].name == findItem) {
                            hasFound = true
                            break
                        }
                        cancellationToken()
                    }
                } else {
                    break
                }
                if (hasFound) {
                    foundCount++
                    break
                } else if (i == ns) {
                    needFindInLastSegment = true
                }
                cancellationToken()
            }
            if (needFindInLastSegment) {
                for (i in n - lastSegmentSize until n) {
                    if (entries[i].name == findItem) {
                        hasFound = true
                        break
                    }
                    cancellationToken()
                }
            }
            if (hasFound) {
                foundCount++
            }
        }

        return foundCount
    }

    private fun getDirectoryEntries(): Sequence<Entry> {
        return directoryLines
                .asSequence()
                .map {
                    val separatorPosition = it.indexOf(' ')
                    val phone = it.substring(startIndex = 0, endIndex = separatorPosition)
                    val name = it.substring(startIndex = separatorPosition + 1)
                    Entry(phone = phone, name = name)
                }
    }

    data class Entry(val phone: String, val name: String)
}