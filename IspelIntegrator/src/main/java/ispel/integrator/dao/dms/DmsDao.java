package ispel.integrator.dao.dms;

import ispel.integrator.domain.dms.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DmsDao {

    private JdbcTemplate jdbcTemplate;

    private static final String GET_FRANCHISE_CODE_SQL = "select dealer_cislo from komunik_conf where lower(nazov)=?";
    private static final String GET_DMS_VERSION_SQL = "select val from conf_ini where var='DMS_VERSION'";
    private static final String GET_NISSAN_SOURCE_SEQUENCE_SQL = "select val from conf_ini where var='NISSAN_SOURCE_SEQUENCE'";
    private static final String GET_NISSAN_SITE_SEQUENCE_SQL = "select val from conf_ini where var='NISSAN_SITE_SEQUENCE'";
    private static final String UPDATE_NISSAN_SOURCE_SEQUENCE_SQL = "update conf_ini set val=? where var='NISSAN_SOURCE_SEQUENCE'";
    private static final String UPDATE_NISSAN_SITE_SEQUENCE_SQL = "update conf_ini set val=? where var='NISSAN_SITE_SEQUENCE'";
    private static final String GET_ICO_SQL = "select val from conf_ini where var='ICO'";

    private static final String GET_ORDER_INFO_SQL = "select vfpd,skup_vfpd,kdy_uzav,typ_d,ci_reg,user_name,stav_tach,storno,forma_uhr,datum,reklam_c,ci_auto,celkem_sm from se_zakazky where zakazka=? and skupina=?";

    private static final String GET_CUSTOMER_INFO_SQL = "select typ,icdph,organizace,prijmeni,jmeno,titul,ulice,mesto,stat1,psc,email_souhlas,sms_souhlas,tel,sms,email,dt_nar from odber where ci_reg=?";

    private static final String GET_EMPLOYEE_INFO_SQL = "select  m_prijmeni,m_jmeno from pe_pracovnici where uzivatelske_meno=?";

    private static final String GET_VEHICLE_INFO_SQL = "select spz,vin,vyrobce,model,dt_prod,dt_stk_nasl,dt_emis_nasl,tel from se_auta where ci_auto=?";

    private static final String GET_WORK_INFO_SQL = "select s.pracpoz,s.popis_pp,s.nh,s.opakovani,s.cena,s.cena_bdph,s.pp_id,s.procento,s.druh_pp,p.m_prijmeni,p.m_jmeno " +
                                                    "from se_zprace s " +
                                                    "left join pe_pracovnici p on p.uzivatelske_meno=s.user_name " +
                                                    "where s.zakazka=? and s.skupina=?";

    private static final String GET_PART_INFO_SQL = "select s.katalog,m.nazov_p1,m.original_nd,s.mnozstvi,s.cena_skl,s.cena_bdp,s.cena_prodej,ms.cena_nakup,ms.pocet,ms.dt_vydej,ms.dt_prijem,ms.druh_tovaru,s.sklad from se_zdily s " +
                                                    "join mz_conf_sklad m on s.sklad=m.kod " +
                                                    "join mz_sklad ms on ms.sklad=s.sklad and ms.katalog=s.katalog " +
                                                    "where s.zakazka=? and s.skupina=?";


    @Autowired
    public DmsDao(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    public String getGetFranchiseCode(String franchise) {
        return jdbcTemplate.queryForObject(GET_FRANCHISE_CODE_SQL,
                String.class, franchise.toLowerCase());
    }

    public String getGetDmsVersion() {
        return jdbcTemplate.queryForObject(GET_DMS_VERSION_SQL,
                String.class);
    }

    public BigInteger getDmsSourceSequenceNextVal() {
        String sequence = jdbcTemplate.queryForObject(GET_NISSAN_SOURCE_SEQUENCE_SQL,
                String.class);
        long nextVal = Long.valueOf(sequence).longValue() + 1;
        jdbcTemplate.update(UPDATE_NISSAN_SOURCE_SEQUENCE_SQL, nextVal);
        return BigInteger.valueOf(nextVal);
    }

    public BigInteger getDmsSiteSequenceNextVal() {
        String sequence = jdbcTemplate.queryForObject(GET_NISSAN_SITE_SEQUENCE_SQL,
                String.class);
        long nextVal = Long.valueOf(sequence).longValue() + 1;
        jdbcTemplate.update(UPDATE_NISSAN_SITE_SEQUENCE_SQL, nextVal);
        return BigInteger.valueOf(nextVal);
    }

    public String getIco() {
        return jdbcTemplate.queryForObject(GET_ICO_SQL,
                String.class);
    }

    public OrderInfo getOrderInfo(final String documentNumber, final String documentGroup) {
        return jdbcTemplate.queryForObject(GET_ORDER_INFO_SQL,
                new Object[]{Integer.valueOf(documentNumber), Integer.valueOf(documentGroup)},
                new RowMapper<OrderInfo>() {
                    public OrderInfo mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        OrderInfo orderInfo = new OrderInfo();
                        orderInfo.setDocumentNumber(documentNumber);
                        orderInfo.setDocumentGroup(documentGroup);
                        orderInfo.setVfpd(String.valueOf(rs.getInt(1)));
                        orderInfo.setSkupVfpd(rs.getString(2));
                        orderInfo.setKdyUzavDoklad(rs.getDate(3));
                        orderInfo.setTypD(rs.getString(4));
                        orderInfo.setCi_reg(rs.getString(5));
                        orderInfo.setUserName(rs.getString(6));
                        orderInfo.setStav_tach(String.valueOf(rs.getInt(7)));
                        orderInfo.setStorno(rs.getString(8));
                        orderInfo.setForma_uhr(rs.getString(9));
                        orderInfo.setDatum(rs.getString(10));
                        orderInfo.setReklam_c(rs.getString(11));
                        orderInfo.setCi_auto(rs.getString(12));
                        orderInfo.setCelkem_sm(rs.getBigDecimal(13));
                        return orderInfo;
                    }
                });
    }

    public CustomerInfo getCustomerInfo(final String customerId) {
        return jdbcTemplate.queryForObject(GET_CUSTOMER_INFO_SQL,
                new Object[]{Integer.valueOf(customerId)},
                new RowMapper<CustomerInfo>() {
                    public CustomerInfo mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        CustomerInfo customerInfo = new CustomerInfo();
                        customerInfo.setCi_reg(customerId);
                        customerInfo.setTyp(String.valueOf(rs.getInt(1)));
                        customerInfo.setIcdph(rs.getString(2));
                        customerInfo.setOrganizace(rs.getString(3));
                        customerInfo.setPrijmeni(rs.getString(4));
                        customerInfo.setJmeno(rs.getString(5));
                        customerInfo.setTitul(rs.getString(6));
                        customerInfo.setUlice(rs.getString(7));
                        customerInfo.setMesto(rs.getString(8));
                        customerInfo.setStat1(rs.getString(9));
                        customerInfo.setPsc(rs.getString(10));
                        customerInfo.setEmail_souhlas(rs.getString(11));
                        customerInfo.setSms_souhlas(rs.getString(12));
                        customerInfo.setTel(rs.getString(13));
                        customerInfo.setSms(rs.getString(14));
                        customerInfo.setEmail(rs.getString(15));
                        customerInfo.setDat_nar(rs.getString(16));
                        return customerInfo;
                    }
                });
    }

    public EmployeeInfo getEmployeeInfo(final String employeeId) {
        return jdbcTemplate.queryForObject(GET_EMPLOYEE_INFO_SQL,
                new Object[]{employeeId},
                new RowMapper<EmployeeInfo>() {
                    public EmployeeInfo mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        EmployeeInfo employeeInfo = new EmployeeInfo();
                        employeeInfo.setUzivatelske_meno(employeeId);
                        employeeInfo.setPrijmeni(rs.getString(1));
                        employeeInfo.setJmeno(rs.getString(2));
                        return employeeInfo;
                    }
                });
    }

    public VehicleInfo getVehicleInfo(final String vehicleId) {
        return jdbcTemplate.queryForObject(GET_VEHICLE_INFO_SQL,
                new Object[]{Integer.valueOf(vehicleId)},
                new RowMapper<VehicleInfo>() {
                    public VehicleInfo mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        VehicleInfo vehicleInfo = new VehicleInfo();
                        vehicleInfo.setCi_auto(vehicleId);
                        vehicleInfo.setSpz(rs.getString(1));
                        vehicleInfo.setVin(rs.getString(2));
                        vehicleInfo.setVyrobce(rs.getString(3));
                        vehicleInfo.setModel(rs.getString(4));
                        vehicleInfo.setDt_prod(rs.getString(5));
                        vehicleInfo.setDt_stk_nasl(rs.getString(6));
                        vehicleInfo.setDt_emis_nasl(rs.getString(7));
                        vehicleInfo.setTel(rs.getString(8));
                        return vehicleInfo;
                    }
                });
    }

    public List<WorkInfo> getWorkInfoList(final String orderNumber, final String orderGroup) {
        List<WorkInfo> works = new ArrayList<WorkInfo>();
        List<Map<String, Object>> rows =  jdbcTemplate.queryForList(GET_WORK_INFO_SQL,
                new Object[]{Long.valueOf(orderNumber), Long.valueOf(orderGroup)});
        for (Map<String, Object> row : rows) {
            WorkInfo workInfo = new WorkInfo();
            workInfo.setOrderGroup(orderGroup);
            workInfo.setOrderNumber(orderNumber);
            workInfo.setPracpoz((String) row.get("pracpoz"));
            workInfo.setPopis_pp((String) row.get("popis_pp"));
            workInfo.setNh((BigDecimal) row.get("nh"));
            workInfo.setOpakovani((BigDecimal) row.get("opakovani"));
            workInfo.setCena((BigDecimal) row.get("cena"));
            workInfo.setCenabdph((BigDecimal) row.get("cena_bdph"));
            workInfo.setPp_id((Long) row.get("pp_id"));
            workInfo.setProcento((Long) row.get("procento"));
            workInfo.setDruh_pp((String) row.get("druh_pp"));
            workInfo.setPrijmeni((String) row.get("m_prijmeni"));
            workInfo.setJmeno((String) row.get("m_jmeno"));
            works.add(workInfo);
        }
        return works;
    }

    public List<PartInfo> getPartInfoList(final String orderNumber, final String orderGroup) {
        List<PartInfo> parts = new ArrayList<PartInfo>();
        List<Map<String, Object>> rows =  jdbcTemplate.queryForList(GET_PART_INFO_SQL,
                new Object[]{Long.valueOf(orderNumber), Long.valueOf(orderGroup)});
        for (Map<String, Object> row : rows) {
            PartInfo partInfo = new PartInfo();
            partInfo.setOrderGroup(orderGroup);
            partInfo.setOrderNumber(orderNumber);
            partInfo.setKatalog((String) row.get("katalog"));
            partInfo.setNazov_p1((String) row.get("nazov_p1"));
            partInfo.setOriginal_nd((String) row.get("original_nd"));
            partInfo.setMnozstvi((BigDecimal) row.get("mnozstvi"));
            partInfo.setCena_skl((BigDecimal) row.get("cena_skl"));
            partInfo.setCena_bdp((BigDecimal) row.get("cena_bdp"));
            partInfo.setCena_prodej((BigDecimal) row.get("cena_prodej"));
            partInfo.setCena_nakup((BigDecimal) row.get("cena_nakup"));
            partInfo.setPocet((BigDecimal) row.get("pocet"));
            partInfo.setDt_vydej((String) row.get("dt_vydej"));
            partInfo.setDt_prijem((String) row.get("dt_prijem"));
            partInfo.setDruh_tovaru((String) row.get("druh_tovaru"));
            partInfo.setSklad((Long) row.get("sklad"));
            parts.add(partInfo);
        }
        return parts;
    }

}
