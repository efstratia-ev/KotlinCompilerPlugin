package elements
import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmClass
import org.jetbrains.kotlin.backend.common.ir.isFinalClass
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationParent
import org.jetbrains.kotlin.ir.declarations.IrFile
import org.jetbrains.kotlin.ir.types.typeWith
import org.jetbrains.kotlin.ir.util.*


class ClassReporter(declaration: IrClass, fileEntry: SourceManager.FileEntry,fileName:String,packageName:String) : JvmClass() {
  init {
    super.setPosition(getPosition(declaration,fileEntry)) //TODO:diorthosi
    super.setSourceFileName(fileName)
    /**TODO::exists in source?
    If true, this symbol appears in the sources. If false, this is a
    compiler-generated/synthetic element or it appears in binary dependencies.
    */
    super.setSource(true)
    super.setName(declaration.name.asString())
    super.setPackageName(packageName)
    super.setSymbolId(declaration.parent.fqNameForIrSerialization.toString()+"."+declaration.name.asString())
    super.setInterface(declaration.isInterface)
    super.setEnum(declaration.isEnumClass) //TODO::enum class vs enum entry
    super.setStatic(false)
    super.setInner(declaration.isInner)
    super.setAnonymous(declaration.isAnonymousObject)
    super.setAbstract(declaration.modality.toString() == "ABSTRACT")
    super.setFinal(declaration.isFinalClass)
    super.setPublic(declaration.visibility.toString() == "public")
    super.setProtected(declaration.visibility.toString() == "protected")
    super.setPrivate(declaration.visibility.toString() == "private")
  }

  private fun getPosition(declaration: IrClass, fileEntry: SourceManager.FileEntry): Position {
    val info = fileEntry.getSourceRangeInfo(declaration.startOffset, declaration.endOffset)
    return Position(
      info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
  }
}

