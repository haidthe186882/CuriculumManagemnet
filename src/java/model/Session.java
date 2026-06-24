package model;

public class Session {
    private String sessionId;
    private String syllabusId;
    private int sessionNo;
    private String topic;
    private String learningTeachingType;
    private String lo;
    private String itu;
    private String studentMaterials;
    private String studentTasks;
    private String urls;

    public Session() {}

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getSyllabusId() { return syllabusId; }
    public void setSyllabusId(String syllabusId) { this.syllabusId = syllabusId; }

    public int getSessionNo() { return sessionNo; }
    public void setSessionNo(int sessionNo) { this.sessionNo = sessionNo; }

    public String getTopic() { return topic; }
    public void setTopic(String topic) { this.topic = topic; }

    public String getLearningTeachingType() { return learningTeachingType; }
    public void setLearningTeachingType(String learningTeachingType) { this.learningTeachingType = learningTeachingType; }

    public String getLo() { return lo; }
    public void setLo(String lo) { this.lo = lo; }

    public String getItu() { return itu; }
    public void setItu(String itu) { this.itu = itu; }

    public String getStudentMaterials() { return studentMaterials; }
    public void setStudentMaterials(String studentMaterials) { this.studentMaterials = studentMaterials; }

    public String getStudentTasks() { return studentTasks; }
    public void setStudentTasks(String studentTasks) { this.studentTasks = studentTasks; }

    public String getUrls() { return urls; }
    public void setUrls(String urls) { this.urls = urls; }
}
