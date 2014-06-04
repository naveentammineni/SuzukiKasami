import java.io.Serializable;


public class Request implements Serializable {
	int id;
	int reqNo;
	
	public Request(){
		this.id = 0;
		this.reqNo = 0;
	}
	public Request(int id, int reqNo){
		this.id = id;
		this.reqNo = reqNo;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getReqNo() {
		return reqNo;
	}
	public void setReqNo(int reqNo) {
		this.reqNo = reqNo;
	}
	
}
