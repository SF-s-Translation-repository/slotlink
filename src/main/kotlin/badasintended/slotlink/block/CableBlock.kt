package badasintended.slotlink.block

import badasintended.slotlink.block.entity.CableBlockEntity
import badasintended.slotlink.util.bbCuboid
import com.google.common.collect.ImmutableMap
import net.fabricmc.fabric.api.`object`.builder.v1.block.FabricBlockSettings
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags
import net.minecraft.block.*
import net.minecraft.block.entity.BlockEntity
import net.minecraft.item.ItemPlacementContext
import net.minecraft.state.StateManager
import net.minecraft.state.property.BooleanProperty
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Direction
import net.minecraft.util.shape.VoxelShape
import net.minecraft.util.shape.VoxelShapes
import net.minecraft.world.BlockView
import net.minecraft.world.WorldAccess

open class CableBlock(id: String = "cable", be: () -> BlockEntity = ::CableBlockEntity) : ChildBlock(id, be, SETTINGS) {

    companion object {
        val SETTINGS: Settings =
            FabricBlockSettings.of(Material.GLASS).breakByHand(true).breakByTool(FabricToolTags.PICKAXES).hardness(3f)

        val NORTH: BooleanProperty = BooleanProperty.of("north")
        val SOUTH: BooleanProperty = BooleanProperty.of("south")
        val EAST: BooleanProperty = BooleanProperty.of("east")
        val WEST: BooleanProperty = BooleanProperty.of("west")
        val UP: BooleanProperty = BooleanProperty.of("up")
        val DOWN: BooleanProperty = BooleanProperty.of("down")

        val propertyMap: ImmutableMap<Direction, BooleanProperty> = ImmutableMap
            .builder<Direction, BooleanProperty>()
            .put(Direction.NORTH, NORTH)
            .put(Direction.SOUTH, SOUTH)
            .put(Direction.EAST, EAST)
            .put(Direction.WEST, WEST)
            .put(Direction.UP, UP)
            .put(Direction.DOWN, DOWN)
            .build()
    }

    protected open fun canConnect(world: WorldAccess, neighborPos: BlockPos): Boolean {
        val block = world.getBlockState(neighborPos).block
        return block is ModBlock
    }

    override fun appendProperties(builder: StateManager.Builder<Block, BlockState>) {
        builder.add(NORTH, SOUTH, EAST, WEST, UP, DOWN)
    }

    override fun getPlacementState(ctx: ItemPlacementContext): BlockState? {
        val world = ctx.world
        val pos = ctx.blockPos

        return defaultState
            .with(NORTH, canConnect(world, pos.north()))
            .with(SOUTH, canConnect(world, pos.south()))
            .with(EAST, canConnect(world, pos.east()))
            .with(WEST, canConnect(world, pos.west()))
            .with(UP, canConnect(world, pos.up()))
            .with(DOWN, canConnect(world, pos.down()))
    }

    override fun getStateForNeighborUpdate(
        state: BlockState,
        facing: Direction,
        neighborState: BlockState,
        world: WorldAccess,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return state.with(propertyMap[facing], canConnect(world, neighborPos))
    }

    override fun getOutlineShape(state: BlockState, view: BlockView, pos: BlockPos, ctx: ShapeContext): VoxelShape {
        val north = bbCuboid(6, 6, 0, 4, 4, 10)
        val south = bbCuboid(6, 6, 6, 4, 4, 10)
        val east = bbCuboid(6, 6, 6, 10, 4, 4)
        val west = bbCuboid(0, 6, 6, 10, 4, 4)
        val up = bbCuboid(6, 6, 6, 4, 10, 4)
        val down = bbCuboid(6, 0, 6, 4, 10, 4)

        var result = bbCuboid(6, 6, 6, 4, 4, 4)

        if (state[NORTH]) result = VoxelShapes.union(result, north)
        if (state[SOUTH]) result = VoxelShapes.union(result, south)
        if (state[EAST]) result = VoxelShapes.union(result, east)
        if (state[WEST]) result = VoxelShapes.union(result, west)
        if (state[UP]) result = VoxelShapes.union(result, up)
        if (state[DOWN]) result = VoxelShapes.union(result, down)

        return result
    }

}
