package win.winlocker.alt;

import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class AltManagerStore {
	private static boolean loaded;
	private static final List<AltAccount> accounts = new ArrayList<>();
	private static AltAccount selected;

	private AltManagerStore() {
	}

	public static List<AltAccount> getAccounts() {
		loadIfNeeded();
		return Collections.unmodifiableList(accounts);
	}

	public static void add(AltAccount acc) {
		loadIfNeeded();
		accounts.add(acc);
		save();
	}

	public static void remove(int idx) {
		loadIfNeeded();
		if (idx < 0 || idx >= accounts.size()) {
			return;
		}
		AltAccount removed = accounts.remove(idx);
		if (selected != null && selected.getName().equals(removed.getName())) {
			selected = null;
		}
		save();
	}

	public static AltAccount getSelected() {
		loadIfNeeded();
		return selected;
	}

	public static void select(int idx) {
		loadIfNeeded();
		if (idx < 0 || idx >= accounts.size()) {
			return;
		}
		selected = accounts.get(idx);
		save();

		// Update Minecraft session for offline mode
		try {
			Minecraft mc = Minecraft.getInstance();
			net.minecraft.client.User newUser = new net.minecraft.client.User(
					selected.getName(),
					java.util.UUID.nameUUIDFromBytes(("OfflinePlayer:" + selected.getName()).getBytes(java.nio.charset.StandardCharsets.UTF_8)),
					"",
					java.util.Optional.empty(),
					java.util.Optional.empty(),
					net.minecraft.client.User.Type.LEGACY
			);
			((win.winlocker.mixin.client.MinecraftAccessor) mc).setUser(newUser);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void loadIfNeeded() {
		if (loaded) {
			return;
		}
		loaded = true;
		Path file = getFilePath();
		if (!Files.exists(file)) {
			return;
		}
		try (BufferedReader br = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			accounts.clear();
			selected = null;

			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("selected:")) {
					String sel = line.substring("selected:".length()).trim();
					if (!sel.isEmpty()) {
						selected = new AltAccount(sel);
					}
					continue;
				}
				if (line.startsWith("acc:")) {
					String name = line.substring("acc:".length()).trim();
					if (!name.isEmpty()) {
						accounts.add(new AltAccount(name));
					}
				}
			}
		} catch (Exception ignored) {
		}
	}

	private static void save() {
		Path file = getFilePath();
		try {
			Files.createDirectories(file.getParent());
			try (BufferedWriter bw = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
				if (selected != null) {
					bw.write("selected: " + selected.getName());
					bw.newLine();
				}
				for (AltAccount acc : accounts) {
					bw.write("acc: " + acc.getName());
					bw.newLine();
				}
			}
		} catch (Exception ignored) {
		}
	}

	private static Path getFilePath() {
		Path dir = Minecraft.getInstance().gameDirectory.toPath();
		return dir.resolve("config").resolve("tloader_alts.txt");
	}
}
