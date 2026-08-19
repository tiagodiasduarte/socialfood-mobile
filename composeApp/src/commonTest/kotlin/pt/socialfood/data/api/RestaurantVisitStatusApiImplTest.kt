package pt.socialfood.data.api

import pt.socialfood.domain.model.VisitStatus
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantVisitStatusApiImplTest {
    @Test
    fun `given WISHLIST when pathSegment is read then returns wishlist`() {
        // Given
        val status = VisitStatus.WISHLIST

        // When
        val result = status.pathSegment

        // Then
        assertEquals("wishlist", result)
    }

    @Test
    fun `given VISITED when pathSegment is read then returns visited`() {
        // Given
        val status = VisitStatus.VISITED

        // When
        val result = status.pathSegment

        // Then
        assertEquals("visited", result)
    }
}
