## Categorias

### Para poder acceder a algunas de las funciones de la API de las categorias debes tener acceso **ADMIN**. Se detallara posteriormente.

#### Estructura JSON

El formato de entrada de una categoria tiene la siguiente estructura:

```json
{
    "id": "int (null si es un nueva categoria)",
    "text": "string"
}
```

El formato de salida de una categoria tiene la siguiente estructura:

```json
{
    "id": "int",
    "text": "string",
    "recipes": "num de recetas",
    "created_at": "fecha en timestamp",
    "updated_at": "fecha en timestamp"
}
```

#### Todas las categorias

Esta URI muestra todas las categorias en una array. Para ello se necesita pasar en la cabecera **Accept-Pagination** el valor `false`:

```
GET /categories
```

```json
[
    {
        "id": "int",
        "text": "string",
        "recipes": "num de recetas",
        "created_at": "fecha en timestamp",
        "updated_at": "fecha en timestamp"
    }
]
```

#### Todas las categorias con paginación

Para ello se necesita pasar en la cabecera **Accept-Pagination** el valor `true`. Acepta varios parametros:
-  page: página
- size: tamaño por página
- search: cadena de busqueda
- order: campo por el que ordenar

```
GET /categories(?page=*&size=*)

GET /categories
GET /categories?page=2
GET /categories?size=3
GET /categories?page=2&size=3&search=postre&order=text
```
Devuelve una lista de categorias con los links de paginación:

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
    "link-prev": "/categories",
    "link-next": "/categories?page=3",
    "link-self": "/categories?page=2"
}
```

#### Obtener una categoria por ID

A partir de un `ID` se obtiene una categoria con el [formato de salida](#estructura-json):

```
GET /categories/{id}

GET /categories/1
```

Devuelve un error **404** si no se encuentra la categoria:

```json
{
    "error": "no encontrado la categoria {id}"
}
```

#### Crear una categoria

Para crear una categoria se necesita enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token** y el [formato de entrada de la categoria](#estructura-json):

```
POST /categories
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Si se crea correctamente devuelve el nueva categoria con un código **201**, pero si ocurre algún error en el input se recibe un **400** con todos los errores:

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


#### Actualizar una categoria

Para actualizar los datos de una categoria se debe enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token** y el [formato de entrada de la categoria](#estructura-json) añadiendo el `ID`:

```
PUT   /categories/{id}
PATCH /categories/{id}

PUT   /categories/1
PATCH /categories/1
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Si se actualiza correctamente devuelve la categoria con los nuevas datos con un código **200**, pero si ocurre algún error en el input se recibe un **400** con todos los errores:

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


#### Borrar una categoria

Se borra una categoria a partir del `ID`. Para ello se debe enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token**:

```
DELETE /categories/{id}

DELETE /categories/1
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "se ha borrado la categoria {id}"
}
```

Si el `JWT` es incorrecto o no es un admin se devuelve el código **401**.

Devuelve un error **404** si no se encuentra la categoria:

```json
{
    "error": "no encontrado la categoria {id}"
}
```

#### Borrar multiples categoriaa

A la siguiente ruta se le puede pasar multiples `ID` para borrar las categorias. Para ello se debe enviar el `JWT` del login de un admin en la cabecera **X-Auth-Token**:

```
DELETE /categories

DELETE /categories?ids=2&ids=1
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "se han borrado {n} categorias"
}
```
