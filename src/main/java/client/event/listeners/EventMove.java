package client.event.listeners;

import client.event.Event;
import client.utils.RotationUtils;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.math.BlockPos;

public class EventMove extends Event<EventMove>
{

    @Setter
    @Getter
    public double x, y, z;

    private double lastX, lastY, lastZ;

    public boolean isModded()
    {
        return lastX != x || lastY != y || lastZ != z;
    }

    public EventMove(double x, double y, double z)
    {
        super();
        this.x = x;
        this.y = y;
        this.z = z;

        this.lastX = x;
        this.lastY = y;
        this.lastZ = z;
    }

    public void setPosition(double x, double y, double z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void setPosition(BlockPos pos)
    {
        this.x = pos.getX() + .5;
        this.y = pos.getY();
        this.z = pos.getZ() + .5;
    }

}
