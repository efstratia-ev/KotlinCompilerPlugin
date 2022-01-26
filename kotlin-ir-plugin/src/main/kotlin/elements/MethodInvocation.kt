package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmMethodInvocation
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType

class MethodInvocation(expression: IrCall, fileEntry: SourceManager.FileEntry, fileName:String,function: Function?, classReporter: ClassReporter?):JvmMethodInvocation() {
  init {
    super.setPosition(getPosition(expression,fileEntry))
    super.setSourceFileName(fileName)
    super.setSource(true) //TODO
    super.setName(expression.symbol.owner.name.toString())
    super.setInvokingMethodId(function?.symbolId ?: "") //TODO:lathos
    super.setInIIB(getinIIB(function,classReporter)) //TODO
    super.setSymbolId(createSymbolId(expression)) //TODO:arithmisi
  }

  private fun getPosition(expression: IrCall, fileEntry: SourceManager.FileEntry): Position {
    val info=fileEntry.getSourceRangeInfo(expression.startOffset,expression.endOffset)
    return Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun createSymbolId(expression: IrCall):String{
    return super.getInvokingMethodId()+"/"+expression.symbol.owner.returnType.originalKotlinType.toString()+"."+super.getName()+"/"
  }

  private fun getinIIB(function: Function?, classReporter: ClassReporter?): Boolean {
    if(function==null) return true
    if(classReporter==null) return false
    if(function.declaringClassId==classReporter.id) return false
    return true
  }

}
