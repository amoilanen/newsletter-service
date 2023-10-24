package com.github.amoilanen

import org.mockito.Mockito.{mock => mockitoMock}
import scala.reflect.ClassTag

trait MockitoSugar:
  def mock[T <: AnyRef](using classTag: ClassTag[T]): T =
    val runtimeClass = classTag.runtimeClass
    mockitoMock(runtimeClass.asInstanceOf[Class[T]])
