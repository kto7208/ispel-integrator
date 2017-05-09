package ispel.integrator.dao;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import ispel.integrator.domain.CarDetail;
import ispel.integrator.domain.CarInfo;
import ispel.integrator.service.ServiceCallTimestampHolder;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;



@Repository
public class DaoImpl implements Dao {

	private static final String IMAGES_DIR_CONST = "DIR_FOTO_AU";

	private static final Logger logger = Logger.getLogger(DaoImpl.class);

	private static final String GET_CAR_INFO_SQL = "select vin,spz,dt_stk,dt_stk_nasl,dt_emis,dt_emis_nasl,"
			+ "dt_ko,km_stk,km_emis,km_ko, dt_overenievozidla, dt_getvinexpert"
			+ " from se_auta where ci_auto=?";

	private static final String GET_CAR_DETAIL_SQL = "select id, typ_inf, info from se_auta_ws where ci_auto=?";

	private static final String UPDATE_CAR_DETAIL_SQL = "update se_auta_ws set write_time=?, info=? where typ_inf=? and ci_auto=?";

	private static final String INSERT_CAR_DETAIL_SQL = "insert into se_auta_ws (write_time, ci_auto,typ_inf,info) values(?,?,?,?)";

	private static final String LOG_RESULT_SQL = "insert into se_auta_ws_log (write_time, method_name,url,processed,ci_auto,zakazka,skupina,err_text,xml_input,xml_output,db_datasource) "
			+ "values(?,?,?,?,?,?,?,?,?,?,?)";

	private static final String UPDATE_CAR_INFO_SQL = "update se_auta set write_time=?,dt_stk=?,dt_stk_nasl=?,dt_emis=?,dt_emis_nasl=?,"
			+ "dt_ko=?,km_stk=?,km_emis=?,km_ko=?,dt_overenievozidla=?,dt_getvinexpert=?  where ci_auto=?";

	private static final String GET_IMAGES_DIRECTORY_SQL = "select val from conf_ini where var=?";

	private static final String INSERT_PHOTO_INFO_SQL = "insert into se_auta_foto (write_time, ci_auto, foto_nazev) values(?,?,?)";

	private JdbcTemplate jdbcTemplate;

	@Autowired
	public DaoImpl(DataSource ds) {
		this.jdbcTemplate = new JdbcTemplate(ds);
	}

	public void logResult(final Result result) {
		this.jdbcTemplate.update(LOG_RESULT_SQL, new PreparedStatementSetter() {

			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setTimestamp(1,
						new Timestamp(ServiceCallTimestampHolder.getAsLong()));
				ps.setString(2, result.getMethodName());
				ps.setString(3, result.getUrl());
				ps.setInt(4, result.getProcessed());
				ps.setInt(5, result.getCarId());
				ps.setInt(6, result.getOrderNumber());
				ps.setInt(7, result.getOrderGroup());
				ps.setString(8, result.getErrorText());
				ps.setString(9, result.getXmlInput());
				ps.setString(10, result.getXmlOutput());
				ps.setString(11, result.getDataSourceName());
			}
		});
	}

	public CarInfo getCarInfoById(final String id) {
		if (id == null) {
			throw new IllegalArgumentException("id is null");
		}
		CarInfo carInfo = jdbcTemplate.queryForObject(GET_CAR_INFO_SQL,
				new Object[] { id }, new RowMapper<CarInfo>() {
					public CarInfo mapRow(ResultSet rs, int arg1)
							throws SQLException {
						CarInfo carInfo = new CarInfo();
						carInfo.setCarId(Long.valueOf(id));
						carInfo.setEcvString(rs.getString(2));
						carInfo.setVinString(rs.getString(1));
						carInfo.setDt_stk(rs.getString(3));
						carInfo.setDt_stk_nasl(rs.getString(4));
						carInfo.setDt_emis(rs.getString(5));
						carInfo.setDt_emis_nasl(rs.getString(6));
						carInfo.setDt_ko(rs.getString(7));
						carInfo.setKm_stk(rs.getInt(8));
						carInfo.setKm_emis(rs.getInt(9));
						carInfo.setKm_ko(rs.getInt(10));
						carInfo.setDt_overenievozidla(rs.getTimestamp(11));
						carInfo.setDt_getvinexpert(rs.getTimestamp(12));
						return carInfo;
					}

				});
		if (carInfo != null) {
			List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					GET_CAR_DETAIL_SQL, new Object[] { id });
			for (Map<String, Object> row : rows) {
				CarDetail detail = new CarDetail();
				detail.setId((Long) row.get("id"));
				detail.setType(CarDetail.Type.getTypeByCode((String) row
						.get("typ_inf")));
				detail.setText((String) row.get("info"));
				carInfo.setDetail(detail);
			}
		}
		return carInfo;
	}

	public void persistCarInfo(CarInfo carInfo,
			AdapterRequest.MethodName methodName) {
		if (AdapterRequest.MethodName.GetVinExpert.equals(methodName)) {
			CarDetail detail = carInfo.getDetail(CarDetail.Type.VinInfo);
			if (detail.getId() <= 0) {
				insertCarDetail(carInfo.getCarId(), detail);
			} else {
				updateCarDetail(carInfo.getCarId(), detail);
			}
		} else if (AdapterRequest.MethodName.VerifyCar.equals(methodName)) {
			for (CarDetail detail : carInfo.getDetails()) {
				if (CarDetail.Type.VinInfo.equals(detail.getType())) {
					continue;
				}
				if (detail.getId() <= 0) {
					insertCarDetail(carInfo.getCarId(), detail);
				} else {
					updateCarDetail(carInfo.getCarId(), detail);
				}
			}
		}
		updateCarInfo(carInfo);
	}

	private void insertCarDetail(final long carId, final CarDetail detail) {
		int i = jdbcTemplate.update(INSERT_CAR_DETAIL_SQL,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setTimestamp(1, new Timestamp(
								ServiceCallTimestampHolder.getAsLong()));
						ps.setLong(2, carId);
						ps.setString(3, detail.getType().getCode());
						ps.setString(4, detail.getText());
					}

				});
		logger.debug("carId: " + carId + ", detail: " + detail.getType().name()
				+ "number of inserted: " + i);
	}

	private void updateCarDetail(final long carId, final CarDetail detail) {
		int i = jdbcTemplate.update(UPDATE_CAR_DETAIL_SQL,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setTimestamp(1, new Timestamp(
								ServiceCallTimestampHolder.getAsLong()));
						ps.setString(2, detail.getText());
						ps.setString(3, detail.getType().getCode());
						ps.setLong(4, carId);
					}

				});
		logger.debug("carId: " + carId + ", detail: " + detail.getType().name()
				+ "number of updated: " + i);
	}

	private void updateCarInfo(final CarInfo carInfo) {
		int i = jdbcTemplate.update(UPDATE_CAR_INFO_SQL,
				new PreparedStatementSetter() {

					public void setValues(PreparedStatement ps)
							throws SQLException {
						ps.setTimestamp(1, new Timestamp(
								ServiceCallTimestampHolder.getAsLong()));
						ps.setString(2, carInfo.getDt_stk());
						ps.setString(3, carInfo.getDt_stk_nasl());
						ps.setString(4, carInfo.getDt_emis());
						ps.setString(5, carInfo.getDt_emis_nasl());
						ps.setString(6, carInfo.getDt_ko());
						ps.setInt(7, carInfo.getKm_stk());
						ps.setInt(8, carInfo.getKm_emis());
						ps.setInt(9, carInfo.getKm_ko());
						ps.setTimestamp(
								10,
								carInfo.getDt_overenievozidla() != null ? carInfo
										.getDt_overenievozidla() : null);
						ps.setTimestamp(
								11,
								carInfo.getDt_getvinexpert() != null ? carInfo
										.getDt_getvinexpert() : null);
						ps.setLong(12, carInfo.getCarId());
					}

				});
		logger.debug("update CarInfo carId: " + ", number of updated: " + i);
	}

	public String getImagesDirectory() {
		return jdbcTemplate.queryForObject(GET_IMAGES_DIRECTORY_SQL,
				String.class, IMAGES_DIR_CONST);
	}

	@Override
	public void persistPhotoInfo(final List<String> photoList, final long carId) {
		for (final String photo : photoList) {
			int i = jdbcTemplate.update(INSERT_PHOTO_INFO_SQL,
					new PreparedStatementSetter() {
						public void setValues(PreparedStatement ps)
								throws SQLException {
							ps.setTimestamp(1, new Timestamp(
									ServiceCallTimestampHolder.getAsLong()));
							ps.setLong(2, carId);
							ps.setString(3, photo);
						}

					});
			logger.debug("carId: " + carId + ", photo: "
					+ photo + "number of inserted: " + i);
		}
	}
}
