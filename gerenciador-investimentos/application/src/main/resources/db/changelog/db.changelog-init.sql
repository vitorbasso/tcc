--liquibase formatted sql

--changeset vitor:1590866979692-1
CREATE TABLE client (
    cpf VARCHAR(20) NOT NULL,
    email VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NULL,
    avatar_image VARCHAR(255) NULL,
    user_id BIGINT NOT NULL,
    date_created timestamp DEFAULT NOW() NOT NULL,
    date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL,
    CONSTRAINT PK_CLIENT PRIMARY KEY (cpf),
    UNIQUE (email)
);
--rollback DROP TABLE client;

--changeset vitor:1590866979692-2
CREATE TABLE stock (
    symbol VARCHAR(6) NOT NULL,
    current_value DECIMAL(13,
    2) NOT NULL,
    type CHAR(1) NULL,
    name VARCHAR(255) NULL,
    corporation VARCHAR(255) NULL,
    business_area VARCHAR(255) NULL,
    CONSTRAINT PK_STOCK PRIMARY KEY (symbol)
);
--rollback DROP TABLE stock;

--changeset vitor:1590866979692-3
CREATE TABLE stock_assets (
    id BIGINT AUTO_INCREMENT NOT NULL,
    wallet_id BIGINT NOT NULL,
    stock_symbol VARCHAR(6) NOT NULL,
    average_cost DECIMAL(13,
    2) NOT NULL,
    amount INT NOT NULL,
    date_created timestamp DEFAULT NOW() NOT NULL,
    date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL,
    CONSTRAINT PK_STOCK_ASSETS PRIMARY KEY (id)
);
--rollback DROP TABLE stock_assets;

--changeset vitor:1590866979692-4
CREATE TABLE transaction (
    id BIGINT AUTO_INCREMENT NOT NULL,
    type VARCHAR(3) NOT NULL,
    quantity INT NULL,
    value DECIMAL(13,
    2) NULL,
    stock_symbol VARCHAR(6) NOT NULL,
    stock_assets_id BIGINT NOT NULL,
    date_created timestamp DEFAULT NOW() NOT NULL,
    date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL,
    CONSTRAINT PK_TRANSACTION PRIMARY KEY (id)
);
--rollback DROP TABLE transaction;

--changeset vitor:1590866979692-5
CREATE TABLE user (
    id BIGINT AUTO_INCREMENT NOT NULL,
    password VARCHAR(255) NOT NULL,
    date_created timestamp DEFAULT NOW() NOT NULL,
    date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL,
    CONSTRAINT PK_USER PRIMARY KEY (id)
);
--rollback DROP TABLE user;

--changeset vitor:1590866979692-6
CREATE TABLE wallet (
    id BIGINT AUTO_INCREMENT NOT NULL,
    name VARCHAR(255) NULL,
    broker VARCHAR(255) NULL,
    loss_daytrade DECIMAL(13,
    2) NULL,
    loss DECIMAL(13,
    2) NULL,
    balance_daytrade DECIMAL(13,
    2) NULL,
    balance DECIMAL(13,
    2) NULL,
    client_cpf VARCHAR(255) NOT NULL,
    date_created timestamp DEFAULT NOW() NOT NULL,
    date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL,
    CONSTRAINT PK_WALLET PRIMARY KEY (id)
);
--rollback DROP TABLE wallet;

--changeset vitor:1590866979692-7
CREATE INDEX client_cpf ON wallet(client_cpf);
--rollback ALTER TABLE wallet DROP INDEX client_cpf;

--changeset vitor:1590866979692-8
CREATE INDEX stock_assets_id ON transaction(stock_assets_id);
--rollback ALTER TABLE transaction DROP INDEX stock_assets_id;

--changeset vitor:1590866979692-9
CREATE INDEX stock_symbol ON stock_assets(stock_symbol);
--rollback ALTER TABLE stock_assets DROP INDEX stock_symbol;

--changeset vitor:1590866979692-10
CREATE INDEX stock_symbol ON transaction(stock_symbol);
--rollback ALTER TABLE transaction DROP INDEX stock_symbol;

--changeset vitor:1590866979692-11
CREATE INDEX user_id ON client(user_id);
--rollback ALTER TABLE client DROP INDEX user_id;

--changeset vitor:1590866979692-12
CREATE INDEX wallet_id ON stock_assets(wallet_id);
--rollback ALTER TABLE stock_assets DROP INDEX wallet_id;

--changeset vitor:1590866979692-13
ALTER TABLE client
ADD CONSTRAINT client_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE client DROP FOREIGN KEY client_ibfk_1;

--changeset vitor:1590866979692-14
ALTER TABLE stock_assets
ADD CONSTRAINT stock_assets_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE stock_assets DROP FOREIGN KEY stock_assets_ibfk_1;

--changeset vitor:1590866979692-15
ALTER TABLE stock_assets
ADD CONSTRAINT stock_assets_ibfk_2 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE stock_assets DROP FOREIGN KEY stock_assets_ibfk_2;

--changeset vitor:1590866979692-16
ALTER TABLE transaction
ADD CONSTRAINT transaction_ibfk_1 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE transaction DROP FOREIGN KEY transaction_ibfk_1;

--changeset vitor:1590866979692-17
ALTER TABLE transaction
ADD CONSTRAINT transaction_ibfk_2 FOREIGN KEY (stock_assets_id) REFERENCES stock_assets (id)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE transaction DROP FOREIGN KEY transaction_ibfk_2;

--changeset vitor:1590866979692-18
ALTER TABLE wallet
ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_cpf) REFERENCES client (cpf)
ON UPDATE RESTRICT ON DELETE RESTRICT;
--rollback ALTER TABLE wallet DROP FOREIGN KEY wallet_ibfk_1;

