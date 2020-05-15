package com.basso.gerenciadorinvestimentos.domain.concrete

import java.io.Serializable
import java.sql.Timestamp

abstract class BaseEntity(
        protected val dateCreated: Timestamp = Timestamp(java.util.Date().time)
) : Serializable