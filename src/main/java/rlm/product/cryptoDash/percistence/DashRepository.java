package rlm.product.cryptoDash.percistence;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import rlm.product.cryptoDash.model.DashRule;

@Component
public interface DashRepository extends CrudRepository<DashRule, Long> {

}
