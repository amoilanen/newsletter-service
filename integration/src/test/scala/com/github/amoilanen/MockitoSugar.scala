package com.github.amoilanen

import org.mockito.Mockito.{mock => mockitoMock}
import scala.reflect.ClassTag

trait MockitoSugar:
  def mock[T <: AnyRef](using ClassTag[T]): T =
    val runtimeClass = summon[ClassTag[T]].runtimeClass
    mockitoMock(runtimeClass.asInstanceOf[Class[T]])
