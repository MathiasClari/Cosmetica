package com.eyezah.cosmetics.cosmetics;

import com.eyezah.cosmetics.Cosmetica;
import com.eyezah.cosmetics.cosmetics.model.BakableModel;
import com.eyezah.cosmetics.cosmetics.model.OverriddenModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.ModelManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;

public class BackBling<T extends Player> extends CustomLayer<T, PlayerModel<T>> {
	private ModelManager modelManager;
	private EntityModelSet entityModelSet;

	public BackBling(RenderLayerParent<T, PlayerModel<T>> renderLayerParent, EntityModelSet entityModelSet) {
		super(renderLayerParent);
		this.modelManager = Minecraft.getInstance().getModelManager();
		this.entityModelSet = entityModelSet;
	}

	@Override
	public void render(PoseStack stack, MultiBufferSource multiBufferSource, int packedLightProbably, T player, float f, float g, float pitch, float j, float k, float l) {
		BakableModel modelData = overridden.get(() -> Cosmetica.getPlayerData(player).backBling());

		if (modelData == null) return; // if it has a model

		stack.pushPose();
		doCoolRenderThings(modelData, this.getParentModel().body, stack, multiBufferSource, packedLightProbably, 0, -0.0f, 0.1f);
		stack.popPose();
	}

	public static final OverriddenModel overridden = new OverriddenModel();
}