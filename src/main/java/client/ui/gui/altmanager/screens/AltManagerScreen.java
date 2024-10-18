/*
 * Copyright (c) 2014-2024 Wurst-Imperium and contributors.
 *
 * This source code is subject to the terms of the GNU General Public
 * License, version 3. If a copy of the GPL was not distributed with this
 * file, You can obtain one at: https://www.gnu.org/licenses/gpl-3.0.txt
 */
package client.ui.gui.altmanager.screens;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.StringJoiner;

import client.Client;
import client.alts.Alt;
import client.alts.AltManager;
import client.ui.gui.altmanager.LoginException;
import client.ui.gui.altmanager.screens.DirectLoginScreen;
import client.utils.ListWidget;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.Selectable;
import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.fabricmc.fabric.api.client.screen.v1.Screens;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.gui.screen.ConfirmScreen;
import net.minecraft.client.gui.screen.NoticeScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.render.Tessellator;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Unique;

public final class AltManagerScreen extends Screen
{
    private static final HashSet<Alt> failedLogins = new HashSet<>();

    private final Screen prevScreen;
    @Unique
    private  List<Drawable> drawables2 = Lists.newArrayList();
    private final AltManager altManager;

    private ListGui listGui;
    private int errorTimer;
    public static MinecraftClient client = MinecraftClient.getInstance();

    private ButtonWidget useButton;
    private ButtonWidget deleteButton;



    public AltManagerScreen(Screen prevScreen)
    {
        super(Text.literal("Alt Manager"));
        this.prevScreen = prevScreen;
        this.altManager = Client.altManager;
    }

    @Override
    public void init()
    {
        listGui = new ListGui(client, this, altManager.getAlts());

        addDrawableChild(useButton =
                ButtonWidget.builder(Text.literal("Login"), b -> pressLogin())
                        .dimensions(width / 2 - 154, height - 52, 100, 20).build());

        addDrawableChild(ButtonWidget
                .builder(Text.literal("Direct Login"),
                        b -> client.setScreen(new DirectLoginScreen(this)))
                .dimensions(width / 2 - 50, height - 52, 100, 20).build());

        addDrawableChild(ButtonWidget
                .builder(Text.literal("Add"),
                        b -> client.setScreen(new AddAltScreen(this, altManager)))
                .dimensions(width / 2 + 54, height - 52, 100, 20).build());

        addDrawableChild(deleteButton =
                ButtonWidget.builder(Text.literal("Delete"), b -> pressDelete())
                        .dimensions(width / 2 + 2, height - 28, 74, 20).build());

        addDrawableChild(ButtonWidget
                .builder(Text.literal("Cancel"), b -> client.setScreen(prevScreen))
                .dimensions(width / 2 + 80, height - 28, 75, 20).build());


    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton)
    {
        listGui.mouseClicked(mouseX, mouseY, mouseButton);

        if(mouseY >= 36 && mouseY <= height - 57)
            if(mouseX >= width / 2 + 140 || mouseX <= width / 2 - 126)
                listGui.selected = -1;

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button,
                                double deltaX, double deltaY)
    {
        listGui.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button)
    {
        listGui.mouseReleased(mouseX, mouseY, button);
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY,
                                 double horizontalAmount, double verticalAmount)
    {
        listGui.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount,
                verticalAmount);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {
        if(keyCode == GLFW.GLFW_KEY_ENTER)
            useButton.onPress();

        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void tick()
    {
        boolean altSelected = listGui.selected >= 0
                && listGui.selected < altManager.getAlts().size();

        useButton.active = altSelected;

        deleteButton.active = altSelected;

    }

    private void pressLogin()
    {
        Alt alt = listGui.getSelectedAlt();
        if(alt == null)
            return;

        altManager.login(alt);
        failedLogins.remove(alt);

    }



    private void pressDelete()
    {
        Alt alt = listGui.getSelectedAlt();
        if(alt == null)
            return;

        Text text = Text.literal("Are you sure you want to remove this alt?");

        String altName = alt.getUsername();
        Text message = Text.literal(
                "\"" + altName + "\" will be lost forever! (A long time!)");

        ConfirmScreen screen = new ConfirmScreen(this::confirmRemove, text,
                message, Text.literal("Delete"), Text.literal("Cancel"));
        client.setScreen(screen);
    }







    private void confirmRemove(boolean confirmed)
    {
        if(listGui.getSelectedAlt() == null)
            return;

        if(confirmed)
            altManager.remove(listGui.selected);

        client.setScreen(this);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY,
                       float partialTicks)
    {
        renderBackground(context, mouseX, mouseY, partialTicks);
        listGui.render(context, mouseX, mouseY, partialTicks);

        MatrixStack matrixStack = context.getMatrices();
        Matrix4f matrix = matrixStack.peek().getPositionMatrix();
        Tessellator tessellator = RenderSystem.renderThreadTesselator();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        RenderSystem.setShader(GameRenderer::getPositionProgram);


        // title text
        context.drawCenteredTextWithShadow(textRenderer, "Alt Manager",
                width / 2, 4, 16777215);
        context.drawCenteredTextWithShadow(textRenderer,
                "Logged in as:"+MinecraftClient.getInstance().getSession().getUsername()+ "    Alts: " + altManager.getAlts().size(), width / 2, 14, 16777215);

        // red flash for errors
        if(errorTimer > 0)
        {
            RenderSystem.setShader(GameRenderer::getPositionProgram);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glEnable(GL11.GL_BLEND);

            RenderSystem.setShaderColor(1, 0, 0, errorTimer / 16F);

            bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
                    VertexFormats.POSITION);
            bufferBuilder.vertex(matrix, 0, 0, 0).next();
            bufferBuilder.vertex(matrix, width, 0, 0).next();
            bufferBuilder.vertex(matrix, width, height, 0).next();
            bufferBuilder.vertex(matrix, 0, height, 0).next();
            tessellator.draw();

            GL11.glEnable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_BLEND);
            errorTimer--;
        }

        for(Drawable drawable : drawables2())
            drawable.render(context, mouseX, mouseY, partialTicks);

    }
    private List<Drawable> drawables2() {
        return  drawables2;
    }
    protected <T extends Element & Drawable & Selectable> T addDrawableChild(T drawableElement) {
        this.drawables2.add((Drawable)drawableElement);
        return this.addSelectableChild(drawableElement);
    }




    @Override
    public void close()
    {
        client.setScreen(prevScreen);
    }

    public static final class ListGui extends ListWidget
    {
        private final List<Alt> list;
        private int selected = -1;
        private AltManagerScreen prevScreen;
        private long lastTime;

        public ListGui(MinecraftClient minecraft, AltManagerScreen prevScreen,
                       List<Alt> list)
        {
            super(minecraft, prevScreen.width, prevScreen.height, 36,
                    prevScreen.height - 56, 30);

            this.prevScreen = prevScreen;
            this.list = list;
        }

        @Override
        protected boolean isSelectedItem(int id)
        {
            return selected == id;
        }


        /**
         * @return The selected Alt, or null if no Alt is selected.
         */
        protected Alt getSelectedAlt()
        {
            if(selected < 0 || selected >= list.size())
                return null;

            return list.get(selected);
        }

        @Override
        protected int getItemCount()
        {
            return list.size();
        }

        @Override
        protected boolean selectItem(int index, int button, double mouseX,
                                     double mouseY)
        {
            if(index == selected && Util.getMeasuringTimeMs() - lastTime < 250)
                prevScreen.pressLogin();

            if(index >= 0 && index < list.size())
                selected = index;

            lastTime = Util.getMeasuringTimeMs();
            return true;
        }

        @Override
        protected void renderBackground()
        {

        }

        @Override
        protected void renderItem(DrawContext context, int id, int x, int y,
                                  int var4, int var5, int var6, float partialTicks)
        {
            Alt alt = list.get(id);

            MatrixStack matrixStack = context.getMatrices();
            Matrix4f matrix = matrixStack.peek().getPositionMatrix();
            Tessellator tessellator = RenderSystem.renderThreadTesselator();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            RenderSystem.setShader(GameRenderer::getPositionProgram);

            // green glow when logged in
            if(client.getSession().getUsername().equals(alt.getUsername()))
            {
                GL11.glDisable(GL11.GL_CULL_FACE);
                GL11.glEnable(GL11.GL_BLEND);

                float opacity =
                        0.3F - Math.abs(MathHelper.sin(System.currentTimeMillis()
                                % 10000L / 10000F * (float)Math.PI * 2.0F) * 0.15F);

                RenderSystem.setShaderColor(0, 1, 0, opacity);

                bufferBuilder.begin(VertexFormat.DrawMode.QUADS,
                        VertexFormats.POSITION);
                bufferBuilder.vertex(matrix, x - 2, y - 2, 0).next();
                bufferBuilder.vertex(matrix, x - 2 + 220, y - 2, 0).next();
                bufferBuilder.vertex(matrix, x - 2 + 220, y - 2 + 30, 0).next();
                bufferBuilder.vertex(matrix, x - 2, y - 2 + 30, 0).next();
                tessellator.draw();

                GL11.glEnable(GL11.GL_CULL_FACE);
                GL11.glDisable(GL11.GL_BLEND);
            }


            // name / email
            context.drawText(MinecraftClient.getInstance().textRenderer,
                    "Name: " + alt.getUsername(), x + 31, y + 3, 10526880,
                    false);
            context.drawText(client.textRenderer,
                    "Name: " + alt.getUsername(), x + 31, y + 3, 10526880,
                    false);
        }
    }
}
