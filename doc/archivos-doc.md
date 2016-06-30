## Archivos

#### Todas las imagenes

Partiendo de un `ID` se un usuario existente se pueden obtener un listado de todas las imagenes. Para ello se debe estar logueado con el `JWT` en la cabecera **X-Auth-Token**.

```json
[
    {
        "id": {id},
        "url": {url completa},
        "type": {string},
        "title": {titulo original},
        "new_title": {titulo autogenerado},
        "user": {owner},
        "recipesImageMain": {numero de veces usada como principal},
        "recipes": {numero de veces usada},
        "created_at": {timestamp},
        "updated_at": {timestamp}
    }
]
```

#### Obtener por nombre del archivo

A partir de un `ID` o `nombre de archivo`(debe tener extensión) y un `ID` del usuario al que le pertenece se obtiene un archivo:

```
GET /user/{user-id}/files/{id}
GET /user/{user-id}/files/{nombre}

GET /user/1/files/1
GET /user/1/files/foto-1.jpg
```

Devuelve un error **404** si no se encuentra el archivo:

```json
{
    "error": "no encontrado el archivo: {id/nombre}"
}
```

#### Subida del archivo

Para subir un archivo se necesita enviar el `JWT` en la cabecera **X-Auth-Token** y el archivo en el body de la request:

```
POST /user/{user-id}/files
```
Si el `JWT` es incorrecto se devuelve el código **401**. Tambien ocurre si el `user-id` no coincide con el usuario autentificado, salvo que el usuario autentificado es un **admin**.

Si se crea correctamente devuelve el código **200** con el siguiente mensaje:

```json
{
    "msg": "fichero <em>{nombre}</em> subido"
}
```

Si no se sube ningun fichero o el `ID` de la receta no existe se devuelve un **404**. Tambien si ocurre algún error con el fichero se recibe un `500` con el mensaje de error:

```json
{
    "error": "error subiendo el fichero <strong>{nombre}</strong>"
}
```

#### Borrar archivo

Se borra un archivo a partir del `ID` o el `nombre del fichero` (con extension). Para ello tambien se debe enviar el `JWT` en la cabecera **X-Auth-Token**:

```
DELETE /user/{user-id}/files/{id}
DELETE /user/{user-id}/files/{nombre}

DELETE /user/1/files/1
DELETE /user/1/files/foto-1.jpg
```

Si se borra correctamente devuelve un mensaje advirtiendo de que se ha completado la acción:

````json
{
    "msg": "se ha borrado el fichero {id/nombre}"
}
```

Si el `JWT` es incorrecto se devuelve el código **401**.

Devuelve un error **404** si no se encuentra el archivo:

```json
{
    "error": "no encontrado el archivo: {id/nombre}"
}
```
