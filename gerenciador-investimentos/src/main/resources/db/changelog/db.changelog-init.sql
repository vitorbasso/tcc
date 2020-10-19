--liquibase formatted sql

--changeset vitor:1596411424796-1
CREATE TABLE asset (id BIGINT AUTO_INCREMENT NOT NULL, wallet_id BIGINT NOT NULL, stock_ticker VARCHAR(6) NOT NULL, average_cost DECIMAL(13, 2) NOT NULL, amount INT NOT NULL, asset_balance DECIMAL(20, 2) NOT NULL, number_of_transactions INT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_ASSET PRIMARY KEY (id));

--changeset vitor:1596411424796-2
CREATE TABLE client (id BIGINT AUTO_INCREMENT NOT NULL, cpf VARCHAR(20) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NULL, avatar_image VARCHAR(255) NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (id), UNIQUE (email), UNIQUE (cpf));

--changeset vitor:1596411424796-3
CREATE TABLE stock (ticker VARCHAR(25) NOT NULL, current_value DECIMAL(20, 2) NULL, closing_value DECIMAL(20, 2) NULL, opening_value DECIMAL(20, 2) NULL, highest_value DECIMAL(20, 2) NULL, lowest_value DECIMAL(20, 2) NULL, variation DECIMAL(20, 4) NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_STOCK PRIMARY KEY (ticker));

--changeset vitor:1596411424796-4
CREATE TABLE transaction (id BIGINT AUTO_INCREMENT NOT NULL, type INT NOT NULL, quantity INT NULL, value DECIMAL(13, 2) NULL, asset_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_TRANSACTION PRIMARY KEY (id));

--changeset vitor:1596411424796-5
CREATE TABLE wallet (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, broker VARCHAR(255) NOT NULL, loss_daytrade DECIMAL(13, 2) NULL, loss DECIMAL(13, 2) NULL, balance_daytrade DECIMAL(13, 2) NULL, balance DECIMAL(13, 2) NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

--changeset vitor:1596411424796-6
CREATE INDEX asset_id ON transaction(asset_id);

--changeset vitor:1596411424796-7
CREATE INDEX client_id ON wallet(client_id);

--changeset vitor:1596411424796-8
CREATE INDEX stock_ticker ON asset(stock_ticker);

--changeset vitor:1596411424796-10
CREATE INDEX wallet_id ON asset(wallet_id);

--changeset vitor:1596411424796-11
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1596411424796-12
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_2 FOREIGN KEY (stock_ticker) REFERENCES stock (ticker) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1596411424796-14
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_2 FOREIGN KEY (asset_id) REFERENCES asset (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1596411424796-15
ALTER TABLE wallet ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

