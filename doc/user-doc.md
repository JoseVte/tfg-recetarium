## Usuarios

### Para poder acceder a las funciones de la API de los usuarios debes tener acceso **ADMIN**.

#### Estructura JSON

El formato de entrada de un usuario tiene la siguiente estructura:

```json
{
  "id": "int (null si es un nuevo usuario)",
  "username": "string",
  "email": "string",
  "first_name": "string (nullable)",
  "last_name": "string (nullable)",
  "type": "ADMIN/COMUN"
}
```

El formato de salida de un usuario tiene la siguiente estructura:

```json
{
  "id": "int",
  "username": "string",
  "email": "string",
  "first_name": "string",
  "last_name": "string",
  "type": "ADMIN/COMUN",
  "created_at": "fecha en timestamp",
  "updated_at": "fecha en timestamp"
}
```

#### Paginación

Acepta tanto la página como el tamaño de las lista. Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**:

```
GET /users(?page=*&size=*)

GET /users
GET /users?page=2
GET /users?size=3
GET /users?page=2&size=3
```
Si el `JWT` es incorrecto se devuelve el codigo `401`.

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

A partir de un `ID` se obtiene un usuario con el [formato de salida](#estructura-json). Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**:

```
GET /users/{id}

GET /users/1
```

Si el `JWT` es incorrecto se devuelve el codigo `401`.

Devuelve un error `404` si no se encuentra el usuario:

```json
{
    "error": "Not found {id}"
}
```

#### Crear un usuario nuevo

Para crear un usuario se necesita enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token** y el [formato de entrada del usuario](#estructura-json):

```
POST /users
```

Si el `JWT` es incorrecto o no es un admin se devuelve el codigo `401`.

Si se crea correctamente devuelve el nuevo usuario con un codigo `201`, pero si ocurre algún error en el input se recibe un `400` con todos los errores:

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

Para actualizar los datos de un usuario se debe enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token** y el [formato de entrada del usuario](#estructura-json) añadiendo el `ID`:

```
PUT   /users/{id}
PATCH /users/{id}

PUT   /users/1
PATCH /users/1
```

Si el `JWT` es incorrecto o no es un admin se devuelve el codigo `401`.

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

Se borra un usuario a partir del `ID`. Para ello se debe enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token**:

```
DELETE /users/{id}

DELETE /users/1
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "Deleted {id}"
}
```

Si el `JWT` es incorrecto o no es un admin se devuelve el codigo `401`.

Devuelve un error `404` si no se encuentra el usuario:

```json
{
    "error": "Not found {id}"
}
```
