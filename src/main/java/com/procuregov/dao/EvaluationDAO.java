package com.procuregov.dao;

import com.procuregov.model.Evaluation;
import java.util.List;
import java.util.Map;

public interface EvaluationDAO {
    boolean saveEvaluation(Evaluation evaluation);
    boolean saveOrUpdate(Evaluation evaluation);
    Evaluation findByBidAndEvaluator(int bidId, int evaluatorId);
    List<Evaluation> findByBid(int bidId);
    List<Evaluation> findByEvaluator(int evaluatorId);
    List<Evaluation> findByTender(int tenderId);
    double getAverageWeightedScoreForBid(int bidId);
    double getAverageTechnicalScoreForBid(int bidId);
    boolean allEvaluatorsHaveScored(int tenderId);
    boolean hasEvaluatorScored(int tenderId, int evaluatorId);
    int getEvaluatorCount();
    int getCompletedEvaluatorCountForTender(int tenderId);
    Map<Integer, Double> getAverageScoresPerBid(int tenderId);
}
