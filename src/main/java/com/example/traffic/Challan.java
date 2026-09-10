package com.example.traffic;

import java.time.LocalDateTime;

public class Challan {

    private final String challanId;
    private final String vehicleNumber;
    private final String violationId;
    private final ViolationType violationType;
    private final double fineAmount;
    private final LocalDateTime issuedAt;

    private PaymentStatus paymentStatus;

    public Challan(
            String challanId,
            String vehicleNumber,
            String violationId,
            ViolationType violationType,
            double fineAmount) {

        if (challanId == null
                || challanId.isBlank()) {
            throw new IllegalArgumentException(
                    "Challan ID cannot be empty");
        }

        if (fineAmount <= 0) {
            throw new IllegalArgumentException(
                    "Fine amount must be greater than zero");
        }

        this.challanId = challanId;
        this.vehicleNumber =
                vehicleNumber.toUpperCase();
        this.violationId = violationId;
        this.violationType = violationType;
        this.fineAmount = fineAmount;
        this.issuedAt = LocalDateTime.now();
        this.paymentStatus =
                PaymentStatus.UNPAID;
    }

    public String getChallanId() {
        return challanId;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public String getViolationId() {
        return violationId;
    }

    public ViolationType getViolationType() {
        return violationType;
    }

    public double getFineAmount() {
        return fineAmount;
    }

    public LocalDateTime getIssuedAt() {
        return issuedAt;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void markAsPaid() {
        paymentStatus = PaymentStatus.PAID;
    }

    public boolean isPaid() {
        return paymentStatus == PaymentStatus.PAID;
    }

    @Override
    public String toString() {

        return "Challan ID: "
                + challanId
                + " | Vehicle: "
                + vehicleNumber
                + " | Violation: "
                + violationType
                + " | Fine: ₹"
                + String.format(
                        "%.2f", fineAmount)
                + " | Payment: "
                + paymentStatus;
    }
}
