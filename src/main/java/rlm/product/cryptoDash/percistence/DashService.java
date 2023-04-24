package rlm.product.cryptoDash.percistence;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rlm.product.cryptoDash.model.DashRule;

@Service
public class DashService {
    @Autowired
    private DashRepository dashRepo;

    public Iterable<DashRule> giveAllDashRules() {
        return dashRepo.findAll();
    }

    public void addDashRule(DashRule dashRule) {
        dashRepo.save(dashRule);
    }

    public void deleteDashRule(long dashRuleId) {
        dashRepo.deleteById(dashRuleId);
    }
}