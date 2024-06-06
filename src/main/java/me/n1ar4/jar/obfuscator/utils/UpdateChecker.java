package me.n1ar4.jar.obfuscator.utils;

import me.n1ar4.jar.obfuscator.Const;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateChecker {
    public static void check() {
        System.out.println("[*] checking for updates...");
        try {
            URL url = new URL(Const.UPDATE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            // 国内访问这个不应该超过 3 秒的延迟
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            String version = getString(connection);
            System.out.println("[*] current version is " + ColorUtil.green(Const.VERSION));
            System.out.println("[*] the latest version is " + ColorUtil.yellow(version));
            if (!version.equals(Const.VERSION)) {
                System.out.println("[*] download url: " + ColorUtil.red(Const.DOWNLOAD_URL));
            }
            connection.disconnect();
        } catch (Exception ignored) {
            System.out.println(ColorUtil.red("[-] update check failed"));
        }
    }

    private static String getString(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        BufferedReader reader;
        if (status > 299) {
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        } else {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        }
        String line;
        StringBuilder content = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            content.append(line);
        }
        reader.close();
        return content.toString().trim();
    }
}
