package lunatech.models

final case class Title( titleType: String,
                        primaryTitle: String,
                        originalTitle: String,
                        isAdult: Boolean,
                        startYear: Integer,
                        endYear: Integer,
                        runtimeMinutes: Integer,
                        genres: String)
