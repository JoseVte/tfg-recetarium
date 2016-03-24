[![Issue Stats](http://issuestats.com/github/JoseVte/tfg-recetarium/badge/pr?style=flat)](http://issuestats.com/github/JoseVte/tfg-recetarium)
[![Issue Stats](http://issuestats.com/github/JoseVte/tfg-recetarium/badge/issue?style=flat)](http://issuestats.com/github/JoseVte/tfg-recetarium)

[![wercker status](https://app.wercker.com/status/31e5e6a4da1640a571cf2a58897c1cd8/m "wercker status")](https://app.wercker.com/project/bykey/31e5e6a4da1640a571cf2a58897c1cd8)

API Recetarium
================================

## RUTAS DE LA API

#### Recetas

| Acción | URI | Login |
| ------ | --- | :----: |
| [Listado y busqueda por páginas](/doc/recetas-doc.md#paginación) | **GET** /recipes(?page=*&size=*&search=*) | --- |
| [Obtener por slug](/doc/recetas-doc.md#obtener-una-receta-por-slug) | **GET** /recipes/{slug} | --- |
| [Crear](/doc/recetas-doc.md#crear-una-receta-nueva) | **POST** /recipes | OWNER / ADMIN |
| [Actualizar](/doc/recetas-doc.md#actualizar-una-receta-ya-existente) |  **PUT**    /recipes/{id} <br> **PATCH**  /recipes/{id} | OWNER / ADMIN |
| [Borrar](/doc/recetas-doc.md#borrar-una-receta) |  **DELETE** /recipes/{id} | OWNER / ADMIN |
| [Comprobar slug](/doc/recetas-doc.md#comprobar-slug-de-una-receta) |  **HEAD** /recipes/{slug}/check <br> **HEAD** /recipes/{slug}/check/{id} | COMUN |
| [Comprobar propietario](/doc/recetas-doc.md#comprobar-si-una-receta-es-del-usuario-logueado) |  **HEAD** /recipes/{slug}/mine | COMUN |
| [Añadir un ingrediente](/doc/recetas-doc.md#añadir-un-nuevo-ingrediente-a-una-receta) | **POST** /recipes/{id-receta}/ingredient | OWNER / ADMIN |
| [Borrar un ingrediente](/doc/recetas-doc.md#borrar-un-ingrediente-de-una-receta) | **DELETE** /recipes/{id-receta}/ingredient/{id} | OWNER / ADMIN |

#### Categorias

| Acción | URI | Login |
| ------ | --- | :----: |
| [Todas las categorias](/doc/categorias-doc.md#todas-las-categorias) | **GET**  /categories | --- |

#### Tags

| Acción | URI | Login |
| ------ | --- | :----: |
| [Buscar tags](/doc/tag-doc.md#buscar-tags-por-cadena) | **GET**  /tags(?search=*) | --- |

#### Autentificación

| Acción | URI | Login |
| ------ | --- | :----: |
| [Loguear un usuario](/doc/auth-doc.md#loguear-un-usuario) | **POST**    /auth/login | --- |
| [Registrar un usuario](/doc/auth-doc.md#registrar-un-usuario) | **POST**    /auth/register | --- |
| [Enviar email para reiniciar la password](/doc/auth-doc.md#enviar-email-para-reiniciar-la-password) | **POST**    /auth/reset/password | --- |
| [Cambia la password](/doc/auth-doc.md#cambiar-la-password) | **PUT**     /auth/reset/password <br> **PATCH**   /auth/reset/password | --- |

#### Usuarios

| Acción | URI | Login |
| ------ | --- | :----: |
| [Listado por páginas](/doc/user-doc.md#paginación) | **GET**    /users(?page=*&size=*&search=*) | COMUN |
| [Obtener por id](/doc/user-doc.md#obtener-un-usuario-por-id) | **GET**    /users/{id} | COMUN |
| [Crear](/doc/user-doc.md#crear-un-usuario-nuevo) | **POST**   /users | ADMIN |
| [Actualizar](/doc/user-doc.md#actualizar-un-usuario-ya-existente) |  **PUT**    /users/{id} <br> **PATCH**  /users/{id} | ADMIN |
| [Borrar](/doc/user-doc.md#borrar-un-usuario) |  **DELETE** /users/{id} | ADMIN |

#### Archivos

| Acción | URI | Login |
| ------ | --- | :----: |
| [Obtener por nombre del archivo](/doc/archivos-doc.md#obtener-por-nombre-del-archivo) | **GET**    /recipes/{idReceta}/media/{id} <br> **GET**    /recipes/{idReceta}/media/{nombreFichero} | --- |
| [Subida del archivo](/doc/archivos-doc.md#subida-del-archivo) | **POST**   /recipes/{idReceta}/media | OWNER / ADMIN |
| [Borrar archivo](/doc/archivos-doc.md#borrar-archivo) |  **DELETE** /recipes/{idReceta}/media/{id} <br> **DELETE** /recipes/{idReceta}/media/{nombre} | OWNER / ADMIN |

-----

## DOCUMENTACIÓN EXPANDIDA

- [Autentificación](/doc/auth-doc.md)
- [Recetas](/doc/recetas-doc.md)
- [Usuarios](/doc/user-doc.md)
- [Archivos](/doc/archivos-doc.md)
- [Categorias](/doc/categorias-deoc.md)
- [Tags](/doc/tags-doc.md)

-----

### CHANGELOG

#### [![0.8.1](/doc/rocket-blue.png) 0.8.1](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.8.1)

- CRUD de recetas

###### [![0.8.0-hotfix](/doc/release.png) 0.8.0-hotfix](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.8.0-hotfix)

- Ordenar recetas por fecha
- Nuevo sistema para contar las recetas

###### [![0.7.1](/doc/release.png) 0.7.1](https://github.com/JoseVte/tfg-recetarium-angularjs/releases/tag/0.7.1)

- Añadida galeria para elegir imagenes en la receta y el perfil de usuario

###### [![0.6.1](/doc/release.png) 0.6.1](https://github.com/JoseVte/tfg-recetarium-angularjs/releases/tag/0.6.1)

- Añadidas rutas para añadir a favoritos
- Añadida posibilidad de puntuar una receta del 0 al 5

###### [![0.5.2](/doc/release.png) 0.5.2](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.5.2)

- Nuevas rutas para el perfil
- Cambiados algunos metodos del UserService

###### [![0.5.1](/doc/release.png) 0.5.1](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.5.1)

- Generar borrador de una receta

###### [![0.5.0-hotfix-2](/doc/release.png) 0.5.0-hotfix-2](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.5.0-hotfix-2)

- Arreglado problema con la visibilidad de las recetas

###### [![0.5.0-hotfix](/doc/release.png) 0.5.0-hotfix](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.5.0-hotfix)

- Arreglado login cuando se ha enviado un correo para restaurar la password

###### [![0.5.0](/doc/release.png) 0.5.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.5.0)

- Añadida visibilidad a las recetas

###### [![0.4.1](/doc/release.png) 0.4.1](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.4.1)

- Busqueda de recetas
- Actualizada la documentación
- Añadidos nuevos test

###### [![0.4.0](/doc/release.png) 0.4.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.4.0)

- Gestion de ficheros con dropbox
- Cambio del servidor CI a Travis
- Añadidas las URL en los mails
- Cambios en la estructura del modelo de la receta
- Fix en el JWT y añadido token sin expiración
- Subida de múltiples archivos

###### [![0.3.4](/doc/release.png) 0.3.4](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.4)

- Subida, lectura y borrado de archivos en el servidor.

###### [![0.3.3](/doc/release.png) 0.3.3](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.3)

- Nuevos formatos de JSON para el input y output de los usuarios y recetas.

###### [![0.3.2-hotfix](/doc/release.png) 0.3.2-hotfix](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.2-hotfix)

- Nombre de los test fixeado en Jenkins

###### [![0.3.2](/doc/release.png) 0.3.2](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.2)

- Añadidos los middleware para evitar el acceso a zonas privadas.
- Versionado de la API con tag en el README.
- Test para los middleware.
- Nombre de los test fixeado en Linux (testear en Jenkins).

###### [![0.3.1-hotfix](/doc/release.png) 0.3.1-hotfix](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.1-hotfix)

- Hotfix: Añadido el filtro CORS para poder acceder desde la APP web.

###### [![0.3.1](/doc/release.png) 0.3.1](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.1)

- Despliegue de la aplicación en Heroku: [recetarium](https://recetarium.herokuapp.com/)

###### [![0.3.0](/doc/release.png) 0.3.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.0)

- Nuevo README.
- Implementación de registro,login y reinicio de password en un nuevo **controller**.
- Acceso a rutas privadas mediante **JWT**.
- Test para el nuevo **controller**

###### [![0.2.2](/doc/release.png) 0.2.2](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.2.2)

- **Controller** para las recetas.
- Test para el nuevo controllador.

###### [![0.2.1](/doc/release.png) 0.2.1](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.2.1)

- Cambiado el nombre del modelo **Section** por **Category**.
- Refactorización de los **controllers**, **DAO** y **Models**.
- Personalizacón de la salida de los test en el terminal.
- Refactorización de los test y clarificación de los nombres de los test.

###### [![0.2.0](/doc/release.png) 0.2.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.2.0)

- Añadidos todos los modelos básicos y sus relaciones:
    - User
    - Recipe
    - Comment
    - Media
    - Tag
    - Section
- Clases **DAO** y **Service** para los modelos.
- CRUD y servicios para usuarios, recetas, comentarios, categorias, tags y archivos.
- Automatización de los campos `created_at` y `updated_at` para los modelos.
- Creados los **controllers** básicos para usuarios y recetas.
- Creados los test para las clases **DAO** y **Service** usando un fichero `YAML`.

###### [![0.1.0](/doc/release.png) 0.1.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.1.0)

- Integración con Jenkins

###### [![0.0.0](/doc/release.png) 0.0.0](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.0.0)

- App base
