/*
 * Copyright 2001-2023 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package rife.bld.operations;

import rife.bld.Project;
import rife.bld.operations.exceptions.ExitStatusException;
import rife.tools.FileUtils;
import rife.tools.StringUtils;

import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Generates javadocs for the main project sources.
 *
 * @author Geert Bevin (gbevin[remove] at uwyn dot com)
 * @since 1.5.10
 */
public class JavadocOperation extends AbstractOperation<JavadocOperation> {
    private File buildDirectory_;
    private final List<String> classpath_ = new ArrayList<>();
    private final List<File> sourceFiles_ = new ArrayList<>();
    private final JavadocOptions javadocOptions_ = new JavadocOptions();
    private final List<Diagnostic<? extends JavaFileObject>> diagnostics_ = new ArrayList<>();
    private final List<Pattern> included_ = new ArrayList<>();
    private final List<Pattern> excluded_ = new ArrayList<>();

    /**
     * Performs the compile operation.
     *
     * @since 1.5.10
     */
    public void execute()
    throws IOException, ExitStatusException {
        executeCreateBuildDirectories();
        executeBuildSources();
        if (!diagnostics().isEmpty()) {
            throw new ExitStatusException(ExitStatusException.EXIT_FAILURE);
        }
        if (!silent()) {
            System.out.println("Javadoc generated successfully.");
        }
    }

    /**
     * Part of the {@link #execute} operation, creates the build directories.
     *
     * @since 1.5.10
     */
    public void executeCreateBuildDirectories() {
        if (buildDirectory() != null) {
            buildDirectory().mkdirs();
        }
    }

    /**
     * Part of the {@link #execute} operation, builds the main sources.
     *
     * @since 1.5.10
     */
    public void executeBuildSources()
    throws IOException {
        executeBuildSources(
            classpath(),
            sourceFiles(),
            buildDirectory());
    }

    /**
     * Part of the {@link #execute} operation, build sources to a destination.
     *
     * @param classpath   the classpath list used for the compilation
     * @param sources     the source files to compile
     * @param destination the destination directory
     * @since 1.5.10
     */
    public void executeBuildSources(List<String> classpath, List<File> sources, File destination)
    throws IOException {
        if (sources.isEmpty() || destination == null) {
            return;
        }

        var filtered_sources = new ArrayList<File>();
        for (var source : sources) {
            if (StringUtils.filter(source.getAbsolutePath(), included(), excluded())) {
                filtered_sources.add(source);
            }
        }

        var documentation = ToolProvider.getSystemDocumentationTool();
        try (var file_manager = documentation.getStandardFileManager(null, null, null)) {
            var compilation_units = file_manager.getJavaFileObjectsFromFiles(filtered_sources);
            var diagnostics = new DiagnosticCollector<JavaFileObject>();
            var options = new ArrayList<>(List.of("-d", destination.getAbsolutePath(), "-cp", FileUtils.joinPaths(classpath)));
            options.addAll(javadocOptions());
            var documentation_task = documentation.getTask(null, file_manager, diagnostics, null, options, compilation_units);
            if (!documentation_task.call()) {
                diagnostics_.addAll(diagnostics.getDiagnostics());
                executeProcessDiagnostics(diagnostics);
            }
        }
    }

    /**
     * Part of the {@link #execute} operation, processes the compilation diagnostics.
     *
     * @param diagnostics the diagnostics to process
     * @since 1.5.10
     */
    public void executeProcessDiagnostics(DiagnosticCollector<JavaFileObject> diagnostics) {
        for (var diagnostic : diagnostics.getDiagnostics()) {
            System.err.print(executeFormatDiagnostic(diagnostic));
        }
    }

    /**
     * Part of the {@link #execute} operation, format a single diagnostic.
     *
     * @param diagnostic the diagnostic to format
     * @return a string representation of the diagnostic
     * @since 1.5.10
     */
    public String executeFormatDiagnostic(Diagnostic<? extends JavaFileObject> diagnostic) {
        return diagnostic.toString() + System.lineSeparator();
    }

    /**
     * Configures a javadoc operation from a {@link Project}.
     *
     * @param project the project to configure the javadoc operation from
     * @since 1.5.10
     */
    public JavadocOperation fromProject(Project project) {
        return buildDirectory(project.buildJavadocDirectory())
            .classpath(project.compileMainClasspath())
            .sourceFiles(project.mainSourceFiles())
            .javadocOptions(project.javadocOptions());
    }

    /**
     * Provides the javadoc build destination directory.
     *
     * @param directory the directory to use for the javadoc build destination
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation buildDirectory(File directory) {
        buildDirectory_ = directory;
        return this;
    }

    /**
     * Provides the list of entries for the javadoc classpath.
     * <p>
     * A copy will be created to allow this list to be independently modifiable.
     *
     * @param classpath the list of classpath entries
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation classpath(List<String> classpath) {
        classpath_.addAll(classpath);
        return this;
    }

    /**
     * Provides the list of files for which documentation should be generation.
     * <p>
     * A copy will be created to allow this list to be independently modifiable.
     *
     * @param files the list of source files
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation sourceFiles(List<File> files) {
        sourceFiles_.addAll(files);
        return this;
    }

    /**
     * Provides a list of options to provide to the javadoc tool.
     * <p>
     * A copy will be created to allow this list to be independently modifiable.
     *
     * @param options the list of javadoc options
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation javadocOptions(List<String> options) {
        javadocOptions_.addAll(options);
        return this;
    }

    /**
     * Provides a list of patterns that will be evaluated to determine which files
     * will be included in the javadoc generation.
     *
     * @param included the list of inclusion patterns
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation included(List<Pattern> included) {
        included_.addAll(included);
        return this;
    }

    /**
     * Provides a list of patterns that will be evaluated to determine which files
     * will be excluded from the javadoc generation.
     *
     * @param excluded the list of exclusion patterns
     * @return this operation instance
     * @since 1.5.10
     */
    public JavadocOperation excluded(List<Pattern> excluded) {
        excluded_.addAll(excluded);
        return this;
    }

    /**
     * Retrieves the build destination directory.
     *
     * @return the javadoc build destination
     * @since 1.5.10
     */
    public File buildDirectory() {
        return buildDirectory_;
    }

    /**
     * Retrieves the list of entries for the javadoc classpath.
     * <p>
     * This is a modifiable list that can be retrieved and changed.
     *
     * @return the javadoc classpath list
     * @since 1.5.10
     */
    public List<String> classpath() {
        return classpath_;
    }

    /**
     * Retrieves the list of files for which documentation should be generation.
     * <p>
     * This is a modifiable list that can be retrieved and changed.
     *
     * @return the list of files documentation is generated for
     * @since 1.5.10
     */
    public List<File> sourceFiles() {
        return sourceFiles_;
    }

    /**
     * Retrieves the list of options for the javadoc tool.
     * <p>
     * This is a modifiable list that can be retrieved and changed.
     *
     * @return the list of javadoc options
     * @since 1.5.10
     */
    public JavadocOptions javadocOptions() {
        return javadocOptions_;
    }

    /**
     * Retrieves the list of diagnostics resulting from the compilation.
     *
     * @return the list of compilation diagnostics
     * @since 1.5.10
     */
    public List<Diagnostic<? extends JavaFileObject>> diagnostics() {
        return diagnostics_;
    }

    /**
     * Retrieves the list of patterns that will be evaluated to determine which files
     * will be included in the javadoc generation.
     * <p>
     * This is a modifiable list that can be retrieved and changed.
     *
     * @return the javadoc inclusion patterns
     * @since 1.5.10
     */
    public List<Pattern> included() {
        return included_;
    }

    /**
     * Retrieves the list of patterns that will be evaluated to determine which files
     * will be excluded the javadoc generation.
     * <p>
     * This is a modifiable list that can be retrieved and changed.
     *
     * @return the javadoc exclusion patterns
     * @since 1.5.10
     */
    public List<Pattern> excluded() {
        return excluded_;
    }
}
