package com.example.data.features.repos.models.apis

enum class RepoSortApi constructor(val serverValue: String) {
    STARS("stars"),
    FORKS("forks"),
    HELP_WANTED_ISSUES("help-wanted-issues"),
    UPDATED("updated")
}