package interfaces;

/**
 * Interface for the pay service
 */
public interface IPayService {
    void requestToChargeSupporter(String payinfo, double amount);

    void requestToPayProjectStarter(String payinfo, double amount);
}
