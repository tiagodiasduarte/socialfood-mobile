package pt.socialfood.presentation.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.elementDescriptors
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@OptIn(ExperimentalSerializationApi::class)
class NavKeySerializationTest {

    private val serializersModule = serializersConfig.serializersModule
    private val json = Json { serializersModule = this@NavKeySerializationTest.serializersModule }
    private val serializer = PolymorphicSerializer(NavKey::class)

    @Test
    fun `given every feature route interface when its routes are looked up as NavKey then each one resolves`() {
        // Given
        val routeNames = featureRouteSerializers.flatMap { sealedSerializer ->
            val descriptor = sealedSerializer.descriptor
            assertEquals(PolymorphicKind.SEALED, descriptor.kind, "${descriptor.serialName} isn't sealed")
            // A sealed descriptor's second element ("value") lists one descriptor per subclass.
            descriptor.getElementDescriptor(1).elementDescriptors.map { it.serialName }
        }

        // When
        val unresolved = routeNames.filter { name ->
            serializersModule.getPolymorphic(NavKey::class, serializedClassName = name) == null
        }

        // Then
        assertTrue(routeNames.isNotEmpty())
        assertEquals(emptyList(), unresolved, "Routes missing from serializersConfig")
    }

    @Test
    fun `given the top level destinations when encoded and decoded as NavKey then every tab is restored`() {
        // Given
        val topLevelRoutes = TOP_LEVEL_DESTINATIONS.keys.toList()

        // When
        val restored = topLevelRoutes.map { route ->
            json.decodeFromString(serializer, json.encodeToString(serializer, route))
        }

        // Then
        assertEquals(topLevelRoutes, restored)
    }
}
