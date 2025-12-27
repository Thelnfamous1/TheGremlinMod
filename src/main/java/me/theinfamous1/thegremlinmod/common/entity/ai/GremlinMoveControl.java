package me.theinfamous1.thegremlinmod.common.entity.ai;

import me.theinfamous1.thegremlinmod.common.entity.AbstractGremlin;
import net.minecraft.world.entity.ai.control.MoveControl;

public class GremlinMoveControl<T extends AbstractGremlin> extends MoveControl {
    private final T gremlin;

    public GremlinMoveControl(T mob) {
        super(mob);
        this.gremlin = mob;
    }

    public void tick() {
        if (this.gremlin.canMove()) {
            super.tick();
        }
    }
}