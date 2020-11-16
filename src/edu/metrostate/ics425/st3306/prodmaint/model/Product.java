package edu.metrostate.ics425.st3306.prodmaint.model;

import java.io.Serializable;
import java.time.LocalDate;

public interface Product extends Serializable {
	final static int FUTURE_RELEASE = -1;
	final static int UNSET_RELEASE = -2;

	String getProductCode();

	String getDescription();

	Double getPrice();

	LocalDate getReleaseDate();

	int getYearsReleased();
}