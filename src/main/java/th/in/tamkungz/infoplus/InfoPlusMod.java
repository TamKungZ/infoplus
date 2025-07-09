package th.in.tamkungz.infoplus;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import th.in.tamkungz.infoplus.command.InfoCommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("infoplus")
public class InfoPlusMod {
    public static final String MOD_ID = "infoplus";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public InfoPlusMod() {
        MinecraftForge.EVENT_BUS.register(new InfoCommand());
        LOGGER.info("InfoPlus Ready Up");
    }
}