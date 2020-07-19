--liquibase formatted sql

--changeset vitor:1595116624813-1
CREATE TABLE asset (id BIGINT AUTO_INCREMENT NOT NULL, wallet_id BIGINT NOT NULL, stock_symbol VARCHAR(6) NOT NULL, average_cost DECIMAL(13, 2) NOT NULL, amount INT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_ASSET PRIMARY KEY (id));

--changeset vitor:1595116624813-2
CREATE TABLE client (id BIGINT AUTO_INCREMENT NOT NULL, cpf VARCHAR(20) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NULL, avatar_image VARCHAR(255) NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (id), UNIQUE (email), UNIQUE (cpf));

--changeset vitor:1595116624813-3
CREATE TABLE stock (symbol VARCHAR(6) NOT NULL, current_value DECIMAL(13, 2) NOT NULL, type CHAR(1) NULL, name VARCHAR(255) NULL, corporation VARCHAR(255) NULL, business_area VARCHAR(255) NULL, CONSTRAINT PK_STOCK PRIMARY KEY (symbol));

--changeset vitor:1595116624813-4
CREATE TABLE transaction (id BIGINT AUTO_INCREMENT NOT NULL, type VARCHAR(3) NOT NULL, quantity INT NULL, value DECIMAL(13, 2) NULL, stock_symbol VARCHAR(6) NOT NULL, asset_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_TRANSACTION PRIMARY KEY (id));

--changeset vitor:1595116624813-5
CREATE TABLE wallet (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, broker VARCHAR(255) NOT NULL, loss_daytrade DECIMAL(13, 2) NULL, loss DECIMAL(13, 2) NULL, balance_daytrade DECIMAL(13, 2) NULL, balance DECIMAL(13, 2) NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

--changeset vitor:1595116624813-6
CREATE INDEX asset_id ON transaction(asset_id);

--changeset vitor:1595116624813-7
CREATE INDEX client_id ON wallet(client_id);

--changeset vitor:1595116624813-8
CREATE INDEX stock_symbol ON asset(stock_symbol);

--changeset vitor:1595116624813-9
CREATE INDEX stock_symbol ON transaction(stock_symbol);

--changeset vitor:1595116624813-10
CREATE INDEX wallet_id ON asset(wallet_id);

--changeset vitor:1595116624813-11
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595116624813-12
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_2 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595116624813-13
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_1 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595116624813-14
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_2 FOREIGN KEY (asset_id) REFERENCES asset (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595116624813-15
ALTER TABLE wallet ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

