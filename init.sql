use `gerenciador_investimento`;

create table if not exists `client`(
	`id` bigint auto_increment,
	`cpf` varchar(20) not null unique,
	`email` varchar(255) not null unique,
    `password` varchar(255) not null,
	`first_name` varchar(255) not null,
    `last_name` varchar(255),
    `avatar_image` varchar(255),
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`id`)
)engine=InnoDB;

create table if not exists `wallet`(
	`id` bigint auto_increment,
	`name` varchar(255) not null,
    `broker` varchar(255) not null,
    `loss_daytrade` decimal(13,2),
    `loss` decimal(13,2),
    `balance_daytrade` decimal(13,2),
    `balance` decimal(13,2),
    `client_id` bigint not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
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
    `average_cost` decimal(13,2) not null,
    `amount` int not null,
    `lifetime_balance` decimal(20, 2) not null,
    `average_count` int not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`id`),
    foreign key (`wallet_id`) references `wallet` (`id`),
    foreign key (`stock_ticker`) references `stock` (`ticker`)
)engine=InnoDB;

create table if not exists `transaction`(
	`id` bigint auto_increment,
	`type` int not null,
    `quantity` int,
    `value` decimal(13,2),
    `transaction_date` date not null,
    `daytrade` tinyint not null,
    `daytrade_quantity` int not null,
    `asset_id` bigint not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`id`),
    foreign key (`asset_id`) references `asset` (`id`) 
)engine=InnoDB;