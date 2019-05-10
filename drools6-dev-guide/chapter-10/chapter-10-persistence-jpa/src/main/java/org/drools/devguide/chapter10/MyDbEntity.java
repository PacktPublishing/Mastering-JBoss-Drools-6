package org.drools.devguide.chapter10;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(name="myDbEntityIdSeq", sequenceName="MY_DB_ENTITY_ID_SEQ")
public class MyDbEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator="myDbEntityIdSeq")
	private Long id;
	
	private String data1;
	
	private String data2;
	
	public MyDbEntity() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getData1() {
		return data1;
	}

	public void setData1(String data1) {
		this.data1 = data1;
	}

	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data1 == null) ? 0 : data1.hashCode());
		result = prime * result + ((data2 == null) ? 0 : data2.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyDbEntity other = (MyDbEntity) obj;
		if (data1 == null) {
			if (other.data1 != null)
				return false;
		} else if (!data1.equals(other.data1))
			return false;
		if (data2 == null) {
			if (other.data2 != null)
				return false;
		} else if (!data2.equals(other.data2))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "MyDbEntity [id=" + id + ", data1=" + data1 + ", data2=" + data2 + "]";
	}
}
