-- liquibase formatted sql

-- changeset VladimirKudriavtsev:1
CREATE TABLE bot_user
(
    id      INTEGER PRIMARY KEY,
    chat_id INTEGER      NOT NULL,
    state   VARCHAR(255) NOT NULL
);