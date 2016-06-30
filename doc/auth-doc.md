## Autentificaciónn

#### Estructuras JSON

##### Login

```json
{
  "email": "string",
  "password": "string"
}
```

##### Registro

```json
{
  "username": "string",
  "email": "string",
  "password": "string",
  "passwordRepeat": "string",
  "first_name": "string nullable",
  "last_name": "string nullable"
}
```

##### Guardar perfil

```json
{
  "password": "string",
  "passwordRepeat": "string",
  "first_name": "string nullable",
  "last_name": "string nullable",
  "avatar": "int nullable",
  "language": "string nullable"
}
```

##### Reinicio password

```json
{
  "email": "string"
}
```

##### Cambio de password

```json
{
  "password": "string",
  "token": "string"
}
```

##### Comprobar JWT

```json
{
  "email": "string",
  "setExpiration": "bool"
}
```

##### Activar cuenta

```json
{
  "token": "string"
}
```

#### Loguear un usuario

Para loguear un usuario es necesario un [email y password](#login) y enviarlos a esta ruta:

```
POST /auth/login
```

Si el login es correcto devuelve un JWT junto al lenguaje del usuario y la clave de pusher:

```json
{
    "token": "{token}",
    "language": "{language}",
    "pusher_key": "{pusher_key}"
}
```

Si los datos son correctos pero el email no existe o la contraseña no es correcta se devuelve un `401` vacio.

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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

#### Registrar un usuario

Para registrar un usuario se han de introducir [estos datos](#registro) en esta ruta:

```
POST /auth/register
```

Si el registro es correcto, se crea la cuenta y se envia un email para que el usuario active la cuenta.

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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

#### Activar la cuenta del usuario

Una vez un usuario esta registrado, se le enviara un email con la url para activar la cuenta. En esta url se encuentra el token para activarlo manualmente usando la siguiente URI y [este formulario](#activar-cuenta):

```
PUT   /auth/active
PATCH /auth/active
```

Si el usuario al que le perteneceel token no existe, ya sea porque el token es incorrecto como si ese usuario ya esta activado, se devuelve un `404`:

```json
{
    "error": "no encontrado email {email}"
}
```

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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

#### Enviar email para reiniciar la password

Para enviar un email a un usuario para poder reiniciar la contraseña se tiene que enviar el [email](#reinicio-password) con esta ruta:

```
POST /auth/reset/password
```

Cuando el email se haya enviado correctamente se recebirá este mensaje:

```json
{
    "msg": "email para resetear la contraseña enviado"
}
```

Si el email no existe se devuelve un `404`:

```json
{
    "error": "no encontrado email {email}"
}
```

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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

#### Cambia la password

Para cambiar la contraseña se debe enviar [este formulario](#cambio-de-password) a esta ruta:

```
PUT   /auth/reset/password
PATCH /auth/reset/password
```

Si se cambia la password correctamente se recibirá este mensaje:

```json
{
    "msg": "contraseña cambiada correctamente"
}
```

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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

#### Comprobar JWT

Esta URI permite regenerar un JWT partiendo de uno preexistente en la cabecera **X-Auth-Token** y [este formulario](#comprobar-jwt):

```
POST /auth/check
```

Si todo es correcto devuelve los mismos datos que el login:

```json
{
    "token": "{token}",
    "language": "{language}",
    "pusher_key": "{pusher_key}"
}
```

Si los datos son incorrectos se devuelve un **401**.

#### Obtener los detalles del perfil

Para poder acceder al perfil se debe pasa el `JWT` en la cabecera **X-Auth-Token** usando la siguiente URI:

```
GET /auth/profile
```

#### Guardar los cambios del perfil

Para guardar los datos en el perfil, se debe estar autentificado.

```
PUT   /auth/profile
PATCH /auth/profile
```

Si hubiese algún problema con los datos introducidos se devuelve un `400` con los errores:

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
