package com.basso.gerenciadorinvestimentos.application.exception

import com.basso.gerenciadorinvestimentos.application.enum.ManagerErrorCode

class CustomEntityNotFoundException(error: ManagerErrorCode) : CustomManagerException(error.name, error.cause)