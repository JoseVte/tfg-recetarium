API Recetarium
================================

## RUTAS DE LA API

#### Recetas

```
GET    /recipes(?page=*&size=*)

GET    /recipes/{slug}

POST   /recipes

PUT    /recipes/{id}
PATCH  /recipes/{id}

DELETE /recipes/{id}
```

-----

## DOCUMENTACIÓN EXPANDIDA

- [Autentificación](/doc/auth-doc.md)
- [Recetas](/doc/recetas-doc.md)
- [Usuarios](/doc/user-doc.md)

## CHANGELOG

### Versión beta-0.1

- CRUD y servicios para usuarios, recetas, comentarios, categorias, tags y archivos
- Controladores para usuarios y recetas
- Implementación de registro,login y reinicio de password
- Acceso a rutas privadas mediante JWT

### Versión alpha

- App base
