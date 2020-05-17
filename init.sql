use `gerenciador_investimento`;

create table if not exists `user`(
	`id` bigint auto_increment,
    `password` varchar(255) not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`id`)
)engine=InnoDB;

create table if not exists `client`(
	`email` varchar(255),
    `cpf` varchar(20) not null unique,
	`first_name` varchar(255) not null,
    `last_name` varchar(255),
    `avatar_image` varchar(255),
    `user_id` bigint not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`email`),
    foreign key (`user_id`) references `user` (`id`)
)engine=InnoDB;

create table if not exists `wallet`(
	`id` bigint auto_increment,
	`name` varchar(255),
    `broker` varchar(255),
    `loss_daytrade` decimal(13,2),
    `loss` decimal(13,2),
    `balance_daytrade` decimal(13,2),
    `balance` decimal(13,2),
    `client_cpf` varchar(255) not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
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
	`id` bigint auto_increment,
    `wallet_id` bigint not null,
    `stock_symbol` varchar(6) not null,
    `average_cost` decimal(13,2) not null,
    `amount` int not null,
    `date_created` timestamp not null,
    `date_updated` timestamp,
    primary key (`id`),
    foreign key (`wallet_id`) references `wallet` (`id`),
    foreign key (`stock_symbol`) references `stock` (`symbol`)
)engine=InnoDB;