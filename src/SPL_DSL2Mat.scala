package ch.ethz.spirals.cgo2015


import scala.virtualization.lms._
import internal._

trait SPL_DSL2Mat extends GraphTraversal{


  val IR: SPL_Exp

  import IR._
  import scala.collection.mutable.HashMap
  import org.apache.commons.math3.linear.BlockFieldMatrix
  import org.apache.commons.math3.complex.{ComplexField, Complex}

  //-----------------------------------------Matrix Representation Part --------------------------------
  def SPL2Mat (start: Exp[Any]) = {
    val deflist = buildScheduleForResult(start)
    val f_array = new Array[ BlockFieldMatrix[Complex] ](deflist.size)
    val index_array = new HashMap[Int,Int]
    var i : Int = 0
    for (TP(sym, rhs) <- deflist) {
      val index = sym match {
        case Sym(n) => n
        case _ => -1
      }
      index_array += (index -> i)
      f_array(i) = matrix_emitNode(sym, rhs,f_array , index_array)
      i = i + 1
    }
    f_array
  }

  def kronecker (A: BlockFieldMatrix[Complex], B: BlockFieldMatrix[Complex] ): BlockFieldMatrix[Complex] = {
    val x = A.getRowDimension() * B.getRowDimension()
    val y = A.getColumnDimension() * B.getColumnDimension()
    val m = new BlockFieldMatrix[Complex](ComplexField.getInstance(), x, y)
    for (i <- 0 until x)
      for (j <- 0 until y)
      {
        val aentry = A.getEntry( (i/B.getRowDimension()), j/B.getColumnDimension() )
        val bentry = B.getEntry(i % B.getRowDimension(), j % B.getColumnDimension())
        m.setEntry(i,j, aentry.multiply(bentry))
      }
    m
  }


  def matrix_directsum(A: BlockFieldMatrix[Complex], B: BlockFieldMatrix[Complex] ): BlockFieldMatrix[Complex] = {
    val x = A.getRowDimension() + B.getRowDimension()
    val y = A.getColumnDimension() + B.getColumnDimension()
    val m = new BlockFieldMatrix[Complex](ComplexField.getInstance(), x, y)

    val ar = A.getRowDimension()
    val ac = A.getColumnDimension()
    for (i <- 0 until ar )
      for (j <- 0 until ac)
        m.setEntry(i,j,A.getEntry(i,j))
    for (i <- ar until x)
      for (j <- ac until y)
        m.setEntry(i,j,B.getEntry(i - ar, j - ac))
    m
  }


  def matrix_emitNode(sym: Sym[Any], rhs: Def[Any],f_array: Array[ BlockFieldMatrix[Complex]] , lt : HashMap[Int,Int]):
  // returns matrix
  (  BlockFieldMatrix[Complex] ) =
    rhs match {

    //--------------------------------Compose -----------------------------
    case Compose(Sym(a),Sym(b)) => f_array(lt(a)).multiply( f_array(lt(b)) )
    case Compose(Sym(a),Const(x: SPL)) => f_array(lt(a)).multiply( x.toMatrix() )
    case Compose(Const(x: SPL), Sym(b)) =>  x.toMatrix().multiply( f_array(lt(b)) )
    case Compose(Const(x: SPL), Const(y: SPL)) =>  x.toMatrix().multiply( y.toMatrix() )
    //-------------------------------Tensor--------------------------------
    case Tensor(Sym(a),Sym(b)) => kronecker(f_array(lt(a)),f_array(lt(b)))
    case Tensor(Sym(a),Const(x: SPL)) => kronecker(f_array(lt(a)), x.toMatrix())
    case Tensor(Const(x: SPL), Sym(b)) =>  kronecker(x.toMatrix(),f_array(lt(b)))
    case Tensor(Const(x: SPL), Const(y: SPL)) =>  kronecker(x.toMatrix(),y.toMatrix())
    //-------------------------------SPL_DirectSum--------------------------------
    case DirectSum(Sym(a),Sym(b)) => matrix_directsum(f_array(lt(a)),f_array(lt(b)))
    case DirectSum(Sym(a),Const(x: SPL)) => matrix_directsum(f_array(lt(a)),x.toMatrix())
    case DirectSum(Const(x: SPL), Sym(b)) => matrix_directsum(x.toMatrix(),f_array(lt(b)))
    case DirectSum(Const(x: SPL), Const(y: SPL)) => matrix_directsum(x.toMatrix(),y.toMatrix())
  }



}

