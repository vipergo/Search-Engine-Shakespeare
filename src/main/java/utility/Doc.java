package utility;

abstract class Doc {
	private String playId;
	private String sceneId;
	private int sceneNum;
	
	Doc(String pid, String sid, int docId){
		playId = pid;
		sceneId = sid;
		sceneNum = docId;
	}
	
	Doc(){
		
	}
	
	public String getPlayId() {
		return playId;
	}
	
	public String getSceneId() {
		return sceneId;
	}
	
	public int getDocId() {
		return sceneNum;
	}
	
	public void setPlayId(String pid) {
		playId = pid;
	}
	
	public void setSceneId(String sid) {
		sceneId = sid;
	}
	
	public void setSceneNum(int docId) {
		sceneNum = docId;
	}
}
