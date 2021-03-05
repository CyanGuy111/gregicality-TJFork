package gregicadditions.widgets.monitor;

import gregicadditions.machines.multi.centralmonitor.MetaTileEntityMonitorScreen;
import gregtech.api.gui.IRenderContext;
import gregtech.api.gui.Widget;
import gregtech.api.metatileentity.MetaTileEntityUIFactory;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Mouse;

public class WidgetScreenGrid extends Widget {
    public int x,y;
    public static final int width = 20;
    public static final int height = 20;
    MetaTileEntityMonitorScreen monitorScreen;
    private int color = 0XFF000000;

    public WidgetScreenGrid(int xPosition, int yPosition, int x, int y) {
        super(new Position(xPosition + x * width, yPosition + y * height), new Size(width, height));
        this.x = x; this.y = y;
    }

    public void setScreen(MetaTileEntityMonitorScreen monitorScreen) {
        this.monitorScreen = monitorScreen;
        color = (monitorScreen != null && monitorScreen.isActive())? 0XFF4F66FF : 0XFF000000;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void drawInBackground(int mouseX, int mouseY, IRenderContext context) {
        int x = this.getPosition().x;
        int y = this.getPosition().y;
        int width = this.getSize().width;
        int height = this.getSize().height;
        int color = (monitorScreen != null && monitorScreen.isActive())? monitorScreen.frameColor:this.color;
        Gui.drawRect(x + 1, y + 1, x + width - 2, y + height - 2, color);
        if (monitorScreen.scale > 1) {
            width = monitorScreen.scale * width;
            height = monitorScreen.scale * height;
            Gui.drawRect(x, y, x + width - 1, y + 1, color);
            Gui.drawRect(x, y + height - 1, x + width - 1, y + height, color);
            Gui.drawRect(x, y, x + 1, y + height, color);
            Gui.drawRect(x + width - 1, y, x + width, y + height, color);
        }
        if (this.isMouseOverElement(mouseX, mouseY)) {
            Gui.drawRect(x, y, x + width, y + height, 0x4B6C6C6C);
        }
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int button) {
        if (this.isMouseOverElement(mouseX, mouseY)) {
            ClickData clickData = new ClickData(Mouse.getEventButton(), this.isShiftDown(), this.isCtrlDown());
            this.writeClientAction(1, clickData::writeToBuf);
            this.playButtonClickSound();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void handleClientAction(int id, PacketBuffer buffer) {
        super.handleClientAction(id, buffer);
        if (id == 1) {
            if (monitorScreen != null) {
                MetaTileEntityUIFactory.INSTANCE.openUI(monitorScreen.getHolder(), (EntityPlayerMP) this.gui.entityPlayer);
            }
        }
    }
}