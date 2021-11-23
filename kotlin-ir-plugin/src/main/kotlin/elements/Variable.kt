package elements

import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrVariable
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.isLocal

class Variable(declaration: IrVariable, fileEntry: SourceManager.FileEntry, fileName:String, function: Function?) :org.clyze.persistent.model.jvm.JvmVariable(){
  init {
    super.setPosition(getPosition(declaration,fileEntry))
    super.setSourceFileName(fileName)
    super.setSource(true)
    super.setName(declaration.name.asString())
    super.setType(declaration.type.originalKotlinType.toString())
    super.setLocal(declaration.isLocal)
    super.setParameter(false) //TODO::method parameter?
    super.setInIIB(getinIIB(declaration))
    super.setDeclaringMethodId(getDeclaringMethodId(function)) //TODO: when inIIB is true?
    super.setSymbolId(createSymbolId())
  }

  private fun getPosition(declaration: IrVariable, fileEntry: SourceManager.FileEntry): Position {
    val info=fileEntry.getSourceRangeInfo(declaration.startOffset,declaration.endOffset)
    var a= declaration.initializer?.startOffset?.let { declaration.initializer?.endOffset?.let { it1 ->
      fileEntry.getSourceRangeInfo(it,
        it1
      )
    } }
    return Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun createSymbolId():String{
    return super.getDeclaringMethodId()+"/VAR"+"@"+super.getType()+"@"+super.getName()
  }

  private fun getinIIB(declaration: IrVariable): Boolean{
    return declaration.parent is IrClass
  }

  private fun getDeclaringMethodId(function: Function?):String{
    if(super.isInIIB()) return "" //TODO?
    return function!!.symbolId
  }
}
