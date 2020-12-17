package de.skyrising.aoc2020

import java.nio.ByteBuffer

class BenchmarkDay5 : BenchmarkDay(5)

fun seatId(s: String): Int {
    val chars = s.toCharArray()
    var id = 0
    for (i in 0..9) {
        id += if (chars[i] == 'F' || chars[i] == 'L') 0 else 1 shl (9 - i)
    }
    return id
}


fun seatId(b: ByteBuffer, offset: Int): Int {
    val first = (b.getLong(offset) xor 0x4646464646464652L) and 0x404040404040404L
    val second = (b.getShort(offset + 8).toInt() xor 0x5252) and 0x40404
    //println("${first.toString(16)}, ${second.toString(16)}")
    val firstBits = ((first ushr 51) or (first ushr 44) or (first ushr 37) or (first ushr 30) or (first ushr 23) or (first ushr 16) or (first ushr 9) or (first ushr 2)) and 0xff
    val secondBits = ((second ushr 9) or (second ushr 2)) and 3
    //println("${firstBits.toString(2)}, ${secondBits.toString(2)}")
    return ((firstBits shl 2).toInt() or secondBits) xor 7
}

/*
fun seatIdVector(b: ByteBuffer, offset: Int): Int {
    val vecFFFFFFFRRR = ByteVector.fromArray(ByteVector.SPECIES_128, byteArrayOf(
        0, 0, 0, 0,
        0, 0, 'R'.toByte(), 'R'.toByte(),
        'R'.toByte(), 'F'.toByte(), 'F'.toByte(), 'F'.toByte(),
        'F'.toByte(), 'F'.toByte(), 'F'.toByte(), 'F'.toByte()
    ), 0)
    val vecShuffle = VectorShuffle.iota(ByteVector.SPECIES_128, 15, -1, false)
    val vec = if (offset <= b.remaining() - 16) {
        ByteVector.fromByteBuffer(ByteVector.SPECIES_128, b, offset, ByteOrder.nativeOrder())
    } else {
        val arr = ByteArray(16)
        b.get(offset, arr, 0, 11)
        ByteVector.fromArray(ByteVector.SPECIES_128, arr, 0)
    }
    //println(vec.toArray()!!.contentToString())
    return ((vec.rearrange(vecShuffle).lt(vecFFFFFFFRRR).toLong() shr 6).toInt() xor 7) and 0x3ff
}
*/

fun registerDay5() {
    //println(seatId(ByteBuffer.wrap("BFFFBBFRRR".toByteArray()), 0))
    puzzleLS(5, "Binary Boarding v1") {
        var highest = 0
        for (line in it) {
            highest = maxOf(highest, seatId(line))
        }
        highest
    }
    puzzleB(5, "Binary Boarding v2") {
        var highest = 0
        for (i in 0 until it.remaining() step 11) {
            highest = maxOf(highest, seatId(it, i))
        }
        highest
    }
    /*
    puzzleB(5, "Binary Boarding v3") {
        var highest = 0
        for (i in 0 until it.remaining() step 11) {
            highest = maxOf(highest, seatIdVector(it, i))
        }
        highest
    }
    */
    puzzleLS(5, "Part Two v1") {
        var highest = 0
        var lowest = 1 shl 10
        val seats = LongArray(lowest shr 6)
        for (line in it) {
            val id = seatId(line)
            lowest = minOf(lowest, id)
            highest = maxOf(highest, id)
            setBit(seats, id)
        }
        for (i in lowest + 1 until highest) {
            if (!isBitSet(seats, i)) return@puzzleLS i
        }
        -1
    }
    puzzleB(5, "Part Two v2") {
        var highest = 0
        var lowest = 1 shl 10
        val seats = LongArray(lowest shr 6)
        for (i in 0 until it.remaining() step 11) {
            val id = seatId(it, i)
            lowest = minOf(lowest, id)
            highest = maxOf(highest, id)
            setBit(seats, id)
        }
        for (i in lowest + 1 until highest) {
            if (!isBitSet(seats, i)) return@puzzleB i
        }
        -1
    }
    /*
    puzzleB(5, "Part Two v3") {
        var highest = 0
        var lowest = 1 shl 10
        val seats = LongArray(lowest shr 6)
        for (i in 0 until it.remaining() step 11) {
            val id = seatIdVector(it, i)
            lowest = minOf(lowest, id)
            highest = maxOf(highest, id)
            setBit(seats, id)
        }
        for (i in lowest + 1 until highest) {
            if (!isBitSet(seats, i)) return@puzzleB i
        }
        -1
    }
    */
}