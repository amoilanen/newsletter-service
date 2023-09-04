package com.github.amoilanen.conf

import pureconfig._
import pureconfig.generic.derivation.default._

case class DatabaseConfig(url: String, user: String, password: String, driver: String, maxPoolSize: Int) derives ConfigReader