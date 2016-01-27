package com.net.flavix.dto;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;



@Entity
public class AlphaflashCalendar extends Persister<AlphaflashCalendar> {
	
	
	@Id
	@GeneratedValue
	@Column(name = "id")
	private long id;
	
	@Column
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime acquiredDate;
	
	@Column
	@Convert(converter = LocalDateTimePersistenceConverter.class)
	private LocalDateTime date;
	
	@Column(name = "category")
	private int category = -1;
	
	@Column(name = "type")
	private int type = -1;
	
	
	public AlphaflashCalendar() {
		
	}
	
	public AlphaflashCalendar(LocalDateTime date, int category, int type) {
		this.category = category;
		this.type = type;
		this.date = date;
		this.acquiredDate = LocalDateTime.now();
	}
	
	public static void persistWithRandomData() throws Exception {
		AlphaflashCalendar c1 = new AlphaflashCalendar(LocalDateTime.of(2015, 5, 17, 23, 30), 90, 1);
		AlphaflashCalendar c2 = new AlphaflashCalendar(LocalDateTime.of(2015, 5, 18, 13, 30), 90, 0);
		AlphaflashCalendar c3 = new AlphaflashCalendar(LocalDateTime.of(2015, 5, 20, 23, 30), 20003, 1);
		AlphaflashCalendar c4 = new AlphaflashCalendar(LocalDateTime.of(2015, 5, 22, 9, 30), 20003, 0);
		new AlphaflashCalendar().persist(c1,c2,c3,c4);
	}
	
}
