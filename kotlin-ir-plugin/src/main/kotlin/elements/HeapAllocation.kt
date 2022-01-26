package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmHeapAllocation
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.isArray

class HeapAllocation(expression: IrConstructorCall, fileEntry: SourceManager.FileEntry, fileName:String,function: Function?,classReporter: ClassReporter?):JvmHeapAllocation() {
  init {
    super.setPosition(getPosition(expression,fileEntry))
    super.setSourceFileName(fileName)
    super.setSource(true) //TODO
    super.setAllocatedTypeId(createAllocatedTypeId(expression))
    super.setAllocatingMethodId(function?.symbolId ?: "") //TODO: when inIIB is true?
    super.setInIIB(getinIIB(function,classReporter))
    super.setArray(expression.type.isArray())
    super.setSymbolId(createSymbolId()) //TODO:arithmisi
  }

  private fun getPosition(expression: IrConstructorCall, fileEntry: SourceManager.FileEntry): Position {
    val info=fileEntry.getSourceRangeInfo(expression.startOffset, expression.endOffset)
    return Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun createSymbolId():String{
    if(super.isInIIB()) return ""//TODO::
    return super.getAllocatingMethodId()+"/new "+super.getAllocatedTypeId()+"/"
  }

  private fun createAllocatedTypeId(expression: IrConstructorCall):String{
    return expression.type.getClass()!!.symbol.signature.packageFqName().toString()+"."+expression.type.getClass()!!.name.toString()
  }

  private fun getinIIB(function: Function?,classReporter: ClassReporter?): Boolean {
    if(function==null) return true
    if(classReporter==null) return false
    if(function.declaringClassId==classReporter.id) return false
    return true
  }
}
