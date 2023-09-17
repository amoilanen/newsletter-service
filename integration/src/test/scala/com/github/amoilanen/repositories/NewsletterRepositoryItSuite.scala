package com.github.amoilanen.repositories

import cats.effect.IO
import munit.CatsEffectSuite

class NewsletterRepositoryItSuite extends CatsEffectSuite {

  test("test hello world says hi") {
    IO("Hello Cats!").map(it => assertEquals(it, "Hello Cats!"))
  }
}
