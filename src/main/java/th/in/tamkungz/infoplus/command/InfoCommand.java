package th.in.tamkungz.infoplus.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import th.in.tamkungz.infoplus.api.SystemData;
import th.in.tamkungz.infoplus.api.SystemInfoAPI;
import static th.in.tamkungz.infoplus.InfoPlusMod.LOGGER;

public class InfoCommand {

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("info")
                .requires(source -> source.hasPermission(2))
                
                // /info system
                .then(Commands.literal("system")
                    .executes(ctx -> sendSystemInfo(ctx, ctx.getSource().getPlayerOrException()))
                    
                    // /info system <player>
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            try {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                return sendSystemInfo(ctx, player);
                            } catch (Exception e) {
                                ctx.getSource().sendFailure(Component.literal("§c[InfoPlus] §rError: " + e.getMessage()));
                                LOGGER.error("Error running /info system <player>", e);
                                return 0;
                            }
                        })
                    )
                )

                // /info all
                .then(Commands.literal("all")
                    .executes(ctx -> sendAllSystemInfo(ctx, ctx.getSource().getPlayerOrException()))
                    
                    // /info all <player>
                    .then(Commands.argument("player", EntityArgument.player())
                        .executes(ctx -> {
                            try {
                                ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
                                return sendAllSystemInfo(ctx, player);
                            } catch (Exception e) {
                                ctx.getSource().sendFailure(Component.literal("§c[InfoPlus] §rError: " + e.getMessage()));
                                LOGGER.error("Error running /info all <player>", e);
                                return 0;
                            }
                        })
                    )
                )
        );
    }

    private int sendSystemInfo(CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("§e[InfoPlus] §rFetching system info for " + targetPlayer.getName().getString() + "..."), false);

        new Thread(() -> {
            SystemData data = SystemInfoAPI.getSystemData();

            String cpuLine = String.format("§bCPU:§r %s\n Cores: %dP/%dT | %s | Load: %.1f%%",
                    data.cpuModel, data.cpuPhysical, data.cpuLogical, data.cpuFreqStr, data.cpuLoad);
            String ramLine = String.format("§bRAM:§r %s / %s (Free: %s)", data.ramUsed, data.ramTotal, data.ramAvail);
            String gpuLine = "§bGPU:§r " + data.gpuInfo;
            String osLine = "§bOS:§r " + data.osInfo;
            String javaLine = String.format("§bJava:§r %s (%s)\n §7%s", data.javaVersion, data.javaVendor, data.javaHome);
            String mcLine = String.format("§bMinecraft:§r %s | ModLoader: Forge %s", data.mcVersion, data.loaderVersion);

            context.getSource().getServer().execute(() -> {
                source.sendSuccess(() -> Component.literal("§e[InfoPlus] §rSystem info for " + targetPlayer.getName().getString()), false);
                source.sendSuccess(() -> Component.literal(cpuLine), false);
                source.sendSuccess(() -> Component.literal(ramLine), false);
                source.sendSuccess(() -> Component.literal(gpuLine), false);
                source.sendSuccess(() -> Component.literal(osLine), false);
                source.sendSuccess(() -> Component.literal(javaLine), false);
                source.sendSuccess(() -> Component.literal(mcLine), false);
            });
        }).start();

        return Command.SINGLE_SUCCESS;
    }

    private int sendAllSystemInfo(CommandContext<CommandSourceStack> context, ServerPlayer targetPlayer) {
        CommandSourceStack source = context.getSource();
        source.sendSuccess(() -> Component.literal("§e[InfoPlus] §rFetching full system info for " + targetPlayer.getName().getString() + "..."), false);

        new Thread(() -> {
            SystemData d = SystemInfoAPI.getSystemData();

            String[] lines = {
                "§bCPU:§r " + d.cpuModel,
                String.format(" Cores: %dP/%dT | %s | Load: %.1f%%", d.cpuPhysical, d.cpuLogical, d.cpuFreqStr, d.cpuLoad),
                String.format("§bRAM:§r %s / %s (Free: %s)", d.ramUsed, d.ramTotal, d.ramAvail),
                "§bGPU:§r " + d.gpuInfo,
                "§bMotherboard:§r " + d.motherboardVendor + " - " + d.motherboardModel,
                "§bBIOS:§r " + d.biosVersion,
                "§bDisk:§r " + d.diskInfo,
                "§bNetwork:§r " + d.netInfo,
                "§bBattery:§r " + d.batteryInfo,
                "§bOS:§r " + d.osInfo,
                "§bBoot Time:§r " + d.bootTime,
                "§bFile Systems:§r " + d.fileSystems,
                "§bJava:§r " + d.javaVersion + " (" + d.javaVendor + ")",
                "§7" + d.javaHome,
                "§bMinecraft:§r " + d.mcVersion + " | ModLoader: Forge " + d.loaderVersion
            };

            context.getSource().getServer().execute(() -> {
                source.sendSuccess(() -> Component.literal("§e[InfoPlus] §rFull system info for " + targetPlayer.getName().getString()), false);
                for (String line : lines) {
                    source.sendSuccess(() -> Component.literal(line), false);
                }
            });
        }).start();

        return Command.SINGLE_SUCCESS;
    }
}
