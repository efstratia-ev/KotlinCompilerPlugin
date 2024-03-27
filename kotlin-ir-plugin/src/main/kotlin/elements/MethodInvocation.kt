package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmMethodInvocation
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import util.KotlinFileInfo

class MethodInvocation(expression: org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression, fileInfo: KotlinFileInfo, function: Function):JvmMethodInvocation() {
  init {
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setName(expression.symbol.owner.name.toString())
    super.setTargetReturnType(expression.type.originalKotlinType.toString())
    super.setPosition(getPosition(expression,fileInfo))
    super.setInIIB(getInIIB(function))
    super.setInvokingMethodId(getInvokingMethodId(function))
    super.setTargetType(getTargetType(expression))
    super.setSymbolId(createSymbolId(function))
    super.setSource(fileInfo.existsInSources(position))
    super.setTargetParamTypes(getParamType(expression))
  }

  private fun getPosition(expression: org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression, fileInfo: KotlinFileInfo): Position {
    return fileInfo.getPosition(expression.startOffset,if(name=="<init>") expression.type.originalKotlinType.toString() else name)
  }
  private fun getParamType(expression: org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression):String{ //check
    val paramCount=expression.valueArgumentsCount
    var parameterTypes=""
    for (i in 0 until paramCount) {
      parameterTypes= expression.getValueArgument(i)?.type?.originalKotlinType.toString()
      if(i!=paramCount-1) parameterTypes+=","
    }
    return parameterTypes
  }

  private fun getTargetType(expression: org.jetbrains.kotlin.ir.expressions.IrFunctionAccessExpression):String?{
    if(expression.extensionReceiver!=null) {
      return expression.extensionReceiver!!.type.toString()
    }
    return null
  }

  private fun createSymbolId(function: Function):String{
    var id=if(isInIIB) "<"+function.declaringClassId+">" else super.getInvokingMethodId()
    id+="/"+ if(targetType!=null) { "$targetType." } else ""
    return id+"."+super.getName() +"/"+function.getMethodInvocationsCounter(id)
  }

  private fun getInIIB(function: Function): Boolean{
    return function.symbolId==""
  }

  private fun getInvokingMethodId(function: Function):String{
    if(isInIIB) return ""
    return function.symbolId
  }

}
