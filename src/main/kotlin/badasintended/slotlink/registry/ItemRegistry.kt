@file:Suppress("MemberVisibilityCanBePrivate")

package badasintended.slotlink.registry

import badasintended.slotlink.item.*
import net.minecraft.util.registry.Registry
import net.minecraft.util.registry.Registry.ITEM

object ItemRegistry {

    val MULTI_DIM_REMOTE = MultiDimRemoteItem()
    val UNLIMITED_REMOTE = UnlimitedRemoteItem()
    val LIMITED_REMOTE = LimitedRemoteItem()

    fun init() {
        r(MULTI_DIM_REMOTE, UNLIMITED_REMOTE, LIMITED_REMOTE)
    }

    private fun r(vararg items: ModItem) {
        items.forEach { Registry.register(ITEM, it.id, it) }
    }

}
