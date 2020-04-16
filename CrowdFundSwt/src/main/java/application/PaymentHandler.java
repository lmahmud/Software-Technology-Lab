package application;

import interfaces.IPayService;

/**
 * Dummy payservice
 */
public class PaymentHandler implements IPayService {

    @Override
    public void requestToChargeSupporter(String payinfo, double amount) {
        System.err.printf("Charged %s from %s%n.", amount, payinfo);
    }

    @Override
    public void requestToPayProjectStarter(String payinfo, double amount) {
        System.err.printf("Paid %s to %s%n.", amount, payinfo);
    }
}
