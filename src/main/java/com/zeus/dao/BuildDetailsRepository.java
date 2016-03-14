package com.zeus.dao;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import com.zeus.model.BuildDetails;

public interface BuildDetailsRepository extends CrudRepository<BuildDetails, Integer>{
   
	public List<BuildDetails> findByName(String name);
	public List<BuildDetails> findByNameAndPath(String name, String path);
}
