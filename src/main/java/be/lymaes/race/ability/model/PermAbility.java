package be.lymaes.race.ability.model;

import be.lymaes.race.Race;
import be.lymaes.race.ability.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermAbility implements CommandSender {

    private final String perm;

    public PermAbility(String permission) {
        this.perm = permission;
    }

    private PermissionAttachment getPermission(Player player) {
        for(PermissionAttachmentInfo info : player.getEffectivePermissions()) {
            PermissionAttachment attachment = info.getAttachment();

            if (attachment != null && attachment.getPlugin().equals(Race.getInstance())) {
                return attachment;
            }
        }
        return null;
    }

    @Override
    public void addPermission(Player player) {
        if(player.hasPermission(perm)) return;

        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            attachment = player.addAttachment(Race.getInstance());

        attachment.setPermission(perm, true);
        player.updateCommands();
    }

    @Override
    public void removePermission(Player player) {
        PermissionAttachment attachment = getPermission(player);
        if(attachment == null)
            return;

        attachment.setPermission(perm, false);
        player.updateCommands();
    }
}
