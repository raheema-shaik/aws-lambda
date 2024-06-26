package com.task11.pojo;

//public class Reservations {
//
//}
//
//
//
//
//package com.task10.pojo;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
 
import lombok.Data;
 
@Data

@DynamoDBTable(tableName = "cmtr-55717e2b-Reservations-test")

public class Reservations {

    @DynamoDBHashKey

    private String id;

    private int tableNumber;

    private String clientName;

    private String phoneNumber;

    private String date;

    private String slotTimeStart;

    private String slotTimeEnd;

}