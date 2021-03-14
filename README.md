# Library API

## Books API-Rotas

### POST
/api/books
```
Content: {
    "title": "string",
    "autor": "string",
    "isbn": "string"
}

Response: CREATED(201)
```

### PUT
/api/books/id
```
Content: {
    "title": "string",
    "autor": "string",
    "isbn": "string"
}

Response: OK(200)
```

### DELETE
/api/books/id
```
Response: No Content(204)
```

### GET
/api/books/id
```
Response: OK(200)
```

### GET
/api/books?title=''&author=''&isbn=''
```
Response: OK(200)
```

## Books API-Erros

### BAD REQUEST(400) - POST, PUT E DELETE
```
Content: {
    "errors": [
        "erro"
    ]
}
```

### NOT FOUND(404) - GET
/api/books/id

## Loans API-Rotas

### POST
/api/loans
```
Content: {
    "isbn": "string",
    "customer": "string"
}

Response: CREATED(201)
Error: BadRequest(400) "unavaliable book"
```

### PATCH
/api/loans/ID
```
Content: {
    "returned": "true"
}

Response: OK(200)
Error: Not Found(404) "no loan"
```

### GET
/api/loans?isbn="&customer="
```
Response: OK(200)
```
