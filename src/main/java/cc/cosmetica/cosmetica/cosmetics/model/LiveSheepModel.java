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

package cc.cosmetica.cosmetica.cosmetics.model;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.QuadrupedModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.entity.animal.Sheep;

public class LiveSheepModel<T extends Sheep> extends QuadrupedModel<T> {
	private float headXRot;
	final ModelPart root;

	public LiveSheepModel(ModelPart modelPart) {
		super(modelPart, false, 8.0F, 4.0F, 2.0F, 2.0F, 24);
		this.root = modelPart;
	}

	public void prepareMobModel(T sheep, float f, float g, float h) {
		super.prepareMobModel(sheep, f, g, h);
		this.head.y = 6.0F + sheep.getHeadEatPositionScale(h) * 9.0F;
		this.headXRot = sheep.getHeadEatAngleScale(h);
	}

	public void setupAnim(T sheep, float f, float g, float h, float i, float j) {
		super.setupAnim(sheep, f, g, h, i, j);
		this.head.xRot = this.headXRot;
	}

	public void renderOnShoulder(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j) {
		poseStack.scale(0.35F, 0.35F, 0.35F);
		root.render(poseStack, vertexConsumer, i, j);
	}
}
