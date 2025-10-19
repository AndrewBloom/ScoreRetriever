package com.scoreretriever.domain.model

import org.junit.Assert.*
import org.junit.Test

/**
 * Unit tests for Score domain model.
 *
 * Tests cover:
 * - Valid score creation
 * - Validation rules (require statements)
 * - Percentage calculation
 * - isValid() method
 * - Edge cases
 */
class ScoreTest {

    @Test
    fun `create score with valid values succeeds`() {
        // Given
        val score = 514
        val maxScore = 700

        // When
        val result = Score(score = score, maxScore = maxScore)

        // Then
        assertEquals(514, result.score)
        assertEquals(700, result.maxScore)
    }

    @Test
    fun `percentage is calculated correctly`() {
        // Given
        val score = Score(score = 514, maxScore = 700)

        // When
        val percentage = score.percentage

        // Then
        assertEquals(0.734f, percentage, 0.001f)
    }

    @Test
    fun `percentage with max score returns 1`() {
        // Given
        val score = Score(score = 700, maxScore = 700)

        // When
        val percentage = score.percentage

        // Then
        assertEquals(1.0f, percentage, 0.001f)
    }

    @Test
    fun `percentage with zero score returns 0`() {
        // Given
        val score = Score(score = 0, maxScore = 700)

        // When
        val percentage = score.percentage

        // Then
        assertEquals(0.0f, percentage, 0.001f)
    }

    @Test
    fun `isValid returns true for valid score`() {
        // Given
        val score = Score(score = 514, maxScore = 700)

        // When
        val isValid = score.isValid()

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `isValid returns true when score equals maxScore`() {
        // Given
        val score = Score(score = 700, maxScore = 700)

        // When
        val isValid = score.isValid()

        // Then
        assertTrue(isValid)
    }

    @Test
    fun `isValid returns true when score is zero`() {
        // Given
        val score = Score(score = 0, maxScore = 700)

        // When
        val isValid = score.isValid()

        // Then
        assertTrue(isValid)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create score with negative maxScore throws exception`() {
        // When/Then
        Score(score = 100, maxScore = -1)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create score with zero maxScore throws exception`() {
        // When/Then
        Score(score = 100, maxScore = 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create score with negative score throws exception`() {
        // When/Then
        Score(score = -1, maxScore = 700)
    }

    @Test(expected = IllegalArgumentException::class)
    fun `create score exceeding maxScore throws exception`() {
        // When/Then
        Score(score = 701, maxScore = 700)
    }

    @Test
    fun `minimum valid score is 0 out of 1`() {
        // When
        val score = Score(score = 0, maxScore = 1)

        // Then
        assertEquals(0, score.score)
        assertEquals(1, score.maxScore)
        assertEquals(0.0f, score.percentage, 0.001f)
        assertTrue(score.isValid())
    }

    @Test
    fun `data class equality works correctly`() {
        // Given
        val score1 = Score(score = 514, maxScore = 700)
        val score2 = Score(score = 514, maxScore = 700)
        val score3 = Score(score = 500, maxScore = 700)

        // Then
        assertEquals(score1, score2)
        assertNotEquals(score1, score3)
    }

    @Test
    fun `data class copy works correctly`() {
        // Given
        val original = Score(score = 514, maxScore = 700)

        // When
        val copy = original.copy(score = 600)

        // Then
        assertEquals(600, copy.score)
        assertEquals(700, copy.maxScore)
        assertEquals(514, original.score) // Original unchanged
    }

    @Test
    fun `toString returns meaningful output`() {
        // Given
        val score = Score(score = 514, maxScore = 700)

        // When
        val string = score.toString()

        // Then
        assertTrue(string.contains("514"))
        assertTrue(string.contains("700"))
    }
}
