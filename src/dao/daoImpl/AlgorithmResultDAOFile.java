package dao.daoImpl;

import dao.AlgorithmResultDAO;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import models.AlgorithmResult;

public class AlgorithmResultDAOFile implements AlgorithmResultDAO {
    
    private final File storageDocument;
    
    public void eraseStorageContent() {
        try {
            FileWriter documentWriter = new FileWriter(this.storageDocument, false);
            documentWriter.close();
        } catch (IOException fileException) {
            System.err.println("Error al limpiar el archivo: " + fileException.getMessage());
        }
    }
    
    public List<AlgorithmResult> retrieveAllRecords() {
        ArrayList<AlgorithmResult> recordCollection = new ArrayList();
        if (!this.storageDocument.exists())
            return recordCollection;
        try {
            BufferedReader documentReader = new BufferedReader(new FileReader(this.storageDocument));
            try {
                String textLine;
                while ((textLine = documentReader.readLine()) != null) {
                    String[] dataTokens = textLine.split(",");
                    if (dataTokens.length == 3) {
                        String algorithmName = dataTokens[0];
                        int pathLength = Integer.parseInt(dataTokens[1]);
                        long executionTime = Long.parseLong(dataTokens[2]);
                        recordCollection.add(new AlgorithmResult(algorithmName, pathLength, executionTime));
                    }
                }
                documentReader.close();
            } catch (Throwable readerException) {
                try {
                    documentReader.close();
                } catch (Throwable closeException) {
                    readerException.addSuppressed(closeException);
                }
                throw readerException;
            }
        } catch (IOException|NumberFormatException processingException) {
            System.err.println("Error reading results from file: " + processingException.getMessage());
        }
        return recordCollection;
    }
    
    public void persistAlgorithmData(AlgorithmResult resultData) {
        List<AlgorithmResult> existingRecords = retrieveAllRecords();
        boolean recordUpdated = false;
        for (int index = 0; index < existingRecords.size(); index++) {
            if (((AlgorithmResult)existingRecords.get(index)).fetchProcessName().equalsIgnoreCase(resultData.fetchProcessName())) {
                existingRecords.set(index, resultData);
                recordUpdated = true;
                break;
            }
        }
        if (!recordUpdated)
            existingRecords.add(resultData);
        try {
            FileWriter documentWriter = new FileWriter(this.storageDocument, false);
            try {
                for (AlgorithmResult record : existingRecords)
                    documentWriter.write(record.toString() + "\n");
                documentWriter.close();
            } catch (Throwable writerException) {
                try {
                    documentWriter.close();
                } catch (Throwable closeException) {
                    writerException.addSuppressed(closeException);
                }
                throw writerException;
            }
        } catch (IOException fileException) {
            System.err.println("Error escribiendo el resultado:  " + fileException.getMessage());
        }
    }
    
    public AlgorithmResultDAOFile(String documentPath) {
        this.storageDocument = new File(documentPath);
    } 
}