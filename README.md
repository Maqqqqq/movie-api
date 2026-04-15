# Movie-API

A simple Movie-API project to store movies, genres, actros. Genres, movies and actors can be connected.

# Setup and Installation Instructions

### 1. **Clone the Repository**
   ```
    git clone https://gitea.kood.tech/markusstamm/kmdb.git 
   ```

### 2. **Running Application**
   ```
    mvn spring-boot:run
   ```
## Usage Guide
Included Postman Collection with everything for ease of use.

```
Open Postman

Press CTRL + O

Choose included Postman collection (Movie Database API.postman_collection)
```

- ### **Create a new Genre**

**Endpoint:** POST [/api/genres](http://localhost:8080/api/genres)

**Request Body(JSON):**
   ```
    {
      "name": "Action"
    }
   ```
- ### **Retrieve a Genre by ID**<br>
    **Endpoint:** GET [/api/genres/{id} (1-10)](http://localhost:8080/api/genres)<br>

- ### **Retrieve all Genres**<br>
**Endpoint:** GET [/api/genres](http://localhost:8080/api/genres)<br>

- ### **Update a Genre**
**Endpoint:** PATCH [api/genres/{id}](http://localhost:8080/api/genres/1)<br>

- ### **Delete a Genre**

**Endpoint:** DELETE [/api/genres/{id}](http://localhost:8080/api/genres/1)<br>
**Response:**
  ```
   400 Bad Request - Genre has connections
   204 No Content - Deletion was successful
  ```
**Force Delete:**
  ```
  Use Force Delete to delete regardless of connections
  ```
**Endpoint:** DELETE [/api/genres/{id}](http://localhost:8080/api/genres/1?force=true)
  ```
    Response: 204 No Content
  ```
### **Movie API**
- ### **Create new Movie**
**Endpoint:** POST [/api/movies](http://localhost:8080/api/movies)<br>

**Request Body(JSON):**
```
{
    "title": "Dungeons & Dragons: Honour Among Thieves",
    "releaseYear": 2023,
    "duration": 134,
    "actorIds": [],
    "genreIds": [1]
}
```
- ### **Retrieve all Movies**
- **Endpoint:** GET [/api/movies](http://localhost:8080/api/movies)<br>

- ### **Retrieve a Movie by ID**
- **Endpoint:** GET [/api/movies/{id}](http://localhost:8080/api/movies/4)<br>

- ### **Retrieve Movies by Genre**
- **Endpoint:** GET [/api/movies?genre={genreId}](http://localhost:8080/api/movies?genre=4)<br>

### **Actor API**
- ### **Create a new Actor**
- **Endpoint:** [POST /api/actors](http://localhost:8080/api/actors)<br>
```
    {
        "name": "John Doe",
        "birthDate": "2000-11-01"
    }
```