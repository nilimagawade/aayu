package com.ebixcash.aayu.repositories;

import org.springframework.data.jpa.repository.JpaRepository;


import com.ebixcash.aayu.model.RequestResponseLog;

public interface APIServiceLogsRepository extends JpaRepository<RequestResponseLog, Integer>{

}
