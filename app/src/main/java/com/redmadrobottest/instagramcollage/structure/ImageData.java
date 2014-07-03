package com.redmadrobottest.instagramcollage.structure;

public class ImageData {

    private String lowResolutionUri;
    private String standardResolutionUri;
    private boolean checked;
    private String path;

    public ImageData(String lowResolutionUri, String standardResolutionUri) {
        this.lowResolutionUri = lowResolutionUri;
        this.standardResolutionUri = standardResolutionUri;
        checked = false;
        path = null;
    }

    public String getLowResolutionUri() {
        return lowResolutionUri;
    }

    public String getStandardResolutionUri() {
        return standardResolutionUri;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean getChecked() {
        return checked;
    }

}