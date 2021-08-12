package org.dw.hive.analyser.parse;

import java.util.List;

import org.dw.hive.analyser.entity.Entity;

public interface EntityManager {
	
	void addEntity(List<Entity> list, Entity entity);
	
	void removeEntity(List<Entity> list, Entity entity);
	
	void removeEntity(List<Entity> source, List<Entity> target);
	
}
