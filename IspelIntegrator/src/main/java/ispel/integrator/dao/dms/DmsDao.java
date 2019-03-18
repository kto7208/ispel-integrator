package ispel.integrator.dao.dms;

import ispel.integrator.domain.dms.*;
import ispel.integrator.service.ServiceCallTimestampHolder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class DmsDao {

    private static final Logger logger = Logger
            .getLogger(DmsDao.class);

    private JdbcTemplate jdbcTemplate;

    private static final String GET_ORGANIZACE_CODE_SQL = "select val from conf_ini where var='ORGANIZACE'";
    private static final String GET_ORGANIZACE_ISPEL_CODE_SQL = "select val from conf_ini where var='ORGANIZACE_ISPEL'";
    private static final String GET_FRANCHISE_CODE_SQL = "select dealer_cislo from komunik_conf where lower(nazov)=?";
    private static final String GET_DMS_VERSION_SQL = "select val from conf_ini where var='DMS_VERSION'";
    private static final String GET_NISSAN_PARTS_ONLY_SQL = "select val from conf_ini where var='NISSAN_PARTS_ONLY'";
    private static final String GET_NISSAN_SOURCE_SEQUENCE_SQL = "select val from conf_ini where var='NISSAN_SOURCE_SEQUENCE'";
    private static final String GET_NISSAN_SITE_SEQUENCE_SQL = "select val from conf_ini where var='NISSAN_SITE_SEQUENCE'";
    private static final String UPDATE_NISSAN_SOURCE_SEQUENCE_SQL = "update conf_ini set val=? where var='NISSAN_SOURCE_SEQUENCE'";
    private static final String UPDATE_NISSAN_SITE_SEQUENCE_SQL = "update conf_ini set val=? where var='NISSAN_SITE_SEQUENCE'";
    private static final String GET_ICO_SQL = "select val from conf_ini where var='ICO'";

    private static final String GET_ORDER_INFO_SQL = "select s.vfpd,s.skup_vfpd,s.kdy_uzav_doklad,s.typ_d,s.ci_reg,s.user_name,s.stav_tach,s.storno,s.forma_uhr,s.datum,s.reklam_c,s.ci_auto,s.celkem_sm,svf.oznaceni oznaceni_svf, spd.oznaceni oznaceni_spd,svf.interne from se_zakazky s" +
            " left outer join c_svf svf on svf.skvf=s.skup_vfpd" +
            " left outer join c_spd spd on spd.skpd=s.skup_vfpd" +
            " where zakazka=? and skupina=?";

    private static final String GET_CUSTOMER_INFO_SQL = "select typ,icdph,organizace,prijmeni,jmeno,titul,ulice,mesto,stat1,psc,email_souhlas,sms_souhlas,tel,sms,email,dt_nar,souhlas,forma from odber where ci_reg=?";

    private static final String GET_EMPLOYEE_INFO_SQL = "select  m_prijmeni,m_jmeno from pe_pracovnici where uzivatelske_meno=?";

    private static final String GET_VEHICLE_INFO_SQL = "select spz,vin,vyrobce,model,dt_prod,dt_stk_nasl,dt_emis_nasl,tel,rok,dt_vyroby,typ_vozidla,barva,barva_nazev,popis from se_auta where ci_auto=?";

    private static final String GET_WORK_INFO_SQL = "select s.pracpoz,s.popis_pp,s.nh,s.opakovani,s.cena,s.cena_bdph,s.pp_id,s.procento,s.druh_pp,p.m_prijmeni,p.m_jmeno,s.ostatni,s.cena_jedn,s.hlavna_pp,s.vlastna_pp,r.saga1 " +
                                                    "from se_zprace s " +
                                                    "left join pe_pracovnici p on p.uzivatelske_meno=s.user_name " +
            "left join reklamace r on r.skupina=s.skupina and r.zakazka=s.zakazka " +
            "where s.fakturovat=1 and s.zakazka=? and s.skupina=?";

    private static final String GET_PART_INFO_SQL = "select s.katalog,m.nazov_p1,m.original_nd,s.mnozstvi,s.cena_skl,s.cena_bdp,s.cena_prodej,ms.cena_nakup,ms.pocet,ms.dt_vydej,ms.dt_prijem,ms.druh_tovaru,s.sklad,ms.cena_dopor,s.ostatni,s.nazev,m.tlac_sklad from se_zdily s " +
            "left outer join mz_conf_sklad m on s.sklad=m.kod " +
            "left outer join mz_sklad ms on ms.sklad=s.sklad and ms.katalog=s.katalog " +
            "where s.fakturovat=1 and s.zakazka=? and s.skupina=?";

    private static final String GET_DESCRIPTION_INFO_SQL = "select popis,poradi from se_popisopr where zakazka=? and skupina=? order by poradi asc";


    private static final String GET_SLIP_INFO_SQL = "select m.vf_pd,m.skup_vfpd,m.dt_uzavreni,m.doklad_typ,m.ci_reg,m.user_name,c.doklad from mz_doklady m " +
            "left join c_cdd c on m.cis_pohybu=c.cpo " +
            "where m.ci_dok=? and m.sklad=? and m.doklad='VYD'";

    private static final String GET_SLIP_PART_INFO_SQL = "select p.pocet p_pocet,p.cena,p.celkem_pro,p.cena_prodej,s.druh_tovaru,p.katalog,s.pocet s_pocet,s.dt_vydej,s.dt_prijem,s.cena_nakup,m.nazov_p1,m.original_nd,m.tlac_sklad from mz_pohyby p " +
            "left outer join mz_sklad s on p.sklad=s.sklad and s.katalog=p.katalog " +
            "left outer join mz_conf_sklad m on p.sklad=m.kod " +
            "where p.ci_dok=? and p.sklad=? and p.doklad='VYD'";

    private static final String UPDATE_ORDER_SQL = "update se_zakazky set write_time=?,nissan_processed=? where zakazka=? and skupina=?";

    private static final String UPDATE_SLIP_SQL = "update mz_doklady set write_time=?,nissan_processed=? where ci_dok=? and sklad=? and doklad='VYD'";

    private static final String GET_SLIPS_SQL = "select mz_doklady.ci_dok,mz_doklady.sklad from mz_doklady " +
            "left join mz_conf_sklad on mz_doklady.SKLAD=mz_conf_sklad.KOD " +
            "where mz_conf_sklad.TLAC_SKLAD=21 and mz_doklady.STAV='Z' " +
            "and mz_doklady.DOKLAD='VYD' and DOKLAD_TYP in ('PD','VF') and mz_doklady.NISSAN_PROCESSED='N'";

    private static final String GET_ORDERS_SQL = "select s.zakazka,s.SKUPINA from se_zakazky s " +
            "left join se_skupz sz on s.SKUPINA=sz.SKUPINA where sz.TLAC_TYP=21 AND s.STAV='U' and s.NISSAN_PROCESSED='N'";

    @Autowired
    public DmsDao(DataSource ds) {
        this.jdbcTemplate = new JdbcTemplate(ds);
    }


    public String getOrganizace() {
        return jdbcTemplate.queryForObject(GET_ORGANIZACE_CODE_SQL,
                String.class);
    }

    public String getOrganizaceIspel() {
        return jdbcTemplate.queryForObject(GET_ORGANIZACE_ISPEL_CODE_SQL,
                String.class);
    }
    public String getFranchiseCode(String franchise) {
        logger.debug("franchise: " + franchise);
        return jdbcTemplate.queryForObject(GET_FRANCHISE_CODE_SQL,
                String.class, franchise.toLowerCase());
    }

    public String getDmsVersion() {
        return jdbcTemplate.queryForObject(GET_DMS_VERSION_SQL,
                String.class);
    }

    public boolean getNissanPartsOnly() {
        String n = jdbcTemplate.queryForObject(GET_NISSAN_PARTS_ONLY_SQL,
                String.class);
        if ("ANO".equalsIgnoreCase(n)) {
            return true;
        } else {
            return false;
        }
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
        logger.debug("zakazka: " + documentNumber);
        logger.debug("skupina: " + documentGroup);
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
                        orderInfo.setOznaceni_svf(rs.getString(14));
                        orderInfo.setOznaceni_spd(rs.getString(15));
                        orderInfo.setInterne(rs.getInt(16));
                        return orderInfo;
                    }
                });
    }

    public CustomerInfo getCustomerInfo(final String customerId) {
        logger.debug("ci_reg: " + customerId);
        try {
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
                            customerInfo.setSouhlas(rs.getString(17));
                            customerInfo.setForma(rs.getInt(18));
                            return customerInfo;
                        }
                    });
        } catch (EmptyResultDataAccessException e) {
            logger.info("no customer info");
            return null;
        }
    }

    public EmployeeInfo getEmployeeInfo(final String employeeId) {
        logger.debug("uzivatelske_meno: " + employeeId);
        try {
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
        } catch (EmptyResultDataAccessException e) {
            EmployeeInfo employeeInfo = new EmployeeInfo();
            employeeInfo.setPrijmeni("client");
            employeeInfo.setJmeno("");
            employeeInfo.setUzivatelske_meno(employeeId);
            return employeeInfo;
        }
    }

    public VehicleInfo getVehicleInfo(final String vehicleId) {
        logger.debug("ci_auto: " + vehicleId);
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
                        vehicleInfo.setRok(rs.getInt(9));
                        vehicleInfo.setDt_vyroby(rs.getString(10));
                        vehicleInfo.setTyp_vozidla(rs.getString(11));
                        vehicleInfo.setBarva(rs.getString(12));
                        vehicleInfo.setBarva_nazev(rs.getString(13));
                        vehicleInfo.setPopis(rs.getString(14));
                        return vehicleInfo;
                    }
                });
    }

    public List<WorkInfo> getWorkInfoList(final String orderNumber, final String orderGroup) {
        logger.debug("zakazka: " + orderNumber);
        logger.debug("skupina: " + orderGroup);
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
            workInfo.setOstatni((String) row.get("ostatni"));
            workInfo.setCena_jednotkova((BigDecimal) row.get("cena_jedn"));
            workInfo.setHlavna_pp((String) row.get("hlavna_pp"));
            workInfo.setVlastna_pp((String) row.get("vlastna_pp"));
            workInfo.setSaga1((String) row.get("saga1"));
            works.add(workInfo);
        }
        return works;
    }

    public List<PartInfo> getPartInfoList(final String orderNumber, final String orderGroup) {
        logger.debug("zakazka: " + orderNumber);
        logger.debug("skupina: " + orderGroup);
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
            partInfo.setCena_dopor((BigDecimal) row.get("cena_dopor"));
            partInfo.setOstatni((String) row.get("ostatni"));
            partInfo.setNazev((String) row.get("nazev"));
            partInfo.setTlac_sklad((Long) row.get("tlac_sklad"));
            parts.add(partInfo);
        }
        return parts;
    }

    public List<DescriptionInfo> getDescriptionInfoList(final String orderNumber, final String orderGroup) {
        logger.debug("zakazka: " + orderNumber);
        logger.debug("skupina: " + orderGroup);
        List<DescriptionInfo> descriptions = new ArrayList<DescriptionInfo>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_DESCRIPTION_INFO_SQL,
                new Object[]{Long.valueOf(orderNumber), Long.valueOf(orderGroup)});
        for (Map<String, Object> row : rows) {
            DescriptionInfo descriptionInfo = new DescriptionInfo();
            descriptionInfo.setDocumentGroup(orderGroup);
            descriptionInfo.setDocumentNumber(orderNumber);
            descriptionInfo.setPopis((String) row.get("popis"));
            descriptionInfo.setPoradi((Long) row.get("poradi"));
            descriptions.add(descriptionInfo);
        }
        return descriptions;
    }

    public SlipInfo getSlipInfo(final String ci_dok, final String sklad) {
        logger.debug("ci_dok: " + ci_dok);
        logger.debug("sklad: " + sklad);
        return jdbcTemplate.queryForObject(GET_SLIP_INFO_SQL,
                new Object[]{Integer.valueOf(ci_dok), Integer.valueOf(sklad)},
                new RowMapper<SlipInfo>() {
                    public SlipInfo mapRow(ResultSet rs, int arg1)
                            throws SQLException {
                        SlipInfo slipInfo = new SlipInfo();
                        slipInfo.setCidok(ci_dok);
                        slipInfo.setSklad(sklad);
                        slipInfo.setVfpd(String.valueOf(rs.getInt(1)));
                        slipInfo.setSkupvfpd(rs.getString(2));
                        slipInfo.setDtuzavreni(rs.getDate(3));
                        slipInfo.setDoklad_typ(rs.getString(4));
                        slipInfo.setCi_reg(String.valueOf(rs.getInt(5)));
                        slipInfo.setUser_name(rs.getString(6));
                        slipInfo.setDoklad(rs.getString(7));
                        return slipInfo;
                    }
                });
    }

    public List<SlipPartInfo> getSlipPartInfoList(final String ci_dok, final String sklad) {
        logger.debug("ci_dok: " + ci_dok);
        logger.debug("sklad: " + sklad);
        List<SlipPartInfo> parts = new ArrayList<SlipPartInfo>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_SLIP_PART_INFO_SQL,
                new Object[]{Long.valueOf(ci_dok), Long.valueOf(sklad)});
        for (Map<String, Object> row : rows) {
            SlipPartInfo slipPartInfo = new SlipPartInfo();
            slipPartInfo.setSklad(sklad);
            slipPartInfo.setCi_dok(ci_dok);
            slipPartInfo.setPocet((BigDecimal) row.get("p_pocet"));
            slipPartInfo.setCena((BigDecimal) row.get("cena"));
            slipPartInfo.setCelkem_pro((BigDecimal) row.get("celkem_pro"));
            slipPartInfo.setCena_prodej((BigDecimal) row.get("cena_prodej"));
            slipPartInfo.setDruh_tovaru((String) row.get("druh_tovaru"));
            slipPartInfo.setKatalog((String) row.get("katalog"));
            slipPartInfo.setSklad_pocet((BigDecimal) row.get("s_pocet"));
            slipPartInfo.setDt_vydej((String) row.get("dt_vydej"));
            slipPartInfo.setDt_prijem((String) row.get("dt_prijem"));
            slipPartInfo.setDruh_tovaru((String) row.get("druh_tovaru"));
            slipPartInfo.setCena_nakup((BigDecimal) row.get("cena_nakup"));
            slipPartInfo.setNazov_p1((String) row.get("nazov_p1"));
            slipPartInfo.setOriginal_nd((String) row.get("original_nd"));
            slipPartInfo.setTlac_sklad((Long) row.get("tlac_sklad"));
            parts.add(slipPartInfo);
        }
        return parts;
    }

    public void updateOrder(final String orderNumber, final String orderGroup) {
        jdbcTemplate.update(UPDATE_ORDER_SQL,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement ps)
                            throws SQLException {
                        ps.setTimestamp(1, new Timestamp(
                                ServiceCallTimestampHolder.getAsLong()));
                        ps.setString(2, "A");
                        ps.setInt(3, Integer.valueOf(orderNumber).intValue());
                        ps.setInt(4, Integer.valueOf(orderGroup).intValue());
                    }

                });
    }

    public void updateSlip(final String ci_dok, final String sklad) {
        jdbcTemplate.update(UPDATE_SLIP_SQL,
                new PreparedStatementSetter() {
                    public void setValues(PreparedStatement ps)
                            throws SQLException {
                        ps.setTimestamp(1, new Timestamp(
                                ServiceCallTimestampHolder.getAsLong()));
                        ps.setString(2, "A");
                        ps.setInt(3, Integer.valueOf(ci_dok).intValue());
                        ps.setInt(4, Integer.valueOf(sklad).intValue());
                    }

                });
    }

    public List<OrderKey> getOrdersForMultipleProcessing() {
        List<OrderKey> keys = new ArrayList<OrderKey>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_ORDERS_SQL);
        for (Map<String, Object> row : rows) {
            OrderKey key = new OrderKey();
            key.setZakazka((Long) row.get("zakazka"));
            key.setSkupina((Integer) row.get("skupina"));
            keys.add(key);
        }
        return keys;
    }

    public List<OrderKey> getSlipsForMultipleProcessing() {
        List<OrderKey> keys = new ArrayList<OrderKey>();
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(GET_SLIPS_SQL);
        for (Map<String, Object> row : rows) {
            OrderKey key = new OrderKey();
            key.setZakazka((Long) row.get("ci_dok"));
            key.setSklad((Long) row.get("sklad"));
            keys.add(key);
        }
        return keys;
    }

}
