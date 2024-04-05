package com.guhao.fix_guard;

import org.spongepowered.asm.mixin.Mixins;
import org.spongepowered.asm.mixin.connect.IMixinConnector;

public class MixinConnector implements IMixinConnector {
	@Override public void connect(){
		Mixins.addConfiguration("fix_guard.mixins.json");
	}
}
