package elements
import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.JvmClass
import org.jetbrains.kotlin.backend.common.ir.isFinalClass
import org.jetbrains.kotlin.ir.declarations.IrClass
import org.jetbrains.kotlin.ir.util.*
import util.KotlinFileInfo


class Class(declaration: IrClass, fileInfo: KotlinFileInfo) : JvmClass() {
  init {
    super.setSourceFileName(fileInfo.sourceFileName)
    super.setName(declaration.name.asString())
    super.setPosition(getPosition(declaration, fileInfo))
    super.setSource(fileInfo.existsInSources(position))
    super.setPackageName(packageName)
    super.setSymbolId(declaration.parent.fqNameForIrSerialization.toString()+"."+declaration.name.asString())
    super.setInterface(declaration.isInterface)
    super.setEnum(declaration.isEnumClass)
    super.setStatic(declaration.isCompanion)
    super.setInner(declaration.isInner)
    super.setAnonymous(declaration.isAnonymousObject)
    super.setAbstract(declaration.modality.toString() == "ABSTRACT")
    super.setFinal(declaration.isFinalClass)
    super.setPublic(declaration.visibility.toString() == "public")
    super.setProtected(declaration.visibility.toString() == "protected")
    super.setPrivate(declaration.visibility.toString() == "private")
  }

  private fun getPosition(declaration: IrClass, fileInfo: KotlinFileInfo): Position {
    var startIndex=declaration.startOffset
    when (declaration.kind.toString()) {
      "CLASS" -> startIndex = fileInfo.findKeyword(startIndex,"class")
      "INTERFACE" -> startIndex = fileInfo.findKeyword(startIndex,"interface")
      "ENUM_CLASS" -> startIndex = fileInfo.findKeyword(startIndex,"class")
    }
    return fileInfo.getPosition(fileInfo.skipWhitespaces(startIndex),name)
  }


}

