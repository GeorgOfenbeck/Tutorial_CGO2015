package ch.ethz.spirals.cgo2015


import org.apache.commons.math3.complex.{ComplexField, Complex}
import org.apache.commons.math3.linear.BlockFieldMatrix
import org.scalacheck.Arbitrary._
import org.scalacheck.Prop
import org.scalacheck.Prop._
import org.scalacheck._


import scala.virtualization.lms.common.ReifyPure




import org.scalatest.FunSpec

class TestSPL2MetaScala extends FunSpec {
  describe("Debug me") {

    BreakdownRules.genRandomWHTRuleTree(4).sample.map ( bd => {
      val dsl = new SPL_DSL

      val scaladsl = new StagedScala_DSL
      val spltransformation = new BreakDown2SPL_DSL with SPL_DSL2MetaScala  {
        val IR: SPL_Exp = dsl
        val targetIR = scaladsl
      }
      val (byscaladecomp, finalnode) = spltransformation.SPL2MetaScala(spltransformation.bd2spl(bd))
      val finalfunction = byscaladecomp(finalnode)

      val printer = new PrintStagedScala {
        override val IR: spltransformation.targetIR.type = spltransformation.targetIR
      }
      printer.printStagedScala(finalfunction,bd.nt.size)
      println("bla")
    })
  }
}