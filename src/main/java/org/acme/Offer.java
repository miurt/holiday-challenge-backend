package org.acme;

import lombok.Data;

@Data
public class Offer {
    // 453,"2022-07-04T18:25:00+02:00","2022-07-08T07:30:00+02:00","2","0","1023","PMI","LEJ","EW",
    // "2022-07-08T10:00:00+02:00","LEJ","PMI","EW","2022-07-04T20:55:00+02:00","none","false","program"
    private int hotelid; // 0
    private String departuredate; // 1
    private String returndate; // 2
    // private int countadults; //3
    // private int countchildren; //4
    private int price; // 5
    // private String inbounddepartureairport; //6
    // private String inboundarrivalairport; // 7
    private String inboundairline; // 8
    private String inboundarrivaldatetime; // 9
    // private String outbounddepartureairport; //10
    // private String outboundarrivalairport; //11
    private String outboundairline; // 12
    private String outboundarrivaldatetime; // 13
    private String mealtype; // 14
    private boolean oceanview; // 15
    private String roomtype; // 16

    public Offer(int hotelid, String departuredate, String returndate, int price,
            String inboundairline, String inboundarrivaldatetime, String outboundairline, 
            String outboundarrivaldatetime, String mealtype, boolean oceanview, String roomtype) {
        this.hotelid = hotelid;
        this.departuredate = departuredate;
        this.returndate = returndate;
        // this.countadults = countadults;
        // this.countchildren = countchildren;
        this.price = price;
        // this.inbounddepartureairport = inbounddepartureairport;
        // this.inboundarrivalairport = inboundarrivalairport;
        this.inboundairline = inboundairline;
        this.inboundarrivaldatetime = inboundarrivaldatetime;
        // this.outbounddepartureairport = outbounddepartureairport;
        // this.outboundarrivalairport = outboundarrivalairport;
        this.outboundairline = outboundairline;
        this.outboundarrivaldatetime = outboundarrivaldatetime;
        this.mealtype = mealtype;
        this.oceanview = oceanview;
        this.roomtype = roomtype;
    }

}
