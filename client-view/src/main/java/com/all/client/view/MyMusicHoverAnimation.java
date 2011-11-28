package com.all.client.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.all.client.view.controllers.Animation;
import com.all.client.view.controllers.AnimationController;
import com.all.core.common.view.SynthFonts;
import com.all.i18n.Messages;

@Component
public class MyMusicHoverAnimation implements Animation {
	private final LocalContentTitlePanel contentTitlePanel;
	private String text;
	private String name;
	private AnimationController animationController;
	private final Messages messages;

	@Autowired
	public MyMusicHoverAnimation(LocalContentTitlePanel contentTitlePanel, Messages messages) {
		this.contentTitlePanel = contentTitlePanel;
		this.messages = messages;
	}

	@Override
	public long animate(int frame) {
		frame = frame % 4;
		long time = 175;
		switch (frame) {
		case 0:
			contentTitlePanel.setName("myMusicPanelGlow01");
			break;
		case 1:
		case 3:
			contentTitlePanel.setName("myMusicPanelGlow02");
			break;
		case 2:
			contentTitlePanel.setName("myMusicPanelGlow03");
			break;
		default:
			time = -1;
		}
		contentTitlePanel.repaint();
		return time;
	}

	@Override
	public String id() {
		return "MyMusicHoverAnimation";
	}

	@Override
	public void setup() {
		text = contentTitlePanel.getMyMusicLabel().getText();
		name = contentTitlePanel.getName();
		contentTitlePanel.getMyMusicLabel().setText(messages.getMessage("previewPanel.mymusic.glow"));
		// TODO: Change this using synth
//		contentTitlePanel.getMyMusicLabel().setForeground(new Color(120, 39, 139));
//		contentTitlePanel.getMyMusicLabel().setFont(new Font("Dialog", Font.BOLD, 13));
		contentTitlePanel.getMyMusicLabel().setName(SynthFonts.BOLD_FOND13_PURPLE120_39_139);
		
	}

	@Override
	public void teardown() {
		contentTitlePanel.getMyMusicLabel().setText(text);
		contentTitlePanel.setName(name);
	}

	@Autowired
	public void setAnimationController(AnimationController animationController) {
		this.animationController = animationController;
	}

	public void start() {
		animationController.animate(this);
	}

	public void stop() {
//		contentTitlePanel.getMyMusicLabel().setForeground(color);
//		contentTitlePanel.getMyMusicLabel().setFont(new Font("Dialog", Font.BOLD, 12));
		contentTitlePanel.getMyMusicLabel().setName("myMusicTitleLabel");
		animationController.stop(this);
	}
}
