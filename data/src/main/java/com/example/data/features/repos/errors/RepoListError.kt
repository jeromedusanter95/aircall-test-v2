package com.example.data.features.repos.errors

import java.io.IOException

class RepoListError(cause: Throwable? = null) {
    val isNetwork: Boolean = cause != null && cause is IOException
}

class RepoDetailsError(cause: Throwable? = null)