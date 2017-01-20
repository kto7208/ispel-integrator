package ispel.integrator.dao;

import java.util.List;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;
import ispel.integrator.domain.CarInfo;

public interface Dao {

	void logResult(Result result);

	CarInfo getCarInfoById(String id);

	void persistCarInfo(CarInfo carInfo, AdapterRequest.MethodName methodName);

	String getImagesDirectory();

	void persistPhotoInfo(List<String> photoList, long carId);
}
