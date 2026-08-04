package pt.socialfood.random

import pt.socialfood.domain.model.PresignedUrlData

fun randomPresignedUrlData(uploadUrl: String = randomUrl(), publicUrl: String = randomUrl()) =
    PresignedUrlData(uploadUrl = uploadUrl, publicUrl = publicUrl)
