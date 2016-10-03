package moe.lukas.shiro.voice

import groovy.transform.CompileStatic
import org.json.JSONObject

@CompileStatic
class AudioInfo {
    public JSONObject jsonInfo
    public String title
    public String origin
    public String id
    public String encoding
    public String description
    public String extractor
    public String thumbnail
    public String error
    public boolean isLive
    public AudioTimestamp duration
}