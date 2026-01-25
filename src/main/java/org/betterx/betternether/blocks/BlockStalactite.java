package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourStone;
import org.betterx.bclib.blocks.BlockProperties;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nullable;

public class BlockStalactite extends BlockBaseNotFull implements BehaviourStone, LiquidBlockContainer {

    public static final IntegerProperty SIZE = BlockProperties.SIZE;
    public static final IntegerProperty LAVA_LEVEL = IntegerProperty.create("lava_level", 0, 8);
    public static final BooleanProperty LAVA_FALLING = BooleanProperty.create("lava_falling");
    private static final VoxelShape[] SHAPES;

    public BlockStalactite(Block source) {
        super(BlockBehaviour.Properties.copy(source).noOcclusion());
        this.registerDefaultState(getStateDefinition().any()
                                                    .setValue(SIZE, 0)
                                                    .setValue(LAVA_LEVEL, 0)
                                                    .setValue(LAVA_FALLING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> stateManager) {
        stateManager.add(SIZE, LAVA_LEVEL, LAVA_FALLING);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter view, BlockPos pos, CollisionContext ePos) {
        return SHAPES[state.getValue(SIZE)];
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        FluidState fluidState = ctx.getLevel().getFluidState(ctx.getClickedPos());
        int lavaLevel = isLava(fluidState) ? fluidState.getAmount() : 0;
        boolean falling = isLavaFalling(fluidState);
        return this.defaultBlockState()
                   .setValue(LAVA_LEVEL, Math.min(8, lavaLevel))
                   .setValue(LAVA_FALLING, falling);
    }

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(this) && state.getValue(LAVA_LEVEL) == 0) {
            FluidState fluidState = oldState.getFluidState();
            if (isLava(fluidState)) {
                level.setBlock(pos, state
                        .setValue(LAVA_LEVEL, Math.min(8, fluidState.getAmount()))
                        .setValue(LAVA_FALLING, isLavaFalling(fluidState)), Block.UPDATE_ALL);
            }
        }
        super.onPlace(state, level, pos, oldState, movedByPiston);
    }

    @Override
    public BlockState updateShape(
            BlockState state,
            Direction facing,
            BlockState neighborState,
            LevelAccessor world,
            BlockPos pos,
        BlockPos neighborPos
    ) {
        if (state.getValue(LAVA_LEVEL) > 0) {
            world.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(world));
        }
        return super.updateShape(state, facing, neighborState, world, pos, neighborPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        int lavaLevel = state.getValue(LAVA_LEVEL);
        boolean falling = state.getValue(LAVA_FALLING);
        if (lavaLevel >= 8) {
            return falling ? Fluids.LAVA.getFlowing(8, true) : Fluids.LAVA.getSource(false);
        }
        if (lavaLevel > 0) {
            return Fluids.LAVA.getFlowing(lavaLevel, falling);
        }
        return super.getFluidState(state);
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter world, BlockPos pos, BlockState state, Fluid fluid) {
        return isLava(fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor world, BlockPos pos, BlockState state, FluidState fluidState) {
        if (isLava(fluidState)) {
            if (!world.isClientSide()) {
                int lavaLevel = Math.min(8, fluidState.getAmount());
                boolean falling = isLavaFalling(fluidState);
                if (state.getValue(LAVA_LEVEL) != lavaLevel || state.getValue(LAVA_FALLING) != falling) {
                    world.setBlock(pos, state
                            .setValue(LAVA_LEVEL, lavaLevel)
                            .setValue(LAVA_FALLING, falling), Block.UPDATE_ALL);
                }
                world.scheduleTick(pos, Fluids.LAVA, Fluids.LAVA.getTickDelay(world));
            }
            return true;
        }
        return false;
    }

    private static boolean isLava(Fluid fluid) {
        return fluid.isSame(Fluids.LAVA);
    }

    private static boolean isLava(FluidState fluidState) {
        return fluidState.getType().isSame(Fluids.LAVA);
    }

    private static boolean isLavaFalling(FluidState fluidState) {
        if (fluidState.getType() instanceof FlowingFluid && fluidState.hasProperty(FlowingFluid.FALLING)) {
            return fluidState.getValue(FlowingFluid.FALLING);
        }
        return false;
    }

    @Override
    public void setPlacedBy(
            Level world,
            BlockPos pos,
            BlockState state,
            @Nullable LivingEntity placer,
            ItemStack itemStack
    ) {
        final MutableBlockPos POS = new MutableBlockPos();

        if (world.getBlockState(pos.below()).getBlock() instanceof BlockStalactite) {
            POS.setX(pos.getX());
            POS.setZ(pos.getZ());
            for (int i = 1; i < 8; i++) {
                POS.setY(pos.getY() - i);
                if (world.getBlockState(POS).getBlock() instanceof BlockStalactite) {
                    BlockState state2 = world.getBlockState(POS);
                    int size = state2.getValue(SIZE);
                    if (size < i) {
                        world.setBlockAndUpdate(POS, state2.setValue(SIZE, i));
                    } else
                        break;
                } else
                    break;
            }
        }
        if (world.getBlockState(pos.above()).getBlock() instanceof BlockStalactite) {
            POS.setX(pos.getX());
            POS.setZ(pos.getZ());
            for (int i = 1; i < 8; i++) {
                POS.setY(pos.getY() + i);
                if (world.getBlockState(POS).getBlock() instanceof BlockStalactite) {
                    BlockState state2 = world.getBlockState(POS);
                    int size = state2.getValue(SIZE);
                    if (size < i) {
                        world.setBlockAndUpdate(POS, state2.setValue(SIZE, i));
                    } else
                        break;
                } else
                    break;
            }
        }
    }

    @Override
    public void destroy(LevelAccessor world, BlockPos pos, BlockState state) {
        BlockPos pos2 = pos.above();
        BlockState state2 = world.getBlockState(pos2);
        if (state2.getBlock() instanceof BlockStalactite && state2.getValue(SIZE) < state.getValue(SIZE)) {
            state2.getBlock().destroy(world, pos2, state2);
            world.destroyBlock(pos2, true);
        }

        pos2 = pos.below();
        state2 = world.getBlockState(pos2);
        if (state2.getBlock() instanceof BlockStalactite && state2.getValue(SIZE) < state.getValue(SIZE)) {
            state2.getBlock().destroy(world, pos2, state2);
            world.destroyBlock(pos2, true);
        }
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader world, BlockPos pos) {
        return canPlace(world, pos, Direction.UP) || canPlace(world, pos, Direction.DOWN);
    }

    private boolean canPlace(LevelReader world, BlockPos pos, Direction dir) {
        return world.getBlockState(pos.relative(dir)).getBlock() instanceof BlockStalactite || canSupportCenter(
                world,
                pos.relative(
                        dir),
                dir.getOpposite()
        );
    }

    static {
        SHAPES = new VoxelShape[8];
        for (int i = 0; i < 8; i++)
            SHAPES[i] = box(7 - i, 0, 7 - i, 9 + i, 16, 9 + i);
    }
}
