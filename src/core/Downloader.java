package core;

import javax.swing.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Downloader {
    private final String fileURL;
    private final int numThreads;
    private final String fileFormat;
    private final JTextArea logArea;
    private String outputFileName;


    public Downloader(String fileURL, int numThreads, String fileFormat, JTextArea logArea) {
        this.fileURL = fileURL;
        this.numThreads = numThreads;
        this.fileFormat = fileFormat;
        this.logArea = logArea;
    }

    public void startDownload() {
        try {
            URL url = new URL(fileURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            int fileSize = conn.getContentLength();
            conn.disconnect();

            log("Taille du fichier : " + fileSize + " octets");

            // Génération d'un nom unique basé sur la date/heure
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            outputFileName = "downloads/fichier-final_" + timeStamp + "." + fileFormat;

            int partSize = fileSize / numThreads;
            DownloadThread[] threads = new DownloadThread[numThreads];

            for (int i = 0; i < numThreads; i++) {
                int start = i * partSize;
                int end = (i == numThreads - 1) ? fileSize - 1 : (start + partSize - 1);
                threads[i] = new DownloadThread(fileURL, i, start, end, logArea);
                threads[i].start();
            }

            for (DownloadThread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    log("Thread interrompu : " + e.getMessage());
                }
            }

            assembleParts(numThreads);
            log("Téléchargement terminé et fichier assemblé: " + outputFileName);

        } catch (Exception e) {
            log("Erreur : " + e.getMessage());
        }
    }


  
    private void assembleParts(int parts) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outputFileName))) {
            for (int i = 0; i < parts; i++) {
                try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream("part" + i))) {
                    byte[] buffer = new byte[4096];
                    int len;
                    while ((len = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, len);
                    }
                }
                new File("part" + i).delete(); // nettoyage
            }
        }
    }

    private void log(String message) {
        SwingUtilities.invokeLater(() -> {
            logArea.append("\n" + message);
            logArea.setCaretPosition(logArea.getDocument().getLength());
        });
    }
}

    
