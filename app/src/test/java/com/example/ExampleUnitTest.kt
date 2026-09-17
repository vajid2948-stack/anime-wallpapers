package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun wallpapers_containsIconicAnime() {
    val wallpapers = com.example.data.WallpaperData.wallpapers
    assertTrue(wallpapers.any { it.character.contains("Naruto", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Gojo", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Luffy", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Itachi", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Tanjiro", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Sukuna", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Kakashi", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Madara", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Zoro", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Goku", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Jinwoo", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Ichigo", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Rengoku", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Minato", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Vegeta", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Sanji", ignoreCase = true) })
    assertTrue(wallpapers.any { it.character.contains("Megumi", ignoreCase = true) })
  }
}
