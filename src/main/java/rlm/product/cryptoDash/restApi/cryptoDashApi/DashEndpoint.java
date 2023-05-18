package rlm.product.cryptoDash.restApi.cryptoDashApi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import rlm.product.cryptoDash.model.DashRule;
import rlm.product.cryptoDash.percistence.DashService;

@RestController
public class DashEndpoint {
    @Autowired
    private DashService dashService;

    @GetMapping("getDashBoard")
    public Iterable<DashRule> giveAllDashRules() {
        return dashService.giveAllDashRules();
    }

    @PostMapping("addDashRule")
    public void addDash(@RequestBody DashRule dashRule) {

        /* The DashRule totals are updated with every add performed.
        *  If the coin is already present in the DB the amount and
        *  price will be added to previously saved totals */

        String coinName = dashRule.getCryptoName();
        double amount = dashRule.getTotalAmount();
        double buyPrice = dashRule.getTotalBuyPrice();

        // Get all dashRules out of the DB
        Iterable<DashRule> allSavedRules = dashService.giveAllDashRules();

        // Check if the coin is already present in the DB
        for (DashRule thisRule: allSavedRules) {
            if(coinName.equals(thisRule.getCryptoName())) {
                System.out.println("Found " + thisRule.getCryptoName() + " update performed...");

                // Add coin amount and buy price to previously saved totals
                amount += thisRule.getTotalAmount();
                buyPrice += thisRule.getTotalBuyPrice();

                // Set the new total amount and buy price to the dashRule
                dashRule.setTotalAmount(amount);
                dashRule.setTotalBuyPrice(buyPrice);

                // Discard the old dashRule
                long id = thisRule.getId();
                deleteDashRule((int) id);
            }
        }
        // Add the new dashRule
        dashService.addDashRule(dashRule);
    }

    @DeleteMapping("deleteDashRule/{dashRuleId}")
    public void deleteDashRule(@PathVariable("dashRuleId") int dashRuleId) {
        dashService.deleteDashRule(dashRuleId);
    }
}