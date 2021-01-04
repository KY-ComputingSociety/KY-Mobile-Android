package com.example.kymobile.models;

public class ModelEvent {
    String Cover,ShortDesc,Title,FullDesc,StartDate,EndDate,StartTime,EndTime,Venue;

    public ModelEvent(){
    }
    public ModelEvent(String Cover,String ShortDesc,String Title,String FullDesc,String StartDate,String EndDate, String StartTime,String EndTime,String Venue){
        this.FullDesc = FullDesc;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.StartTime = StartTime;
        this.EndTime = EndTime;
        this.Venue = Venue;
        this.Cover = Cover;
        this.ShortDesc = ShortDesc;
        this.Title = Title;
    }

    public String getCover() {
        return Cover;
    }

    public void setCover(String cover) {
        Cover = cover;
    }

    public String getShortDesc() {
        return ShortDesc;
    }

    public void setShortDesc(String shortDesc) {
        ShortDesc = shortDesc;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getFullDesc() {
        return FullDesc;
    }

    public void setFullDesc(String fullDesc) {
        FullDesc = fullDesc;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }

    public String getVenue() {
        return Venue;
    }

    public void setVenue(String venue) {
        Venue = venue;
    }
}
