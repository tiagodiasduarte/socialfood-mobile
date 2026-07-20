package pt.socialfood.domain.model

/**
 * Shared tuning for polling a restaurant until the backend finishes async enrichment
 * (see APPS-16). Lives in the repository layer (RestaurantsRepositoryImpl), the only
 * place with access to the raw "still enriching" signal from the network response.
 */
object RestaurantEnrichmentPolling {
    const val POLL_INTERVAL_MS = 2_000L
    const val MAX_POLL_ATTEMPTS = 10
}
