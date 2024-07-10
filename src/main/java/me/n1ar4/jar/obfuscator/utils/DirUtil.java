package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.core.ObfEnv;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
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

    public static void zip(String sourceDir, String outputZip) throws IOException {
        File sourceFolder = new File(sourceDir);
        try (ZipOutputStream jos = new ZipOutputStream(Files.newOutputStream(Paths.get(outputZip)))) {
            if (sourceFolder.exists() && sourceFolder.isDirectory()) {
                File[] files = sourceFolder.listFiles();
                if (files != null) {
                    for (File file : files) {
                        addToZip(file, jos, "");
                    }
                }
            }
        } catch (IOException e) {
            logger.error("zip Zip error: {}", e.toString());
        }
    }

    public static void addToZip(File source, ZipOutputStream jos, String parentDir) throws IOException {
        if (source.isDirectory()) {
            String dirPath = parentDir + source.getName() + "/";
            ZipEntry entry = new ZipEntry(dirPath);
            jos.putNextEntry(entry);
            jos.closeEntry();
            for (File file : Objects.requireNonNull(source.listFiles())) {
                addToZip(file, jos, dirPath);
            }
        } else {
            String entryName = parentDir + source.getName();
            ZipEntry entry = new ZipEntry(entryName);
            jos.putNextEntry(entry);
            if (ObfEnv.config.isModifyManifest()) {
                if (ObfEnv.config.isEnablePackageName() || ObfEnv.config.isEnableClassName()) {
                    if (entryName.contains("META-INF/MANIFEST.MF")) {
                        byte[] data = Files.readAllBytes(Paths.get(source.getAbsolutePath()));
                        if (data.length > 0) {
                            String dataString = new String(data);
                            try {
                                dataString = dataString.replace(
                                        ObfEnv.MAIN_CLASS.replace("/", "."), ObfEnv.NEW_MAIN_CLASS);
                            } catch (Exception ignored) {
                            }
                            jos.write(dataString.getBytes(), 0, dataString.length());
                            jos.closeEntry();
                            return;
                        }
                    }
                }
            }
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
