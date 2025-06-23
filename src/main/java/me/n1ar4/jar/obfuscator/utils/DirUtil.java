/*
 * MIT License
 *
 * Project URL: https://github.com/jar-analyzer/jar-obfuscator
 *
 * Copyright (c) 2024-2025 4ra1n (https://github.com/4ra1n)
 *
 * This project is distributed under the MIT license.
 *
 * https://opensource.org/license/mit
 */

package me.n1ar4.jar.obfuscator.utils;

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
            if (ZipEntry.isDirectory()) {
                boolean s = file.mkdirs();
                if (!s) {
                    logger.warn("create dir {} error", destDirectory);
                }
            } else {
                // BUG FIX
                try {
                    Files.createDirectories(Paths.get(file.getParent()));
                } catch (Exception ignored) {
                }
                byte[] buffer = new byte[1024];
                FileOutputStream fos;
                try {
                    fos = new FileOutputStream(file);
                } catch (Exception ignored) {
                    continue;
                }
                int bytesRead;
                while ((bytesRead = ZipInputStream.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesRead);
                }
                fos.close();
            }
            ZipInputStream.closeEntry();
        }
        ZipInputStream.close();
    }

    public static void zip(String sourceDir, String outputZip) {
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
        } catch (IOException e) {
            System.err.println("zip Zip error: " + e.toString());
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
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
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
