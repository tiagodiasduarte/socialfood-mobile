package pt.socialfood.random

import pt.socialfood.domain.model.PresignedUrlData
import kotlin.random.Random

fun Random.nextPresignedUrlData(uploadUrl: String = nextUrl(), publicUrl: String = nextUrl()) =
    PresignedUrlData(uploadUrl = uploadUrl, publicUrl = publicUrl)
