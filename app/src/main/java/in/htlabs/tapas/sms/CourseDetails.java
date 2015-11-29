package in.htlabs.tapas.sms;

/**
 * Created by Tapas on 4/30/2015.
 */
public class CourseDetails {
    private String courseId;
    private String courseName;
    private String contactHours;

    public CourseDetails(){}

    public CourseDetails(String courseId,String courseName,String contactHours){
        this.courseId=courseId;
        this.courseName=courseName;
        this.contactHours=contactHours;
    }
    public void setCourseId(String courseId){
        this.courseId=courseId;
    }
    public String getCourseId(){
        return this.courseId;
    }
    public void setCourseName(String courseName){
        this.courseName=courseName;
    }
    public String getCourseName(){
        return this.courseName;
    }
    public void setContactHours(String contactHours){
        this.contactHours=contactHours;
    }
    public String getContactHours(){
        return this.contactHours;
    }
}
