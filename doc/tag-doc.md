## Etiquetas

### Para poder acceder a algunas de las funciones de la API de las etiquetas debes tener acceso **ADMIN**. Se detallara posteriormente.

#### Estructura JSON

El formato de salida de una etiqueta tiene la siguiente estructura:

```json
{
    "id": "int",
    "text": "string",
    "created_at": "fecha en timestamp",
    "updated_at": "fecha en timestamp"
}
```

#### Buscar etiquetas por cadena

Esta URI muestra todas las etiquetas en una array. :

```
GET /tags
```

```json
[
    {
        "id": "int",
        "text": "string",
        "created_at": "fecha en timestamp",
        "updated_at": "fecha en timestamp"
    }
]
```

#### Obtener una etiqueta por ID

A partir de un `ID` se obtiene una etiqueta con el [formato de salida](#estructura-json):

```
GET /tags/{id}

GET /tags/1
```

Devuelve un error **404** si no se encuentra la etiqueta:

```json
{
    "error": "no encontrado la etiqueta {id}"
}
```
