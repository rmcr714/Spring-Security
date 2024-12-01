package com.springsecurity.repository;

import com.springsecurity.model.Notice;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface NoticeRepository extends CrudRepository<Notice, Long> {
	
	@Query(value = "from Notice where CURDATE() BETWEEN noticBegDt AND noticEndDt")
	List<Notice> findAllActiveNotices();

}