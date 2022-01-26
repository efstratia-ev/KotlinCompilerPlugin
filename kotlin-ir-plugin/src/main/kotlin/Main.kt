import com.bnorm.template.TemplateComponentRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import java.io.File
import kotlin.system.exitProcess

fun main(args: Array<String>) {
    val outputDirFlag = "--out"
    val sourceDirFlag = "--source"

    var outputDir = System.getProperty("user.dir")
    var sourceDir = ""

    val iterator = args.iterator()
    while (iterator.hasNext()) {
      val arg = iterator.next()
      if (arg == outputDirFlag) {
        if (iterator.hasNext()) outputDir = iterator.next()
        else {
          println("Flag $outputDirFlag requires an argument")
          exitProcess(-1)
        }
      } else if (arg == sourceDirFlag) {
        if (iterator.hasNext()) sourceDir = iterator.next()
        else {
          println("Flag $sourceDirFlag requires an argument")
          exitProcess(-1)
        }
      }
    }
    if (!File(sourceDir).isDirectory) {
      println("Flag $sourceDirFlag must be a directory: $sourceDir")
      return
    }
    val outDir = File(outputDir)
    println("Using output directory: ${outDir.canonicalPath}")
    if (!outDir.exists()) {
      println("$outputDir does not exist, creating...")
      outDir.mkdirs()
    }
    val componentRegistrar = TemplateComponentRegistrar(outputDir)
    File(sourceDir).walkTopDown().forEach {
      if (it.toString().endsWith(".kt")) {
        KotlinCompilation().apply {
          sources = listOf(SourceFile.fromPath(File(it.toString())))
          useIR = true
          compilerPlugins = listOf(componentRegistrar)
          inheritClassPath = true
        }.compile()
      }
    }
}


