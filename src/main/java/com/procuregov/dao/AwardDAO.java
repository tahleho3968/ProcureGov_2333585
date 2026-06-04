package com.procuregov.dao;

import com.procuregov.model.Award;

public interface AwardDAO {
    boolean saveAward(Award award);
    Award findByTenderId(int tenderId);
    Award findByWinningBidId(int winningBidId);
    boolean updateAward(Award award);
}
