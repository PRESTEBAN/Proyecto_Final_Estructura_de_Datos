package dao.daoImpl;

import dao.AlgorithmResultDAO;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import models.AlgorithmResult;


public class AlgorithmResultDAOFile implements AlgorithmResultDAO {
    
    private final String archivePath;
    
    public AlgorithmResultDAOFile(String fileName) {
        this.archivePath = fileName;
    }
    
    @Override
    public void saveResult(AlgorithmResult result) {
        List<AlgorithmResult> existingData = getAllResults();
        
        boolean wasUpdated = false;
        for (int i = 0; i < existingData.size(); i++) {
            if (existingData.get(i).fetchProcessName().equals(result.fetchProcessName())) {
                existingData.set(i, result);
                wasUpdated = true;
                break;
            }
        }
        
        if (!wasUpdated) {
            existingData.add(result);
        }
        
        writeAllResultsToFile(existingData);
    }
    
    @Override
    public List<AlgorithmResult> getAllResults() {
        List<AlgorithmResult> resultsList = new ArrayList<>();
        
        try {
            Scanner fileScanner = new Scanner(new FileReader(archivePath));
            
            while (fileScanner.hasNextLine()) {
                String dataLine = fileScanner.nextLine().trim();
                
                if (!dataLine.isEmpty()) {
                    AlgorithmResult parsedResult = parseResultFromLine(dataLine);
                    if (parsedResult != null) {
                        resultsList.add(parsedResult);
                    }
                }
            }
            
            fileScanner.close();
            
        } catch (IOException e) {
            System.out.println("Archivo no encontrado o error de lectura: " + e.getMessage());
        }
        
        return resultsList;
    }
    
    @Override
    public void clearAllResults() {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivePath, false));
            writer.write(""); 
            writer.close();
        } catch (IOException e) {
            System.out.println("Error limpiando archivo: " + e.getMessage());
        }
    }
    
    private void writeAllResultsToFile(List<AlgorithmResult> results) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(archivePath, false));
            
            for (AlgorithmResult result : results) {
                writer.write(result.toString());
                writer.newLine();
            }
            
            writer.close();
            
        } catch (IOException e) {
            System.out.println("Error escribiendo archivo: " + e.getMessage());
        }
    }

    private AlgorithmResult parseResultFromLine(String line) {
        try {
            String[] parts = line.split(",");
            
            if (parts.length >= 3) {
                String algorithmName = parts[0].trim();
                int pathLength = Integer.parseInt(parts[1].trim());
                long executionTime = Long.parseLong(parts[2].trim());
                
                return new AlgorithmResult(algorithmName, pathLength, executionTime);
            }
        } catch (NumberFormatException e) {
            System.out.println("Error parseando línea: " + line);
        }
        
        return null;
    }
}