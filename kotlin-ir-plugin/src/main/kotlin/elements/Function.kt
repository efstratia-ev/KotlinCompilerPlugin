package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmMethod
import org.jetbrains.kotlin.backend.common.ir.isStatic
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.resolve.jvm.annotations.findSynchronizedAnnotation


class Function(declaration: IrFunction, fileEntry: SourceManager.FileEntry, fileName:String):JvmMethod() {
  init {
    super.setPosition(getPosition(declaration,fileEntry))//TODO:diorthosi
    super.setSourceFileName(fileName)
    super.setSource(true) //TODO
    super.setName(declaration.name.asString())
    super.setDeclaringClassId(getDeclaringClassId(declaration))
    super.setReturnType(declaration.returnType.originalKotlinType.toString())
    super.setParams(getParams(declaration.valueParameters))
    super.setParamTypes(getParamTypes(declaration.valueParameters))
    super.setSymbolId(createSymbolId())
    super.setStatic(declaration.isStatic)
    super.setInterface(declaration.parentClassOrNull?.isInterface == true)
    super.setAbstract(declaration.toIrBasedDescriptor().modality.toString()=="ABSTRACT")
    super.setNative(declaration.isExternal)
    super.setSynchronized(declaration.toIrBasedDescriptor().findSynchronizedAnnotation()!=null)
    super.setFinal(declaration.toIrBasedDescriptor().modality.toString()=="FINAL")
    super.setSynthetic(declaration.origin.isSynthetic)
    super.setPublic(declaration.visibility.toString() == "public")
    super.setProtected(declaration.visibility.toString() == "protected")
    super.setPrivate(declaration.visibility.toString() == "private")
    super.setOuterPosition(getOuterPosition(declaration,fileEntry))
  }

  private fun getPosition(declaration: IrFunction, fileEntry: SourceManager.FileEntry): Position {
    val info = fileEntry.getSourceRangeInfo(declaration.startOffset, declaration.endOffset)
    return Position(
      info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun getOuterPosition(declaration: IrFunction, fileEntry: SourceManager.FileEntry): Position {
    val info = fileEntry.getSourceRangeInfo(declaration.startOffset, declaration.endOffset)
    return Position(
      info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun getParams(parameters: List<IrValueParameter>): Array<String?> {
    val params = arrayOfNulls<String>(parameters.size)
    for ((counter, param) in parameters.withIndex()) {
      params[counter] = param.name.toString()
    }
    return params
  }

  private fun getParamTypes(parameters: List<IrValueParameter>): Array<String?> {
    //TODO::fix types
    val paramTypes = arrayOfNulls<String>(parameters.size)
    for ((counter, param) in parameters.withIndex()) {
      paramTypes[counter] = param.type.originalKotlinType.toString()
    }
    return paramTypes
  }

  private fun getDeclaringClassId(declaration: IrFunction):String{
    if(declaration.parentClassOrNull==null) return ""
    return declaration.parent.fqNameForIrSerialization.toString()+"."+declaration.parentAsClass.name.toString()
  }

  private fun createSymbolId():String{
    return "<"+super.getDeclaringClassId()+ ": "+super.getReturnType()+
      " "+super.getName()+"("+super.getParamTypes().joinToString(separator=",")+")>"
  }

}
