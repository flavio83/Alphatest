package tsauto;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Type;
import org.joda.time.DateTime;



@Entity
public class AlphaNews {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "id")
	private Long id = null;
	
	@Column(name = "categoryID", columnDefinition="NUMERIC")
	private int categoryID;
	
	@Type(type="org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@Column(name = "date", columnDefinition="TIMESTAMP")
	private DateTime date;
	
	@Column(name = "value", columnDefinition="NUMERIC")
	private double value;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "entryInLong",columnDefinition = "SMALLINT")
	private boolean entryInLong;
	
	@Column(name = "currency", columnDefinition="TEXT")
	private String currency;
	
	@Column(name = "description", columnDefinition="TEXT", nullable=true)
	private String description = null;
	
	public String toString() {
		return String.valueOf(categoryID);
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public int getCategoryID() {
		return categoryID;
	}

	public void setCategoryID(int categoryID) {
		this.categoryID = categoryID;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public boolean isEntryInLong() {
		return entryInLong;
	}

	public void setEntryInLong(boolean entryInLong) {
		this.entryInLong = entryInLong;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}



}
