package me.n1ar4.jar.obfuscator.core;

import me.n1ar4.jar.obfuscator.base.ClassReference;
import me.n1ar4.jar.obfuscator.base.MethodReference;
import me.n1ar4.jar.obfuscator.utils.IOUtils;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BuiltinFilter {
    private static final Map<String, String> builtinFilters = new HashMap<>();

    @SuppressWarnings("all")
    public static void doFilter() {
        InputStream is = BuiltinFilter.class.getClassLoader().getResourceAsStream("builtin.map.txt");

        try {
            byte[] data = IOUtils.readAllBytes(is);
            String dataS = new String(data);
            String[] splits = dataS.split("\n");
            for (String split : splits) {
                if (split.endsWith("\r")) {
                    split = split.substring(0, split.length() - 1);
                }
                split = split.trim();
                if (split.startsWith("#")) {
                    continue;
                }
                if (split == null || split.length() == 0 || split.equals("")) {
                    continue;
                }
                String[] items = split.split(" ");
                builtinFilters.put(items[0], items[1]);
            }
        } catch (Exception ignored) {
            return;
        }

        for (Map.Entry<ClassReference.Handle, List<MethodReference>> entry :
                AnalyzeEnv.methodsInClassMap.entrySet()) {
            ClassReference.Handle key = entry.getKey();
            List<MethodReference> value = entry.getValue();
            ClassReference cr = AnalyzeEnv.classMap.get(key);

            List<String> ignoredMethods = new ArrayList<>();

            for (Map.Entry<String, String> en : builtinFilters.entrySet()) {
                if (cr.getSuperClass().equals(en.getKey())) {
                    for (MethodReference mr : value) {
                        String name = mr.getName();
                        if (name.startsWith(en.getValue())) {
                            ignoredMethods.add(name);
                        }
                    }
                }
                if (cr.getInterfaces().contains(en.getKey())) {
                    for (MethodReference mr : value) {
                        String name = mr.getName();
                        if (name.startsWith(en.getValue())) {
                            ignoredMethods.add(name);
                        }
                    }
                }
            }

            ObfEnv.ignoredClassMethodsMapping.put(cr.getName(), ignoredMethods);
        }
    }
}
