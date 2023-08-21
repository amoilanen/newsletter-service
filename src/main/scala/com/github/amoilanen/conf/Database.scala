package com.github.amoilanen.conf

import pureconfig._
import pureconfig.generic.derivation.default._

case class Database(url: String, user: String, password: String, driver: String) derives ConfigReader