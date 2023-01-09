package ua.zakharov.aws.model;

import lombok.Data;

@Data
public class MessageBody {
    private String merchantId;
    private String merchantName;
    private Integer rate;
    private String feedback;

    @Override
    public String toString() {
        return "merchantId = " + this.merchantId + "\n" +
        "merchantName = " + this.merchantName + "\n" + "rate = " + this.rate + "\n" +
                "feedback = " + this.feedback;
    }
}
