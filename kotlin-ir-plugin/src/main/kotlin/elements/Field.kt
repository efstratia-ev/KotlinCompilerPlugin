package elements

import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.util.parentAsClass

class Field(declaration: IrField, fileEntry: SourceManager.FileEntry, fileName:String, packageName:String):org.clyze.persistent.model.jvm.JvmField() {
  init {
    super.setPosition(getPosition(declaration,fileEntry)) //TODO:check
    super.setSourceFileName(fileName)
    super.setSource(true) //TODO
    super.setName(declaration.name.asString())
    super.setType(declaration.type.originalKotlinType.toString())
    super.setDeclaringClassId(getDeclaringClassId(declaration,packageName))
    super.setStatic(declaration.isStatic)
    super.setSymbolId(createSymbolId())
  }

  private fun getPosition(declaration: IrField, fileEntry: SourceManager.FileEntry): Position {
    val info=fileEntry.getSourceRangeInfo(declaration.startOffset,declaration.endOffset)
    return Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }

  private fun getDeclaringClassId(declaration: IrField,packageName: String):String{
    return packageName+"."+declaration.parentAsClass.name.toString()
  }

  private fun createSymbolId():String{
    return "<"+super.getDeclaringClassId()+ ": "+super.getType()+ " "+super.getName()+">"
  }
}
