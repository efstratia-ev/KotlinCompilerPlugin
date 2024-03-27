package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmHeapAllocation
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.getClass
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.util.fqNameForIrSerialization
import util.KotlinFileInfo

class HeapAllocation(expression: IrConstructorCall, fileInfo: KotlinFileInfo, function: Function):JvmHeapAllocation() {
  init {
    super.setPosition(getPosition(expression,fileInfo))
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setSource(fileInfo.existsInSources(position))
    super.setAllocatedTypeId(createAllocatedTypeId(expression))
    super.setAllocatingMethodId(getAllocatingMethodId(function))
    super.setInIIB(getInIIB(function))
    super.setArray(expression.type.isArray())
    super.setSymbolId(createSymbolId(function))
  }

  private fun getPosition(expression: IrConstructorCall, fileInfo: KotlinFileInfo): Position {
    val a=fileInfo.seeSource(expression.startOffset,expression.endOffset)
    return fileInfo.getPosition(expression.startOffset,expression.type.originalKotlinType.toString())
  }

  private fun createSymbolId(function: Function):String{
    val id=if(isInIIB) "<"+function.declaringClassId+">" else super.getAllocatingMethodId()+"/"+function.getHeapAllocationsCounter(id)
    return id+"/new "+super.getAllocatedTypeId()+"/"+function.getHeapAllocationsCounter(id)
  }

  private fun createAllocatedTypeId(expression: IrConstructorCall):String{
    return expression.type.getClass()!!.parent.fqNameForIrSerialization.toString()+"."+expression.type.getClass()!!.name.asString()
  }

  private fun getInIIB(function: Function): Boolean{
    return function.symbolId==""
  }

  private fun getAllocatingMethodId(function: Function):String{
    if(isInIIB) return ""
    return function.symbolId
  }
}
