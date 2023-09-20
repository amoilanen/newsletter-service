package com.github.amoilanen.repositories.utils

import cats.effect.{IO, Resource}
import doobie.{ConnectionIO, Transactor}
import doobie.implicits.*
import munit.CatsEffectSuite

trait DoobieSuite extends CatsEffectSuite {

  def setupTransactor(): Resource[IO, Transactor[IO]]

  protected val transactor = ResourceSuiteLocalFixture("transactor", setupTransactor())

  override def munitFixtures = super.munitFixtures ++ List(transactor)

  override def munitValueTransforms: List[ValueTransform] =
    super.munitValueTransforms ++ List(connectionIOTransform)

  private val connectionIOTransform: ValueTransform =
    new ValueTransform(
      "ConnectionIO",
      { case e: ConnectionIO[_] => e.transact(transactor()).unsafeToFuture() }
    )
}
