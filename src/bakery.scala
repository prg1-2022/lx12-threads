// sbtからの実行方法: runMain Bakery {loop,seq,conc}

object Bakery {
  val NANO = 1.0E-9
  var t_start: Double = System.nanoTime().toDouble
  def seconds() = (System.nanoTime() - t_start) * NANO

  def printbakery(i: Int, message: String): Unit = {
    println(f"${seconds()}%02.1f: ${i}個目の$message")
  }

  def loop(secs_limit: Int): Unit = {
    for (t <- 1 to Int.MaxValue) {
      if (seconds() > secs_limit) return
      Thread.sleep(1000)
      println(f"t = $t%2d")
    }
  }

  def sequential(secs_limit: Int): Unit = {
    for (i <- 1 to Int.MaxValue) {
      if (seconds() > secs_limit) return
      printbakery(i, "仕事を始めましょう")

      Thread.sleep(3000)
      printbakery(i, "パン生地ができました。")

      Thread.sleep(5000)
      printbakery(i, "パンが焼きあがりました。")

      Thread.sleep(7000)
      printbakery(i, "パンをお店に出しました。")

      Thread.sleep(10000)
      printbakery(i, "パンをレストランに届けました。")
    }
  }

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext.Implicits.global

  def concurrent(secs_limit: Int): Unit = {
    // パン込ね職人のスレッド
    Future {
      for (i <- 1 to Int.MaxValue) {
        Thread.sleep(3000)
        printbakery(i, "パン生地ができました。")
      }
    }

    // ほかの職人たちの仕事の内容
    for ((t, message) <-
         List((5000, "パンが焼きあがりました。"),
           (7000, "パンをお店に出しました。"),
           (10000, "パンをレストランに届けました。"))) {
       Future {
         for (i <- 1 to Int.MaxValue) {
           Thread.sleep(t)
           printbakery(i, message)
         }
       }
     }

    // 店主のスレッド
    println("さあ、パン屋を開きましょう。")
    Thread.sleep(secs_limit * 1000)
    println("今日はもう閉店です。")
  }

  def main(arguments: Array[String]): Unit = {
    println("sbtからの実行方法: runMain Bakery {loop,seq,conc}")
    (arguments match {
      case Array("seq", _*) => sequential _
      case Array("conc", _*) => concurrent _
      case _ => loop _
    })(30)
  }
}
