package elements

import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmMethod
import org.jetbrains.kotlin.backend.common.ir.isStatic
import org.jetbrains.kotlin.ir.declarations.IrFunction
import org.jetbrains.kotlin.ir.declarations.IrValueParameter
import org.jetbrains.kotlin.ir.descriptors.toIrBasedDescriptor
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.*
import org.jetbrains.kotlin.resolve.jvm.annotations.findSynchronizedAnnotation
import util.KotlinFileInfo


open class Function:JvmMethod{
  private val heapAllocations = mutableMapOf<String, Int>()
  private val methodInvocations = mutableMapOf<String, Int>()
  constructor(classReporter: Class):super() {
    super.setDeclaringClassId(classReporter.symbolId)
    super.setSymbolId("")
  }
  constructor(declaration: IrFunction, fileInfo: KotlinFileInfo, declaringClass: Class?):super(){
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setName(declaration.name.asString())
    super.setPosition(getPosition(declaration,fileInfo))
    super.setSource(fileInfo.existsInSources(position))
    super.setDeclaringClassId(getDeclaringClassId(declaringClass))
    super.setReturnType(declaration.returnType.originalKotlinType.toString())
    super.setParams(getParams(declaration.valueParameters))
    super.setParamTypes(getParamTypes(declaration.valueParameters))
    super.setSymbolId(createSymbolId(declaration))
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
    super.setOuterPosition(getOuterPosition(declaration,fileInfo))
  }



  private fun getPosition(declaration: IrFunction, fileInfo: KotlinFileInfo): Position {
    val a=fileInfo.seeSource(declaration.startOffset,declaration.endOffset) //TODO:remove

    if(name=="<init>"){
      val startIndex=fileInfo.findKeyword(declaration.startOffset,"constructor")
      if(startIndex>=0) return fileInfo.getPosition(fileInfo.skipWhitespaces(startIndex),"constructor")
      //TODO primary constructor pos?
    }

    if(declaration.extensionReceiverParameter!=null){ //if extension receiver exists function name is after
      return fileInfo.getPosition(declaration.extensionReceiverParameter!!.endOffset+1,name)
    }
    if(declaration.typeParameters.isNotEmpty()){ //If type parameters exist function name is after the last one
      return fileInfo.getPosition(fileInfo.skipWhitespaces(declaration.typeParameters.last().endOffset+1),name)
    }
    val startIndex=fileInfo.findKeyword(declaration.startOffset,"fun")
    return fileInfo.getPosition(fileInfo.skipWhitespaces(startIndex),name)
  }

  private fun getOuterPosition(declaration: IrFunction, fileInfo: KotlinFileInfo): Position {
    return fileInfo.getPosition(declaration.startOffset,declaration.endOffset)
  }

  private fun getParams(parameters: List<IrValueParameter>): Array<String?> {
    val params = arrayOfNulls<String>(parameters.size)
    for ((counter, param) in parameters.withIndex()) {
      params[counter] = param.name.toString()
    }
    return params
  }

  private fun getParamTypes(parameters: List<IrValueParameter>): Array<String?> {
    val paramTypes = arrayOfNulls<String>(parameters.size)
    for ((counter, param) in parameters.withIndex()) {
      paramTypes[counter] = param.type.originalKotlinType.toString()
    }
    return paramTypes
  }

  private fun getDeclaringClassId(declaringClass: Class?):String{
    return if (declaringClass!=null) declaringClass.symbolId else ""
  }

  private fun createSymbolId(declaration: IrFunction):String{
    if(super.getDeclaringClassId()=="")
      return "<"+declaration.parent.fqNameForIrSerialization.toString()+ ": "+super.getReturnType()+
              " "+super.getName()+"("+super.getParamTypes().joinToString(separator=",")+")>"
    return "<"+super.getDeclaringClassId()+ ": "+super.getReturnType()+
      " "+super.getName()+"("+super.getParamTypes().joinToString(separator=",")+")>"
  }

   fun getHeapAllocationsCounter(symbolID: String):Int{
    val counter= heapAllocations[symbolID]
    if(counter!=null){
      heapAllocations[symbolID]=counter+1
      return counter
    }
    heapAllocations[symbolID]=1
    return 0
  }

   fun getMethodInvocationsCounter(symbolID: String):Int{
    val counter= methodInvocations[symbolID]
    if(counter!=null){
      methodInvocations[symbolID]=counter+1
      return counter
    }
    methodInvocations[symbolID]=1
    return 0
  }

}
