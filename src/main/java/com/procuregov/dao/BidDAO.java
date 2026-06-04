package com.procuregov.dao;

import com.procuregov.model.Bid;
import java.util.List;

public interface BidDAO {
    boolean saveBid(Bid bid);
    Bid findById(int bidId);
    List<Bid> findBidsByTender(int tenderId);
    List<Bid> findBidsBySupplier(int supplierId);
    boolean hasSupplierBidOnTender(int tenderId, int supplierId);
    boolean updateBid(Bid bid);
    boolean deleteBid(int bidId);
    int getBidCountForTender(int tenderId);
    List<Bid> findBidsByTenderWithSupplierDetails(int tenderId);
}
