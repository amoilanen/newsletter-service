package com.github.amoilanen.conf

import pureconfig._
import pureconfig.generic.derivation.default._

case class AppConfig(database: DatabaseConfig) derives ConfigReader
