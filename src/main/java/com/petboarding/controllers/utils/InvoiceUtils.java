package com.petboarding.controllers.utils;

import com.petboarding.models.*;
import com.petboarding.models.data.InvoiceDetailRepository;
import com.petboarding.models.data.InvoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InvoiceUtils {
    public static List<InvoiceDetail> getServicesFromStay(Stay stay, Invoice invoice) {
        List<InvoiceDetail> detailsList = new ArrayList<>();
        Long daysStay = DateUtils.getDaysDifference(stay.getCheckInTime(), stay.getCheckOutTime());
        InvoiceDetail stayDetail = new InvoiceDetail();
        stayDetail.setInvoice(invoice);
        stayDetail.setQuantity(daysStay.floatValue());
        stayDetail.setService(stay.getReservation().getService());
        stayDetail.setPricePerUnit(stay.getReservation().getService().getPricePerUnit());
        stayDetail.setDescription("Stay from " +
                DateUtils.format(stay.getCheckInTime(), "MM/dd/yyyy") +
                " to " +
                DateUtils.format(stay.getCheckOutTime(), "MM/dd/yyyy"));
        detailsList.add(stayDetail);
        for(StayService service: stay.getAdditionalServices()) {
            InvoiceDetail detail = new InvoiceDetail(service);
            detail.setInvoice(invoice);
            detailsList.add(detail);
        }
        return detailsList;
    }


    public static Double round(double value, int places) {
        if(places < 0) return null;
        BigDecimal number = new BigDecimal(Double.toString(value));
        number = number.setScale(places, RoundingMode.HALF_UP);
        return number.doubleValue();
    }
}
