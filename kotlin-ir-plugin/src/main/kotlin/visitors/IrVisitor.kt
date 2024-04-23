package visitors

import elements.Class
import elements.HeapAllocation
import elements.MethodInvocation
import org.clyze.persistent.metadata.Configuration
import org.clyze.persistent.metadata.FileReporter
import org.clyze.persistent.metadata.Printer
import org.clyze.persistent.model.Position
import org.jetbrains.kotlin.backend.jvm.ir.getKtFile
import java.nio.file.Paths
import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.IrCall
import org.jetbrains.kotlin.ir.expressions.IrConst
import org.jetbrains.kotlin.ir.expressions.IrConstructorCall
import org.jetbrains.kotlin.ir.types.impl.originalKotlinType
import org.jetbrains.kotlin.ir.visitors.IrElementVisitorVoid
import org.jetbrains.kotlin.psi.psiUtil.getElementTextWithContext
import util.KotlinFileInfo
import java.util.*

class IrVisitor(private val outputPath: String, private val artifact: String): IrElementVisitorVoid {
  private var configuration = Configuration(Printer(true))
  private var fileInfo: KotlinFileInfo? =null
  private var functionStack=Stack<elements.Function>()
  private var classStack=Stack<Class>()

  override fun visitElement(element: IrElement) {
    element.acceptChildren(this, null)
  }

  override fun visitFile(declaration: IrFile) {
    val fileContents= declaration.getKtFile()?.getElementTextWithContext().toString()
    fileInfo = KotlinFileInfo(declaration.fqName.toString(), declaration.name, declaration.fileEntry, fileContents)
    super.visitFile(declaration)
    val reporter = FileReporter(configuration, fileInfo!!.elements)
    val nm=Paths.get(declaration.name).fileName.toString()
    fileInfo!!.outputFilePath
    reporter.createReportFile(outputPath+"/"+nm.substring(0,nm.length-3)+".json")
    reporter.printReportStats()
  }

  override fun visitClass(declaration: IrClass) {
    declaration.kind
    val classElement=Class(declaration, fileInfo!!)
    fileInfo!!.elements.jvmClasses.add(classElement)
    classStack.push(classElement)
    super.visitClass(declaration)
    classStack.pop()
  }

  override fun visitFunction(declaration: IrFunction) {
    val functionElement=elements.Function(declaration,fileInfo!!,if(classStack.isNotEmpty()) classStack.peek() else null)
    fileInfo!!.elements.jvmMethods.add(functionElement)
    functionStack.add(functionElement)
    super.visitFunction(declaration)
    functionStack.pop()
  }

  override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer) {
    val functionElement=elements.Function(classStack.peek())
    functionStack.add(functionElement)
    super.visitAnonymousInitializer(declaration)
    functionStack.pop()
  }

  override fun visitDeclaration(declaration: IrDeclarationBase) {
    super.visitDeclaration(declaration)
  }
  override fun <T> visitConst(expression: IrConst<T>) {
    super.visitConst(expression)
  }
  override fun visitField(declaration: IrField) {
    val fieldElement=elements.Field(declaration, fileInfo!!,classStack.peek())
    fileInfo!!.elements.jvmFields.add(fieldElement)

    super.visitField(declaration)
  }

  override fun visitVariable(declaration: IrVariable) {
    val variableElement = elements.Variable(declaration, fileInfo!!,functionStack.peek(), false)
    fileInfo!!.elements.jvmVariables.add(variableElement)
    super.visitVariable(declaration)
  }

  override fun visitValueParameter(declaration: IrValueParameter) {
    if(declaration.index >=0 ){ //Ignore parameter <this>
       val variableElement = elements.Variable(declaration, fileInfo!!,functionStack.peek(), true)
      fileInfo!!.elements.jvmVariables.add(variableElement)
    }
    super.visitValueParameter(declaration)
  }

  override fun visitCall(expression: IrCall) {
    val methodInv= MethodInvocation(expression,fileInfo!!,functionStack.peek())
    fileInfo!!.elements.jvmInvocations.add(methodInv)
    super.visitCall(expression)
  }


  override fun visitConstructorCall(expression: IrConstructorCall) {
    val methodInv=MethodInvocation(expression,fileInfo!!,functionStack.peek())
    fileInfo!!.elements.jvmInvocations.add(methodInv)
    val heapAllocationElement= HeapAllocation(expression,fileInfo!!,functionStack.peek())
    fileInfo!!.elements.jvmHeapAllocations.add(heapAllocationElement)
    super.visitConstructorCall(expression)
  }

}



