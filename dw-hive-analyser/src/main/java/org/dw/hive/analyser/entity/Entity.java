package org.dw.hive.analyser.entity;

public class Entity {
	private EntityType entityType;
	private String database;
	private String name;
	
	public Entity(String database, String name) {
		this(EntityType.TABLE, database, name);
	}
	
	public Entity(EntityType entityType,String database, String name) {
		this.entityType = entityType;
		this.database = database;
		this.name = name;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj) { return true;}
		if(obj instanceof Entity) {
			Entity entity = (Entity) obj;
			return (entityType!=entity.getEntityType() || !database.equals(entity.getDatabase()) || !name.equals(entity.getName())) ? false : true;
		}
		return false;
	}
	
	public EntityType getEntityType() {
		return entityType;
	}
	public String getDatabase() {
		return database;
	}
	public String getName() {
		return name;
	}
}
