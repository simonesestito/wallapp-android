package it.simonesestito.wallapp.model

data class Category(
        val id: String,
        val displayName: String,
        val description: String
) {
    constructor(id: String, map: Map<String, Any>) : this(
            id = id,
            displayName = map["displayName"].toString(),
            description = map["description"].toString())
}