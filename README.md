# Lunatech IMDb Assessment 

## Details:
This project is based on the popular website [IMDb](https://www.imdb.com/)
which offers movie and TV show information. Its consist of a a web application 
that can present the user with different informations depending on the entered endpoint

## Requirement:
* sbt
* Docker

### Setup:
Run a [PostgreSQL](https://www.postgresql.org/) instance with all the data loaded
```
docker-compose up
```
Launch the application with the following commands
```
cd lunatech-imdb-assessment/
sbt ~reStart
```
## Getting started:

### Feature #1:

IMDb copycat: An endpoint that allow the user to search by
movie’s primary title or original title. The outcome is related
information to that title, including cast and crew.

In your favorite browser, go the following URL
```
http://localhost:8080/title/<title_name>        where <title_name> is the name of the title, for example:
http://localhost:8080/title/The Godfather
```
You will be be presented with a json output containing related informations about the title 
(it is better to copy the result in a json formatter (vscode for instance), to see clearly the informations):
```json
{
    "informations": [
        {
            "cast": [
                {
                    "category": "actor",
                    "characters": "['Michael Corleone']",
                    "job": "",
                    "name": {
                        "birthYear": 1940,
                        "deathYear": 0,
                        "knownForTitles": "tt0072890,tt0070666,tt0099422,tt0068646",
                        "primaryName": "Al Pacino",
                        "primaryProfession": "actor,producer,soundtrack"
                    }
                },
                ....
            ],
            "crew": [{ "directors": "nm0000338", "writers": "nm0701374,nm0000338" }],
            "title": {
                "endYear": 0,
                "genres": "Crime,Drama",
                "originalTitle": "The Godfather",
                "primaryTitle": "The Godfather",
                "runtimeMinutes": 175,
                "startYear": 1972,
                "titleType": "movie"
            }
        }
    ]
}
```

### Feature #2:

Top rated movies: An endpoint that present the top rated movies for a genre 
(for example horror, which shows a list of top rated horror movies).

In your favorite browser, go the following URL
```
http://localhost:8080/title/toprated/<genre>        where <genre> is the genre of the movies, for example:
http://localhost:8080/title/torated/Comedy
```
You will be be presented with a json output containing the list of the top 10 rated movies of the genre entered
(again, it is better to use a json formatter), the output consist of the movies's primary title as well as the 
corresponding rating:
```json
{
    "movies": [
        { "primaryTittle": "An Intervention", "rating": { "averageRating": 9.9, "numVotes": 18 } },
        { "primaryTittle": "The Last Regret", "rating": { "averageRating": 9.8, "numVotes": 871 } },
        {
            "primaryTittle": "Vaarthakal Ithuvare",
            "rating": { "averageRating": 9.6, "numVotes": 477 }
        }, 
        ....
    ]
}
```

### Feature #3:

[Six degrees of KevinBacon](https://en.wikipedia.org/wiki/Six_Degrees_of_Kevin_Bacon): 
An endpoint that provide what’s the degree of separation between the person
(e.g. actor or actress) the user has entered and Kevin Bacon. 

In your favorite browser, go the following URL
```
http://localhost:8080/title/sixdegrees/<person's name>        where <person's name> is the name of the person, for example:
http://localhost:8080/title/sixdegrees/Al Pacino
```
You will be be presented with a number representing the degree of separation, as well as a sequence of strings
reflecing the connections between the persons:
```txt
The degree of separation is : 2 (Al Pacino -> James Caan -> Kevin Bacon)
```