package io.codegalaxy.app

import scommons.react.test.TestSpec

abstract class BaseStateReducerSpec[T <: AnyRef](createState: => T,
                                                 val reduce: (Option[T], Any) => T
                                                ) extends TestSpec {

  it should "return default state when state is None" in {
    //when & then
    reduce(None, "") shouldBe createState
  }

  it should "return current state for all other actions" in {
    //given
    val currState = createState
    val action = "some other action"

    //when
    val result = reduce(Some(currState), action)

    //then
    result should be theSameInstanceAs currState
  }
}
