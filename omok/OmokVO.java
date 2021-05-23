package omok;

public class OmokVO {
	private String id;
	private int total;
	private int win;
	private int rate;
	private int rank;
	
	public OmokVO(){ }
	
	public OmokVO(String id){
		this.id = id;
	}
	
	public OmokVO(String id, int total, int win, int rate){
		this.id = id;
		this.total = total;
		this.win = win;
		this.rate = rate;
	}
	
	public OmokVO(String id, int total, int win, int rate, int rank) {
		super();
		this.id = id;
		this.total = total;
		this.win = win;
		this.rate = rate;
		this.rank = rank;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getRate() {
		return rate;
	}

	public void setRate(int rate) {
		this.rate = rate;
	}

	public int getRank() {
		return rank;
	}

	public void setRank(int rank) {
		this.rank = rank;
	}
	
	
	
}
