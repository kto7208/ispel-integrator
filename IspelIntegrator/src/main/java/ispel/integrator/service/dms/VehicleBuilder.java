package ispel.integrator.service.dms;

import generated.*;
import ispel.integrator.domain.dms.CustomerInfo;
import ispel.integrator.domain.dms.OrderInfo;
import ispel.integrator.domain.dms.VehicleInfo;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

@Component
public class VehicleBuilder {

    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override
        protected DateFormat initialValue() {
            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            return df;
        }
    };

    public Builder newInstance() {
        return new Builder();
    }

    public class Builder {

        private ServiceInvoiceLine[] serviceInvoiceLines;
        private PartsInvoiceLine[] partsInvoiceLines;
        private OtherInvoiceLine[] otherInvoiceLines;

        private VehicleInfo vehicleInfo;
        private OrderInfo orderInfo;
        private CustomerInfo customerInfo;

        private Builder() {
        }

        public Builder withServiceInvoiceLines(ServiceInvoiceLine[] serviceInvoiceLines) {
            this.serviceInvoiceLines = serviceInvoiceLines;
            return this;
        }

        public Builder withOtherInvoiceLines(OtherInvoiceLine[] otherInvoiceLines) {
            this.otherInvoiceLines = otherInvoiceLines;
            return this;
        }

        public Builder withPartsInvoiceLines(PartsInvoiceLine[] partsInvoiceLines) {
            this.partsInvoiceLines = partsInvoiceLines;
            return this;
        }

        public Builder withVehicleInfo(VehicleInfo vehicleInfo) {
            this.vehicleInfo = vehicleInfo;
            return this;
        }

        public Builder withOrderInfo(OrderInfo orderInfo) {
            this.orderInfo = orderInfo;
            return this;
        }

        public Builder withCustomerInfo(CustomerInfo customerInfo) {
            this.customerInfo = customerInfo;
            return this;
        }

        public Vehicle build() {
            Vehicle vehicle = new Vehicle();
            if (vehicleInfo != null) {
                vehicle.setRegistration(this.vehicleInfo.getSpz());
                vehicle.setVIN(this.vehicleInfo.getVin());
                vehicle.setManufacturer(this.vehicleInfo.getVyrobce());
                vehicle.setModel(this.vehicleInfo.getModel());
                Odometer odometer = new Odometer();
                odometer.setKilometres(buildKilometers());
                vehicle.setOdometer(odometer);
                vehicle.setRegDate(buildRegDate());
                vehicle.setTechTestDueDate(buildTechTestDueDate());
                vehicle.setNationalEmissionDueDate(buildEmissionDueDate());

                VehicleUser vehicleUser = new VehicleUser();
                if (customerInfo != null) {
                    vehicleUser.setTitle(customerInfo.getTitul());
                    vehicleUser.setFirstName(customerInfo.getJmeno());
                    vehicleUser.setLastName(customerInfo.getPrijmeni());
                    vehicleUser.setName(buildName());
                    vehicleUser.setAddress(buildCustomerAddress());
                    vehicleUser.setPostcode(customerInfo.getPsc());
                    vehicleUser.setPrivatePhone(buildPrivatePhone());
                    vehicleUser.setBusinessPhone(buildBusinessPhone());
                    vehicleUser.setMobilePhone(buildMobilePhone());
                    vehicleUser.setEmail(customerInfo.getEmail());
                    vehicleUser.setDateOfBirth(buildDateOfBirth());
                } else {
                    vehicleUser.setIdentityNotKnown("");
                }
                vehicle.setVehicleUser(vehicleUser);
                VehicleOwner vehicleOwner = new VehicleOwner();
                vehicleOwner.setSeeVehicleUser(new SeeVehicleUserType());
                vehicle.setVehicleOwner(vehicleOwner);
            } else {
                vehicle.setIdentityNotKnown("");
            }

            vehicle.getJobReference().add(buildJobReference());
            vehicle.getPartsInvoiceLineOrServiceInvoiceLineOrOtherInvoiceLine().addAll(Arrays.asList(serviceInvoiceLines));
            vehicle.getPartsInvoiceLineOrServiceInvoiceLineOrOtherInvoiceLine().addAll(Arrays.asList(partsInvoiceLines));
            vehicle.getPartsInvoiceLineOrServiceInvoiceLineOrOtherInvoiceLine().addAll(Arrays.asList(otherInvoiceLines));
            return vehicle;
        }

        private BigInteger buildKilometers() {
            BigInteger kilometers = new BigInteger(this.orderInfo.getStav_tach());
            if (kilometers.compareTo(BigInteger.ZERO) <= 0) {
                return BigInteger.ONE;
            } else {
                return kilometers;
            }
        }

        private Date buildRegDate() {
            try {
                if (vehicleInfo.getDt_prod() != null &&
                        vehicleInfo.getDt_prod().length() > 0 &&
                        !"00000000".equals(vehicleInfo.getDt_prod())) {
                    return dateFormat.get().parse(vehicleInfo.getDt_prod());
                }
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Date buildTechTestDueDate() {
            try {
                if (vehicleInfo.getDt_stk_nasl() != null &&
                        vehicleInfo.getDt_stk_nasl().length() > 0 &&
                        !"00000000".equals(vehicleInfo.getDt_stk_nasl())) {
                    return dateFormat.get().parse(vehicleInfo.getDt_stk_nasl());
                }
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Date buildEmissionDueDate() {
            try {
                if (vehicleInfo.getDt_emis_nasl() != null &&
                        vehicleInfo.getDt_emis_nasl().length() > 0 &&
                        !"00000000".equals(vehicleInfo.getDt_emis_nasl())) {
                    return dateFormat.get().parse(vehicleInfo.getDt_emis_nasl());
                }
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private Date buildDateOfBirth() {
            try {
                if (customerInfo.getDat_nar() != null && customerInfo.getDat_nar().length() > 0) {
                    return dateFormat.get().parse(customerInfo.getDat_nar());
                }
            } catch(ParseException e) {
                throw new RuntimeException(e);
            }
            return null;
        }

        private String buildName() {
            if (customerInfo.getOrganizace() != null && customerInfo.getOrganizace().length() > 0) {
                return customerInfo.getOrganizace();
            } else {
                return new StringBuilder()
                        .append(customerInfo.getPrijmeni())
                        .append(" ")
                        .append(customerInfo.getJmeno())
                        .toString();
            }
        }

        private Address buildCustomerAddress() {
            Address address = new Address();
            address.getAddressLine().add(this.customerInfo.getUlice());
            address.getAddressLine().add(this.customerInfo.getMesto());
            if (this.customerInfo.getStat1() != null && this.customerInfo.getStat1().length() > 0) {
                address.getAddressLine().add(this.customerInfo.getStat1());
            }
            return address;
        }

        private String buildJobReference() {
            return new StringBuilder()
                    .append(orderInfo.getDocumentNumber())
                    .append("-")
                    .append(orderInfo.getDocumentGroup())
                    .toString();
        }

        private String buildPrivatePhone() {
            if (customerInfo.getTel() != null && customerInfo.getTel().trim().length() > 0) {
                return customerInfo.getTel().trim();
            } else if (customerInfo.getSms() != null && customerInfo.getSms().trim().length() > 0) {
                return customerInfo.getSms().trim();
            } else if (vehicleInfo.getTel() != null && vehicleInfo.getTel().trim().length() > 0) {
                return vehicleInfo.getTel().trim();
            } else {
                return null;
            }
        }

        private String buildBusinessPhone() {
            if (vehicleInfo.getTel() != null && vehicleInfo.getTel().trim().length() > 0) {
                return vehicleInfo.getTel().trim();
            } else if (customerInfo.getTel() != null && customerInfo.getTel().trim().length() > 0) {
                return customerInfo.getTel().trim();
            } else if (customerInfo.getSms() != null && customerInfo.getSms().trim().length() > 0) {
                return customerInfo.getSms().trim();
            } else {
                return null;
            }
        }

        private String buildMobilePhone() {
            if (customerInfo.getSms() != null && customerInfo.getSms().trim().length() > 0) {
                return customerInfo.getSms().trim();
            } else if (customerInfo.getTel() != null && customerInfo.getTel().trim().length() > 0) {
                return customerInfo.getTel().trim();
            } else if (vehicleInfo.getTel() != null && vehicleInfo.getTel().trim().length() > 0) {
                return vehicleInfo.getTel().trim();
            } else {
                return null;
            }
        }
    }
}
