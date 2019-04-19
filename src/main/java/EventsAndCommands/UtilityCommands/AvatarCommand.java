package EventsAndCommands.UtilityCommands;

import EventsAndCommands.Categories;
import EventsAndCommands.MemberParser;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;

public class AvatarCommand extends Command {

    public AvatarCommand() {
        this.name = "getavatar";
        this.help = "Tag a user and get their avatar";
        this.category = Categories.Utility;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        Member memberToGetAvatarFrom = null;

        memberToGetAvatarFrom = MemberParser.getMemberFromArgs(commandEvent);

        if (memberToGetAvatarFrom != null) {

            final User user = memberToGetAvatarFrom.getUser();
            final String avatarUrl = user.getAvatarUrl();
            if(avatarUrl.isEmpty()){
                commandEvent.reply("This user does not have an avatar");
            }

            commandEvent.reply(avatarUrl + "?size=512");

        } else {
            commandEvent.reply("Could not find user");
        }

    }

    private Member getMemberFromArgs(CommandEvent commandEvent) {

        String args = commandEvent.getArgs();
        final Guild guild = commandEvent.getGuild();
        final Message message = commandEvent.getMessage();
        String name = args.split("#")[0];
        String identifier = args.split("#")[1];

        // Try by mention
        if (!message.getMentionedMembers().isEmpty()) {
            return message.getMentionedMembers().get(0);
        }

        // If they supplied a name + identifier
        if (name != null && identifier != null) {

            //Incase multiple members have the same name we look for the correct discriminator
            if (guild.getMembersByEffectiveName(name, true).size() > 1) {
                for (Member member : guild.getMembersByEffectiveName(name, true)) {
                    if (member.getUser().getDiscriminator().equals(identifier)) {
                        return member;
                    }
                }
            } else {
                return guild.getMembersByEffectiveName(name, true).get(0);
            }
        }

        // Try by name
        if (!guild.getMembersByEffectiveName(args, true).isEmpty()) {
            return guild.getMembersByEffectiveName(args, true).get(0);
        }

        // try by ID
        if (guild.getMemberById(args) != null) {
            return guild.getMemberById(args);
        }

        // No user found, so we return null
        return null;

    }
}
