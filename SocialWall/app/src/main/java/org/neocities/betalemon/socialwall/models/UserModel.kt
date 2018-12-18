package org.neocities.betalemon.socialwall.models

data class UserModel(
        var userId: String? = null,
        var username: String? = null,
        var email: String? = null,
        var avatarUrl: String? = null
)