package pt.socialfood.core

import kotlin.native.Platform

actual val isDebugBuild: Boolean = Platform.isDebugBinary
