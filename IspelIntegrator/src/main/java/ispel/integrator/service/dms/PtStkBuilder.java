package ispel.integrator.service.dms;

import generated.PtStk;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;

@Component
public class PtStkBuilder {

    public class Builder {

        private String num;
        private BigDecimal qty;
        private BigDecimal qtyOnOrder;
        private Date lastOutDate;
        private Date lastInDate;
        private BigDecimal unicost;

        public Builder withNum(String num) {
            this.num = num;
            return this;
        }

        public Builder withQty(BigDecimal qty) {
            this.qty = qty;
            return this;
        }

        public Builder withQtyOnOrder(BigDecimal qtyOnOrder) {
            this.qtyOnOrder = qtyOnOrder;
            return this;
        }

        public Builder withLastOutDate(Date lastOutDate) {
            this.lastOutDate = lastOutDate;
            return this;
        }

        public Builder withLastInDate(Date lastInDate) {
            this.lastInDate = lastInDate;
            return this;
        }

        public Builder withUnicost(BigDecimal unicost) {
            this.unicost = unicost;
            return this;
        }

        public PtStk build() {
            PtStk ptStk = new PtStk();
            ptStk.setNum(this.num);
            ptStk.setQty(this.qty);
            ptStk.setQtyOnOrder(this.qtyOnOrder);
            ptStk.setLastOutDate(this.lastOutDate);
            ptStk.setLastInDate(this.lastInDate);
            ptStk.setUnitCost(this.unicost);
            return ptStk;
        }
    }
}
