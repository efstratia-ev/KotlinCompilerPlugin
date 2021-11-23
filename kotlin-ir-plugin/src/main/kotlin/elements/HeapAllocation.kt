package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmHeapAllocation
import org.jetbrains.kotlin.backend.common.serialization.proto.IrClass
import org.jetbrains.kotlin.backend.jvm.codegen.AnnotationCodegen.Companion.annotationClass
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.util.dump

class HeapAllocation(expression: IrConstructorCall, fileEntry: SourceManager.FileEntry, fileName:String,function: Function?):JvmHeapAllocation() {
  init {
    super.setPosition(getPosition(expression,fileEntry))
    super.setSourceFileName(fileName)
    super.setSource(true) //TODO
    super.setAllocatedTypeId(createAllocatedTypeId(expression))
    super.setAllocatingMethodId(function?.symbolId ?: "") //TODO: when inIIB is true?
    super.setInIIB(false)
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

  private fun getinIIB(expression: IrConstructorCall,function: Function?): Boolean {
    if(function==null) return true
    return false
  }
}
