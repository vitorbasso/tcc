package com.vitorbasso.gerenciadorinvestimentos.exception

import com.vitorbasso.gerenciadorinvestimentos.enum.ManagerErrorCode

class CustomWrongDateException(
    error: ManagerErrorCode = ManagerErrorCode.MANAGER_09
) : CustomManagerException(error.name, error.cause)