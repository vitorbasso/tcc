use `gerenciamento_investimento`;

create table if not exists `client`(
	`email` varchar(255) not null,
    `cpf` varchar(20) not null unique,
	`name` varchar(255) not null,
    `avatar_image` varchar(255),
    primary key (`email`)
)engine=InnoDB;

create table if not exists `user`(
	`id` bigint not null,
    `password` varchar(255) not null,
    `client_email` varchar(255) not null,
    primary key (`id`),
    foreign key (`client_email`) references `client` (`email`)
)engine=InnoDB;

create table if not exists `wallet`(
	`id` bigint not null,
	`name` varchar(255),
    `broker` varchar(255),
    `loss_daytrade` decimal(13,2),
    `loss` decimal(13,2),
    `balance_daytrade` decimal(13,2),
    `balance` decimal(13,2),
    `client_cpf` varchar(255) not null,
    primary key (`id`),
    foreign key (`client_cpf`) references `client` (`cpf`)
)engine=InnoDB;

create table if not exists `stock` (
	`symbol` varchar(6) not null,
    `current_value` decimal(13,2) not null,
    `type` char,
    `name` varchar(255),
    `corporation` varchar(255),
    `business_area` varchar(255),
    primary key (`symbol`)
)engine=InnoDB;

create table if not exists `stock_assets`(
	`id` bigint not null,
    `wallet_id` bigint not null,
    `stock_symbol` varchar(6) not null,
    `average_cost` decimal(13,2) not null,
    `amount` int not null,
    primary key (`id`),
    foreign key (`wallet_id`) references `wallet` (`id`),
    foreign key (`stock_symbol`) references `stock` (`symbol`)
)engine=InnoDB;