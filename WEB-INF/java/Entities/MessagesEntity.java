package Entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MessagesEntity implements java.io.Serializable {
	
	private Integer id;
	private int authorId;
	private String text;
	private Date publishDate;
	private Integer originalId;
	private Integer previousId;
	
	private UsersEntity user;
	
	/*Forward info*/
	private String forwarderName;
	private String forwarderAlias;

	private ArrayList<String> hashtags;

	public MessagesEntity() {
		//this.extractHashtags();
        this.originalId = 0;
        this.previousId = 0;
	}

	public MessagesEntity(int authorId, String text, Date publishDate) {
		//this.extractHashtags();
		this.authorId = authorId;
		this.text = text;
		this.publishDate = publishDate;
		this.originalId = 0;
        this.previousId = 0;
	}

	public MessagesEntity(int authorId, String text, Date publishDate,
			Integer originalId, Integer previousId) {
		//this.extractHashtags();
		this.authorId = authorId;
		this.text = text;
		this.publishDate = publishDate;
		this.originalId = originalId;
		this.previousId = previousId;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public int getAuthorId() {
		return this.authorId;
	}

	public void setAuthorId(int authorId) {
		this.authorId = authorId;
	}

	public String getText() {
		return this.text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Date getPublishDate() {
		return this.publishDate;
	}

	public void setPublishDate(Date publishDate) {
		this.publishDate = publishDate;
	}

	public Integer getOriginalId() {
		return this.originalId;
	}

	public void setOriginalId(Integer originalId) {
		this.originalId = originalId;
	}

	public Integer getPreviousId() {
		return this.previousId;
	}

	public void setPreviousId(Integer previousId) {
		this.previousId = previousId;
	}
	
	public void extractHashtags() {

		this.hashtags = new ArrayList<String>();
		String regex = "#\\p{Alnum}(-|\\p{Alnum})+";
		Pattern pattern = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);
		Matcher matcher = pattern.matcher(this.text);
		while (matcher.find()) 
		{
			String ht = matcher.group().substring(1, matcher.group().length());
			if(!this.hashtags.contains(ht))
			{
				this.hashtags.add(ht);
			}				
			//System.out.println("[Hashtag found]: " + matcher.group());
		}
		
	}
	
	public String getTextHtml(String appRoot) {

		this.extractHashtags();

		String result = this.text;

		if (this.hashtags != null)
		{
			for(String hashtag : this.hashtags)
			{
				String html = "<a href=/" + appRoot + "/hashtag/" + hashtag + ">#" + hashtag + "</a>";
				result = result.replace("#" + hashtag, html);
			}
		}

		return result;
	}	

	public String toString()
	{
		String result = "===[MESSAGE]===\n";
		result += "ID: " + this.id + "\n";
		result += "TEXT: " + this.text + "\n";
		result += "DATE: " + this.publishDate + "\n";
		result += "FWD_NAME: " + this.forwarderName + "\n";
		result += "FWD_ALIAS: " + this.forwarderAlias + "\n";

		if (hashtags != null)
		{
			for(String hashtag : this.hashtags)
			{
				result += "HASHTAG: " + hashtag + "\n";
			}
		}
		result += "===[END OF MESSAGE]===\n";
		return result;
	}

	public UsersEntity getUser() {
		return user;
	}

	public void setUser(UsersEntity user) {
        if (user != null)
            this.setAuthorId(user.getId());
		this.user = user;
	}

	public String getForwarderName() {
		return forwarderName;
	}

	public void setForwarderName(String forwarderName) {
		this.forwarderName = forwarderName;
	}

	public String getForwarderAlias() {
		return forwarderAlias;
	}

	public void setForwarderAlias(String forwarderAlias) {
		this.forwarderAlias = forwarderAlias;
	}
    
    public ArrayList <String> getHashtags() {
        return hashtags;
    }

}
