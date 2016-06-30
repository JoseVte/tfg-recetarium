## Comentarios

#### Estructura JSON

El formato de entrada de un comentario tiene la siguiente estructura:

```json
{
    "text": "string"
}
```

El formato de salida de un comentario tiene la siguiente estructura:

```json
{
    "id": "int",
    "text": "string",
    "user": {},
    "parent": {
        "id": "int"
    },
    "replies": [],
    "created_at": "fecha en timestamp",
    "updated_at": "fecha en timestamp"
}
```

#### Todas las respuestas

Esta URI muestra todas las respuestas de un comentario en concreto:

```
GET /recipes/{recipe-id}/comments/{comment-id}

GET /recipes/1/comments/12
```

```json
[
    {
        "id": "int",
        "text": "string",
        "user": {},
        "parent": {
            "id": "int"
        },
        "replies": [],
        "created_at": "fecha en timestamp",
        "updated_at": "fecha en timestamp"
    }
]
```

#### Crear un comentario

Para crear un comentario se necesita enviar el `JWT` del login en la cabecera **X-Auth-Token** y el [formato de entrada de el comentario](#estructura-json). Opcionalmente se puede añadir el `ID` de otro comentario para crear una respuesta:

```
POST /recipes/{recipe-id}/comments(/{comment-id})

POST /recipes/1/comments
POST /recipes/1/comments/12
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Si se crea correctamente devuelve el nuevo comentario con un código **201**, pero si ocurre algún error en el input se recibe un **400** con todos los errores:

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


#### Actualizar un comentario

Para actualizar los datos de un comentario se debe enviar el `JWT` del login del propietario o de un admin en la cabecera **X-Auth-Token** y el [formato de entrada de el comentario](#estructura-json):

```
PUT   /recipes/{recipe-id}/comments/{comment-id}
PATCH /recipes/{recipe-id}/comments/{comment-id}

PUT   /recipes/1/comments/12
PATCH /recipes/1/comments/12
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Si se actualiza correctamente devuelve el comentario con los nuevas datos con un código **200**, pero si ocurre algún error en el input se recibe un **400** con todos los errores:

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


#### Borrar un comentario

Se borra un comentario a partir del `ID`. Para ello se debe enviar el `JWT` del login del propietario o de un admin en la cabecera **X-Auth-Token**:

```
DELETE /recipes/{recipe-id}/comments/{comment-id}

DELETE /recipes/1/comments/12
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción. Todas las respuestas del comentario tambien son borradas:

````json
{
    "msg": "se ha borrado el comentario {id}"
}
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Devuelve un error **404** si no se encuentra el comentario:

```json
{
    "error": "no encontrado el comentario {id}"
}
```
