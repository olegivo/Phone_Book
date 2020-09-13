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
    private lateinit var entriesQuickSorted: List<Entry>

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

    fun quickSort() {
//        repeat(100) { testQuickSort() }
        val list = unsortedEntries.toMutableList()
        quickSort(list, 0, list.size - 1)
        entriesQuickSorted = list
    }

/*
    private fun testQuickSort() {
        val list = (1..10).map { Random.nextInt(0, 9) }
        val qsList = list.toMutableList()
        val sb = StringBuilder()
        quickSort(qsList, 0, list.size - 1, sb)
        val sorted = list.sorted()
        sorted.indices.forEach {
            assert(sorted[it] == qsList[it]) { "${sorted.toList()} != ${qsList.toList()}\n\n$sb" }
        }
    }
*/

    private fun <T : Comparable<T>> quickSort(list: MutableList<T>, startPos: Int, endPos: Int/*, sb: StringBuilder? = null*/) {
//        sb?.appendln("\nquick sort $startPos..$endPos ($list)")
        if (endPos - startPos < 1) return
//        sb?.appendln("sorting items: ${list.subList(startPos, endPos + 1)}")
        val pivot = list[endPos]
//        sb?.appendln("pivot = $pivot")
        val less = LinkedList<T>()
        val greater = LinkedList<T>()
        val equal = LinkedList<T>().apply { add(pivot) }
        (startPos until endPos).forEach {
            val entry = list[it]
            when {
                entry < pivot -> less
                entry > pivot -> greater
                else -> equal
            }.add(entry)
        }
//        sb?.appendln("less: $less")
//        sb?.appendln("equal: $equal")
//        sb?.appendln("greater: $greater")
        less.forEachIndexed { index, entry -> list[startPos + index] = entry }
        val equalPos = startPos + less.size
        equal.forEachIndexed { index, entry -> list[equalPos + index] = entry }
        val greaterPos = equalPos + equal.size
        greater.forEachIndexed { index, entry -> list[greaterPos + index] = entry }
//        sb?.appendln("sorted items: ${list.subList(startPos, endPos + 1)}")
        quickSort(list, startPos, equalPos - 1/*, sb*/)
        quickSort(list, greaterPos, endPos/*, sb*/)
    }

    fun binarySearch(): Int {
//        repeat(100) { testBinarySearch() }
        return findItems.count { findItem ->
            binarySearch(entriesQuickSorted, Entry("", findItem), 0, entriesQuickSorted.size - 1)
        }
    }

//    private fun testBinarySearch() {
//        val list = (1..10).map { Random.nextInt(1, 50) }.sorted()
//        val find = Random.nextInt(1, 100)
//        assert(binarySearch(list, find, 0, list.lastIndex, StringBuilder()) == list.contains(find))
//    }

    private fun <T : Comparable<T>> binarySearch(list: List<T>, findItem: T, startPos: Int, endPos: Int, sb: StringBuilder? = null): Boolean {
        sb?.appendln("\n binarySearch $findItem in $list ($startPos..$endPos)")
        if (startPos > endPos) {
            sb?.appendln("not found")
            return false
        }
        val m = startPos + (endPos - startPos) / 2
        return when {
            list[m] == findItem -> true
            findItem < list[m] -> {
                sb?.appendln("sought item less than ${list[m]}, search to the left")
                binarySearch(list, findItem, startPos, m - 1, sb)
            }
            else -> {
                sb?.appendln("sought sought greater than ${list[m]}, search to the right")
                binarySearch(list, findItem, m + 1, endPos, sb)
            }
        }
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

    data class Entry(val phone: String, val name: String) : Comparable<Entry> {
        override fun compareTo(other: Entry): Int {
            return this.name.compareTo(other.name)
        }

        override fun equals(other: Any?): Boolean {
            return (other as? Entry)?.hashCode() == hashCode()
        }

        override fun hashCode(): Int {
            return name.hashCode()
        }
    }
}