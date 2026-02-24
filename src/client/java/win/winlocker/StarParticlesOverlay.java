package win.winlocker;

import win.winlocker.module.ModuleManager;
import win.winlocker.module.Module;
import win.winlocker.module.render.ParticlesModule;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class StarParticlesOverlay {
	private static final ResourceLocation STAR_TEXTURE = ResourceLocation.withDefaultNamespace("textures/item/nether_star.png");
	private static final int STAR_TEX_SIZE = 16;

	private final Random random = new Random();
	private final List<StarParticle> particles = new ArrayList<>();
	private long lastFrameNs;

	public void render(GuiGraphics graphics) {
        if (!ModuleManager.modules.isEmpty()) {
            Module m = ModuleManager.getModule(ParticlesModule.class);
            if (m == null || !m.isEnabled()) return;
        } else {
            return;
        }
        
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		update();
		renderInternal(graphics);
	}

	private void update() {
		long now = System.nanoTime();
		if (lastFrameNs == 0L) {
			lastFrameNs = now;
			return;
		}

		double dt = (now - lastFrameNs) / 1_000_000_000.0;
		lastFrameNs = now;
		if (dt <= 0.0 || dt > 0.25) {
			dt = 1.0 / 60.0;
		}

		ParticlesModule m = (ParticlesModule) ModuleManager.getModule(ParticlesModule.class);
		int target = (m != null && m.isEnabled()) ? (int) ParticlesModule.countSetting.get() : 0;
		while (particles.size() < target) {
			particles.add(spawnParticle(true));
		}
		while (particles.size() > target) {
			particles.remove(particles.size() - 1);
		}

		int w = Minecraft.getInstance().getWindow().getGuiScaledWidth();
		int h = Minecraft.getInstance().getWindow().getGuiScaledHeight();
		for (int i = 0; i < particles.size(); i++) {
			StarParticle p = particles.get(i);
			p.y += p.speed * dt;
			p.x += p.drift * dt;
			p.rot += p.rotSpeed * dt;
			if (p.y > h + 20) {
				particles.set(i, spawnParticle(false));
			}
			if (p.x < -40) {
				p.x = w + 40;
			} else if (p.x > w + 40) {
				p.x = -40;
			}
		}
	}

	private StarParticle spawnParticle(boolean randomY) {
		int w = Math.max(1, Minecraft.getInstance().getWindow().getGuiScaledWidth());
		int h = Math.max(1, Minecraft.getInstance().getWindow().getGuiScaledHeight());
		double x = random.nextDouble() * w;
		double y = randomY ? random.nextDouble() * h : -random.nextDouble() * 40.0;
		double speed = 35.0 + random.nextDouble() * 80.0;
		double drift = -10.0 + random.nextDouble() * 20.0;
		float size = 0.7f + random.nextFloat() * 1.1f;
		double rot = random.nextDouble() * Math.PI * 2.0;
		double rotSpeed = (-2.0 + random.nextDouble() * 4.0);
		int alpha = 110 + random.nextInt(110);
		return new StarParticle(x, y, speed, drift, size, rot, rotSpeed, alpha);
	}

	private void renderInternal(GuiGraphics graphics) {
		ParticlesModule m = (ParticlesModule) ModuleManager.getModule(ParticlesModule.class);
		if (m == null || !m.isEnabled() || particles.isEmpty()) {
			return;
		}

		RenderSystem.enableBlend();
		for (StarParticle p : particles) {
			graphics.pose().pushPose();
			graphics.pose().translate(p.x, p.y, 0.0);
			graphics.pose().mulPose(com.mojang.math.Axis.ZP.rotation((float) p.rot));
			graphics.pose().scale(p.size, p.size, 1.0f);
			RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, (p.alpha & 0xFF) / 255.0f);
			graphics.blit(RenderType::guiTextured, STAR_TEXTURE, -8, -8, 0.0f, 0.0f, STAR_TEX_SIZE, STAR_TEX_SIZE, STAR_TEX_SIZE, STAR_TEX_SIZE);
			graphics.pose().popPose();
		}
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
	}

	private static final class StarParticle {
		double x;
		double y;
		double speed;
		double drift;
		float size;
		double rot;
		double rotSpeed;
		int alpha;

		private StarParticle(double x, double y, double speed, double drift, float size, double rot, double rotSpeed, int alpha) {
			this.x = x;
			this.y = y;
			this.speed = speed;
			this.drift = drift;
			this.size = size;
			this.rot = rot;
			this.rotSpeed = rotSpeed;
			this.alpha = alpha;
		}
	}
}
