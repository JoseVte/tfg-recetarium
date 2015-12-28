SET FOREIGN_KEY_CHECKS=0;
TRUNCATE TABLE comments;
TRUNCATE TABLE favorites;
TRUNCATE TABLE friends;
TRUNCATE TABLE ratings;
TRUNCATE TABLE ingredients;
TRUNCATE TABLE media;
TRUNCATE TABLE recipe_tags;
TRUNCATE TABLE recipes;
TRUNCATE TABLE tags;
TRUNCATE TABLE users;
SET FOREIGN_KEY_CHECKS=1;

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

INSERT INTO `recipes` (`id`, `title`, `slug`, `steps`, `duration` , `num_persons`, `difficulty`,  `user_id`, `created_at`, `updated_at`)
VALUES
	(1, 'Tarta de 3 chocolates', 'tarta-de-3-chocolates',
	'Comenzaremos preparando la base, para ello trituramos con un robot de cocina o bien pasándoles un rodillo a las galletas metidas dentro de una bolsa de plástico. Cuando estén en polvo fino les añadimos los 100 gramos de mantequilla derretida y formamos una masa que iremos pegando en la base de un molde para tartas desmontable de 20 centímetros forrado con papel de horno por la parte de abajo. Reservamos en la nevera. Seguiremos preparando la capa de chocolate negro. Ponemos para ello un cacito con la nata y 100 mililitros de leche a calentar, dejando los otros 100 aparte y en los cuales disolveremos el sobre de cuajada bien vigilando que no queden grumos. Una vez que esta caliente la leche y la nata añadimos el chocolate negro en trozos menudos y removemos con varillas hasta que se disuelva bien en el líquido. Añadimos el resto de la leche con la cuajada disuelta y llevamos sin dejar de remover a ebullición, cociendo la mezcla durante dos minutos más o menos vigilando que no se pegue al fondo del cacito. Es importante que hierva para que la cuajada funcione y la crema quede sólida. Vertemos la crema de chocolate negro encima de la base y dejamos reposar para que vaya cuajando mientras preparamos la del chocolate con leche. Para prepara la del chocolate con leche seguimos las mismas instrucciones que para el anterior, lo único que cambia es el tipo de chocolate. Una vez cocida esta crema la vertemos encima de la de chocolate negro vertiéndola con cuidado por encima de una espátula para que no nos agujeree la capa de chocolate negro. Volvemos a dejar reposar mientras preparamos la capa de chocolate blanco. Realizamos el mismo proceso que las otras dos veces, pero con el chocolate blanco, y acabamos echando la capa de chocolate blanco como capa final. Dejamos un mínimo de seis horas en la nevera para que cuaje perfectamente y desmoldamos con cuidado una vez bien fría.'
    , '00:30:00', 0, 'EASY', 1, now(), now()),
	(2, 'Arroz y marisco', 'arroz-y-marisco',
	'Lo primero que haremos es limpiar bien los calamares y la sepia así como cortarlos en anillas y trozos pequeños respectivamente; el tomate lo rallaremos y reservaremos, así como el ajo y el perejil finamente picado. (2) Las almejas las ponemos en agua con sal al menos media hora antes de lavarlas para que puedan soltar la arena que puedan llevar; los mejillones los lavamos bien y después los abrimos en una olla aparte para aprovechar el caldo. Cortamos el bonito a tacos pequeños. (3) En la pieza oval ponemos el aceite y calentamos a fuego fuerte, cuando en el “visiotherm” la franja roja este llegando a la figura de “chuleta” pondremos a sofreír el bonito y las gambas, tapamos, esperamos unos minutos a que la franja roja llegue al 90 o más. (4) Luego destapamos, damos la vuelta y añadimos sal al bonito, volvemos a tapar y esperamos a que vuelva a subir la temperatura al 98 (la gamba debe soltar bien los jugos), destapamos y añadimos los calamares y la sepia, le añadimos sal y tapamos. (5) Repetimos la misma operación tapando y esperando para dar la vuelta (es conveniente pasar un papel absorbente de cocina para eliminar el agua de condensación que queda en el interior de la tapadera, ya que si no lo hacemos esta agua vuelve a caer en el sofrito). (6) Cuando se evaporé el agua que pueda haber soltado el calamar y la sepia será el momento de añadir el tomate, le ponemos sal, pimienta, el ajo y perejil mezclamos bien y dejamos que el tomate se sofría, adquiera un color oscuro y comencemos a ver el aceite como se separa por los limites del sofrito. (7) Entonces estará bien hecho el sofrito.Incorporamos las almejas, tapamos y esperamos a que se abran, cuando se pierda el agua añadimos el arroz, mezclamos con el sofrito y le damos varias vueltas para sofreír un poco el arroz. (8) Añadimos el agua de cocción de los mejillones una vez colada junto con el resto del caldo a utilizar (nosotros en este caso hemos aprovechado un caldo de cocer camarón pero puede ser caldo de pescado). (9) A continuación, añadimos colorante alimentario si queremos que el arroz tenga el color amarillo característico, ponemos los mejillones que habíamos abierto previamente y rectificamos de sal. (10) Taparemos y programaremos el “audiotherm” 25 minutos en función cocer “zanahoria”,ponemos el fuego al máximo y cuando la franja roja llegue a dicha función y nos avise bajamos el fuego y esperamos esos 25 minutos. (11) Cuando pasen los 25 minutos apagamos el fuego, después destapamos, secamos el interior de la tapadera y volvemos a tapar dejando reposar unos minutos. Ya esta listo para servir.'
    , '00:45:00', 4, 'MEDIUM', 1, now(), now()),
    (3, 'Sushi variado', 'sushi-variado',
	'(1) Ponemos en remojo el arroz durante media hora. (2) Quitamos el agua restante y aplastamos bien en arroz contra el suelo del recipiente. (3) Añadimos agua fría y removemos para que suelte el almidón. (4) Repetimos el paso 2) y 3) tantas veces como sea necesario, sabremos cuando parar cuando el agua salga totalmente transparente. (5) Agregamos en una olla el arroz. Por cada taza de arroz, se añade 1/4 de taza de agua. (6) Ponemos la olla a fuego fuerte con la tapa puesta, cuando empiece a hervir, lo bajamos al mínimo. Cuando vuelva a hervir, apagamos el fuego y dejamos reposar durante 15 minutos con la tapa. (7) Preparamos la mezcla para añadir a nuestro arroz de sushi. En una taza, añadimos el vinagre de arroz y el ajimoto y removemos. (8) En una bandeja, agregamos el arroz, estirándolo bien. Una vez estirado, vamos agregando y repartiendo la mezcla del vinagre, intentando que llegue a toda la bandeja. (9) Dejamos que se enfríe a temperatura ambiente, aunque si queremos que vaya más rápido, podemos abanicarlo. (10) En una sartén, agregamos la mezcla del huevo batico con el azúcar para formar una tortilla dulce. (11) Troceamos en tiras finas el plátano, el pepino y el surimi. (12) Sobre la esterilla, ponemos paprl film y encima el alga nori, dejando la cara brillante hacia la esterilla. Con ayuda de un pincel, humedecemos el alga con un poco de vinagre de arroz. (13) Una vez frío el arroz, lo colocamos sobre el alga, dejando un espacio en los bordes laterales para poder cerrar. En el centro agregamos el queso de untar a modo de hilera de arriba hacia debajo. En el mismo sentido, el plátano. (14) Vamos enrollando desde un extremo con ayuda de la esterilla y el papel, haciendo presión para que el rulo quede compacto. Una vez que queda enrollado, con un pincel, sellamos el extremo con vinagre de arroz. (15) Repetimos desde el paso 12) pero esta vez, en el centro colocamos las tiras de surimi, pepino y las tiras de la tortilla que hemos hecho. Se vuelve a enrollar y sellar con cuidado. (16) Con un cuchillo jamonero y con mucho cuidado, vamos cortando los cilindros de sushi del grosor que queramos. (17) No os olvidéis de acompañarlo con salsa de soja, wasabi, jengibre o con lo que más os guste.'
     , '00:50:00', 2, 'HARD', 1, now(), now());

/*!40000 ALTER TABLE `recipes` ENABLE KEYS */;
UNLOCK TABLES;

## INGREDIENTS ##

LOCK TABLES `ingredients` WRITE;
/*!40000 ALTER TABLE `ingredients` DISABLE KEYS */;

INSERT INTO `ingredients` (`name`, `count`, `recipe_id`, `created_at`, `updated_at`)
VALUES
	('galletas maria', '200 g', 1, now(), now()),
    ('mantequilla', '100 g', 1, now(), now()),
    ('chocolate negro de repostería', '150 g', 1, now(), now()),
    ('chocolate con leche de repostería', '150 g', 1, now(), now()),
    ('chocolate blanco de repostería', '150 g', 1, now(), now()),
    ('nata líquida con 35%MG', '600 ml', 1, now(), now()),
    ('leche', '600 ml', 1, now(), now()),
    ('sobres de cuajada', '3', 1, now(), now()),
    ('arroz', '250 ml', 2, now(), now()),
    ('gambas rojas medianas', '8', 2, now(), now()),
    ('bonito fresco', '100 g', 2, now(), now()),
    ('mejillones', '12', 2, now(), now()),
    ('sepa mediana', '1', 2, now(), now()),
    ('tomate grande rallado', '1', 2, now(), now()),
    ('perejil picado', null, 2, now(), now()),
    ('ajo picado', '1 diente', 2, now(), now()),
    ('pimienta negra molida', null, 2, now(), now()),
    ('aceite de oliva virgen extra', '5 cdas', 2, now(), now()),
    ('caldo de cocer los mejillones y camarón', '650 ml', 2, now(), now()),
    ('colorante alimentario', null, 2, now(), now()),
    ('arroz blanco', '300 g', 3, now(), now()),
    ('ajimoto', '2 cdas', 3, now(), now()),
    ('vinagre blanco', '75 g', 3, now(), now()),
    ('lgo nori', '2 hojas', 3, now(), now()),
    ('plátano', '1', 3, now(), now()),
    ('queso de untar', '2 cdas', 3, now(), now()),
    ('pepino', '1', 3, now(), now()),
    ('surimi', '3 barritas', 3, now(), now()),
    ('huevo', '1', 3, now(), now()),
    ('azúcar', '1 cda', 3, now(), now()),
    ('aceite de girasol', null, 3, now(), now());

/*!40000 ALTER TABLE `ingredients` ENABLE KEYS */;
UNLOCK TABLES;

## MEDIA ##

LOCK TABLES `media` WRITE;
/*!40000 ALTER TABLE `media` DISABLE KEYS */;

INSERT INTO `media` (`id`, `filename`, `recipe_id`, `created_at`, `updated_at`)
VALUES
	(1, 'main.jpg', 1, now(), now()),
	(2, 'main.jpg', 2, now(), now()),
	(3, 'main.jpg', 3, now(), now());

/*!40000 ALTER TABLE `media` ENABLE KEYS */;
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
	(1, 'Chocolate', now(), now()),
	(2, 'Postre', now(), now()),
    (3, 'Arroz', now(), now()),
    (4, 'Marisco', now(), now());

/*!40000 ALTER TABLE `tags` ENABLE KEYS */;
UNLOCK TABLES;

## RECIPE TAGS ##

LOCK TABLES `recipe_tags` WRITE;
/*!40000 ALTER TABLE `recipe_tags` DISABLE KEYS */;

INSERT INTO `recipe_tags` (`id`, `recipe_id`, `tag_id`)
VALUES
	(1, 1, 1),
	(2, 1, 2),
    (3, 2, 3),
	(4, 2, 4),
    (5, 3, 4);

/*!40000 ALTER TABLE `recipe_tags` ENABLE KEYS */;
UNLOCK TABLES;
