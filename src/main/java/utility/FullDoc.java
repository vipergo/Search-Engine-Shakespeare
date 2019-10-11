package utility;

public class FullDoc extends Doc{
	private String text;
	
	public FullDoc(String p, String s, int di, String t){
		super(p, s, di);
		text = t;
	}
	
	public FullDoc(){
		
	}
	
	public String getText() {
		return text;
	}
	
	public void setText(String t) {
		text = t;
	}
}
