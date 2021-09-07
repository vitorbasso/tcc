-- liquibase formatted sql

-- changeset vitor:1630805448346-1
CREATE TABLE asset (id BIGINT AUTO_INCREMENT NOT NULL, wallet_id BIGINT NOT NULL, stock_ticker VARCHAR(6) NOT NULL, average_cost DECIMAL(13, 2) DEFAULT 0.00 NOT NULL, amount BIGINT DEFAULT 0 NOT NULL, lifetime_balance DECIMAL(20, 2) DEFAULT 0.00 NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_ASSET PRIMARY KEY (id));

-- changeset vitor:1630805448346-2
CREATE TABLE client (id BIGINT AUTO_INCREMENT NOT NULL, email VARCHAR(255) NOT NULL, password VARCHAR(255) NOT NULL, name VARCHAR(255) NOT NULL, wallet_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_CLIENT PRIMARY KEY (id), UNIQUE (email));

-- changeset vitor:1630805448346-3
CREATE TABLE monthly_wallet (id BIGINT AUTO_INCREMENT NOT NULL, balance_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, balance DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, wallet_id BIGINT NOT NULL, wallet_month date NOT NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_MONTHLY_WALLET PRIMARY KEY (id));

-- changeset vitor:1630805448346-4
CREATE TABLE stock (ticker VARCHAR(25) NOT NULL, current_value DECIMAL(20, 2) NULL, last_close DECIMAL(20, 2) NULL, last_week_close DECIMAL(20, 2) NULL, last_month_close DECIMAL(20, 2) NULL, last_year_close DECIMAL(20, 2) NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_STOCK PRIMARY KEY (ticker));

-- changeset vitor:1630805448346-5
CREATE TABLE tax_deductible (id BIGINT AUTO_INCREMENT NOT NULL, deducted DECIMAL(20, 2) DEFAULT 0.00 NULL, daytrade_deducted DECIMAL(20, 2) DEFAULT 0.00 NULL, month date NOT NULL, client_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_TAX_DEDUCTIBLE PRIMARY KEY (id));

-- changeset vitor:1630805448346-6
CREATE TABLE transaction (id BIGINT AUTO_INCREMENT NOT NULL, type INT NOT NULL, quantity BIGINT DEFAULT 0 NULL, value DECIMAL(13, 2) DEFAULT 0.00 NULL, transaction_date timestamp DEFAULT NOW() NOT NULL, checking_value DECIMAL(13, 2) DEFAULT 0.00 NULL, checking_quantity BIGINT DEFAULT 0 NULL, daytrade_quantity BIGINT DEFAULT 0 NOT NULL, asset_id BIGINT NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_TRANSACTION PRIMARY KEY (id));

-- changeset vitor:1630805448346-7
CREATE TABLE wallet (id BIGINT AUTO_INCREMENT NOT NULL, balance_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, balance DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn DECIMAL(20, 2) DEFAULT 0.00 NULL, withdrawn_daytrade DECIMAL(20, 2) DEFAULT 0.00 NULL, wallet_month date NOT NULL, date_created timestamp DEFAULT NOW() NOT NULL, date_updated timestamp DEFAULT NOW() NOT NULL, CONSTRAINT PK_WALLET PRIMARY KEY (id));

-- changeset vitor:1630805448346-8
ALTER TABLE monthly_wallet ADD CONSTRAINT unique_monthly_wallet UNIQUE (wallet_month, wallet_id);

-- changeset vitor:1630805448346-9
ALTER TABLE tax_deductible ADD CONSTRAINT unique_tax UNIQUE (client_id, month);

-- changeset vitor:1630805448346-10
CREATE INDEX asset_id ON transaction(asset_id);

-- changeset vitor:1630805448346-11
CREATE INDEX client_id ON monthly_wallet(client_id);

-- changeset vitor:1630805448346-12
CREATE INDEX stock_ticker ON asset(stock_ticker);

-- changeset vitor:1630805448346-13
CREATE INDEX transaction_date ON transaction(transaction_date);

-- changeset vitor:1630805448346-14
CREATE INDEX wallet_id ON asset(wallet_id);

-- changeset vitor:1630805448346-15
CREATE INDEX wallet_id ON client(wallet_id);

-- changeset vitor:1630805448346-16
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1630805448346-17
ALTER TABLE asset ADD CONSTRAINT asset_ibfk_2 FOREIGN KEY (stock_ticker) REFERENCES stock (ticker) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1630805448346-18
ALTER TABLE client ADD CONSTRAINT client_ibfk_1 FOREIGN KEY (wallet_id) REFERENCES wallet (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1630805448346-19
ALTER TABLE monthly_wallet ADD CONSTRAINT monthly_wallet_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1630805448346-20
ALTER TABLE tax_deductible ADD CONSTRAINT tax_deductible_ibfk_1 FOREIGN KEY (client_id) REFERENCES client (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

-- changeset vitor:1630805448346-21
ALTER TABLE transaction ADD CONSTRAINT transaction_ibfk_1 FOREIGN KEY (asset_id) REFERENCES asset (id) ON UPDATE RESTRICT ON DELETE RESTRICT;

