package com.ntkn.calendar;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import javax.persistence.Entity;




public class Main {
	
	//yyyyMMdd'T'HH:mm:ssZ
	CalendarWebService service = new CalendarWebServiceProxy("http://www.alphaflash.com/calendarservice/soap?wsdl");

	public Main() {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ");
		//LocalDateTime start= LocalDateTime.now();
		ZonedDateTime start = ZonedDateTime.of(LocalDateTime.of(2015, 9, 16, 0, 0),ZoneId.of("UTC"));
		ZonedDateTime end = ZonedDateTime.of(LocalDateTime.of(2015, 9, 16, 23, 59),ZoneId.of("UTC"));
		try {
			CalendarEventExt[] events = service.getEvents(formatter.format(start), formatter.format(end), "RELEASE");
			if(events!=null) {
				for(CalendarEventExt event : events) {
					new AlphaflashCalendar(event);
					System.out.println(new Date(event.getDate().getDate().getTimeInMillis()).toString() + " " + event.getCategoryId() + " "+ event.getCountry() + " " + event.getTitle());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		new Main();
	}

	@Entity
	class AlphaflashCalendar {
		
		String addedBy;
		int categoryId;
		String country;
		Date date;
		String description;
		String title;
		String type;
		String uid;
	
		public AlphaflashCalendar(CalendarEventExt event) {
			addedBy = event.getAddedBy();
			categoryId = event.getCategoryId();
			country = event.getCountry();
			date = event.getDate().getDate().getTime();
			description = event.getDescription();
			title = event.getTitle();
			uid = event.getUid();
			type = event.getType().getValue();
		}
			
	}

}
