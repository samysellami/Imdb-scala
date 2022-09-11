package lunatech.models

final case class Title( titleType: String,
                        primaryTitle: String,
                        originalTitle: String,
                        startYear: Int,
                        endYear: Int,
                        runtimeMinutes: Int,
                        genres: String)
