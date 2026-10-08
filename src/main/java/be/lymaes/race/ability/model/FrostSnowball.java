package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.ability.ProjectileDamage;
import be.lymaes.race.ability.ProjectileLauncher;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.block.EntityBlockFormEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public final class FrostSnowball implements ProjectileLauncher {

    private static final int RADIUS = 3;
    private static final int RADIUS_SQ = RADIUS * RADIUS;

    private static final int MAX_SURFACE_SCAN = 6;

    private final Set<Snowball> tracked = new HashSet<>();
    private final List<Snowball> splashed = new ArrayList<>();

    private final Location scratch = new Location(null, 0, 0, 0);

    private BukkitTask task;

    // ------------------------------------------------------------------ Events

    @Override
    public void onLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;

        Snowball ball = (Snowball) e.getEntity();
        tracked.add(ball);

        if (task == null) {
            task = Bukkit.getScheduler().runTaskTimer(Race.getInstance(), this::tick, 1L, 1L);
        }
    }

    @Override
    public void onHit(ProjectileHitEvent e) {
        if (!tracked.remove(e.getEntity())) return;

        Snowball ball = (Snowball) e.getEntity();
        Block hit = e.getHitBlock();
        Block start = hit != null ? hit.getRelative(e.getHitBlockFace()) : ball.getLocation().getBlock();

        if (start.getType() == Material.WATER) {
            freezeFrom(start, ball);
        }
    }

    // ------------------------------------------------------------------ Suivi

    private void tick() {
        if (tracked.isEmpty()) {
            task.cancel();
            task = null;
            return;
        }

        for (Iterator<Snowball> it = tracked.iterator(); it.hasNext(); ) {
            Snowball ball = it.next();

            if (!ball.isValid()) {
                it.remove();
                continue;
            }

            ball.getLocation(scratch);
            if (scratch.getBlock().getType() == Material.WATER) {
                it.remove();
                splashed.add(ball);
            }
        }

        if (splashed.isEmpty()) return;

        for (Snowball ball : splashed) {
            freezeFrom(ball.getLocation(scratch).getBlock(), ball);
            ball.remove();
        }
        splashed.clear();
    }

    // ------------------------------------------------------------------ Gel

    private void freezeFrom(Block start, Snowball ball) {
        World world = start.getWorld();
        int cx = start.getX();
        int cz = start.getZ();

        int y = start.getY();
        int limit = y + MAX_SURFACE_SCAN;
        while (y < limit && world.getBlockAt(cx, y + 1, cz).getType() == Material.WATER) {
            y++;
        }

        ProjectileSource shooter = ball.getShooter();
        Entity cause = shooter instanceof Player ? (Player) shooter : ball;

        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (dx * dx + dz * dz > RADIUS_SQ) continue;

                Block block = world.getBlockAt(cx + dx, y, cz + dz);
                if (block.getType() == Material.WATER
                        && block.getRelative(BlockFace.UP).isEmpty()
                        && ((Levelled) block.getBlockData()).getLevel() == 0) {
                    freeze(block, cause);
                }
            }
        }
    }

    private void freeze(Block block, Entity cause) {
        BlockState state = block.getState();
        state.setType(Material.FROSTED_ICE);

        EntityBlockFormEvent event = new EntityBlockFormEvent(cause, block, state);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            state.update(true, true);
        }
    }

}