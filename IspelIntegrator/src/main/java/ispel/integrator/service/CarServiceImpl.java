package ispel.integrator.service;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.dao.Dao;
import ispel.integrator.domain.CarDetail;
import ispel.integrator.domain.CarInfo;
import org.apache.log4j.Logger;
import org.jdom2.Document;
import org.jdom2.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarServiceImpl implements CarService {

	private static final Logger log = Logger.getLogger(CarServiceImpl.class);

	@Autowired
	Dao dao;

	@Autowired
	DetailVINExpertBuilder detailWINExpert;

	@Autowired
	DetailOriginalityBuilder detailOriginality;

	@Autowired
	DetailHistoryBuilder detailHistory;

	@Autowired
	DetailTechnicalInspectionBuilder detailTechnicalInspection;

	@Autowired
	DetailEmissionControlBuilder detailEmissionControl;

	@Autowired
	DetailExecutionBuilder detailExecution;

	@Autowired
	DetailLeasingBuilder detailLeasing;

	@Autowired
	DetailPhotoBuilder detailPhoto;

	@Autowired
	DetailVINExpertBuilder detailVINExpert;

	@Autowired
	DetailSearchBuilder detailSearch;

	@Autowired
	DateKmHolder dateKmHolder;

	public CarInfo getCarInfoById(String id) {
		return dao.getCarInfoById(id);
	}

	public void processGetVinExpert(CarInfo carInfo, Document vinExpertResult) {
		dateKmHolder.reset();
		CarDetail detail = carInfo.getDetail(CarDetail.Type.VinInfo);
		StringBuilder sb = new StringBuilder();
		sb.append(ServiceCallTimestampHolder.getAsDateTime()).append("\r\n")
				.append("=================");
		detailVINExpert.build(sb, vinExpertResult.getRootElement());
		if (detail.getText() != null) {
			sb.append("\r\n\r\n").append(detail.getText());
		}
		detail.setText(sb.toString());
		// not nice - later refactor
		updateCarInfoDateKmData(carInfo);
		updateServiceCallDate(carInfo, AdapterRequest.MethodName.GetVinExpert);
		dao.persistCarInfo(carInfo, AdapterRequest.MethodName.GetVinExpert);
	}

	@Transactional
	public void processVerifyCar(CarInfo carInfo, Document verifyCarResult) {
		dateKmHolder.reset();
		Element root = verifyCarResult.getRootElement();
		for (CarDetail.Type type : CarDetail.Type.values()) {
			CarDetail detail = carInfo.getDetail(type);
			StringBuilder sb = new StringBuilder();
			sb.append(ServiceCallTimestampHolder.getAsDateTime())
					.append("\r\n").append("=================");
			if (CarDetail.Type.Originality == type) {
				detailOriginality.build(sb, root);
				List<String> photoList = detailPhoto.build(root, carInfo.getCarId());
				dao.persistPhotoInfo(photoList, carInfo.getCarId());
			} else if (CarDetail.Type.Execution == type) {
				detailExecution.build(sb, root);
			} else if (CarDetail.Type.Leasing == type) {
				detailLeasing.build(sb, root);
			} else if (CarDetail.Type.History == type) {
				detailHistory.build(sb, root);
			} else if (CarDetail.Type.EmissionControl == type) {
				detailEmissionControl.build(sb, root);
			} else if (CarDetail.Type.TechnicaInspection == type) {
				detailTechnicalInspection.build(sb, root);
			} else if (CarDetail.Type.Detection == type) {
				detailSearch.build(sb, root);
			} else if (CarDetail.Type.VinInfo == type) {
				continue;
			}
			if (CarDetail.Type.History != type && detail.getText() != null) {
				sb.append("\r\n\r\n").append(detail.getText());
			}
			detail.setText(sb.toString());
		}
		updateCarInfoDateKmData(carInfo);
		updateServiceCallDate(carInfo, AdapterRequest.MethodName.VerifyCar);
		dao.persistCarInfo(carInfo, AdapterRequest.MethodName.VerifyCar);
	}

	private void updateCarInfoDateKmData(CarInfo carInfo) {
		if (dateKmHolder.getDt_stk() != null) {
			carInfo.setDt_stk(dateKmHolder.getDt_stk());
		}
		if (dateKmHolder.getDt_stk_nasl() != null) {
			carInfo.setDt_stk_nasl(dateKmHolder.getDt_stk_nasl());
		}
		if (dateKmHolder.getDt_emis() != null) {
			carInfo.setDt_emis(dateKmHolder.getDt_emis());
		}
		if (dateKmHolder.getDt_emis_nasl() != null) {
			carInfo.setDt_emis_nasl(dateKmHolder.getDt_emis_nasl());
		}
		if (dateKmHolder.getDt_ko() != null) {
			carInfo.setDt_ko(dateKmHolder.getDt_ko());
		}
		if (dateKmHolder.getKm_stk() != 0) {
			carInfo.setKm_stk(dateKmHolder.getKm_stk());
		}
		if (dateKmHolder.getKm_emis() != 0) {
			carInfo.setKm_emis(dateKmHolder.getKm_emis());
		}
		if (dateKmHolder.getKm_ko() != 0) {
			carInfo.setKm_ko(dateKmHolder.getKm_ko());
		}
	}

	private void updateServiceCallDate(CarInfo carInfo,
			AdapterRequest.MethodName method) {
		if (AdapterRequest.MethodName.VerifyCar == method) {
			carInfo.setDt_overenievozidla(ServiceCallTimestampHolder
					.getAsTimestamp());
		} else if (AdapterRequest.MethodName.GetVinExpert == method) {
			carInfo.setDt_getvinexpert(ServiceCallTimestampHolder
					.getAsTimestamp());
		}
	}

}
