package com.guhao.fix_guard.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.api.animation.types.StaticAnimation;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataKey;
import yesman.epicfight.skill.SkillDataKeys;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;
import yesman.epicfight.world.capabilities.entitypatch.player.ServerPlayerPatch;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;

import javax.annotation.Nullable;


@Mixin(value = ParryingSkill.class, priority = 5000, remap = false)
public abstract class GuardMixin extends GuardSkill {

    @Shadow
    @Nullable
    protected abstract StaticAnimation getGuardMotion(PlayerPatch<?> playerpatch, CapabilityItem itemCapability, BlockType blockType);

    public GuardMixin(Builder builder) {
        super(builder);
    }

    @Inject(at = @At("HEAD"), method = "onInitiate", cancellable = true)
    public void mixin_guard(SkillContainer container, CallbackInfo ci) {
        ci.cancel();
        super.onInitiate(container);
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            CapabilityItem itemCapability = ((ServerPlayerPatch)event.getPlayerPatch()).getHoldingItemCapability(InteractionHand.MAIN_HAND);
            if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
                ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).startUsingItem(InteractionHand.MAIN_HAND);
            }

            int lastActive = (Integer)container.getDataManager().getDataValue((SkillDataKey) SkillDataKeys.LAST_ACTIVE.get());
            if (((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).tickCount - lastActive >= 0) {
                container.getDataManager().setData((SkillDataKey)SkillDataKeys.LAST_ACTIVE.get(), ((ServerPlayer)((ServerPlayerPatch)event.getPlayerPatch()).getOriginal()).tickCount);
            }

        });
    }
}
