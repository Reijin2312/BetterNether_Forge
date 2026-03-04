package org.betterx.betternether.entity.model;

import org.betterx.betternether.MHelper;
import org.betterx.betternether.entity.EntityJungleSkeleton;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.SkeletonModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.LegacyRandomSource;

public class ModelJungleSkeleton extends SkeletonModel<EntityJungleSkeleton> {
    private static final float ANGLE45 = (float) Math.PI * 0.25F;
    private static final float ANGLE90 = (float) Math.PI * 0.5F;
    private static final RandomSource RANDOM = new LegacyRandomSource(130520220100l);
    private static final float BOUND_MIN = ANGLE90 * 2F / 3F;
    private static final float BOUND_MAX = ANGLE90 * 4F / 5F;

    public static LayerDefinition createBodyLayer() {
        MeshDefinition modelData = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition modelPartData = modelData.getRoot();
        modelPartData.addOrReplaceChild(
                "right_arm",
                CubeListBuilder.create()
                               .texOffs(40, 16)
                               .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-5.0F, 2.0F, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                "left_arm",
                CubeListBuilder.create()
                               .texOffs(40, 16)
                               .mirror()
                               .addBox(-1.0F, -2.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(5.0F, 2.0F, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                "right_leg",
                CubeListBuilder.create()
                               .texOffs(0, 16)
                               .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(-2.0F, 12.0F, 0.0F)
        );
        modelPartData.addOrReplaceChild(
                "left_leg",
                CubeListBuilder.create()
                               .texOffs(0, 16)
                               .mirror()
                               .addBox(-1.0F, 0.0F, -1.0F, 2.0F, 12.0F, 2.0F),
                PartPose.offset(2.0F, 12.0F, 0.0F)
        );
        PartDefinition modelPartData_HEAD = modelPartData.getChild(PartNames.HEAD);

        for (int i = 0; i < 4; i++) {
            float angle = ANGLE45 + (i / 4F) * MHelper.PI2;

            modelPartData_HEAD.addOrReplaceChild(
                    "leave_" + i,
                    CubeListBuilder.create()
                                   .texOffs(24, 0)
                                   .addBox(
                                           -3.0F,
                                           -8.0F,
                                           0.0F,
                                           6.0F,
                                           8.0F,
                                           0.0F
                                   ),
                    PartPose.offsetAndRotation(
                            (float) -Math.sin(angle),
                            -8,
                            (float) -Math.cos(angle),
                            MHelper.randRange(
                                    BOUND_MIN,
                                    BOUND_MAX,
                                    RANDOM
                            ),
                            angle,
                            0
                    )
            );
            /*ModelPart leaf = new ModelPart(this, 24, 0);
            leaf.setPivot((float) -Math.sin(angle), -8, (float) -Math.cos(angle));
            leaf.addCuboid(-3.0F, -8.0F, 0.0F, 6.0F, 8.0F, 0.0F);
            leaf.pitch = MHelper.randRange(BOUND_MIN, BOUND_MAX, RANDOM);
            leaf.yaw = angle;
            this.head.addChild(leaf);*/
        }
        return LayerDefinition.create(modelData, 64, 32);
    }

    public ModelJungleSkeleton(ModelPart modelPart) {
        super(modelPart);
    }
}
