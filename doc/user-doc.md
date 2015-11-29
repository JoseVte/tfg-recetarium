## Usuarios

#### Estructura JSON

Los usuarios tienen la siguiente estructura:

```json
{
  "id": "int",
  "username": "string",
  "email": "string",
  "password": "string",
  "first_name": "string nullable",
  "last_name": "string nullable",
  "type": "ADMIN/COMUN",
  "created_at": "fecha en timestamp",
  "updated_at": "fecha en timestamp"
}
```

#### Paginación

Acepta tanto la página como el tamaño de las lista:

```
GET /users(?page=*&size=*)

GET /users
GET /users?page=2
GET /users?size=3
GET /users?page=2&size=3
```

Devuelve una lista de usuarios con los links de paginacion:

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
  "link-prev": "/users",
  "link-next": "/users?page=3",
  "link-self": "/users?page=2"
}
```

#### Obtener un usuario por ID

A partir de un `ID` se obtiene un usuario con esta [estructura](#estructura-json):

```
GET /users/{id}

GET /users/1
```

Devuelve un error `404` si no se encuentra el usuario:

```json
{
    "error": "Not found {id}"
}
```

#### Crear un usuario nueva

Para crear un usuario se necesita enviar la [estructura del usuario](#estructura-json) sin el `ID`, `created_at` y `updated_at`:

```
POST /users
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


#### Actualizar un usuario ya existente

Para actualizar los datos de un usuario se debe enviar toda la [estructura del usuario](#estructura-json) menos `created_at` y `updated_at`:

```
PUT   /users/{id}
PATCH /users/{id}

PUT   /users/1
PATCH /users/1
```

Si se actualiza correctamente devuelve el usuario con los nuevos datos con un codigo `200`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

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


#### Borrar un usuario

Se borra un usuario a partir del `ID`:

```
DELETE /users/{id}

DELETE /users/1
```

Si se borra correctamente devuelve un mensage advirtiendo de que se ha completado la acción:

````json
{
    "msg": "Deleted {id}"
}
```

Devuelve un error `404` si no se encuentra el usuario:

```json
{
    "error": "Not found {id}"
}
```
