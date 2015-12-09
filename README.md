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

## CHANGELOG

### Versión beta-0.1

- CRUD y servicios para usuarios, recetas, comentarios, categorias, tags y archivos
- Controladores para usuarios y recetas
- Implementación de registro,login y reinicio de password
- Acceso a rutas privadas mediante JWT

### Versión alpha

- App base
