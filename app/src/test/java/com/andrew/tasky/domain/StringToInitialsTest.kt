package com.andrew.tasky.domain

import com.andrew.tasky.core.StringToInitials
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class StringToInitialsTest {

    @Test
    fun `Test 3 names gives first and last initials`() {
        val name = "John Frank Doe"

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = "JD"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Test 3 lowercase names gives uppercase first and last initials`() {
        val name = "john frank doe"

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = "JD"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Test one lowercase name gives first two letters in uppercase`() {
        val name = "john"

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = "JO"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `One lowercase letter gives one uppercase letter initial`() {
        val name = "j"

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = "J"

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `No input gives empty string and does not crash`() {
        val name = ""

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = ""

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Only whitespace inputted gives empty string`() {
        val name = "     "

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = ""

        assertThat(actual).isEqualTo(expected)
    }

    @Test
    fun `Space in front and end of name trims name and gives initials`() {
        val name = " John Doe "

        val actual = StringToInitials.convertStringToInitials(name)

        val expected = "JD"

        assertThat(actual).isEqualTo(expected)
    }
}
