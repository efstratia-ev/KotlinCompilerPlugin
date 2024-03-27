# KotlinCompilerPlugin

This is a standalone program that generates [code
metadata](https://github.com/clyze/metadata-model) for Kotlin sources.

This is done by invoking the Kotlin compiler plus a special plugin
that traverses the Kotlin IR and outputs the metadata as files.

To build and install the program locally, run:

```shell
./gradlew :kotlin-ir-plugin:installDist
```

To generate metadata for Kotlin sources in X/src/main/kotlin (with classpath lib1.jar and lib2.jar), run:

```shell
kotlin-ir-plugin/build/install/kotlin-ir-plugin/bin/kotlin-ir-plugin --source X/src/main/kotlin --out /path/to/output/directory --add-classpath lib1.jar --add-classpath lib2.jar
```

The generated metadata will be in the directory given after the `--out` command-line option.
