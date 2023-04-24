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

        // Get the name, amount and price of the dashRule to add
        String coinName = dashRule.getCryptoName();
        double amount = dashRule.getTotalAmount();
        double buyPrice = dashRule.getTotalBuyPrice();

        // Get all dashRules out of the DB
        Iterable<DashRule> allSavedRules = dashService.giveAllDashRules();

        for (DashRule thisRule: allSavedRules) {
            // Check if thisRule is already present in the table
            if(coinName.equals(thisRule.getCryptoName())) {
                System.out.println("Found " + thisRule.getCryptoName() + " update performed...");

                // Add current rule amount and total buy price to previously saved totals
                amount += thisRule.getTotalAmount();
                buyPrice += thisRule.getTotalBuyPrice();

                // Set new total amount and buy price to dashRule
                dashRule.setTotalAmount(amount);
                dashRule.setTotalBuyPrice(buyPrice);

                // Discard old dashRule
                long id = thisRule.getId();
                deleteDashRule((int) id);
            }
        }
        // Add new dashRule
        dashService.addDashRule(dashRule);
    }

    @DeleteMapping("deleteDashRule/{dashRuleId}")
    public void deleteDashRule(@PathVariable("dashRuleId") int dashRuleId) {
        dashService.deleteDashRule(dashRuleId);
    }
}