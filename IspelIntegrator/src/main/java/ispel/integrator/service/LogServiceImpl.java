package ispel.integrator.service;

import ispel.integrator.adapter.Result;
import ispel.integrator.dao.Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LogServiceImpl implements LogService {

	@Autowired
	Dao dao;

	public void logResult(Result result) {
		dao.logResult(result);
	}

}
