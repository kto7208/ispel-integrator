package ispel.integrator.service;

import ispel.integrator.adapter.AdapterRequest;
import ispel.integrator.adapter.Result;

public interface AdapterService {

	Result getVinExpert(AdapterRequest request);

	Result verifyCar(AdapterRequest request);

	Result submitInvoiceData(AdapterRequest request);

    Result submitMultipleInvoiceData(AdapterRequest request);

	Result importSZV(AdapterRequest request);
}
