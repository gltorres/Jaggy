CREATE TABLE Users
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	name VARCHAR(20) NOT NULL,
	alias VARCHAR(50) NOT NULL,
	password VARCHAR(26) NOT NULL,
	photo VARCHAR(30),
	
	PRIMARY KEY (id)
) CHARACTER SET utf8;

CREATE TABLE Followers
(
	user_id INT UNSIGNED NOT NULL REFERENCES Users(id),
	followed_id INT UNSIGNED NOT NULL REFERENCES Users(id),

	PRIMARY KEY (user_id, followed_id)
) CHARACTER SET utf8;

CREATE TABLE Messages
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	author_id INT UNSIGNED NOT NULL REFERENCES Users(id),
	text VARCHAR(140) NOT NULL,
	publish_date DATETIME NOT NULL,
	original_id INT UNSIGNED NULL,
	previous_id INT UNSIGNED NULL,

	PRIMARY KEY (id)
) CHARACTER SET utf8;

CREATE TABLE Forwards
(
	forwarder_id INT UNSIGNED NOT NULL REFERENCES Users(id),
	author_id INT UNSIGNED NOT NULL REFERENCES Users(id),
	message_id INT UNSIGNED NOT NULL REFERENCES Messages(id),
	forward_date DATETIME NOT NULL,
	
	PRIMARY KEY (forwarder_id, message_id)
) CHARACTER SET utf8;

CREATE TABLE MessageHashtags
(
	message_id INT UNSIGNED NOT NULL REFERENCES Messages(id),
	hashtag_id INT UNSIGNED NOT NULL REFERENCES Hashtags(id)
) CHARACTER SET utf8;

CREATE TABLE Hashtags
(
	id INT UNSIGNED NOT NULL AUTO_INCREMENT,
	hashtag VARCHAR(139) NOT NULL,
	author_id INT UNSIGNED NOT NULL REFERENCES Users(id),

	creation_date DATETIME NOT NULL,
	
	PRIMARY KEY (id)
) CHARACTER SET utf8;

INSERT INTO Users (name, alias, password) VALUES ('prueba', 'Usuario de prueba!', '123'), ('sebas', 'Sebastián Álvarez', '123'), ('alex', 'Alejandro Gil', '123'), ('raul', 'Raúl Araújo', '123');
INSERT INTO Followers (user_id, followed_id) VALUES (1,2), (2,3), (2,4), (3,1), (3,2), (3,4), (4,1), (4,2), (4,3);