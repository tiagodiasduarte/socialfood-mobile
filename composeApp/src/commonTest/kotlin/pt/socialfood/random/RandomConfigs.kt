package pt.socialfood.random

import pt.socialfood.domain.model.Configs
import kotlin.random.Random

fun Random.nextConfigs(version: String = "${nextInt(1, 9)}.${nextInt(0, 9)}.${nextInt(0, 9)}") =
    Configs(version = version)
