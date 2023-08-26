package com.github.amoilanen.db

import cats.effect.{IO, Resource}
import com.github.amoilanen.conf.DatabaseConfig
import com.zaxxer.hikari.HikariConfig
import doobie.Transactor
import doobie.hikari.HikariTransactor

object Database:
  def createTransactor(databaseConfig: DatabaseConfig): Resource[IO, Transactor[IO]] =
    for
      hikariConfig <- Resource.pure {
        // For the full list of hikari configurations see https://github.com/brettwooldridge/HikariCP#gear-configuration-knobs-baby
        val config = new HikariConfig()
        config.setDriverClassName(databaseConfig.driver)
        config.setJdbcUrl(databaseConfig.url)
        config.setUsername(databaseConfig.user)
        config.setPassword(databaseConfig.password)
        config
      }
      transactor <- HikariTransactor.fromHikariConfig[IO](hikariConfig)
    yield transactor

