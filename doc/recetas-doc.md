## Recetas

#### Estructura JSON

Las recetas tienen la siguiente estructura:

```json
{
  "id": "int",
  "slug": "string",
  "title": "string",
  "description": "string",
  "user": "user en JSON",
  "category": "categoria en JSON",
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

A partir de un `slug` (un string único por el que se identifica una receta) se obtiene una receta con esta [estructura](#estructura-json):

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

Para crear una receta se necesita enviar la [estructura de la receta](#estructura-json) sin el `ID`, `created_at` y `updated_at`:

```
POST /recipes
```

Si se crea correctamente devuelve la nueva receta con un codigo `201`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

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

Para actualizar los datos de una receta se debe enviar toda la [estructura de la receta](#estructura-json) menos `created_at` y `updated_at`:

```
PUT   /recipes/{id}
PATCH /recipes/{id}

PUT   /recipes/1
PATCH /recipes/1
```

Si se actualiza correctamente devuelve la receta con los nuevos datos con un codigo `200`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

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

Se borra una receta a partir del `ID`:

```
DELETE /recipes/{id}

DELETE /recipes/1
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "Deleted {id}"
}
```

Devuelve un error `404` si no se encuentra la receta:

```json
{
    "error": "Not found {id}"
}
```
