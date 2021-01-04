-- liquibase formatted sql

-- changeset vitor:1609726539544-1
CREATE TABLE asset (id BIGINT AUTO_INCREMENT NOT NULL, wallet_id BIGINT NOT NULL, stock_ticker VARCHAR(6) NOT NULL, average_cost DECIMAL(13, 2) DEFAULT 0.00 NOT NULL, amount INT DEFAULT 0 NOT NULL, lifetime_balance DECIMAL(20, 2) DEFAULT 0.00 NOT NULL, average_quantity_count INT DEFAULT 0 NOT NULL, average_value_count DECIMAL(20, 2) DEFAULT 0.00 NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_ASSET PRIMARY KEY (id));

-- changeset vitor:1609726539544-2
CREATE TABLE client (id BIGINT AUTO_INCREMENT NOT NULL, cpf VARCHAR(20) NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, first_name VARCHAR(255) NOT NULL, last_name VARCHAR(255) NULL, avatar_image VARCHAR(255) NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (id), UNIQUE (cpf), UNIQUE (email));

-- changeset vitor:1609726539544-3
CREATE TABLE monthly_wallet (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, broker VARCHAR(255) NOT NULL, monthly_balance_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, monthly_balance DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn DECIMAL(20, 2) DEFAULT 0.00 NULL, wallet_id BIGINT NOT NULL, wallet_month date NOT NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_MONTHLY_WALLET PRIMARY KEY (id), UNIQUE (wallet_id));

-- changeset vitor:1609726539544-4
CREATE TABLE stock (ticker VARCHAR(25) NOT NULL, current_value DECIMAL(20, 2) NULL, closing_value DECIMAL(20, 2) NULL, opening_value DECIMAL(20, 2) NULL, highest_value DECIMAL(20, 2) NULL, lowest_value DECIMAL(20, 2) NULL, variation DECIMAL(20, 4) NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_STOCK PRIMARY KEY (ticker));

-- changeset vitor:1609726539544-5
CREATE TABLE transaction (id BIGINT AUTO_INCREMENT NOT NULL, type INT NOT NULL, quantity INT DEFAULT 0 NULL, value DECIMAL(13, 2) DEFAULT 0.00 NULL, transaction_date date NOT NULL, is_sellout BIT(1) DEFAULT 0 NOT NULL, daytrade BIT(1) DEFAULT 0 NOT NULL, daytrade_quantity INT DEFAULT 0 NOT NULL, asset_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_TRANSACTION PRIMARY KEY (id));

-- changeset vitor:1609726539544-6
CREATE TABLE wallet (id BIGINT AUTO_INCREMENT NOT NULL, name VARCHAR(255) NOT NULL, broker VARCHAR(255) NOT NULL, monthly_balance_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, monthly_balance DECIMAL(20, 2) DEFAULT 0.00 NULL, lifetime_balance_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, lifetime_balance DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn DECIMAL(20, 2) DEFAULT 0.00 NULL, wallet_month date NOT NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

-- changeset vitor:1609726539544-7
CREATE INDEX asset_id ON transaction(asset_id);

-- changeset vitor:1609726539544-8
CREATE INDEX client_id ON monthly_wallet(client_id);

-- changeset vitor:1609726539544-9
CREATE INDEX client_id ON wallet(client_id);

-- changeset vitor:1609726539544-10
CREATE INDEX stock_ticker ON asset(stock_ticker);

-- changeset vitor:1609726539544-11
CREATE INDEX wallet_id ON asset(wallet_id);

-- changeset vitor:1609726539544-12
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1609726539544-13
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_2 FOREIGN KEY (stock_ticker) REFERENCES stock (ticker) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1609726539544-14
ALTER TABLE monthly_wallet ADD CONSTRAINT monthly_wallet_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1609726539544-15
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_1 FOREIGN KEY (asset_id) REFERENCES asset (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1609726539544-16
ALTER TABLE wallet ADD CONSTRAINT wallet_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

