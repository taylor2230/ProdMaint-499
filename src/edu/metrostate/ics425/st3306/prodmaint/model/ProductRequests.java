package edu.metrostate.ics425.st3306.prodmaint.model;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;

/**
 * this class implements the product interface
 * the class controls the ability to create a new product and set or get the details
 */
public class ProductRequests implements Serializable, Product {

    /**
     *
     */
    @Serial
    private static final long serialVersionUID = 2L;

    private String productCode;
    private String description;
    private Double price;
    private LocalDate releaseDate;
    private int yearsReleased;

    public ProductRequests() {
        super();
    }

    /**
     * @return returns the productCode hashcode that is set
     */
    @Override
    public int hashCode() {
        return productCode.hashCode(); //get the hashcode of the productCode
    }

    /**
     * @param product the object of product to compare to
     * @return returns the bool if the two products are equal or not
     */
    public boolean equals(Object product) {
        //return the equivalence of two objects product code
        //product codes assumed to be case insensitive
        ProductRequests a = (ProductRequests) product;
        return productCode.equalsIgnoreCase(a.getProductCode());
    }

    /**
     * @return returns the products information as a string listed line by line
     */
    @Override
    public String toString() {
        //returns the product information in string form
        return "Product Code: " + this.getProductCode()
                + "\nProduct Description: " + this.getDescription()
                + "\nProduct Price: " + this.getPrice()
                + "\nProduct Release Date: " + this.getReleaseDate()
                + "\nYears Released: " + this.getYearsReleased();
    }

    /**
     * @param code a String which is unique to the product
     *             sets the products code
     */
    public void setProductCode(String code) {
        productCode = code; //set the code of the product
    }

    /**
     * @param descrip a String which is the items description
     *                sets the description of the item
     */
    public void setDescription(String descrip) {
        description = descrip; //set the description of the product
    }

    /**
     * @param number a Double which is the price
     *               set the price of the item
     */
    public void setPrice(Double number) {
        price = number; //set the price based on the number passed
    }

    /**
     * @param date field to process the number of years released.
     *             This method will return the value based on if the value was set or not and predefine null values as -1
     *             Non-null but future dates are set as one
     *             date param that is not null and is not in the future is set at this year - release year
     */
    public void setReleaseDate(LocalDate date) {
        releaseDate = date;

        if (releaseDate == null) {
            yearsReleased = UNSET_RELEASE;
        } else if (releaseDate.isAfter(LocalDate.now())) {
            yearsReleased = FUTURE_RELEASE;
        } else yearsReleased = LocalDate.now().getYear() - releaseDate.getYear();
    }

    /**
     * @return returns the product code that is set
     */
    @Override
    public String getProductCode() {
        return productCode; //fetch the product code
    }

    /**
     * @return returns the product description that is set
     */
    @Override
    public String getDescription() {
        return description; //fetch the description
    }

    /**
     * @return returns the price of the product that is set
     */
    @Override
    public Double getPrice() {
        return price; //fetch the price
    }

    /**
     * @return returns the release date of that product that is set
     */
    @Override
    public LocalDate getReleaseDate() {
        return releaseDate; //fetch the release date
    }

    /**
     * @return returns the calculation done when the release date is set
     */
    @Override
    public int getYearsReleased() {
        return yearsReleased; //fetch the years released
    }
}
