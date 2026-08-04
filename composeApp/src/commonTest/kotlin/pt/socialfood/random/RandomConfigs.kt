package pt.socialfood.random

import pt.socialfood.domain.model.Configs

fun randomConfigs(version: String = "${randomInt(1, 9)}.${randomInt(0, 9)}.${randomInt(0, 9)}") =
    Configs(version = version)
