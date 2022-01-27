import com.bnorm.template.TemplateComponentRegistrar
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import org.apache.commons.cli.*
import java.io.File
import java.nio.file.Files

fun main(args: Array<String>) {
    val options = Options()

    val srcOpt = Option.builder().option("s").longOpt("source")
        .hasArg(true).argName("PATH").numberOfArgs(Option.UNLIMITED_VALUES)
        .required(true).desc("Sources (.zip/.jar file or directory).").build()
    options.addOption(srcOpt)

    val irOpt = Option.builder().option("i").longOpt("ir")
        .hasArg(true).argName("PATH").numberOfArgs(Option.UNLIMITED_VALUES)
        .desc("IR .jar file, used to provide code dependencies.").build()
    options.addOption(irOpt)

    val outOpt = Option.builder().option("o").longOpt("out")
        .hasArg(true).argName("PATH")
        .desc("The output directory.").build()
    options.addOption(outOpt)

    val jphantomOpt = Option("j", "run-jphantom", false, "Assuming a single code (fat) JAR, run jphantom. This is an experimental flag.")
    options.addOption(jphantomOpt)

    val helpOpt = Option("h", "help", false, "Print help.")
    options.addOption(helpOpt)

    if (args.isEmpty()) {
        printUsage(options)
        return
    }

    val parser: CommandLineParser = DefaultParser()
    try {
        val cli = parser.parse(options, args)
        if (cli.hasOption(helpOpt.opt)) {
            printUsage(options)
            return
        }
        val outputDir = cli.getOptionValue(outOpt.opt)
        val sourceDir = cli.getOptionValue(srcOpt)
        val irJars = cli.getOptionValues(irOpt) ?: arrayOf()
        if (!File(sourceDir).isDirectory) {
            println("Source parameter must be a directory: $sourceDir")
            return
        }
        val outDir = File(outputDir)
        println("Using output directory: ${outDir.canonicalPath}")
        if (!outDir.exists()) {
            println("$outputDir does not exist, creating...")
            outDir.mkdirs()
        }

        val complement = complementCode(cli, jphantomOpt, irJars)
        val componentRegistrar = TemplateComponentRegistrar(outputDir)
        val ktFiles = File(sourceDir).walkTopDown()
            .filter { it.toString().endsWith(".kt") }
            .map { SourceFile.fromPath(File(it.toString())) }
        val kotlinc = KotlinCompilation().apply {
            sources = ktFiles.toList()
            useIR = true
            compilerPlugins = listOf(componentRegistrar)
            inheritClassPath = true
            classpaths = irJars.map { File(it) }.toList() + complement
        }
        kotlinc.compile()
    } catch (exc : Exception) {
        exc.printStackTrace()
        printUsage(options)
        return
    }
}

private fun printUsage(options: Options) {
    val formatter = HelpFormatter()
    formatter.width = 100
    formatter.printHelp("kotlin-compiler-plugin [OPTION]...", options)
}

private fun complementCode(cli : CommandLine, jphantomOpt : Option, irJars : Array<String?>) : List<File> {
    if (cli.hasOption(jphantomOpt.opt)) {
        println("Computing code complement...")
        val tmpJarComplementDir = Files.createTempDirectory("kotlinc-plugin-jphantom").toFile()
        // tmpJarComplementDir.deleteOnExit()
        val complementJar = File(tmpJarComplementDir, "complement.jar")
        val complementJarPath = complementJar.canonicalPath
        val phantomsDir = File(tmpJarComplementDir, "phantoms")
        phantomsDir.mkdirs()
        if (irJars.size == 1) {
            val irJar = irJars[0]
            org.clyze.jphantom.Driver.main(arrayOf(irJar, "-o", complementJarPath, "-d", phantomsDir.canonicalPath, "-v", "0"))
            if (complementJar.exists()) {
                println("Using complement JAR: $complementJarPath")
                return listOf(complementJar)
            } else
                println("ERROR: could not create complement JAR via jphantom.")
        }
    }
    return listOf()
}