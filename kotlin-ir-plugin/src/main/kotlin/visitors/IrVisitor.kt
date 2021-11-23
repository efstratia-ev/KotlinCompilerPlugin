package visitors

import elements.ClassReporter
import elements.HeapAllocation
import elements.MethodInvocation
import org.clyze.persistent.metadata.Configuration
import org.clyze.persistent.metadata.FileInfo
import org.clyze.persistent.metadata.Printer
import org.clyze.persistent.metadata.jvm.JvmFileReporter
import org.clyze.persistent.metadata.jvm.JvmMetadata
import org.clyze.persistent.model.Position
import org.clyze.persistent.model.jvm.*
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.ObsoleteDescriptorBasedAPI
import org.jetbrains.kotlin.ir.SourceManager
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.types.isArray
import org.jetbrains.kotlin.ir.util.dump
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import java.util.*
//TODO::fix types
class IrVisitor(): IrElementVisitorVoid {
  var packageName: String = ""
  var configuration = Configuration(Printer(true))
  var metadata = JvmMetadata()
  private lateinit var fileEntry: SourceManager.FileEntry
  private var fileName:String=""
  private var functionStack=Stack<elements.Function>()

  override fun visitFile(declaration: IrFile) { //TODO::fix variables
    fileName=declaration.name
    fileEntry=declaration.fileEntry
    super.visitFile(declaration)
    val fileInfo = FileInfo(packageName, "inputName", "input/file/path", "test source", metadata)
    val reporter = JvmFileReporter(configuration, fileInfo)
    reporter.createReportFile("output-path.json")
    reporter.printReportStats()
    println(declaration.dump())
  }

  override fun visitVararg(expression: IrVararg) {
    super.visitVararg(expression)
  }

  override fun visitClass(declaration: IrClass) {
    val classElement=ClassReporter(declaration,fileEntry,fileName,packageName)
    metadata.jvmClasses.add(classElement)

    super.visitClass(declaration)
  }


  override fun visitFunction(declaration: IrFunction) {
    val functionElement=elements.Function(declaration,fileEntry,fileName)
    metadata.jvmMethods.add(functionElement)
    functionStack.add(functionElement)
    super.visitFunction(declaration)
    functionStack.pop()
  }

  override fun visitField(declaration: IrField) {
    val fieldElement=elements.Field(declaration,fileEntry,fileName,packageName)
    metadata.jvmFields.add(fieldElement)

    super.visitField(declaration)
  }

  override fun visitVariable(declaration: IrVariable) {

    val variableElement=elements.Variable(declaration,fileEntry,fileName,if(functionStack.isNotEmpty()) functionStack.peek() else null)
    metadata.jvmVariables.add(variableElement)

    super.visitVariable(declaration)
  }

  override fun visitCall(expression: IrCall) {
    val methodInv=MethodInvocation(expression,fileEntry,fileName,if(functionStack.isNotEmpty()) functionStack.peek() else null)
    metadata.jvmInvocations.add(methodInv)

    super.visitCall(expression)
  }


  override fun visitConstructorCall(expression: IrConstructorCall) {
    val heapAllocationElement=HeapAllocation(expression,fileEntry,fileName,if(functionStack.isNotEmpty()) functionStack.peek() else null)
    metadata.jvmHeapAllocations.add(heapAllocationElement)

    super.visitConstructorCall(expression)
  }

  override fun visitElement(element: IrElement) {
    element.acceptChildren(this, null)
    // println(element.dump())
  }

  override fun visitPackageFragment(declaration: IrPackageFragment) {
    packageName=declaration.fqName.toString()
    fileName=packageName.replace(".","/")+"/"+fileName
    println("package "+ declaration.fqName)
    super.visitPackageFragment(declaration)
  }

  override fun visitTypeParameter(declaration: IrTypeParameter) {
    super.visitTypeParameter(declaration)
  }

  override fun visitProperty(declaration: IrProperty) {
    super.visitProperty(declaration)
  }

  override fun visitFunctionAccess(expression: IrFunctionAccessExpression) {
    super.visitFunctionAccess(expression)
  }

  override fun <T> visitConst(expression: IrConst<T>) {
    val b=expression.type.originalKotlinType
    val info=fileEntry.getSourceRangeInfo(expression.startOffset, expression.endOffset)
    val pos= Position(info.startLineNumber.toLong(), info.endLineNumber.toLong(), info.startColumnNumber.toLong(),
      info.endColumnNumber.toLong()
    )
    //TODO::kaleite 2 fores?
    val ha= JvmHeapAllocation(pos,
      fileName,
      true,//TODO::??
      "String symbolId",
      expression.type.originalKotlinType.toString(),
      "s",//expression.symbol.owner.toString(), //TODO::id??
      false,//TODO::?
      expression.type.isArray())

    metadata.jvmHeapAllocations.add(ha)
    super.visitConst(expression)
  }
}



