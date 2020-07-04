package com.vitorbasso.gerenciadorinvestimentos.exception

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode

class CustomEntityNotFoundException(error: ManagerErrorCode) : CustomManagerException(error.name, error.cause)