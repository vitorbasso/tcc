use `gerenciador_investimento`;

create table if not exists `client`(
	`id` bigint auto_increment,
	`cpf` varchar(20) not null unique,
	`email` varchar(255) not null unique,
    `password` varchar(255) not null,
	`first_name` varchar(255) not null,
    `last_name` varchar(255) default null,
    `avatar_image` varchar(255) default null,
    `date_created` timestamp not null,
    `date_updated` timestamp default NOW(),
    primary key (`id`)
)engine=InnoDB;

create table if not exists `wallet`(
	`id` bigint auto_increment,
	`name` varchar(255) not null,
    `broker` varchar(255) not null,
    `monthly_balance_daytrade` decimal(20,2) default 0,
    `monthly_balance` decimal(20,2) default 0,
    `lifetime_balance_daytrade` decimal(20,2) default 0,
    `lifetime_balance` decimal(20,2) default 0,
    `withdrawn` decimal(20,2) default 0,
    `withdrawn_daytrade` decimal(20,2) default 0,
    `wallet_month` date not null,
    `client_id` bigint not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`client_id`) references `client` (`id`)
)engine=InnoDB;

create table if not exists `monthly_wallet`(
	`id` bigint auto_increment,
	`name` varchar(255) not null,
    `broker` varchar(255) not null,
    `monthly_balance_daytrade` decimal(20,2) default 0,
    `monthly_balance` decimal(20,2) default 0,
    `withdrawn` decimal(20,2) default 0,
    `withdrawn_daytrade` decimal(20,2) default 0,
    `wallet_id` bigint not null unique,
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
    `average_quantity_count` int not null default 0,
    `average_value_count` decimal(20,2) not null default 0,
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
    `is_sellout` boolean not null default false,
    `daytrade` boolean not null default false,
    `daytrade_quantity` int not null default 0,
    `asset_id` bigint not null,
    `date_created` timestamp not null default NOW(),
    `date_updated` timestamp default NOW(),
    primary key (`id`),
    foreign key (`asset_id`) references `asset` (`id`) 
)engine=InnoDB;