/*
create DATABASE `blog` CHARACTER SET utf8 COLLATE utf8_general_ci;


use `blog`;

CREATE TABLE categories (
	id INT(11) NOT NULL AUTO_INCREMENT,
	NAME VARCHAR(255) NOT NULL,
	PRIMARY KEY (id)
) ;

CREATE TABLE posts (
	id INT(11) NOT NULL AUTO_INCREMENT,
	title VARCHAR(255) NOT NULL,
	summary TEXT NOT NULL,
	body TEXT NOT NULL,
	categoryId INT(11) NOT NULL ,
	PRIMARY KEY (id)
) ;

ALTER TABLE posts ADD CONSTRAINT CONSTR_POST_CATEGORY FOREIGN KEY (categoryId) REFERENCES categories (id);


INSERT INTO `categories` (`id`, `name`) VALUES
	(1,'News'),
	(2,'Java');

INSERT INTO `posts` (`id`, `title`, `summary`, `body`, `categoryId`) VALUES
	(1,'Test title', 'Test summary', 'Test body', 1);

INSERT INTO `posts` (`id`, `title`, `summary`, `body`, `categoryId`) VALUES
	(2,'Заголовок', 'Test summary', 'Test body', 1);

COMMIT;


CREATE TABLE comments (
	id INT(11) NOT NULL AUTO_INCREMENT,
	comment VARCHAR(500) NOT NULL,
	created_date DATETIME NOT NULL,
	author TEXT NOT NULL,
	postId INT(11) NOT NULL ,
	PRIMARY KEY (id)
) ;

ALTER TABLE comments ADD CONSTRAINT FK_POST_COMMENTS FOREIGN KEY (postId) REFERENCES posts (id);


INSERT INTO comments (comment, created_date, author, postId)  VALUES ('Отличная новость', CURRENT_TIMESTAMP, 'Ruslan', 1);
COMMIT;

*/

select * from comments;