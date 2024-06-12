package me.n1ar4.jar.obfuscator.jvmti;

import me.n1ar4.jar.obfuscator.utils.JNIUtil;
import me.n1ar4.jar.obfuscator.utils.VerUtil;
import me.n1ar4.log.LogManager;
import me.n1ar4.log.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipOutputStream;

/**
 * Patch core class
 */
public class PatchHelper implements Constants {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Patch a jar
     *
     * @param jarPath     jar path
     * @param libPath     encrypt lib path
     * @param packageName encrypt package name
     * @param key         encrypt key
     */
    @SuppressWarnings("unchecked")
    public static void patchJar(Path jarPath, Path libPath, String packageName, byte[] key) {
        logger.info("start patch jar");

        JNIUtil.loadLib(libPath.toAbsolutePath().toString());
        packageName = packageName.replaceAll("\\.", "/");

        try {
            File srcFile = jarPath.toFile();
            String srcName = srcFile.getName();
            // rename *.jar -> *_encrypted.jar
            String outputFileName = String.format("%s_%s.jar",
                    srcName.substring(0, srcName.lastIndexOf(".")), NewFileSuffix);
            File dstFile = new File(outputFileName);

            FileOutputStream dstFos = new FileOutputStream(dstFile);
            JarOutputStream dstJar = new JarOutputStream(dstFos);
            dstJar.setLevel(JarOutputStream.STORED);

            JarFile srcJar = new JarFile(srcFile);
            byte[] buf = new byte[1024];

            ByteArrayOutputStream bao = new ByteArrayOutputStream();
            Enumeration<JarEntry> enumeration = srcJar.entries();

            while (enumeration.hasMoreElements()) {
                JarEntry entry = enumeration.nextElement();
                InputStream is = srcJar.getInputStream(entry);
                int len;
                while ((len = is.read(buf, 0, buf.length)) != -1) {
                    bao.write(buf, 0, len);
                }
                byte[] bytes = bao.toByteArray();

                String name = entry.getName();

                String tempClassName;
                if (name.toLowerCase().endsWith(ClassFile)) {
                    if (name.startsWith("BOOT-INF/classes/")) {
                        tempClassName = name.split("BOOT-INF/classes/")[1];
                        if (tempClassName.startsWith(packageName)) {
                            try {
                                bytes = CodeEncryptor.encrypt(bytes, bytes.length, key);
                            } catch (Exception e) {
                                logger.error("encrypt error: {}", e.toString());
                                return;
                            }
                        }
                    } else {
                        // encrypt target class
                        if (name.startsWith(packageName)) {
                            try {
                                bytes = CodeEncryptor.encrypt(bytes, bytes.length, key);
                            } catch (Exception e) {
                                logger.error("encrypt error: {}", e.toString());
                                return;
                            }
                        }
                    }
                }

                // resolve springboot jar
                if (name.startsWith("BOOT-INF") &&
                        name.toLowerCase().endsWith(".jar")) {
                    JarEntry ne = new JarEntry(name);
                    ne.setSize(entry.getSize());
                    ne.setCrc(entry.getCrc());
                    ne.setMethod(JarEntry.STORED);
                    dstJar.putNextEntry(ne);
                    dstJar.write(bytes);
                    bao.reset();
                    continue;
                }

                JarEntry ne = new JarEntry(name);
                ne.setMethod(JarEntry.DEFLATED);

                try {
                    dstJar.putNextEntry(ne);

                    if (VerUtil.isJava8()) {
                        // allow duplicate entry
                        // https://stackoverflow.com/questions/39958486/
                        Field namesField = ZipOutputStream.class.getDeclaredField("names");
                        namesField.setAccessible(true);
                        Object obj = namesField.get(dstJar);
                        HashSet<String> names = (HashSet<String>) obj;
                        names.remove(name);
                    }

                    dstJar.write(bytes);
                } catch (Exception z) {
                    logger.warn("put entry: {}", z.toString());
                }
                bao.reset();
            }

            srcJar.close();
            dstJar.close();
            dstFos.close();

            logger.info("encrypt finished");
            logger.info("output file: {}", outputFileName);
        } catch (Exception e) {
            logger.error("patch error: {}", e.toString());
        }
    }
}
