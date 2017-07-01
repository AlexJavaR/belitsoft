package com.belitsoft;

public class Review {
    private String id;
    private String productId;
    private String userId;
    private String text;

    public Review(String id, String productId, String userId) {
        this.id = id;
        this.productId = productId;
        this.userId = userId;
    }

    public Review(String id, String text) {
        this.id = id;
        this.text = text;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getText() {
        return text;
    }

    public int getCount() {
        return 1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Review review = (Review) o;

        if (productId != null ? !productId.equals(review.productId) : review.productId != null) return false;
        return userId != null ? userId.equals(review.userId) : review.userId == null;
    }

    @Override
    public int hashCode() {
        int result = productId != null ? productId.hashCode() : 0;
        result = 31 * result + (userId != null ? userId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "id=" + id + ", productId=" + productId + ", userId=" + userId + ", text=" + text + "\n";
    }
}
