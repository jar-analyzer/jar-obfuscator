/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2026 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.Const;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class DirUtil {
    private static final Logger logger = LogManager.getLogger();

    public static void unzip(String ZipFilePath, String destDir) throws IOException {
        File destDirectory = new File(destDir);
        String destCanonicalPath = destDirectory.getCanonicalPath() + File.separator;
        if (!destDirectory.exists()) {
            boolean s = destDirectory.mkdirs();
            if (!s) {
                logger.warn("create dir {} error", destDirectory);
            }
        }
        ZipInputStream ZipInputStream = new ZipInputStream(Files.newInputStream(
                Paths.get(ZipFilePath)));
        ZipEntry ZipEntry;
        while ((ZipEntry = ZipInputStream.getNextEntry()) != null) {
            File file = new File(destDir, ZipEntry.getName());
            String fileCanonicalPath = file.getCanonicalPath();
            if (!fileCanonicalPath.startsWith(destCanonicalPath)) {
                ZipInputStream.closeEntry();
                throw new IOException("unsafe zip entry: " + ZipEntry.getName());
            }
            if (ZipEntry.isDirectory()) {
                boolean s = file.mkdirs();
                if (!s) {
                    logger.warn("create dir {} error", destDirectory);
                }
            } else {
                Files.createDirectories(Paths.get(file.getParent()));
                byte[] buffer = new byte[1024];
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    int bytesRead;
                    while ((bytesRead = ZipInputStream.read(buffer)) != -1) {
                        fos.write(buffer, 0, bytesRead);
                    }
                }
            }
            ZipInputStream.closeEntry();
        }
        ZipInputStream.close();
    }

    public static void zip(String sourceDir, String outputZip) throws IOException {
        File sourceFolder = new File(sourceDir);
        try (ZipOutputStream jos = new ZipOutputStream(Files.newOutputStream(Paths.get(outputZip)))) {
            // 设置压缩方法为 STORED（不压缩）
            jos.setMethod(ZipOutputStream.STORED);

            if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                File[] files = sourceFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        addToZip(file, jos, "");
                    }
                }
            }
        }
    }

    public static void addToZip(File source, ZipOutputStream jos, String parentDir) throws IOException {
        if (source.isDirectory()) {
            String dirPath = parentDir + source.getName() + "/";
            ZipEntry entry = new ZipEntry(dirPath);
            entry.setMethod(ZipEntry.STORED);
            entry.setSize(0);
            entry.setCompressedSize(0);
            entry.setCrc(0);
            jos.putNextEntry(entry);
            jos.closeEntry();
            for (File file : Objects.requireNonNull(source.listFiles())) {
                addToZip(file, jos, dirPath);
            }
        } else {
            String entryName = parentDir + source.getName();
            ZipEntry entry = new ZipEntry(entryName);
            entry.setMethod(ZipEntry.STORED);
            long size = source.length();
            long crc = computeCRC32(source);

            entry.setSize(size);
            entry.setCompressedSize(size);
            entry.setCrc(crc);
            jos.putNextEntry(entry);
            try (FileInputStream fis = new FileInputStream(source)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = fis.read(buffer)) != -1) {
                    jos.write(buffer, 0, bytesRead);
                }
            }
            jos.closeEntry();
        }
    }

    private static long computeCRC32(File file) throws IOException {
        CRC32 crc = new CRC32();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                crc.update(buffer, 0, bytesRead);
            }
        }
        return crc.getValue();
    }

    public static void deleteDirectory(File directory) {
        try {
            File tempDir = new File(Const.TEMP_DIR).getCanonicalFile();
            File target = directory.getCanonicalFile();
            if (!target.equals(tempDir)) {
                throw new IllegalArgumentException("refuse to delete non-temp directory: " + target);
            }
            deleteDirectoryInternal(target);
        } catch (IOException ex) {
            throw new IllegalStateException("delete temp directory failed: " + directory, ex);
        }
    }

    private static void deleteDirectoryInternal(File directory) {
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectoryInternal(file);
                } else {
                    boolean s = file.delete();
                    if (!s) {
                        logger.debug("delete dir {} error", file);
                    }
                }
            }
        }
        boolean s = directory.delete();
        if (!s) {
            logger.debug("delete dir {} error", directory);
        }
    }
}
