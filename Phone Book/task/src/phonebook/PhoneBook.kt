package phonebook

import java.io.File
import java.util.*

class PhoneBook(
        val directoryFilePath: String,
        val findFilePath: String
) {
    private lateinit var findItems: List<String>
    private lateinit var directoryLines: MutableList<String>
    private lateinit var unsortedEntries: List<Entry>
    private lateinit var entriesBubbleSorted: List<Entry>

    val entriesCount: Int get() = findItems.size

    fun load() {
        val directoryFile = File(directoryFilePath)
        val findFile = File(findFilePath)
        directoryLines = directoryFile.readLines().toMutableList()
        findItems = findFile.readLines()
        unsortedEntries = getDirectoryEntries().toList()
    }

    fun linearSearch(): Int {
        val foundCount = findItems.count { findItem ->
            unsortedEntries.any { findItem == it.name }
        }
        return foundCount
    }

    fun bubbleSort(cancellationToken: () -> Unit) {
        val sorted = unsortedEntries.toMutableList()
        val swap = { i1: Int, i2: Int ->
            val tmp = sorted[i1]
            sorted[i1] = sorted[i2]
            sorted[i2] = tmp
        }
        for (i in sorted.size - 1 downTo 1) {
            for (j in 1..i) {
                if (sorted[j - 1].name > sorted[j].name) {
                    swap(j - 1, j)
                    cancellationToken()
                }
            }
        }
        entriesBubbleSorted = sorted
    }

    fun jumpSearch(cancellationToken: () -> Unit): Int {
        cancellationToken()
        val n = entriesBubbleSorted.size
        val ns = Math.sqrt(n.toDouble()).toInt()
        val lastSegmentSize = n - ns * ns

        var foundCount = 0

        findItems.forEach { findItem ->
            var hasFound = false
            var needFindInLastSegment = false
            for (i in 0 until ns) {
                val nsPosition = i * ns
                val entry = entriesBubbleSorted[nsPosition]
                if (entry.name == findItem) {
                    hasFound = true
                } else if (entry.name > findItem) {
                    for (j in nsPosition - 1 downTo nsPosition - ns) {
                        if (entriesBubbleSorted[j].name == findItem) {
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
                    if (entriesBubbleSorted[i].name == findItem) {
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