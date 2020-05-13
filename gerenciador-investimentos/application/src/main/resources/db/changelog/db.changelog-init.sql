--liquibase formatted sql

--changeset vitor:1589381413369-1
CREATE TABLE client (
    email VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    avatar_image VARCHAR(255) NULL,
    CONSTRAINT PK_CLIENT PRIMARY KEY (email)
);
--rollback DROP TABLE client;

--changeset vitor:1589381413369-2
CREATE TABLE stock (
    symbol VARCHAR(6) NOT NULL,
    current_value DECIMAL(13, 2) NOT NULL,
    type CHAR(1) NULL,
    name VARCHAR(255) NULL,
    corporation VARCHAR(255) NULL,
    business_area VARCHAR(255) NULL,
    CONSTRAINT PK_STOCK PRIMARY KEY (symbol)
 );
--rollback DROP TABLE stock;

--changeset vitor:1589381413369-3
CREATE TABLE stock_assets (
    id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    stock_symbol VARCHAR(6) NOT NULL,
    average_cost DECIMAL(13, 2) NOT NULL,
    amount INT NOT NULL,
    CONSTRAINT PK_STOCK_ASSETS PRIMARY KEY (id)
);
--rollback DROP TABLE stock_assets;

--changeset vitor:1589381413369-4
CREATE TABLE user (
    id BIGINT NOT NULL,
    password VARCHAR(255) NOT NULL,
    client_email VARCHAR(255) NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id)
);
--rollback DROP TABLE user;

--changeset vitor:1589381413369-5
CREATE TABLE wallet (
    id BIGINT NOT NULL,
    name VARCHAR(255) NULL,
    broker VARCHAR(255) NULL,
    loss_daytrade DECIMAL(13, 2) NULL,
    loss DECIMAL(13, 2) NULL,
    balance_daytrade DECIMAL(13, 2) NULL,
    balance DECIMAL(13, 2) NULL,
    client_email VARCHAR(255) NOT NULL,
    CONSTRAINT PK_WALLET PRIMARY KEY (id)
);
--rollback DROP TABLE wallet;

--changeset vitor:1589381413369-6
CREATE INDEX client_email ON user(client_email);
--rollback ALTER TABLE user DROP INDEX client_email;

--changeset vitor:1589381413369-7
CREATE INDEX client_email ON wallet(client_email);
--rollback ALTER TABLE wallet DROP INDEX client_email;

--changeset vitor:1589381413369-8
CREATE INDEX stock_symbol ON stock_assets(stock_symbol);
--rollback ALTER TABLE stock_assets DROP INDEX stock_symbol;

--changeset vitor:1589381413369-9
CREATE INDEX wallet_id ON stock_assets(wallet_id);
--rollback ALTER TABLE stock_assets DROP INDEX wallet_id;

--changeset vitor:1589381413369-10
ALTER TABLE stock_assets
ADD CONSTRAINT stock_assets_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE stock_assets DROP FOREIGN KEY stock_assets_ibfk_1;

--changeset vitor:1589381413369-11
ALTER TABLE stock_assets
ADD CONSTRAINT stock_assets_ibfk_2 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE stock_assets DROP FOREIGN KEY stock_assets_ibfk_2;

--changeset vitor:1589381413369-12
ALTER TABLE user
ADD CONSTRAINT user_ibfk_1 FOREIGN KEY (client_email) REFERENCES client (email)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE user DROP FOREIGN KEY user_ibfk_1;

--changeset vitor:1589381413369-13
ALTER TABLE wallet
ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_email) REFERENCES client (email)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE wallet DROP FOREIGN KEY wallet_ibfk_1;