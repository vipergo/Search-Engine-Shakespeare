package utility;

public class DocStat extends Doc{
	int length;
	
	public DocStat(FullDoc fd) {
		super(fd.getPlayId(), fd.getSceneId(), fd.getDocId());
		length = fd.getText().length();
	}
	
	public DocStat(String pid, String sid, int docid, int len) {
		super(pid, sid, docid);
		length = len;
	}
	
	public int getLength() {
		return length;
	}
}
