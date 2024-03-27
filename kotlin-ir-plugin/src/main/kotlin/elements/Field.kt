package elements

import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.ir.declarations.IrField
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import util.KotlinFileInfo

class Field(declaration: IrField, fileInfo: KotlinFileInfo, classReporter: Class?):org.clyze.persistent.model.jvm.JvmField() {
  init {
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setName(declaration.name.asString())
    super.setPosition(getPosition(declaration,fileInfo))
    super.setSource(fileInfo.existsInSources(position))
    super.setType(declaration.type.originalKotlinType.toString())
    super.setDeclaringClassId(classReporter!!.symbolId)
    super.setStatic(declaration.isStatic)
    super.setSymbolId(createSymbolId())
  }

  private fun getPosition(declaration: IrField, fileInfo: KotlinFileInfo): Position {
    val startIndex:Int = if(declaration.isFinal) fileInfo.findKeyword(declaration.startOffset,"val")
    else fileInfo.findKeyword(declaration.startOffset,"var")
    return fileInfo.getPosition(fileInfo.skipWhitespaces(startIndex),name)
  }

  private fun createSymbolId():String{
    return "<"+super.getDeclaringClassId()+ ": "+super.getType()+ " "+super.getName()+">"
  }
}
