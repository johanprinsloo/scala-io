/*                     __                                               *\
**     ________ ___   / /  ___     Scala API                            **
**    / __/ __// _ | / /  / _ |    (c) 2003-2009, LAMP/EPFL             **
**  __\ \/ /__/ __ |/ /__/ __ |    http://scala-lang.org/               **
** /____/\___/_/ |_/____/_/ | |                                         **
**                          |/                                          **
\*                                                                      */

package scalax.io

/**
 * A modularizing object for containing objects/classes related to reading lines
 */
object Line {
  object Terminators {
    /**
     * The super class for different types of line terminators/seperators
     * @see Auto
     * @see N
     * @see R
     * @see RN
     * @see Custom
     */
    sealed abstract class Terminator {
        def size : Int
        def lineEnd(chars: Seq[Char]) : Boolean
    }
    /**
     * The Auto terminator declares that the line terminator should
     * be detected.  It can detect N,R and RN line terminators
     */
    case class Auto() extends Terminator {
        var choices = NewLine :: Pair :: CarriageReturn :: Nil
        
        def size = {choices map {_.size} max}
        def lineEnd(chars: Seq[Char]) : Boolean = {
            def aMatch(t:Terminator) = t.lineEnd(chars.take(t.size))
            val option = choices find aMatch map { terminator =>
                choices = List(terminator) // update so there is only one to match
                println("EOF found with "+terminator + " for "+chars)
                true
            }
            option.getOrElse(false)
        }
    }
    /*
     * The \n line terminator
     */
    case object NewLine extends Terminator {
        val custom = Custom("\n")
        val size = 1
        def lineEnd(chars: Seq[Char]) = custom.lineEnd (chars)
    }
    /**
     * The \r line terminator
     */
    case object CarriageReturn extends Terminator {
        val custom = Custom("\r")
        val size = 1
        def lineEnd(chars: Seq[Char]) = custom.lineEnd  (chars)
    }
    /**
     * The \r\n line terminator
     */
    case object Pair extends Terminator {
        val custom = Custom("\r\n")
        val size = 2
        def lineEnd(chars: Seq[Char]) = custom.lineEnd  (chars)
    }
    /**
     * A custom line terminator.  It can be an arbitrary string but
     * can be less performant than one of the other terminators
     */
    case class Custom(separator: String) extends Terminator {
        val size = separator.length
        def lineEnd(chars: Seq[Char]) = {
            chars.length == separator.length && (chars.view zip separator forall {case (a,b) => a == b})
        }
    }
  }
}
