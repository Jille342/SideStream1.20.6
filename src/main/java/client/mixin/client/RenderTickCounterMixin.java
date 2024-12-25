package client.mixin.client;

import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;

@Mixin({RenderTickCounter.class})
public class RenderTickCounterMixin implements RenderTickCounterAccessor {
    @Override
    public void setTickTime(float tickTime) {

    }
}
