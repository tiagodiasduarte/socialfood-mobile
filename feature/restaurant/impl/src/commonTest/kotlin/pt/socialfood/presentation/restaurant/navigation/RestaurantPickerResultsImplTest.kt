package pt.socialfood.presentation.restaurant.navigation

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import pt.socialfood.random.nextRestaurant
import pt.socialfood.random.nextString
import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RestaurantPickerResultsImplTest {
    @Test
    fun `given a result published before collecting when results is collected then the result is delivered`() =
        runTest {
            // Given
            val pickerResults = RestaurantPickerResultsImpl()
            val requestKey = Random.nextString()
            val restaurant = Random.nextRestaurant()
            pickerResults.publish(requestKey, restaurant)

            // When / Then
            pickerResults.results(requestKey).test {
                assertEquals(restaurant, awaitItem())
            }
        }

    @Test
    fun `given a collector when a result is published then the result is delivered`() = runTest {
        // Given
        val pickerResults = RestaurantPickerResultsImpl()
        val requestKey = Random.nextString()
        val restaurant = Random.nextRestaurant()

        pickerResults.results(requestKey).test {
            // When
            pickerResults.publish(requestKey, restaurant)

            // Then
            assertEquals(restaurant, awaitItem())
        }
    }

    @Test
    fun `given a result for another key when results is collected then nothing is delivered`() = runTest {
        // Given
        val pickerResults = RestaurantPickerResultsImpl()
        pickerResults.publish(Random.nextString(), Random.nextRestaurant())

        // When / Then
        pickerResults.results(Random.nextString()).test {
            expectNoEvents()
        }
    }

    @Test
    fun `given a delivered result when results is collected again then it is not delivered twice`() = runTest {
        // Given
        val pickerResults = RestaurantPickerResultsImpl()
        val requestKey = Random.nextString()
        pickerResults.publish(requestKey, Random.nextRestaurant())
        pickerResults.results(requestKey).test { awaitItem() }

        // When / Then
        pickerResults.results(requestKey).test {
            expectNoEvents()
        }
    }
}
