package be.lymaes.race.ability.model;

import be.lymaes.race.RaceProfile;
import be.lymaes.race.ability.Interact;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Sapling;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class FastGrowing implements Interact {

    @Override
    public void onInteract(PlayerInteractEvent e, Player player, RaceProfile profile) {
        if(!player.isSneaking()) return;
        if (e.getHand() != EquipmentSlot.HAND) return;
        if(e.getItem() != null || e.getMaterial() != Material.AIR) return;
        if(e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        Block block = e.getClickedBlock();
        if(block == null) return;
        BlockData data = block.getBlockData();
        if(data instanceof Ageable ageable) {

            ageable.setAge(ageable.getMaximumAge());
            block.setBlockData(ageable);
        }
        else if(data instanceof Sapling || block.getType() == Material.MANGROVE_PROPAGULE) {

            if (!growTree(block)) {
                player.sendMessage("Il n'y a pas assez de place pour faire pousser un arbre ici...");
            }
        }
    }

    public static boolean growTree(Block block) {
        Material sapling = block.getType();

        // Essences qui peuvent pousser en 2x2 (arbre géant)
        TreeType megaType = switch (sapling) {
            case DARK_OAK_SAPLING -> TreeType.DARK_OAK;
            case JUNGLE_SAPLING   -> TreeType.JUNGLE;
            case SPRUCE_SAPLING   -> TreeType.MEGA_REDWOOD;
            default -> null;
        };

        if (megaType != null) {
            Block[] square = findSquare(block, sapling);
            if (square != null) {
                // Origine = coin le plus bas en x/z du carré
                Block origin = square[0];
                for (Block b : square) {
                    if (b.getX() < origin.getX() || b.getZ() < origin.getZ()) origin = b;
                }
                if (tryGenerate(square, origin.getLocation(), megaType, sapling)) return true;
            }
        }

        // Arbre simple
        TreeType single = switch (sapling) {
            case OAK_SAPLING      -> TreeType.TREE;
            case BIRCH_SAPLING    -> TreeType.BIRCH;
            case SPRUCE_SAPLING   -> TreeType.REDWOOD;
            case JUNGLE_SAPLING   -> TreeType.SMALL_JUNGLE;
            case ACACIA_SAPLING   -> TreeType.ACACIA;
            case CHERRY_SAPLING   -> TreeType.CHERRY;
            case MANGROVE_PROPAGULE -> TreeType.MANGROVE;
            case AZALEA, FLOWERING_AZALEA -> TreeType.AZALEA;
            default -> null; // dark oak seul : impossible, il faut 2x2
        };
        if (single == null) return false;

        return tryGenerate(new Block[]{block}, block.getLocation(), single, sapling);
    }

    /** Cherche un carré 2x2 de la même pousse contenant `block`. */
    private static Block[] findSquare(Block block, Material type) {
        for (int dx = -1; dx <= 0; dx++) {
            for (int dz = -1; dz <= 0; dz++) {
                Block a = block.getRelative(dx, 0, dz);
                Block b = a.getRelative(1, 0, 0);
                Block c = a.getRelative(0, 0, 1);
                Block d = a.getRelative(1, 0, 1);
                if (a.getType() == type && b.getType() == type
                        && c.getType() == type && d.getType() == type) {
                    return new Block[]{a, b, c, d};
                }
            }
        }
        return null;
    }

    /** Retire les pousses, tente de générer, restaure si échec. */
    private static boolean tryGenerate(Block[] blocks, Location loc, TreeType type, Material sapling) {
        BlockData[] backup = new BlockData[blocks.length];
        for (int i = 0; i < blocks.length; i++) {
            backup[i] = blocks[i].getBlockData();
            blocks[i].setType(Material.AIR, false);
        }

        if (loc.getWorld().generateTree(loc, type)) return true;

        for (int i = 0; i < blocks.length; i++) {
            blocks[i].setBlockData(backup[i], false);
        }
        return false;
    }

}
