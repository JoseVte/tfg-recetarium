SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE play.comments;
TRUNCATE TABLE play.favorites;
TRUNCATE TABLE play.friends;
TRUNCATE TABLE play.ratings;
TRUNCATE TABLE play.recipe_tags;
TRUNCATE TABLE play.recipes;
TRUNCATE TABLE play.tags;
TRUNCATE TABLE play.users;
SET FOREIGN_KEY_CHECKS=1;

use play;

## USERS ##

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;

INSERT INTO `users` (`id`, `username`, `email`, `first_name`, `last_name`, `password`, `type`, `created_at`, `updated_at`)
VALUES
	(1, 'Josrom', 'jvortsromero@gmail.com', 'Jose Vicente', 'Orts Romero', '1000:e5bf55a614b86207c226abed6dbfd999b195fe969396a0fb:16d9a9a92a95fe5087087779e6f5d2a8afcfe79a18c7cb65', 'ADMIN', now(), now()),
	(2, 'Dantar', 'dantar@gmail.com', 'Daniel', 'Ambit', '1000:e5bf55a614b86207c226abed6dbfd999b195fe969396a0fb:16d9a9a92a95fe5087087779e6f5d2a8afcfe79a18c7cb65', 'COMUN', now(), now());

/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

## RECIPES ##

LOCK TABLES `recipes` WRITE;
/*!40000 ALTER TABLE `recipes` DISABLE KEYS */;

INSERT INTO `recipes` (`id`, `title`, `slug`, `description`, `user_id`, `created_at`, `updated_at`)
VALUES
	(1, 'Primera receta de la aplicación', 'primera-receta', 'Descripción de la primera receta', 1, now(), now()),
	(2, 'Segunda receta de la aplicación', 'segunda-receta', 'Descripción de la segunda receta', 1, now(), now());

/*!40000 ALTER TABLE `recipes` ENABLE KEYS */;
UNLOCK TABLES;

## COMMENTS ##

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;

INSERT INTO `comments` (`id`, `text`, `parent_comment_id`, `recipe_id`, `user_id`, `created_at`, `updated_at`)
VALUES
	(1, 'Comentario 1', null, 1, 1, now(), now()),
	(2, 'Subcomentario 1', 1, 1, 2, now(), now());

/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

## FAVORITES ##

LOCK TABLES `favorites` WRITE;
/*!40000 ALTER TABLE `favorites` DISABLE KEYS */;

INSERT INTO `favorites` (`id`, `recipe_id`, `user_id`)
VALUES
	(1, 1, 1),
	(2, 1, 2);

/*!40000 ALTER TABLE `favorites` ENABLE KEYS */;
UNLOCK TABLES;

## FRIENDS ##

LOCK TABLES `friends` WRITE;
/*!40000 ALTER TABLE `friends` DISABLE KEYS */;

INSERT INTO `friends` (`id`, `friend_id`, `user_id`)
VALUES
	(1, 2, 1),
	(2, 1, 2);

/*!40000 ALTER TABLE `friends` ENABLE KEYS */;
UNLOCK TABLES;

## RATINGS ##

LOCK TABLES `ratings` WRITE;
/*!40000 ALTER TABLE `ratings` DISABLE KEYS */;

INSERT INTO `ratings` (`id`,`recipe_id`, `user_id`, `rating`)
VALUES
	(1, 1, 1, 5.0),
	(2, 1, 2, 6.66);

/*!40000 ALTER TABLE `ratings` ENABLE KEYS */;
UNLOCK TABLES;

## TAGS ##

LOCK TABLES `tags` WRITE;
/*!40000 ALTER TABLE `tags` DISABLE KEYS */;

INSERT INTO `tags` (`id`, `text`, `created_at`, `updated_at`)
VALUES
	(1, 'Aperitivo', now(), now()),
	(2, 'Postre', now(), now());

/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

## RECIPE TAGS ##

LOCK TABLES `recipe_tags` WRITE;
/*!40000 ALTER TABLE `recipe_tags` DISABLE KEYS */;

INSERT INTO `recipe_tags` (`id`, `recipe_id`, `tag_id`)
VALUES
	(1, 1, 1),
	(2, 1, 2);

/*!40000 ALTER TABLE `recipe_tags` ENABLE KEYS */;
UNLOCK TABLES;