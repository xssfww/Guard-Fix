package com.guhao.fix_guard.mixin;

import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import yesman.epicfight.skill.SkillContainer;
import yesman.epicfight.skill.SkillDataManager;
import yesman.epicfight.skill.guard.GuardSkill;
import yesman.epicfight.skill.guard.ParryingSkill;
import yesman.epicfight.world.capabilities.item.CapabilityItem;
import yesman.epicfight.world.entity.eventlistener.PlayerEventListener;
@Mixin(value = {ParryingSkill.class}, remap = false)
public abstract class GuardMixin extends GuardSkill {
    @Final
    @Shadow
    private static SkillDataManager.SkillDataKey<Integer> LAST_ACTIVE;//将未初始化的data复制过来
    
    @Final
    @Shadow
    public static SkillDataManager.SkillDataKey<Integer> PARRY_MOTION_COUNTER;//同上
    
    public GuardMixin(Builder builder) {
        super(builder);
    }
    
    @Inject(at = @At("HEAD"), method = "onInitiate", cancellable = true)
    public void InjectOnInitiate(SkillContainer container, CallbackInfo ci) {
        ci.cancel();
        super.onInitiate(container);
        container.getDataManager().registerData(LAST_ACTIVE);//byd作者居然没初始化Data
        container.getDataManager().registerData(PARRY_MOTION_COUNTER);//如果用上一代代码，下行的服务器物品使用监听器会因为得不到data而崩溃
        container.getExecuter().getEventListener().addEventListener(PlayerEventListener.EventType.SERVER_ITEM_USE_EVENT, EVENT_UUID, (event) -> {
            CapabilityItem itemCapability = event.getPlayerPatch().getHoldingItemCapability(InteractionHand.MAIN_HAND);
            if (this.isHoldingWeaponAvailable(event.getPlayerPatch(), itemCapability, BlockType.GUARD) && this.isExecutableState(event.getPlayerPatch())) {
                event.getPlayerPatch().getOriginal().startUsingItem(InteractionHand.MAIN_HAND);
            }
            int lastActive = container.getDataManager().getDataValue(LAST_ACTIVE);
            if (event.getPlayerPatch().getOriginal().tickCount - lastActive > 0) {
                container.getDataManager().setData(LAST_ACTIVE, event.getPlayerPatch().getOriginal().tickCount);
            }
        });
    }
    
}
