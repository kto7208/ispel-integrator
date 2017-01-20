package ispel.integrator.service.dms;

import generated.PartsStk;
import generated.PtStk;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.PartInfo;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class PartsStkBuilder {

    public class Builder {

        private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyymmdd");
            }
        };

        private List<PartInfo> parts;

        private Builder() {
        }

        public Builder withParts(List<PartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public PartsStk[] build() {
            long time = System.currentTimeMillis();
            List<PartsStk> partsStkList = new ArrayList<PartsStk>();
            Map<Long, PartsStk> map = new HashMap<Long, PartsStk>();
            for (PartInfo partInfo : parts) {
                PartsStk partsStk = map.get(partInfo.getSklad());
                if (partsStk == null) {
                    partsStk = new PartsStk();
                    partsStk.setDate(new Date(time));
                    partsStk.setFranchiseName(buildFranchiseName(partInfo));
                    partsStk.setWarehouse(String.valueOf(partInfo.getSklad()));
                    map.put(partInfo.getSklad(),partsStk);
                }
                PtStk ptStk = new PtStk();
                ptStk.setNum(partInfo.getKatalog());
                ptStk.setQty(partInfo.getPocet());
                ptStk.setQtyOnOrder(partInfo.getMnozstvi());
                ptStk.setLastOutDate(buildLastOutDate(partInfo));
                ptStk.setLastInDate(buildLastInDate(partInfo));
                ptStk.setUnitCost(partInfo.getCena_nakup());
                partsStk.getPtStk().add(ptStk);
            }
            return map.values().toArray(new PartsStk[partsStkList.size()]);
        }

        private String buildFranchiseName(PartInfo partInfo) {
            if (partInfo.getNazov_p1() != null && partInfo.getNazov_p1().length() > 0) {
                return partInfo.getNazov_p1();
            } else {
                return "nissan";
            }
        }

        private Date buildLastOutDate(PartInfo partInfo) {
            try {
                return dateFormat.get().parse(partInfo.getDt_vydej());
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }

        }

        private Date buildLastInDate(PartInfo partInfo) {
            try {
                return dateFormat.get().parse(partInfo.getDt_prijem());
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }

        }
    }

    public Builder newInstance() {
        return new Builder();
    }
}
