package ispel.integrator.service.dms;

import generated.PartsStk;
import generated.PtStk;
import ispel.integrator.domain.dms.SlipPartInfo;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class SlipPartsStkBuilder {

    public class Builder {

        private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
            @Override
            protected DateFormat initialValue() {
                return new SimpleDateFormat("yyyyMMdd");
            }
        };

        private List<SlipPartInfo> parts;

        private Builder() {
        }

        public SlipPartsStkBuilder.Builder withParts(List<SlipPartInfo> parts) {
            this.parts = parts;
            return this;
        }

        public PartsStk[] build() {
            long time = System.currentTimeMillis();
            List<PartsStk> partsStkList = new ArrayList<PartsStk>();
            Map<String, PartsStk> map = new HashMap<String, PartsStk>();
            for (SlipPartInfo partInfo : parts) {
                PartsStk partsStk = map.get(partInfo.getSklad());
                if (partsStk == null) {
                    partsStk = new PartsStk();
                    partsStk.setDate(new Date(time));
                    partsStk.setFranchiseName(buildFranchiseName(partInfo));
                    partsStk.setWarehouse(String.valueOf(partInfo.getSklad()));
                    map.put(partInfo.getSklad(), partsStk);
                }
                PtStk ptStk = new PtStk();
                ptStk.setNum(partInfo.getKatalog());
                ptStk.setQty(buildQty(partInfo));
                ptStk.setQtyOnOrder(partInfo.getPocet().abs());
                ptStk.setLastOutDate(buildLastOutDate(partInfo));
                ptStk.setLastInDate(buildLastInDate(partInfo));
                ptStk.setUnitCost(buildUniCost(partInfo));
                partsStk.getPtStk().add(ptStk);
            }
            return map.values().toArray(new PartsStk[partsStkList.size()]);
        }

        private BigDecimal buildUniCost(SlipPartInfo partInfo) {
            return partInfo.getCena_nakup();
        }

        private BigDecimal buildQty(SlipPartInfo partInfo) {
//            return partInfo.getPocet() == null ?
//                    BigDecimal.ZERO : partInfo.getSklad_pocet();
            if (partInfo.getPocet() != null && partInfo.getSklad_pocet() != null) {
                return partInfo.getSklad_pocet();
            } else {
                return BigDecimal.ZERO;
            }
        }

        private String buildFranchiseName(SlipPartInfo partInfo) {
            if (partInfo.getNazov_p1() != null && partInfo.getNazov_p1().length() > 0) {
                return partInfo.getNazov_p1();
            } else {
                return "nissan";
            }
        }

        private Date buildLastOutDate(SlipPartInfo partInfo) {
            try {
                if (partInfo.getDt_vydej() != null) {
                    return dateFormat.get().parse(partInfo.getDt_vydej());
                } else {
                    return null;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

        }

        private Date buildLastInDate(SlipPartInfo partInfo) {
            try {
                if (partInfo.getDt_prijem() != null) {
                    return dateFormat.get().parse(partInfo.getDt_prijem());
                } else {
                    return null;
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }

    }

    public SlipPartsStkBuilder.Builder newInstance() {
        return new SlipPartsStkBuilder.Builder();
    }
}
