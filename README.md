[![Issue Stats](http://issuestats.com/github/JoseVte/tfg-recetarium/badge/pr?style=flat)](http://issuestats.com/github/JoseVte/tfg-recetarium)
[![Issue Stats](http://issuestats.com/github/JoseVte/tfg-recetarium/badge/issue?style=flat)](http://issuestats.com/github/JoseVte/tfg-recetarium)

[![Build Status](http://104.197.128.78/jenkins/job/Recetarium/badge/icon)](http://104.197.128.78/jenkins/job/Recetarium/)

API Recetarium
================================

## RUTAS DE LA API

#### Recetas

| Acción | URI |
| ------ | --- |
| [Listado por páginas](/doc/recetas-doc.md#paginación) | **GET**    /recipes(?page=*&size=*) |
| [Obtener por slug](/doc/recetas-doc.md#obtener-una-receta-por-slug) | **GET**    /recipes/{slug} |
| [Crear](/doc/recetas-doc.md#crear-una-receta-nueva) | **POST**   /recipes |
| [Actualizar](/doc/recetas-doc.md#actualizar-una-receta-ya-existente) |  **PUT**    /recipes/{id} <br> **PATCH**  /recipes/{id} |
| [Borrar](/doc/recetas-doc.md#borrar-una-receta) |  **DELETE** /recipes/{id} |

#### Autentificación

| Acción | URI |
| ------ | --- |
| [Loguear un usuario](/doc/auth-doc.md#loguear-un-usuario) | **POST**    /auth/login |
| [Registrar un usuario](/doc/auth-doc.md#registrar-un-usuario) | **POST**    /auth/register |
| [Enviar email para reiniciar la password](/doc/auth-doc.md#enviar-email-para-reiniciar-la-password) | **POST**    /auth/reset/password |
| [Cambia la password](/doc/auth-doc.md#cambiar-la-password) | **PUT**     /auth/reset/password <br> **PATCH**   /auth/reset/password |

#### Usuarios

| Acción | URI |
| ------ | --- |
| [Listado por páginas](/doc/user-doc.md#paginación) | **GET**    /users(?page=*&size=*) |
| [Obtener por id](/doc/user-doc.md#obtener-un-usuario-por-id) | **GET**    /users/{id} |
| [Crear](/doc/user-doc.md#crear-un-usuario-nuevo) | **POST**   /users |
| [Actualizar](/doc/user-doc.md#actualizar-un-usuario-ya-existente) |  **PUT**    /users/{id} <br> **PATCH**  /users/{id} |
| [Borrar](/doc/user-doc.md#borrar-un-usuario) |  **DELETE** /users/{id} |

-----

## DOCUMENTACIÓN EXPANDIDA

- [Autentificación](/doc/auth-doc.md)
- [Recetas](/doc/recetas-doc.md)
- [Usuarios](/doc/user-doc.md)

-----

### CHANGELOG

#### [![0.3.2](/doc/rocket.png) 0.3.2](https://github.com/JoseVte/tfg-recetarium/releases/tag/0.3.2)

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
