CREATE TABLE image
(
    id             BIGINT AUTO_INCREMENT NULL,
    image_text_key VARCHAR(255) NULL,
    pure_image_key VARCHAR(255) NULL
);

CREATE TABLE place
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    place_id   VARCHAR(255) NULL,
    place_name VARCHAR(255) NULL,
    address    VARCHAR(255) NULL,
    CONSTRAINT pk_place PRIMARY KEY (id)
);

CREATE TABLE place_tag
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    tag_id   BIGINT NULL,
    place_id BIGINT NULL,
    CONSTRAINT pk_placetag PRIMARY KEY (id)
);

CREATE TABLE pung
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_id    BIGINT   NOT NULL,
    place_id   BIGINT   NOT NULL,
    image_id   BIGINT NULL,
    text       VARCHAR(255) NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    CONSTRAINT pk_pung PRIMARY KEY (id)
);

CREATE TABLE review
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_id    BIGINT NULL,
    place_id   BIGINT NULL,
    text       VARCHAR(255) NULL,
    created_at datetime NOT NULL,
    updated_at datetime NOT NULL,
    CONSTRAINT pk_review PRIMARY KEY (id)
);

CREATE TABLE tag
(
    id       BIGINT AUTO_INCREMENT NOT NULL,
    tag_name VARCHAR(255) NULL,
    CONSTRAINT pk_tag PRIMARY KEY (id)
);

CREATE TABLE user
(
    id         BIGINT AUTO_INCREMENT NOT NULL,
    user_email VARCHAR(255) NULL,
    user_name  VARCHAR(255) NULL,
    CONSTRAINT pk_user PRIMARY KEY (id)
);