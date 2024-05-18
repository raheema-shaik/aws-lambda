package com.task11.pojo;

//public class Tables {
//
//}
//
//
//
//
//package com.task10.pojo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperFieldModel;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTyped;
 
import lombok.Data;
 
@Data

@DynamoDBTable(tableName = "cmtr-55717e2b-Tables-test")

public class Tables {

    @DynamoDBHashKey

    private int id;

    private int number;

    private int places;

    @DynamoDBTyped(DynamoDBMapperFieldModel.DynamoDBAttributeType.BOOL)

    private boolean isVip;

    private int minOrder;

}