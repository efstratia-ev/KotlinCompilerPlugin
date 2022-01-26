package com.bnorm.template


fun main() {
  val arrayname = arrayOf(1, 2, 3, 4, 5)
  val l=La(3)
  println(l.debug(3,l))
}
class La(){
  var j=12
  class b{

  }
  fun aa() {
    class b {
      val iii=La(3)
      init { var a = "la" }
    }
  }
  constructor(i: Int) : this() {
    println("Constructor "+i)
  }
  fun debug(a:Int,b:La) = a.toString()
}

