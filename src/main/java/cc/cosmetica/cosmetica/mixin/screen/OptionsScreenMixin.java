/*
 * Copyright 2022 EyezahMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cc.cosmetica.cosmetica.mixin.screen;

import benzenestudios.sulphate.ClassicButton;
import benzenestudios.sulphate.ExtendedScreen;
import cc.cosmetica.cosmetica.screens.LoadingScreen;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
	protected OptionsScreenMixin(Component component) {
		super(component);
	}

	@Shadow
	@Final
	private Options options;

	@Inject(at=@At("RETURN"), method="init")
	private void onInit(CallbackInfo info) {
		for (GuiEventListener eventListener : ((ExtendedScreen) this).getChildren()) {
			// Grid layout is no longer an element in 1.19.4 but a class to prepare element positions, so I can go back to doing this.
			if (eventListener instanceof AbstractWidget widget && widget.getMessage().getContents() instanceof TranslatableContents thisIsRidiculous) {
				if (thisIsRidiculous.getKey().equals("options.skinCustomisation")) {
					widget.visible = false;
					widget.active = false;
					break;
				}
			}
		}

		this.addRenderableWidget(new ClassicButton(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, Component.translatable("cosmetica.cosmetics"),
				button -> this.minecraft.setScreen(new LoadingScreen(this, this.options))));
	}
}
