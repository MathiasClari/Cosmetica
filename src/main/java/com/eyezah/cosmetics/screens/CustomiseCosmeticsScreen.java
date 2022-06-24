package com.eyezah.cosmetics.screens;

import benzenestudios.sulphate.Anchor;
import com.eyezah.cosmetics.cosmetics.PlayerData;
import com.eyezah.cosmetics.cosmetics.model.BakableModel;
import com.eyezah.cosmetics.screens.fakeplayer.FakePlayer;
import com.eyezah.cosmetics.utils.TextComponents;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class CustomiseCosmeticsScreen extends PlayerRenderScreen {
	protected CustomiseCosmeticsScreen(Screen parentScreen, FakePlayer player, ServerOptions options, boolean inlineChangeButton, boolean animation) {
		super(TextComponents.translatable("cosmetica.customizeCosmetics"), parentScreen, player);

		this.setAnchorX(Anchor.RIGHT, () -> this.width / 2 - 50);
		this.setAnchorY(Anchor.CENTRE, () -> this.height / 2);

		this.options = options;
		this.inlineChangeButton = inlineChangeButton;

		if (!animation) {
			this.setTransitionProgress(1.0f);
		}
	}

	private final ServerOptions options;
	private final boolean inlineChangeButton;

	private Section cloakSection;
	private Section loreSection;
	private Section hatsSection;
	private Section shoulderBuddiesSection;
	private Section backBlingSection;

	private Section selected;

	private Section createDisabledSection(String title) {
		Span section = this.addWidget(Span::new, TextComponents.literal(title + " Section"));

		this.addTextTo(section, TextComponents.literal(title), 100, false);
		this.addTextTo(section, TextComponents.literal("Disabled"), 100, false).active = false;

		section.calculateDimensions();
		return section;
	}

	private Section createActiveSection(String title, List<String> items, Button.OnPress onChange) {
		Div section = Div.create(title);
		String headerText = items.isEmpty() ? "No " + title : title;

		if (!this.inlineChangeButton) {
			Span header = section.addChild(new Span(0, 0, 200, 20, TextComponents.literal(title + "Header")));

			this.addTextTo(header, TextComponents.literal(headerText), this.font.width(headerText) + 8, false);
			header.addChild(new Button(0, 0, 60, 20, TextComponents.literal("Change"), onChange));
		}
		else {
			this.addTextTo(section, TextComponents.literal(headerText), 200, false);
		}

		for (String item : items) {
			this.addTextTo(section, TextComponents.literal(item), 200, false).active = false;
		}

		if (this.inlineChangeButton) {
			section.addChild(new Button(0, 0, 100, 20, TextComponents.literal("Change"), onChange));
		}

		section.calculateDimensions();
		return section;
	}

	private List<String> immutableListOf(String str) {
		return str.isEmpty() ? ImmutableList.of() : ImmutableList.of(str);
	}

	@Override
	protected void addWidgets() {
		PlayerData data = this.fakePlayer.getData();

		// cape
		this.cloakSection = this.createActiveSection("Cape", immutableListOf(data.capeName()), b -> System.out.println("would change"));

		// lore
		this.loreSection = this.options.lore.get() ? this.createActiveSection("Lore", immutableListOf(data.lore()), b -> System.out.println("would change")) : this.createDisabledSection("Lore");

		// hats
		this.hatsSection = this.options.hats.get() ? this.createActiveSection("Hats", data.hats().stream().map(BakableModel::name).collect(Collectors.toList()), b -> System.out.println("would change")) : this.createDisabledSection("Hats");

		// sbs
		List<String> shoulderBuddies = ImmutableList.of(
				"Left: " + (data.leftShoulderBuddy() == null ? "None" : data.leftShoulderBuddy().name()),
				"Right: " + (data.rightShoulderBuddy() == null ? "None" : data.rightShoulderBuddy().name())
		);

		this.shoulderBuddiesSection = this.options.shoulderBuddies.get() ? this.createActiveSection("Shoulder Buddies", shoulderBuddies, b -> System.out.println("would change")) : this.createDisabledSection("Shoulder Buddies");

		// back bling
		this.backBlingSection = this.options.backBlings.get() ? this.createActiveSection("Back Bling", data.backBling() == null ? ImmutableList.of() : ImmutableList.of(data.backBling().name()), b -> System.out.println("would change")) : this.createDisabledSection("Back Bling");

		// the whole gang
		List<Section> availableDivs = ImmutableList.of(this.cloakSection, this.loreSection, this.hatsSection, this.shoulderBuddiesSection, this.backBlingSection);

		// if first time, initialise selected to capes
		// otherwise, set selected to the *current* div of the section we want
		// we need to make sure it can resize correctly so we can't just generate stuff once
		if (this.selected == null) {
			this.selected = this.cloakSection;
		}
		else {
			// I did this as a foreach loop instead of a stream b/c I'm hotswapping this in ;)
			for (Section s : availableDivs) {
				if (s.getMessage().equals(this.selected.getMessage())) {
					this.selected = s;
					break;
				}
			}
		}

		// left selection menu

		for (Section section : availableDivs) {
			Button button = this.addButton(100, 20, section.getMessage(), b -> this.select(section));

			if (section == this.selected) {
				button.active = false;
			}
		}

		// right selected area
		this.selected.x = this.width / 2 + 50;
		this.selected.y = this.height / 2 - availableDivs.size() * 12 - 2;
		this.addRenderableWidget(this.selected);

		// done button
		this.addDone(this.height - 40);

		this.initialPlayerLeft = this.width / 3 + 10;
		this.deltaPlayerLeft = this.width / 2 - this.initialPlayerLeft;
	}

	private void select(Section section) {
		this.selected = section;
		this.init(this.minecraft, this.width, this.height);
	}

	@Override
	public void afterInit() {
		for (GuiEventListener widget : this.children()) {
			if (widget instanceof Section section) section.repositionChildren();
		}
	}

	@Override
	public void onClose() {
		if (this.parent instanceof MainScreen main) {
			main.setTransitionProgress(0.0f);
		}

		super.onClose();
	}

	@Override
	public void render(PoseStack matrices, int mouseX, int mouseY, float delta) {
		super.render(matrices, mouseX, mouseY, delta);
	}

	// helper stuff

	private TextWidget addText(Component text, int width, boolean centered) {
		return this.addWidget((x, y, w, h, component) -> new TextWidget(x, y, w, h, centered, component), text, width, 20);
	}

	private TextWidget addTextTo(Section section, Component text, int width, boolean centered) {
		return section.addChild(new TextWidget(0, 0, width, 20, centered, text));
	}

	private static class TextWidget extends AbstractWidget {
		public TextWidget(int x, int y, int width, int height, boolean centered, Component component) {
			super(x, y, width, height, component);
			this.centered = centered;
		}

		private boolean centered;

		@Override
		public void updateNarration(NarrationElementOutput narration) {
			this.defaultButtonNarrationText(narration);
		}

		public boolean mouseClicked(double d, double e, int i) {
			return false;
		}

		@Override
		public void render(PoseStack poseStack, int i, int j, float f) {
			Minecraft minecraft = Minecraft.getInstance();
			Font font = minecraft.font;

			int colour = this.active ? 16777215 : 10526880;

			if (this.centered) {
				drawCenteredString(poseStack, font, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, colour | Mth.ceil(this.alpha * 255.0F) << 24);
			}
			else {
				drawString(poseStack, font, this.getMessage(), this.x, this.y + (this.height - 8) / 2, colour | Mth.ceil(this.alpha * 255.0F) << 24);
			}
		}
	}

	private static class Div extends Section {
		public Div(int x, int y, int width, int height, Component component) {
			super(x, y, width, height, component);
		}

		@Override
		public void calculateDimensions() {
			super.calculateDimensions();
			this.setWidth(this.children.stream().mapToInt(w -> w.getWidth()).max().orElse(0));
			this.height = this.children.stream().mapToInt(w -> w.getHeight()).sum();
		}

		@Override
		public void repositionChildren() {
			int y0 = this.y;

			for (AbstractWidget child : this.children) {
				child.x += this.x;
				child.y += y0;

				y0 += child.getHeight();
			}

			super.repositionChildren();
		}
		
		private static Div create(String name) {
			return new Div(0, 0, 0, 0, TextComponents.literal(name));
		}
	}

	private static class Span extends Section {
		public Span(int x, int y, int width, int height, Component component) {
			super(x, y, width, height, component);
		}

		@Override
		public void calculateDimensions() {
			super.calculateDimensions();
			this.setWidth(this.children.stream().mapToInt(w -> w.getWidth()).sum());
			this.height = this.children.stream().mapToInt(w -> w.getHeight()).max().orElse(0);
		}

		@Override
		public void repositionChildren() {
			int x0 = this.x;

			for (AbstractWidget child : this.children) {
				child.x += x0;
				child.y += this.y;

				x0 += child.getWidth();
			}

			super.repositionChildren();
		}

		private static Span create(String name) {
			return new Span(0, 0, 0, 0, TextComponents.literal(name));
		}
	}

	private abstract static class Section extends AbstractWidget {
		public Section(int i, int j, int k, int l, Component component) {
			super(i, j, k, l, component);
		}

		protected List<AbstractWidget> children = new LinkedList<>();

		public void calculateDimensions() {
			for (AbstractWidget child : this.children) {
				if (child instanceof Section section) section.calculateDimensions();
			}
		}

		public void repositionChildren() {
			for (AbstractWidget child : this.children) {
				if (child instanceof Section section) section.repositionChildren();
			}
		}

		public <T extends AbstractWidget> T addChild(T widget) {
			this.children.add(widget);
			return widget;
		}

		public void removeChildren() {
			this.children.clear();
		}

		@Override
		public boolean mouseClicked(double x, double y, int i) {
			for (AbstractWidget child : this.children) {
				if (child.x <= x && x < child.x + child.getWidth()) {
					if (child.y <= y && y < child.y + child.getHeight()) {
						return child.mouseClicked(x, y, i);
					}
				}
			}

			return false;
		}

		@Override
		public void onRelease(double x, double y) {
			for (AbstractWidget child : this.children) {
				if (child.x <= x && x < child.x + child.getWidth()) {
					if (child.y <= y && y < child.y + child.getHeight()) {
						child.onRelease(x, y);
						return;
					}
				}
			}
		}

		@Override
		public boolean mouseDragged(double x, double y, int button, double prevX, double prevY) {
			for (AbstractWidget child : this.children) {
				if (child.x <= x && x < child.x + child.getWidth()) {
					if (child.y <= y && y < child.y + child.getHeight()) {
						return child.mouseDragged(x, y, button, prevX, prevY);
					}
				}
			}

			return false;
		}

		@Override
		public void updateNarration(NarrationElementOutput narration) {
			for (AbstractWidget child : this.children) {
				child.updateNarration(narration);
			}
		}

		@Override
		public void render(PoseStack poseStack, int i, int j, float f) {
			for (AbstractWidget child : this.children) {
				child.render(poseStack, i, j, f);
			}
		}
	}
}