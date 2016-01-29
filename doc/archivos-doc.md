## Archivos

#### Obtener por nombre del archivo

A partir de un `ID` o `nombre de archivo`(debe tener extensión) y un `ID` de una receta se obtiene un archivo con esta estructura):

```json
{
  "file": "string",
  "content_type": "string"
}
```

```
GET /recipes/{idReceta}/media/{id}
GET /recipes/{idReceta}/media/{nombre}

GET /recipes/1/media/1
GET /recipes/1/media/foto-1.jpg
```

Devuelve un error `404` si no se encuentra el archivo:

```json
{
    "error": "Not found file: {nombre}"
}
```

#### Subida del archivo

Para subir un archivo se necesita enviar el `JWT` en la cabecera **X-Auth-Token** y el archivo en el body de la request:

```
POST /recipes/{idReceta}/media
```
Si el `JWT` es incorrecto se devuelve el código `401`.

Si se crea correctamente devuelve el código `200` con el siguiente mensaje:

````json
{
    "msg": "File '{nombre}' uploaded"
}
```

Si no se sube ningun fichero o el `ID` de la receta no existe se devuelve un `404`. Tambien si ocurre algún error con el fichero se recibe un `500` con el mensaje de error:

```json
{
    "error": "Error uploading the file"
}
```

#### Borrar archivo

Se borra un archivo a partir del `ID` o el `nombre del fichero` (con extension). Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**:

```
DELETE /recipes/{idReceta}/media/{id}
DELETE /recipes/{idReceta}/media/{nombre}

DELETE /recipes/1/media/1
DELETE /recipes/1/media/foto-1.jpg
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "Deleted file {id}"
}
```

Si el `JWT` es incorrecto se devuelve el código `401`.

Devuelve un error `404` si no se encuentra el archivo:

```json
{
    "error": "Not found file {id}"
}
```
