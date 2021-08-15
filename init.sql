use `gerenciador_investimento`;

create table if not exists `wallet`(
	`id` bigint auto_increment,
    `balance_daytrade` decimal(20,2) default 0,
    `balance` decimal(20,2) default 0,
    `withdrawn` decimal(20,2) default 0,
    `withdrawn_daytrade` decimal(20,2) default 0,
    `wallet_month` date not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`)
)engine=InnoDB;

create table if not exists `client`(
	`id` bigint auto_increment,
	`email` varchar(255) not null unique,
    `password` varchar(255) not null,
	`name` varchar(255) not null,
    `wallet_id` bigint not null,
    `date_created` timestamp not null,
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`wallet_id`) references `wallet` (`id`)
)engine=InnoDB;

create table if not exists `tax_deductible` (
	`id` bigint auto_increment,
    `deducted` decimal(20,2) default 0,
    `daytrade_deducted` decimal(20,2) default 0,
    `month` date not null,
    `client_id` bigint not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`client_id`) references `client` (`id`)
)engine=InnoDB;

create table if not exists `monthly_wallet`(
	`id` bigint auto_increment,
    `balance_daytrade` decimal(20,2) default 0,
    `balance` decimal(20,2) default 0,
    `withdrawn` decimal(20,2) default 0,
    `withdrawn_daytrade` decimal(20,2) default 0,
    `wallet_id` bigint not null,
    `wallet_month` date not null,
    `client_id` bigint not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`client_id`) references `client` (`id`)
)engine=InnoDB;

create table if not exists `stock` (
	`ticker` varchar(25),
    `current_value` decimal(20, 2) default null,
    `closing_value` decimal(20, 2) default null,
    `opening_value` decimal(20, 2) default null,
    `highest_value` decimal(20, 2) default null,
    `lowest_value` decimal(20, 2) default null,
    `variation` decimal(20, 4) default null,
    `date_updated` timestamp default NOW(),
    primary key (`ticker`)
)engine=InnoDB;

create table if not exists `asset`(
	`id` bigint auto_increment,
    `wallet_id` bigint not null,
    `stock_ticker` varchar(6) not null,
    `average_cost` decimal(13,2) not null default 0,
    `amount` int not null default 0,
    `lifetime_balance` decimal(20, 2) not null default 0,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`wallet_id`) references `wallet` (`id`),
    foreign key (`stock_ticker`) references `stock` (`ticker`)
)engine=InnoDB;

create table if not exists `transaction`(
	`id` bigint auto_increment,
	`type` int not null,
    `quantity` int default 0,
    `value` decimal(13,2) default 0,
    `transaction_date` timestamp not null default NOW(),
    `checking_value` decimal(13,2) default 0,
    `checking_quantity` int default 0,
    `daytrade_quantity` int not null default 0,
    `asset_id` bigint not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`asset_id`) references `asset` (`id`) 
)engine=InnoDB;

CREATE INDEX `transaction_date` ON `transaction` (`transaction_date`);
ALTER TABLE `transaction` ADD UNIQUE `unique_transactino` (`transaction_date`, `asset_id`);
ALTER TABLE `monthly_wallet` ADD UNIQUE `unique_monthly_wallet` (`wallet_month`, `wallet_id`);
ALTER TABLE `tax_deductible` ADD UNIQUE `unique_tax` (`client_id`, `month`);