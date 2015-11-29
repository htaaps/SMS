package in.htlabs.tapas.sms;

/**
 * Created by Tapas on 4/30/2015.
 */
public class SectionDetails {
    private String sectionId;
    private String sectionName;

    public SectionDetails(){}

    public SectionDetails(String sectionId,String sectionName){
        this.sectionId=sectionId;
        this.sectionName=sectionName;
    }
    public void setSectionId(String sectionId){
        this.sectionId=sectionId;
    }
    public String getSectionId(){
        return this.sectionId;
    }
    public void setSectionName(String sectionName){
        this.sectionName=sectionName;
    }
    public String getSectionName(){
        return this.sectionName;
    }
}
