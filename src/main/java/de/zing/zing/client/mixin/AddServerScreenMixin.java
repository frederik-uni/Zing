package de.zing.zing.client.mixin;

import de.zing.zing.client.module.ModuleManager;
import de.zing.zing.client.module.modules.JoinMessage.SendMessageModule;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.AddServerScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.CyclingButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AddServerScreen.class)
public abstract class AddServerScreenMixin extends Screen {
    @Final
    @Shadow
    private static final Text ENTER_NAME_TEXT = Text.translatable("addServer.enterName");
    @Final
    @Shadow

    private static final Text ENTER_IP_TEXT = Text.translatable("addServer.enterIp");
    @Unique
    public TextFieldWidget messageField;
    @Unique
    private SendMessageModule smm = (SendMessageModule) ModuleManager.getModule("SendMessage");
    @Final
    @Shadow
    private ServerInfo server;
    @Shadow
    @Final
    private BooleanConsumer callback;
    @Shadow
    private ButtonWidget addButton;
    @Shadow
    private TextFieldWidget addressField;
    @Shadow
    private TextFieldWidget serverNameField;
    protected AddServerScreenMixin(Text title) {
        super(title);
    }

    @Shadow
    protected abstract void updateAddButton();

    @Shadow
    protected abstract void addAndClose();

    @Inject(method = "init()V", at = @At(value = "HEAD"), cancellable = true)
    public void onInit(CallbackInfo ci) {
        if (this.smm.isEnabled()) {
            this.serverNameField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 46, 200, 20, Text.translatable("addServer.enterName"));
            this.serverNameField.setText(this.server.name);
            this.serverNameField.setChangedListener((serverName) -> this.updateAddButton());

            this.addSelectableChild(this.serverNameField);
            this.addressField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 86, 200, 20, Text.translatable("addServer.enterIp"));
            this.addressField.setMaxLength(128);
            this.addressField.setText(this.server.address);
            this.addressField.setChangedListener((address) -> this.updateAddButton());

            this.messageField = new TextFieldWidget(this.textRenderer, this.width / 2 - 100, 126, 200, 20, Text.of("Message"));
            this.messageField.setText(this.smm.get(server));
            this.messageField.setChangedListener((message) -> this.updateAddButton());

            this.addSelectableChild(this.addressField);
            this.addDrawableChild(CyclingButtonWidget.builder(ServerInfo.ResourcePackPolicy::getName).values(ServerInfo.ResourcePackPolicy.values()).initially(this.server.getResourcePackPolicy()).build(this.width / 2 - 100, this.height / 4 + 72 + 20, 200, 20, Text.translatable("addServer.resourcePack"), (button, resourcePackPolicy) -> this.server.setResourcePackPolicy(resourcePackPolicy)));
            this.addButton = this.addDrawableChild(ButtonWidget.builder(Text.translatable("addServer.add"), (button) -> this.addAndClose()).dimensions(this.width / 2 - 100, this.height / 4 + 96 + 18 + 10, 200, 20).build());
            this.addDrawableChild(ButtonWidget.builder(ScreenTexts.CANCEL, (button) -> this.callback.accept(false)).dimensions(this.width / 2 - 100, this.height / 4 + 120 + 18 + 10, 200, 20).build());
            this.setInitialFocus(this.serverNameField);
            this.updateAddButton();
            this.addSelectableChild(this.messageField);
            ci.cancel();
        }
    }

    @Inject(method = "render(Lnet/minecraft/client/gui/DrawContext;IIF)V", at = @At("HEAD"), cancellable = true)
    public void onRender(DrawContext context, int mouseX, int mouseY, float delta, CallbackInfo ci) {
        if (this.smm.isEnabled()) {
            super.render(context, mouseX, mouseY, delta);
            context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, 17, 16777215);
            context.drawTextWithShadow(this.textRenderer, ENTER_NAME_TEXT, this.width / 2 - 100 + 1, 33, 10526880);
            context.drawTextWithShadow(this.textRenderer, ENTER_IP_TEXT, this.width / 2 - 100 + 1, 74, 10526880);
            context.drawTextWithShadow(this.textRenderer, "Send Message", this.width / 2 - 100 + 1, 114, 10526880);
            this.serverNameField.render(context, mouseX, mouseY, delta);
            this.addressField.render(context, mouseX, mouseY, delta);
            this.messageField.render(context, mouseX, mouseY, delta);
            ci.cancel();
        }
    }

    @Inject(method = "resize(Lnet/minecraft/client/MinecraftClient;II)V", at = @At("HEAD"), cancellable = true)
    public void onResize(MinecraftClient client, int width, int height, CallbackInfo ci) {
        if (this.smm.isEnabled()) {
            String string = this.addressField.getText();
            String string2 = this.serverNameField.getText();
            String string3 = this.messageField.getText();
            this.init(client, width, height);
            this.addressField.setText(string);
            this.serverNameField.setText(string2);
            this.messageField.setText(string3);
            ci.cancel();
        }
    }

    @Inject(method = "addAndClose()V", at = @At("HEAD"))
    private void onAddAndClose(CallbackInfo ci) {
        if (this.smm.isEnabled()) {
            smm.updateValue(server, this.messageField.getText());
            smm.updateIdent(server, this.serverNameField.getText(), this.addressField.getText());
        }
    }
}
