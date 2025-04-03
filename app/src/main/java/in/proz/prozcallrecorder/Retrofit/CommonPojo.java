package in.proz.prozcallrecorder.Retrofit;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class CommonPojo implements Serializable {
    String status ,message,error,bearer_token,token_type;

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getError() {
        return error;
    }

    public String getBearer_token() {
        return bearer_token;
    }

    public String getToken_type() {
        return token_type;
    }
}
