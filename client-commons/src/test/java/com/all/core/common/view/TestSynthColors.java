package com.all.core.common.view;

import static org.junit.Assert.*;

import java.awt.Color;

import javax.swing.UIManager;

import org.junit.Test;


public class TestSynthColors {
	private static final Color GRAY909090 = UIManager.getDefaults().getColor("Color.gray909090");
	private static final Color PURPLE12040140 = UIManager.getDefaults().getColor("Color.purple12040140");
	private static final Color SKYBLUE11521505 = UIManager.getDefaults().getColor("Color.skyblue11521505");
	private static final Color SKYBLUE12508400 = UIManager.getDefaults().getColor("Color.skyblue12508400");
	private static final Color WHITEBLUE15462645 = UIManager.getDefaults().getColor("Color.whiteblue15462645");
	private static final Color WHITE255_255_255 = UIManager.getDefaults().getColor("Color.white255_255_255");
	private static final Color CLEAR_GRAY245_245_245 = UIManager.getDefaults().getColor("Color.clearGray245_245_245");
	private static final Color BLUE175_205_225 = UIManager.getDefaults().getColor("Color.blue175_205_225");
	private static final Color GRAY77_77_77 = UIManager.getDefaults().getColor("Color.gray77_77_77");
	private static final Color GRAY210_210_210 = UIManager.getDefaults().getColor("Color.gray210_210_210");
	private static final Color GRAY150_150_150 = UIManager.getDefaults().getColor("Color.grid150_150_150");
	private static final Color GRAY170_170_170 = UIManager.getDefaults().getColor("Color.gray170_170_170");
	private static final Color BLUE_201_202_230 = UIManager.getDefaults().getColor("contactTree.selectionOnlineBackground");
	private static final Color CLEAR_GRAY220_220_220 = UIManager.getDefaults().getColor("contactTree.selectionBackground");
	private static final Color CLEAR_GRAY243_243_247 = UIManager.getDefaults().getColor("contactTree.background");
	private static final Color GRAY229_229_235 = UIManager.getDefaults().getColor("contactTree.onlineBackground");
	private static final Color GRAY190_190_190 =  UIManager.getDefaults().getColor("Color.gray190_190_190");
	private static final Color PURPLE149_128_174 =  UIManager.getDefaults().getColor("Color.purple149_128_174");
	private static final Color PURPLE193_171_218 = UIManager.getDefaults().getColor("contactTreeHiglightFillColor");
	private static final Color GRAY235_240_245 = UIManager.getDefaults().getColor("Tree.background");
	
	@Test
	public void shouldGetSynthColors() throws Exception {
		assertEquals(GRAY909090, SynthColors.GRAY909090);
		assertEquals(PURPLE12040140, SynthColors.PURPLE12040140);
		assertEquals(SKYBLUE11521505, SynthColors.SKYBLUE11521505);
		assertEquals(SKYBLUE12508400, SynthColors.SKYBLUE12508400);
		assertEquals(WHITEBLUE15462645, SynthColors.WHITEBLUE15462645);
		assertEquals(WHITE255_255_255, SynthColors.WHITE255_255_255);
		assertEquals(CLEAR_GRAY245_245_245, SynthColors.CLEAR_GRAY245_245_245);
		assertEquals(BLUE175_205_225, SynthColors.BLUE175_205_225);
		assertEquals(GRAY77_77_77, SynthColors.GRAY77_77_77);
		assertEquals(GRAY210_210_210, SynthColors.GRAY210_210_210);
		assertEquals(GRAY150_150_150, SynthColors.GRAY150_150_150);
		assertEquals(GRAY170_170_170, SynthColors.GRAY170_170_170);
		assertEquals(BLUE_201_202_230, SynthColors.BLUE_201_202_230);
		assertEquals(CLEAR_GRAY220_220_220, SynthColors.CLEAR_GRAY220_220_220);
		assertEquals(CLEAR_GRAY243_243_247, SynthColors.CLEAR_GRAY243_243_247);
		assertEquals(GRAY229_229_235, SynthColors.GRAY229_229_235);
		assertEquals(GRAY190_190_190, SynthColors.GRAY190_190_190);
		assertEquals(PURPLE149_128_174, SynthColors.PURPLE149_128_174);
		assertEquals(PURPLE193_171_218, SynthColors.PURPLE193_171_218);
		assertEquals(GRAY235_240_245, SynthColors.GRAY235_240_245);
	}
}
