package org.neocities.betalemon.socialwall.models

import java.util.*

data class NewsModel(
        var author: String? = null,
        var description: String? = null,
        var createdAt: Date? = null,
        var imageUrl: String? = null,
        var title: String? = null
)

data class NewsList(
        var news: ArrayList<NewsModel>?
)