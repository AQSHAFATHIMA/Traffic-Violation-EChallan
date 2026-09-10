package com.example.traffic;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TrafficViolationServiceTest {

    private Vehicle createVehicle(
            String number) {

        return new Vehicle(
                number,
                "Test Owner",
                "9876543210",
                VehicleType.CAR);
    }

    private Violation createViolation(
            String id,
            String vehicleNumber,
            ViolationType type,
            ViolationSeverity severity) {

        double speed = 0;
        double limit = 0;

        if (type == ViolationType.OVER_SPEEDING) {
            speed = 80;
            limit = 60;
        }

        return new Violation(
                id,
                vehicleNumber,
                type,
                severity,
                speed,
                limit,
                "Chennai");
    }

    @Test
    public void testVehicleRegistration()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        assertEquals(
                1,
                service.getVehicleCount());
    }

    @Test
    public void testMultipleVehicleRegistration()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        service.registerVehicle(
                createVehicle("TN02CD5678"));

        service.registerVehicle(
                createVehicle("TN03EF9012"));

        assertEquals(
                3,
                service.getVehicleCount());
    }

    @Test
    public void testDuplicateVehicleRejected()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        assertThrows(
                TrafficViolationException.class,
                () -> service.registerVehicle(
                        createVehicle("TN01AB1234")));
    }

    @Test
    public void testOverSpeedingViolation()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        ViolationSeverity.HIGH);

        service.recordViolation(violation);

        assertEquals(
                1,
                service.getViolationCount());
    }

    @Test
    public void testFineBasedOnSeverity()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        ViolationSeverity.HIGH);

        service.recordViolation(violation);

        double fine =
                service.calculateFine(violation);

        assertEquals(
                2000.0,
                fine,
                0.001);
    }

    @Test
    public void testRepeatedViolationPenalty()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation first =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.MEDIUM);

        service.recordViolation(first);

        double firstFine =
                service.calculateFine(first);

        Violation second =
                createViolation(
                        "V002",
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.MEDIUM);

        service.recordViolation(second);

        double secondFine =
                service.calculateFine(second);

        assertTrue(
                secondFine > firstFine);
    }

    @Test
    public void testChallanGeneration()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW);

        service.recordViolation(violation);

        Challan challan =
                service.generateChallan("V001");

        assertNotNull(challan);

        assertEquals(
                PaymentStatus.UNPAID,
                challan.getPaymentStatus());
    }

    @Test
    public void testDuplicateChallanRejected()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW);

        service.recordViolation(violation);

        service.generateChallan("V001");

        assertThrows(
                TrafficViolationException.class,
                () -> service.generateChallan(
                        "V001"));
    }

    @Test
    public void testChallanPayment()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.LOW);

        service.recordViolation(violation);

        Challan challan =
                service.generateChallan("V001");

        service.payChallan(
                challan.getChallanId());

        assertEquals(
                PaymentStatus.PAID,
                challan.getPaymentStatus());
    }

    @Test
    public void testOutstandingFine()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW);

        service.recordViolation(violation);

        Challan challan =
                service.generateChallan("V001");

        double outstanding =
                service.calculateOutstandingFine(
                        "TN01AB1234");

        assertEquals(
                challan.getFineAmount(),
                outstanding,
                0.001);
    }

    @Test
    public void testOutstandingFineAfterPayment()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW);

        service.recordViolation(violation);

        Challan challan =
                service.generateChallan("V001");

        service.payChallan(
                challan.getChallanId());

        assertEquals(
                0.0,
                service.calculateOutstandingFine(
                        "TN01AB1234"),
                0.001);
    }

    @Test
    public void testVehicleClassification()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        assertEquals(
                "GOOD",
                service.classifyVehicle(
                        "TN01AB1234"));

        service.recordViolation(
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW));

        assertEquals(
                "WARNING",
                service.classifyVehicle(
                        "TN01AB1234"));
    }

    @Test
    public void testInvalidVehicleNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(
                        "",
                        "Arun",
                        "9876543210",
                        VehicleType.CAR));
    }

    @Test
    public void testInvalidPhoneNumber() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Vehicle(
                        "TN01AB1234",
                        "Arun",
                        "12345",
                        VehicleType.CAR));
    }

    @Test
    public void testInvalidOverSpeedingBoundary()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                new Violation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        ViolationSeverity.HIGH,
                        60,
                        60,
                        "Chennai");

        assertThrows(
                TrafficViolationException.class,
                () -> service.recordViolation(
                        violation));
    }

    @Test
    public void testNegativeSpeedRejected() {

        assertThrows(
                IllegalArgumentException.class,
                () -> new Violation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.OVER_SPEEDING,
                        ViolationSeverity.HIGH,
                        -10,
                        60,
                        "Chennai"));
    }

    @Test
    public void testUnknownVehicleRejected()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        Violation violation =
                createViolation(
                        "V001",
                        "TN99ZZ9999",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW);

        assertThrows(
                TrafficViolationException.class,
                () -> service.recordViolation(
                        violation));
    }

    @Test
    public void testViolationHistory()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        service.recordViolation(
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.ILLEGAL_PARKING,
                        ViolationSeverity.LOW));

        service.recordViolation(
                createViolation(
                        "V002",
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.MEDIUM));

        List<Violation> history =
                service.getViolationHistory(
                        "TN01AB1234");

        assertEquals(
                2,
                history.size());
    }

    @Test
    public void testMultipleFailureScenarios()
            throws Exception {

        TrafficViolationService service =
                new TrafficViolationService();

        service.registerVehicle(
                createVehicle("TN01AB1234"));

        Violation violation =
                createViolation(
                        "V001",
                        "TN01AB1234",
                        ViolationType.SIGNAL_VIOLATION,
                        ViolationSeverity.HIGH);

        service.recordViolation(violation);

        Challan challan =
                service.generateChallan("V001");

        service.payChallan(
                challan.getChallanId());

        assertThrows(
                TrafficViolationException.class,
                () -> service.payChallan(
                        challan.getChallanId()));

        assertThrows(
                TrafficViolationException.class,
                () -> service.generateChallan(
                        "V001"));
    }
}
