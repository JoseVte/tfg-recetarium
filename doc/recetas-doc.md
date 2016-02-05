## Recetas

#### Estructura JSON de las recetas

El formato de entrada de una receta tiene la siguiente estructura:

```json
{
    "id": "int (null si es una nueva receta)",
    "slug": "string",
    "title": "string",
    "steps": "string (nullable)",
    "difficulty": "EASY, MEDIUM, HARD",
    "visibility": "PUBLIC, FRIENDS, PRIVATE",
    "duration": "string (formato hh:mm:ss)",
    "num_persons": "int (nullable)",
    "category": "categoria en JSON (nullable)",
    "ingredients": "array de ingredientes (formato ingrediente)",
    "tags": "array de tag (id) ya existentes",
    "newTags": "array de tag (string) que no existen previamente"
}
```

El formato de salida de una receta tiene la siguiente estructura:

```json
{
    "id": "int",
    "slug": "string",
    "title": "string",
    "steps": "string",
    "difficulty": "EASY, MEDIUM, HARD",
    "visibility": "PUBLIC, FRIENDS, PRIVATE",
    "duration": "string (formato hh:mm)",
    "num_persons": "int",
    "user": "user en JSON",
    "category": "categoria en JSON",
    "ingredients": "array de ingredientes",
    "tags": "array de tag",
    "media": "array de archivos",
    "created_at": "fecha en timestamp",
    "updated_at": "fecha en timestamp"
}
```

#### Paginación

Acepta tanto la página como el tamaño de las lista:

```
GET /recipes(?page=*&size=*)

GET /recipes
GET /recipes?page=2
GET /recipes?size=3
GET /recipes?page=2&size=3
```

También acepta una cadena para buscar:

```
GET /recipes(?search=*)

GET /recipes?search=receta
```

Devuelve una lista de recetas con los links de paginacion:

```json
{
    "data": [
    {
        "id": 4
    },
    {
        "id": 5
    },
    {
        "id": 6
    }
  ],
    "total": 7,
    "link-prev": "/recipes",
    "link-next": "/recipes?page=3",
    "link-self": "/recipes?page=2"
}
```

#### Obtener una receta por slug

A partir de un `slug` (un string único por el que se identifica una receta) se obtiene una receta con esta [estructura](#estructura-json-de-las-recetas):

```
GET /recipes/{slug}

GET /recipes/slug-de-la-receta
```

Devuelve un error `404` si no se encuentra la receta:

```json
{
    "error": "Not found {slug}"
}
```

#### Crear una receta nueva

Para crear una receta se necesita enviar el `JWT` en la cabecera **X-Auth-Token** y el [formato de entrada de la receta](#estructura-json-de-las-recetas):

```
POST /recipes
```
Si el `JWT` es incorrecto se devuelve el código `401`.

Si se crea correctamente devuelve la nueva receta con un código `201`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

```json
{
    "campo-1": [
        "Error 1",
        "Error 2"
    ],
    "campo-2": [
        "Error 3"
    ]
}
```


#### Actualizar una receta ya existente

Para actualizar los datos de una receta se debe enviar el `JWT` en la cabecera **X-Auth-Token** y el [formato de entrada de la receta](#estructura-json-de-las-recetas) añadiendo el `ID`:

```
PUT   /recipes/{id}
PATCH /recipes/{id}

PUT   /recipes/1
PATCH /recipes/1
```

Si el `JWT` es incorrecto o la receta no pertenece al usuario actual (salvo que se trate de un **ADMIN**) se devuelve el código `401`.

Si se actualiza correctamente devuelve la receta con los nuevos datos con un código `200`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

```json
{
    "campo-1": [
        "Error 1",
        "Error 2"
    ],
    "campo-2": [
        "Error 3"
    ]
}
```


#### Borrar una receta

Se borra una receta a partir del `ID`. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**:

```
DELETE /recipes/{id}

DELETE /recipes/1
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

```json
{
    "msg": "Deleted {id}"
}
```

Si el `JWT` es incorrecto se devuelve el código `401`.

Devuelve un error `404` si no se encuentra la receta:

```json
{
    "error": "Not found {id}"
}
```

#### Comprobar slug de una receta

Se comprueba si el slug de una receta existe previamente o no. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**.
Si ademas se le pasa el `ID` de una receta excluye a esta receta de la comprobación. Si ya existe se devuelve un código `400`, sino un código `200`.:

```
HEAD /recipes/{slug}/check
HEAD /recipes/{slug}/check/{id}

HEAD /recipes/slug-de-la-receta/check
HEAD /recipes/slug-de-la-receta/check/1
```

#### Comprobar si una receta es del usuario logueado

Se comprueba si, a partir del slug, una receta pertence al usuario logueado. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**.
Si la receta no existe se devuelve un código `404`, si no pertence al usuario actual un código `401`, y sino se devuelve un código `200`. Si el usuario es **ADMIN** también devuelve como suyas las recetas de otros usuarios:

```
HEAD /recipes/{slug}/mine

HEAD /recipes/slug-de-la-receta/mine
```

### Ingredientes

#### Estructrua JSON de los ingredientes:

El formato de entrada de un ingrediente tiene la siguiente estructura:

```json
{
    "id": "int (null si es un nuevo ingrediente)",
    "name": "string",
    "count": "string (nullable)",
}
```

El formato de salida de una receta tiene la siguiente estructura:

```json
{
    "id": "int",
    "name": "string",
    "count": "string",
    "created_at": "fecha en timestamp",
    "updated_at": "fecha en timestamp"
}
```

#### Añadir un nuevo ingrediente a una receta

Se añade un nuevo ingrediente a una receta existente. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**, el `ID` de la receta y el [formato de entrada de un ingrediente](#estructura-json-de-los-ingredientes).
Si la receta no existe se devuelve un código `404`, si no pertence al usuario actual un código `401`, y sino se devuelve un código `200`. Si el usuario es **ADMIN** se le permite añadir el ingrediente aunque la receta no sea suya:

```
POST /recipes/{id-receta}/ingredient

POST /recipes/1/ingredient
```

También, si ocurre algun error con el formato de los datos se devuelve un error `400`:

```json
{
    "campo-1": [
        "Error 1",
        "Error 2"
    ],
    "campo-2": [
        "Error 3"
    ]
}
```


#### Borrar un ingrediente de una receta

Se borra un ingrediente de una receta existente. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token** y el `ID` de la receta y el ingrediente.
Si la receta no existe se devuelve un código `404`, si no pertence al usuario actual un código `401`, y sino se devuelve un código `200`. Si el usuario es **ADMIN** se le permite añadir el ingrediente aunque la receta no sea suya:

```
DELETE /recipes/{id-receta}/ingredient/{id}

DELETE /recipes/1/ingredient/1
```

