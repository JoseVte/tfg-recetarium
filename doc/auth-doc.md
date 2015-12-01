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
##### Reinicio password

```json
{
  "email": "string"
}
```

##### Cambio de password

```json
{
  "email": "string",
  "password": "string",
  "token": "string"
}
```

#### Loguear un usuario

Para loguear un usuario es necesario un [email y password](#login) y enviarlos a esta ruta:

```
POST /auth/login
```

Si el login es correcto devuelve un JWT y lo añade a las cookies:

```json
{
    "token": "{token}"
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

Si el registro es correcto, se autologuea al usuario y se devuelve un JWT y lo añade a las cookies:

```json
{
    "token": "{token}"
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
    "msg": "Reset password email sent"
}
```

Si el email no existe se devuelve un `404`:

```json
{
    "error": "Not found email {email}"
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
    "msg": "Changed password successfully"
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
