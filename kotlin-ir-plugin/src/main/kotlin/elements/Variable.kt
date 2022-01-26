package elements

import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrValueDeclaration
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.isLocal

class Variable(declaration: IrValueDeclaration, fileEntry: SourceManager.FileEntry, fileName:String, function: Function?, isParam :Boolean) :org.clyze.persistent.model.jvm.JvmVariable(){
  init {
    super.setPosition(getPosition(declaration,fileEntry))
    super.setSourceFileName(fileName)
    super.setSource(true)
    super.setName(declaration.name.asString())
    super.setType(declaration.type.originalKotlinType.toString())
    super.setLocal(declaration.isLocal)
    super.setParameter(isParam) //TODO::type parameters?
    super.setInIIB(getinIIB(declaration))
    super.setDeclaringMethodId(getDeclaringMethodId(function)) //TODO: when inIIB is true?
    super.setSymbolId(createSymbolId())
  }

  private fun getPosition(declaration: IrValueDeclaration, fileEntry: SourceManager.FileEntry): Position {
    val info=fileEntry.getSourceRangeInfo(declaration.startOffset,declaration.endOffset)
    return Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun createSymbolId():String{
    return super.getDeclaringMethodId()+"/VAR"+"@"+super.getType()+"@"+super.getName()
  }

  private fun getinIIB(declaration: IrValueDeclaration): Boolean{
    return declaration.parent is IrClass
  }

  private fun getDeclaringMethodId(function: Function?):String{
    if(super.isInIIB()) return "" //TODO?
    return function!!.symbolId
  }
}
