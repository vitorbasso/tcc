package com.vitorbasso.gerenciadorinvestimentos.exception

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode

class CustomForbiddenException(error: ManagerErrorCode) : CustomManagerException(error.name, error.cause)