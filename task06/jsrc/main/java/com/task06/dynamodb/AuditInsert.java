package com.task06.dynamodb;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

public class AuditInsert {
	 private String id;
	    private String itemKey;
	    private LocalDateTime modificationTime;
	    private Object newValue;

	    @DynamoDBHashKey
	    public String getId() {
	        return id;
	    }

	    public void setId(String id) {
	        this.id = id;
	    }

	    @DynamoDBAttribute
	    public String getItemKey() {
	        return itemKey;
	    }

	    public void setItemKey(String itemKey) {
	        this.itemKey = itemKey;
	    }

	    @DynamoDBAttribute
	    public LocalDateTime getModificationTime() {
	        return modificationTime;
	    }

	    public void setModificationTime(LocalDateTime modificationTime) {
	        this.modificationTime = modificationTime;
	    }



	    @DynamoDBAttribute
	    public Object getNewValue() {
	        return newValue;
	    }

	    public void setNewValue(Object newValue) {
	        this.newValue = newValue;
	    }

	    @Override
	    public String toString() {
	        return "AuditInsert{" +
	                "id='" + id + '\'' +
	                ", itemKey='" + itemKey + '\'' +
	                ", modificationTime=" + modificationTime +
	                ", newValue=" + newValue +
	                '}';
	    }
	    public Map<String, AttributeValue> toMap() {
	        Map<String, AttributeValue> map = new HashMap<>();
	        map.put("id", new AttributeValue().withS(id));
	        AttributeValue itemKey = new AttributeValue().withS(this.itemKey);
	        map.put("itemKey", itemKey);
	        map.put("modificationTime", new AttributeValue().withS(modificationTime.toString()));
	        map.put("newValue", new AttributeValue().withM(Map.of("key", itemKey, "value", new AttributeValue().withN(String.valueOf(newValue)))));
	        return map;
	    }

}
