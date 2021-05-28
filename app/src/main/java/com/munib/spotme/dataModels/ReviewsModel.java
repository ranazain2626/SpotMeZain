package com.munib.spotme.dataModels;

public class ReviewsModel {
    String review,review_type,review_by;
    double rating;

    public ReviewsModel(){}

    public ReviewsModel(double rating,String review,String review_type,String review_by){
        this.rating=rating;
        this.review=review;
        this.review_type=review_type;
        this.review_by=review_by;
    }

    public double getRating() {
        return rating;
    }

    public String getReview() {
        return review;
    }

    public String getReview_by() {
        return review_by;
    }

    public String getReview_type() {
        return review_type;
    }
}
