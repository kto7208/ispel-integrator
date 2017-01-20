package ispel.integrator.service;

import org.jdom2.Document;

import ispel.integrator.domain.CarInfo;

public interface CarService {

	CarInfo getCarInfoById(String id);

	void processGetVinExpert(CarInfo carInfo, Document vinExpertResult);

	void processVerifyCar(CarInfo carInfo, Document verifyCarResult);
}
