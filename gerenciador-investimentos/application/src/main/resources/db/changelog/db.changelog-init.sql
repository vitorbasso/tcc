--liquibase formatted sql

--changeset vitor:1589742441104-1
CREATE TABLE client (email VARCHAR(255) NOT NULL, cpf VARCHAR(20) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NULL, avatar_image VARCHAR(255) NULL, user_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (email), UNIQUE (cpf));

--changeset vitor:1589742441104-2
CREATE TABLE stock (symbol VARCHAR(6) NOT NULL, current_value DECIMAL(13, 2) NOT NULL, type CHAR(1) NULL, name VARCHAR(255) NULL, corporation VARCHAR(255) NULL, business_area VARCHAR(255) NULL, CONSTRAINT PK_STOCK PRIMARY KEY (symbol));

--changeset vitor:1589742441104-3
CREATE TABLE stock_assets (id BIGINT AUTO_INCREMENT NOT NULL, wallet_id BIGINT NOT NULL, stock_symbol VARCHAR(6) NOT NULL, average_cost DECIMAL(13, 2) NOT NULL, amount INT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_STOCK_ASSETS PRIMARY KEY (id));

--changeset vitor:1589742441104-4
CREATE TABLE user (id BIGINT AUTO_INCREMENT NOT NULL, password VARCHAR(255) NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_USER PRIMARY KEY (id));

--changeset vitor:1589742441104-5
CREATE TABLE wallet (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NULL, broker VARCHAR(255) NULL, loss_daytrade DECIMAL(13, 2) NULL, loss DECIMAL(13, 2) NULL, balance_daytrade DECIMAL(13, 2) NULL, balance DECIMAL(13, 2) NULL, client_cpf VARCHAR(255) NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

--changeset vitor:1589742441104-6
CREATE INDEX client_cpf ON wallet(client_cpf);

--changeset vitor:1589742441104-7
CREATE INDEX stock_symbol ON stock_assets(stock_symbol);

--changeset vitor:1589742441104-8
CREATE INDEX user_id ON client(user_id);

--changeset vitor:1589742441104-9
CREATE INDEX wallet_id ON stock_assets(wallet_id);

--changeset vitor:1589742441104-10
ALTER TABLE client ADD CONSTRAINT client_ibfk_1 FOREIGN KEY (user_id) REFERENCES user (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

--changeset vitor:1589742441104-11
ALTER TABLE stock_assets ADD CONSTRAINT stock_assets_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

--changeset vitor:1589742441104-12
ALTER TABLE stock_assets ADD CONSTRAINT stock_assets_ibfk_2 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol) ON UPDATE RESTRICT ON DELETE RESTRICT;

--changeset vitor:1589742441104-13
ALTER TABLE wallet ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_cpf) REFERENCES client (cpf) ON UPDATE RESTRICT ON DELETE RESTRICT;

