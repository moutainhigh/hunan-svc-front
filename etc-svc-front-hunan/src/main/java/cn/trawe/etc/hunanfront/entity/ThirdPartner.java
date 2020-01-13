package cn.trawe.etc.hunanfront.entity;


import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Data;

/**
 * @author Jiang Guangxing
 */
@Data
@Table(name="third_partner")
public class ThirdPartner {
	
	@Id
    private long id;
    private String appId ;
    private String name;
    private String accountNo;
    private String password;
    private String key;
    private String secret;
    private String publicKey;
    private String trawePrivateKey;
    private String trawePublicKey;
    private String notifyUrl;
	public ThirdPartner(String trawePrivateKey) {
		super();
		this.trawePrivateKey = trawePrivateKey;
	}
	public ThirdPartner() {
		super();
	}
    
}
