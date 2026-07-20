package pt.socialfood.domain.use_case.restaurant

/**
 * Shared tuning for polling a restaurant until the backend finishes async enrichment
 * (see APPS-16). Used by both the search/add flow and the restaurant detail screen.
 */
object RestaurantEnrichmentPolling {
    const val POLL_INTERVAL_MS = 2_000L
    const val MAX_POLL_ATTEMPTS = 10
}
