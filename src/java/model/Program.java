package model;

public class Program {
    private String programId;
    private String programCode;
    private String programName;
    private String description;
    private String status;

    public Program() {}

    public String getProgramId() { return programId; }
    public void setProgramId(String programId) { this.programId = programId; }
    public String getProgramCode() { return programCode; }
    public void setProgramCode(String programCode) { this.programCode = programCode; }
    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
