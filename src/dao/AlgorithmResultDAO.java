package dao;

import java.util.List;
import models.AlgorithmResult;


public interface AlgorithmResultDAO {
  
  void eraseStorageContent();
  
  List<AlgorithmResult> retrieveAllRecords();
  
  void persistAlgorithmData(AlgorithmResult resultData);
  
}