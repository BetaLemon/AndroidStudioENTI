package org.neocities.betalemon.socialwall.models

import java.util.*

data class MessageModel(
    var username: String? = null,
    var text: String? = null,
    var createdAt: Date? = null,
    var likes: Int? = null,
    var userId: Int? = null
)