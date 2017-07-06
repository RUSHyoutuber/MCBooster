package br.com.mcbooster.models;

import java.util.concurrent.TimeUnit;

import com.gmail.nossr50.datatypes.skills.SkillType;

public class PlayerBooster {
	
	private String nick;
	private SkillType skillType;
	private long endTime;
	private boolean check;
	private long activedTime;
	private long rest;

	public PlayerBooster(String nick, SkillType skillType) {
		this.nick = nick;
		this.skillType = skillType;
		this.endTime = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60);
		this.activedTime = System.currentTimeMillis();
		this.check = true;
	}
	
	public PlayerBooster(String nick, String skillType, long endTime) {
		this.nick = nick;
		this.skillType = SkillType.getSkill(skillType);
		this.endTime = endTime;
	}
	
	public long getRest() {
		return rest;
	}
	
	public void setRest(long rest) {
		this.rest = rest;
	}

	public long getActivedTime() {
		return activedTime;
	}
	
	public String getNick() {
		return nick;
	}
	
	public SkillType getSkillType() {
		return skillType;
	}
	
	public long getEndTime() {
		return endTime;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}
	
}
