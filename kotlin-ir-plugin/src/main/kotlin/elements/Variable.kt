package elements

import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.isImmutable
import org.jetbrains.kotlin.ir.util.isLocal
import util.KotlinFileInfo

class Variable(declaration: IrValueDeclaration, fileInfo: KotlinFileInfo, function: Function, isParam :Boolean):org.clyze.persistent.model.jvm.JvmVariable(){
  init{
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setName(declaration.name.asString())
    super.setPosition(getPosition(declaration,fileInfo))
    super.setSource(fileInfo.existsInSources(position))
    super.setType(declaration.type.originalKotlinType.toString())
    super.setLocal(declaration.isLocal)
    super.setParameter(isParam)
    super.setInIIB(getInIIB(function))
    super.setDeclaringMethodId(getDeclaringMethodId(function))
    super.setSymbolId(createSymbolId(function))
  }
  private fun getPosition(declaration: IrValueDeclaration, fileInfo: KotlinFileInfo): Position {
    if(isParameter){ //TODO:fix for parameter
      val a=fileInfo.seeSource(declaration.startOffset,declaration.endOffset)
      return fileInfo.getPosition(fileInfo.skipWhitespaces(declaration.startOffset),name)
    }
    val startIndex:Int = if(declaration.isImmutable) fileInfo.findKeyword(declaration.startOffset,"val")
    else fileInfo.findKeyword(declaration.startOffset,"var")
    return fileInfo.getPosition(fileInfo.skipWhitespaces(startIndex),name)
  }

  private fun createSymbolId(function: Function):String{
    if(isInIIB) return "<"+function.declaringClassId+">/VAR"+"@"+super.getType()+"@"+super.getName()
    return super.getDeclaringMethodId()+"/VAR"+"@"+super.getType()+"@"+super.getName()
  }

  private fun getInIIB(function: Function): Boolean{
    return function.symbolId==""
  }

  private fun getDeclaringMethodId(function: Function):String{
    if(isInIIB) return ""
    return function.symbolId
  }
}
