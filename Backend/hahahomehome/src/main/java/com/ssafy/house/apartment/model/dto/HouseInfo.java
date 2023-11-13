package com.ssafy.house.apartment.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class HouseInfo {
	long aptCode;
	String apartmentName;
	String roadName;
	String jibun;
	String area;
	String floor;
	String avgPrice;
	String lat;
	String lng;
}
