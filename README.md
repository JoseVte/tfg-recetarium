API Recetarium
================================

## RUTAS DE LA API

#### Recetas

| Acción | URI |
| ------ | --- |
| [Listado por páginas](/doc/recetas-doc.md#paginacion) | **GET**    /recipes(?page=*&size=*) |
| [Obtener por slug](/doc/recetas-doc.md#obtener-una-receta-por-slug) | **GET**    /recipes/{slug} |
| [Crear](/doc/recetas-doc.md#crear-una-receta-nueba) | **POST**   /recipes |
| [Actualizar](/doc/recetas-doc.md#actualizar-una-receta-ya-existente) |  **PUT**    /recipes/{id} <br> **PATCH**  /recipes/{id} |
| [Borrar](/doc/recetas-doc.md#borrar-una-receta) |  **DELETE** /recipes/{id} |

#### Autentificación

| Acción | URI |
| ------ | --- |
| Login | **POST**    /auth/login |
| Registrar | **POST**    /auth/register |
| Enviar email para reiniciar la password | **POST**    /auth/reset/password |
| Cambia la contraseña | **PUT**     /auth/reset/password <br> **PATCH**   /auth/reset/password |

#### Usuarios

| Acción | URI |
| ------ | --- |
| Listado por páginas | **GET**    /users(?page=*&size=*) |
| Obtener por id | **GET**    /users/{id} |
| Crear | **POST**   /users |
| Actualizar |  **PUT**    /users/{id} <br> **PATCH**  /users/{id} |
| Borrar |  **DELETE** /users/{id} |

-----

## DOCUMENTACIÓN EXPANDIDA

- [Autentificación](/doc/auth-doc.md)
- [Recetas](/doc/recetas-doc.md)
- [Usuarios](/doc/user-doc.md)

-----

## CHANGELOG

### Versión beta-0.1

- CRUD y servicios para usuarios, recetas, comentarios, categorias, tags y archivos
- Controladores para usuarios y recetas
- Implementación de registro,login y reinicio de password
- Acceso a rutas privadas mediante JWT

### Versión alpha

- App base
