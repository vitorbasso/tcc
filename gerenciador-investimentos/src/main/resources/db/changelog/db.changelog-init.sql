--liquibase formatted sql

--changeset vitor:1595083874653-1
CREATE TABLE asset (
id BIGINT AUTO_INCREMENT NOT NULL,
wallet_id BIGINT NOT NULL,
stock_symbol VARCHAR(6) NOT NULL,
average_cost DECIMAL(13, 2) NOT NULL,
amount INT NOT NULL,
date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_ASSET PRIMARY KEY (id));

--changeset vitor:1595083874653-2
CREATE TABLE client (
cpf VARCHAR(20) NOT NULL,
email VARCHAR(255) NOT NULL,
password VARCHAR(255) NOT NULL,
first_name VARCHAR(255) NOT NULL,
last_name VARCHAR(255) NULL,
avatar_image VARCHAR(255) NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (cpf), UNIQUE (email));

--changeset vitor:1595083874653-3
CREATE TABLE stock (
symbol VARCHAR(6) NOT NULL,
current_value DECIMAL(13, 2) NOT NULL,
type CHAR(1) NULL,
name VARCHAR(255) NULL,
corporation VARCHAR(255) NULL,
business_area VARCHAR(255) NULL, CONSTRAINT PK_STOCK PRIMARY KEY (symbol));

--changeset vitor:1595083874653-4
CREATE TABLE transaction (
id BIGINT AUTO_INCREMENT NOT NULL,
type VARCHAR(3) NOT NULL,
quantity INT NULL,
value DECIMAL(13, 2) NULL,
stock_symbol VARCHAR(6) NOT NULL,
asset_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_TRANSACTION PRIMARY KEY (id));

--changeset vitor:1595083874653-5
CREATE TABLE wallet (
id BIGINT AUTO_INCREMENT NOT NULL,
name VARCHAR(255) NULL,
broker VARCHAR(255) NULL,
loss_daytrade DECIMAL(13, 2) NULL,
loss DECIMAL(13, 2) NULL,
balance_daytrade DECIMAL(13, 2) NULL, balance DECIMAL(13, 2) NULL, client_cpf VARCHAR(255) NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT '0000-00-00 00:00:00' NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

--changeset vitor:1595083874653-6
CREATE INDEX asset_id ON transaction(asset_id);

--changeset vitor:1595083874653-7
CREATE INDEX client_cpf ON wallet(client_cpf);

--changeset vitor:1595083874653-8
CREATE INDEX stock_symbol ON asset(stock_symbol);

--changeset vitor:1595083874653-9
CREATE INDEX stock_symbol ON transaction(stock_symbol);

--changeset vitor:1595083874653-10
CREATE INDEX wallet_id ON asset(wallet_id);

--changeset vitor:1595083874653-11
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595083874653-12
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_2 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595083874653-13
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_1 FOREIGN KEY (stock_symbol) REFERENCES stock (symbol) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595083874653-14
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_2 FOREIGN KEY (asset_id) REFERENCES asset (id) ON UPDATE NO ACTION ON DELETE NO ACTION;

--changeset vitor:1595083874653-15
ALTER TABLE wallet ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_cpf) REFERENCES client (cpf) ON UPDATE NO ACTION ON DELETE NO ACTION;

