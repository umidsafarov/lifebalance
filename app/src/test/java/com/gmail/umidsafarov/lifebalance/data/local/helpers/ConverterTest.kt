package com.gmail.umidsafarov.lifebalance.data.local.helpers

import com.gmail.umidsafarov.lifebalance.rules.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ConverterTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var converter: Converter

    @Before
    fun setup() = runTest {
        converter = Converter()
    }


    @Test
    fun `convert - when list is not empty - encode and decode successfully`() = runTest {
        // Arrange
        val list = listOf("item1", "item2", "item3")

        // Act
        val result = converter.jsonToList(converter.listToJson(list))

        // Assert
        assertThat(result).hasSize(list.size)
        assertThat(result).isEqualTo(list)
    }


    @Test
    fun `convert - when list empty - encode and decode successfully`() = runTest {
        // Arrange
        val list = listOf<String>()

        // Act
        val result = converter.jsonToList(converter.listToJson(list))

        // Assert
        assertThat(result).hasSize(0)
    }


    @Test
    fun `convert - when list is null - encode and decode successfully`() = runTest {
        // Arrange
        val list: List<String>? = null

        // Act
        val result = converter.jsonToList(converter.listToJson(list))

        // Assert
        assertThat(result).isNull()
    }
}