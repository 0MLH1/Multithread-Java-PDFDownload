package core;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadThread extends Thread {
    private final String url;
    private final int id;
    private final int startByte;
    private final int endByte;
    private final JTextArea logArea;

    public DownloadThread(String url, int id, int startByte, int endByte, JTextArea logArea) {
        this.url = url;
        this.id = id;
        this.startByte = startByte;
        this.endByte = endByte;
        this.logArea = logArea;
    }

    @Override
    public void run() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestProperty("Range", "bytes=" + startByte + "-" + endByte);
            conn.setRequestProperty("Accept-Encoding", "identity");

            try (InputStream in = conn.getInputStream();
                 FileOutputStream out = new FileOutputStream("part" + id)) {

                byte[] buffer = new byte[4096];
                int len;
                int totalDownloaded = 0;
                long lastUpdate = System.currentTimeMillis();

                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                    totalDownloaded += len;

                    long now = System.currentTimeMillis();
                    if (now - lastUpdate > 1000) { // Met à jour toutes les secondes
                        int percent = (int) ((totalDownloaded * 100L) / (endByte - startByte + 1));
                        log("Thread " + id + " : " + percent + "% téléchargé");
                        lastUpdate = now;
                    }
                }
            }

            log("Thread " + id + " terminé : octets " + startByte + "-" + endByte);

        } catch (Exception e) {
            log("Erreur dans le thread " + id + " : " + e.getMessage());
        }
    }


    private void log(String msg) {
        SwingUtilities.invokeLater(() -> logArea.append(msg + "\n"));
    }
}
