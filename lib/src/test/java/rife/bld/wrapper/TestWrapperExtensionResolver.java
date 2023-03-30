/*
 * Copyright 2001-2023 Geert Bevin (gbevin[remove] at uwyn dot com)
 * Licensed under the Apache License, Version 2.0 (the "License")
 */
package rife.bld.wrapper;

import org.junit.jupiter.api.Test;
import rife.Version;
import rife.tools.FileUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static rife.bld.wrapper.Wrapper.MAVEN_CENTRAL;

public class TestWrapperExtensionResolver {
    @Test
    void testNoExtensions()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver = new WrapperExtensionResolver(hash_file, tmp2, Collections.emptySet(), Collections.emptySet());
            resolver.updateExtensions();

            assertTrue(hash_file.exists());
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(2, files2.size());
            Collections.sort(files2);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files2));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testUpdateExtensions()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1"));
            resolver.updateExtensions();

            assertTrue(hash_file.exists());
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(9, files2.size());
            Collections.sort(files2);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files2));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testCheckHash()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1"));
            resolver.updateExtensions();

            assertTrue(hash_file.exists());
            var files = tmp2.listFiles();
            assertEquals(9, files.length);
            Arrays.stream(files).forEach(file -> {
                if (!file.getName().startsWith(Wrapper.WRAPPER_PREFIX)) {
                    file.delete();
                }
            });
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(2, files2.size());
            Collections.sort(files2);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files2));

            resolver.updateExtensions();
            var files3 = FileUtils.getFileList(tmp2);
            assertEquals(2, files3.size());
            Collections.sort(files3);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files3));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testDeleteHash()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1"));
            resolver.updateExtensions();

            assertTrue(hash_file.exists());
            var files = tmp2.listFiles();
            assertEquals(9, files.length);
            Arrays.stream(files).forEach(file -> {
                if (!file.getName().startsWith(Wrapper.WRAPPER_PREFIX)) {
                    file.delete();
                }
            });
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(2, files2.size());
            Collections.sort(files2);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files2));

            resolver.updateExtensions();
            var files3 = FileUtils.getFileList(tmp2);
            assertEquals(2, files3.size());
            Collections.sort(files3);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files3));
            hash_file.delete();

            resolver.updateExtensions();
            var files4 = FileUtils.getFileList(tmp2);
            assertEquals(9, files4.size());
            Collections.sort(files4);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files4));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testUpdateHash()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1"));
            resolver.updateExtensions();

            assertTrue(hash_file.exists());
            var files = tmp2.listFiles();
            assertEquals(9, files.length);
            Arrays.stream(files).forEach(file -> {
                if (!file.getName().startsWith(Wrapper.WRAPPER_PREFIX)) {
                    file.delete();
                }
            });
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(2, files2.size());
            Collections.sort(files2);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files2));

            resolver.updateExtensions();
            var files3 = FileUtils.getFileList(tmp2);
            assertEquals(2, files3.size());
            Collections.sort(files3);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files3));
            FileUtils.writeString("updated", hash_file);

            resolver.updateExtensions();
            var files4 = FileUtils.getFileList(tmp2);
            assertEquals(9, files4.size());
            Collections.sort(files4);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files4));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testAddExtension()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver1 = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1"));
            resolver1.updateExtensions();

            assertTrue(hash_file.exists());
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(9, files2.size());
            Collections.sort(files2);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files2));

            var resolver2 = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1", "org.jsoup:jsoup:1.15.4"));
            resolver2.updateExtensions();
            var files3 = FileUtils.getFileList(tmp2);
            assertEquals(10, files3.size());
            Collections.sort(files3);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                jsoup-1.15.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files3));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }

    @Test
    void testRemoveExtension()
    throws Exception {
        var tmp1 = Files.createTempDirectory("test1").toFile();
        var tmp2 = Files.createTempDirectory("test2").toFile();
        try {
            new Wrapper().createWrapperFiles(tmp2, Version.getVersion());

            var hash_file = new File(tmp1, "wrapper.hash");
            assertFalse(hash_file.exists());
            var files1 = FileUtils.getFileList(tmp2);
            assertEquals(2, files1.size());
            Collections.sort(files1);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties""", String.join("\n", files1));

            var resolver1 = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.antlr:antlr4:4.11.1", "org.jsoup:jsoup:1.15.4"));
            resolver1.updateExtensions();

            assertTrue(hash_file.exists());
            var files2 = FileUtils.getFileList(tmp2);
            assertEquals(10, files2.size());
            Collections.sort(files2);
            assertEquals("""
                ST4-4.3.4.jar
                antlr-runtime-3.5.3.jar
                antlr4-4.11.1.jar
                antlr4-runtime-4.11.1.jar
                bld-wrapper.jar
                bld-wrapper.properties
                icu4j-71.1.jar
                javax.json-1.1.4.jar
                jsoup-1.15.4.jar
                org.abego.treelayout.core-1.0.3.jar""", String.join("\n", files2));

            var resolver2 = new WrapperExtensionResolver(hash_file, tmp2, List.of(MAVEN_CENTRAL), List.of("org.jsoup:jsoup:1.15.4"));
            resolver2.updateExtensions();
            var files3 = FileUtils.getFileList(tmp2);
            assertEquals(3, files3.size());
            Collections.sort(files3);
            assertEquals("""
                bld-wrapper.jar
                bld-wrapper.properties
                jsoup-1.15.4.jar""", String.join("\n", files3));
        } finally {
            tmp2.delete();
            tmp1.delete();
        }
    }
}
