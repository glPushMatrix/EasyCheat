package ru.easycheat;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.JOptionPane;

import org.apache.commons.lang3.ArrayUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.MovementInput;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.StringUtils;
import net.minecraft.util.Timer;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Text;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

@Mod(modid = Main.ModId, name = Main.ModName, version = Main.Version, acceptedMinecraftVersions = Main.AcpMc, modLanguage = Main.ModLang, canBeDeactivated = true)
public class Main implements Runnable {
	
	private HashMap props = new HashMap();
	private List friends = new ArrayList();
	private GuiScreen gui = new GuiScreen();
	private HashMap blocks = new HashMap();
	private RenderItem ri = new RenderItem();
	private Timer timer;
	private static Main instance;
	
	protected AtomicBoolean xs1 = new AtomicBoolean();
	protected AtomicBoolean xs2 = new AtomicBoolean();
	protected AtomicBoolean xs3 = new AtomicBoolean(true);
	
	protected List chestsPool = new ArrayList();
	
	protected static final String ModId = "easycheat";
	protected static final String ModName = "EasyCheat";
	protected static final String Version = "1.0";
	protected static final String AcpMc = "1.7.10";
	protected static final String ModLang = "java";
	private String Anime = "\r\n:::::::::::::::::::::::::::::::::JNI4Dead#7193:::::::::::::::::::::::::::::::::\r\n" +
			"               \\,_     __,----.\r\n" + 
			"           ,_,-/_   _,' _,'-\"\"\"\"\"-.\r\n" + 
			"          ( : : ,),' _,'_,--\"\"\"\"\"---.__\r\n" + 
			"         _,\\ ::',',-'  '_,---\"\"\"\"\"\"---.\\                                       ____\r\n" + 
			"       ,': :\\:\"'       ' _,''\" ___,---.__                              _____,-'. . \\ \r\n" + 
			"      J : : /          ,'__,--'   _____  \\                        _,--::::::: . . . `.\r\n" + 
			"      L: : /           ,'___,----' ___ `--.                     ,::::::::'   . . . . J\r\n" + 
			"      J :-'   .       --'          --.`. ) \\                 _,:::::'     . . . . . .L \r\n" + 
			"      ,' :   : .  ___----.__ --.___ `.`.\\  '               ,::::'      . . . . . . .J\r\n" + 
			"     / : :  : :  ,--::,,   ,`.--.`.`-.`.)               _,::'       . ..::::. . . . L\r\n" + 
			"     L : ':.::   L::'  ``:/   `. `.\\  `.\\            _,:::'      . ..:::'. . . . . / \r\n" + 
			"    J  .:.':::   F::     J `.   `. \\\\   \\\\         ,::::'     . ..::' . .__,-' ..:J \r\n" + 
			"    L,::::::::.  J::     F  ,\"\"\\  \\ LL   \\L     ,-:::'    . . .::' ....:'...  ..::L\r\n" + 
			" ,-,.''''''''':.  L:    J  J ):.`. `JF    '__,-::::.   . . . .....:;:-'.'    ..::/\r\n" + 
			"/ . ;;;;;;;''''::.);     \\ J/CD:\\,F  L:-.-:::::'. . . .  ..:;;;:--'.:''   . ..::/\r\n" + 
			"L:. _     '''\\' (:'         L:;:JP    .::\\:::::. . . .:;;:-'   ,::''     . ..::/\r\n" + 
			"|:.' _,       `._`----.______  \"'      .':L:::::::;;:-'       /:'       . ..::'\r\n" + 
			"J: .::|      .`. \"-------\"\"      ._,    .:|::;;;-'          ,:'      . . .::/\r\n" + 
			"-L:_::J ,    \\`.`.                 (     :J-'             ,:'       . . .::'\r\n" + 
			"::,-`.J J J L\\\\ `.`.   ,- .         ,'\\  .:L             /:      . . ..::/\r\n" + 
			" ; ; ,J  L L \\``. `-;,' ,,oo        \"\"'   :J           ,:'      . . ..::'\r\n" + 
			" :; :  L J\\ \\``.``._/_ =dP                ':L         /\"\"\"\"-.  . . .::/ \r\n" + 
			"':::..,J`.L \\`.\\`._J  =dQ                 .:F       ,'       `. ..:::'\r\n" + 
			"':::    \\\\`\\``.`._ F- =dP. . . . . . . . ;:'       /           L.::/\r\n" + 
			"' ; :   `.\\\\\\_\\`.,-._  ::::::::::::::;:-'        ,'            J::/\r\n" + 
			" ;:  `./   `.`.--`._ `-:;:--.;;;;;--'::\\       .--.            |\\/\r\n" + 
			": : __J         J::.`-----@'\\::.. . .'::\\    ,::'. \\           F L\r\n" + 
			"__::::\\         |:::: J      \\::.. . .'::\\  /:: .'.:L          , J\r\n" + 
			"J :::::L        F:::: |       L::.. . .'::\\/:: .  :.J          L-'\r\n" + 
			" L:::::J       J::::: F       J:::.. .  .::\\: .  .: .L         F\r\n" + 
			" |::::::L      |'':::J      ,-'\"\"\"`-. ..  . ..  .::. J        /\r\n" + 
			" J::'::.J      F: :::|    ,'         `. . .  ` .::. . L      /\r\n" + 
			"  L: '::.L    J .  ::F   /             `.......:'. . .J     J\r\n" + 
			"  |   :: |    F :  :J   J              J \\''''''. . . .\\    J \r\n" + 
			"  J  ,.::J   J  .  :|   | |             \\ \\. . . . . . .`---.\\__ \r\n" + 
			"   L,  :::L  F :. .:L   | J              L \\. . . . `-.: .      `--.__\r\n" + 
			"   |  .:::| /. ::.:J    |  L             J  \\. . .'. ':`-..           `-._\r\n" + 
			"   |   ::;J/: :::::F    |  |              \\  \\       '. . `. .         . dMb \r\n" + 
			"   |  ,::;:L: ::::J     |  |               L  L        . . .`..         .`QJ\r\n" + 
			"   |   ::;:| :::::F     J  J               J   L      . . . . \\:.      . . |\r\n" + 
			"   |   :;::| ::::J       L  L               L  J     . . . . . \\:.. . . . .|\r\n" + 
			"   F  ,';::| ::::F       J  J               J  F. . .,ooo.. . . L::. . . . F\r\n" + 
			"  J  ,  ;::|: . J         L  \\               L/. . ..dM()b . . .J:: . . . J\r\n" + 
			"  | ,  ;:::J . .F         J   \\         __...J: . . .`QMP'. . . J::. . ../\r\n" + 
			"  |    ;::::L ./           \\   L    _,-'   .::L: . . . . . . . .F:: . .:/\r\n" + 
			"  |    ;::::|./             \\  J  ,:::.     ::J:. . . . . . . :/::'. ::'\r\n" + 
			"  |   ;:::::|/               L  \\(\\:::       ::L:. . . . . .::/::::::'\r\n" + 
			"  F   ;::.::|                J   `\"L:::      ::J:::.....:::::'::::;'\\\r\n" + 
			" J    ;:::::|                 \\    J:::.     .::L:::::::::,,._,,\"    L\r\n" + 
			" |   ;::.:::F                  \\    \\:::.     .:J`,:,,_,,\"'\"\"\"\" . ; .J\r\n" + 
			" L   ;::::.J                    L    L::       .:L\"\"\"\"'  .;. .;. ..; J\r\n" + 
			"J.   ;:.:::|                    J    J::.       :J. . ; . .;. .;.  ;.L\r\n" + 
			"L   ;::::::|                     \\    \\::.  . . .:L. . ; . ; . ; .;./\r\n" + 
			" .  ;:.::.:F                      \\    L::.  . . :J . .;. ;/\\ ; ./\\(\r\n" + 
			".  ;::::::J                        \\   J::   :.: .:\\ .;. ; \\/;/\\ \\F\r\n" + 
			" . ;.:::::|                         \\   \\::  ::.. .:\\;  ./\\/\\ \\ \\/\r\n" + 
			". ;::::.::|                          \\   \\:: .::.  .:\\ \\ \\   \\/  |\r\n" + 
			" ;:.::::::L                           L   L::. ::.. .:`.\\/       |\r\n" + 
			";:::::::;J                            |   J::   ::.  .::\\        J\r\n" + 
			":.:::.::;|                            J    L::   ::.  . :`.      |\r\n" + 
			":::::::;.|                             L   J::::.  '   . ::\\     F\r\n" + 
			"::::::;. L                             J    \\::::.      . .:\\   J\r\n" + 
			":::.::; J                            ,'      \\::::.      . .:\\  |\r\n" + 
			":::::; .|                         _,'         `.:::::     . ::L F\r\n" + 
			"::::; . L                       ,'    ,         \\::::.     .::JJ\r\n" + 
			".:::;. J                      ,'    ,'J    __,   `.:::.     .::\\\r\n" + 
			":::;. .|                    ,'    ,'   L,-','____, \\:::..    .::\\\r\n" + 
			"::;. . L                  ,'    ,'       ,'|        `.::..    .::\\ \r\n" + 
			"::; . J                 ,'             ,'  |          \\::..    .::\\ \r\n" + 
			":; . .|                ;'--'M-K\"------.____`           `.::.    .::\\\r\n" + 
			"; . . L              ,'                .   `--._         \\::.    .::\\\r\n" + 
			";. . J              /        ,          . . .:::`--.     /`.:.    .::\\\r\n" + 
			". . .L             /        /            . . .::::::`-.  L  \\. .   .::\\\r\n" + 
			" . .J             J        J            . . . . .::::::`.J   \\. .   .::\\\r\n" + 
			". . L             L       .F             . . . . .::::::\\`    \\. .   .::\\\r\n" + 
			" . J             J        J             . . . . . .::::::\\     \\. .   . .L\r\n" + 
			". .L             F        F. .           . . . . . :::::::\\     \\:.  ..::J\r\n" + 
			" .J              F      . J . .         . . . . . .::::::::\\     ):  ::::J\r\n" + 
			". L              L . . . .:F . .         . . . . . ::: . .::\\   /:  ::::.L\r\n" + 
			" /               J  . . . .J. . .       . . . . . .:: . . .::\\ /.  ::::: L\r\n" + 
			"/                 \\. . . . :\\. . . . . . . . . . .:: . . . .::J.  ::::::.L\r\n" + 
			"                   L. . . .::\\. . . . . . . . . .:: . . . . .:L  ::::::: |\r\n" + 
			"                   L ....:::::`.::...... . . . : .       . . :L. :::::::.|\r\n" + 
			"                  /:'. '':::::::`.::::::. ..::. .         . . J :::::: . |\r\n" + 
			"                 J::  . . .:::::::`.:::::::::.             . .,'::::::: .| \r\n" + 
			"                 L:      . . .::::::\\:::::::. .             ,' .'/ ::.:: J \r\n" + 
			"                /:        . . .::::::\\:::::: . .          ,' .:'/ ::_ ::.J \r\n" + 
			"               J::           . . :::::L:::::. . .       ,'.:''./ ./ / :: :L\r\n" + 
			"               L:           . . .:::::J::::: . . .      '''  ./ ./ / :J :J\r\n" + 
			"              J::          . . . ::::::\\::::: . . .         ,'.:' / :/L :L\r\n" + 
			"              |::           . . .:::::J L::::. . . .      ,'.:'  / :/J :J\r\n" + 
			"              L:           . . . :::::L J:::::. . . .    (_:'   / :/ L :L\r\n" + 
			"             J::          . . . :::::J   \\:::::. . .           / :/ / :/\r\n" + 
			"             |::           . . .:::::L    \\:::: . . .        .'.:' J :/ \r\n" + 
			"             |::          . . . ::::J      \\:::::. . .      I_:'   L:/ \r\n" + 
			"             L:          . . . .::::L       \\:::: . . .           J JL \r\n" + 
			"            J::           . . .::::J         `.::: . .            L_LJ\r\n" + 
			"            |::          . . . ::::L           \\::::. .           . ::L\r\n" + 
			"            |::           . . .:::J             `.:: . .           . :|\r\n" + 
			"            |::          . . .::::L               \\:::.           . .:J\r\n" + 
			"\r\n:::::::::::::::::::::::::::::::::JNI4Dead#7193:::::::::::::::::::::::::::::::::\r\n";
	
	private boolean xredit = false;
	
	private long lasttime = 0L;
	private long xraytime = 0L;
	protected int displayListESP = -1;
	private int tick = 0;
	private int drawy = 0;
	private float nextSpace;
	private int ticks;
	private int tempid;
	private int tempcolor;
	private int r;
	private int g;
	private int b;
	
	private boolean blockHit = false;
	
	public Main() {	
		this.load();
		MinecraftForge.EVENT_BUS.register(this);
		try {
			this.timer = (Timer)this.getField(Minecraft.class, "timer:field_71428_T:S").get(this.mc());
		} catch (Exception ex) {
			;
		}
		instance = this;
		System.out.println(Anime);
		this.showMessage("[" + ModName +"] Successfully loaded! Enjoy the game!");
	}
	
	public static void main() {
		new ru.easycheat.Main();
	}
	
	private void showMessage(String string) {
		if (SystemTray.isSupported()) {
			try {
				SystemTray tray = SystemTray.getSystemTray();
				Image image = Toolkit.getDefaultToolkit().getImage("images/tray.gif");
				
				TrayIcon trayIcon = new TrayIcon(image);
				tray.add(trayIcon);
				
				trayIcon.displayMessage(ModName + " " + Version, string,
						TrayIcon.MessageType.INFO);
			} catch (AWTException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static Main getInstance() {
		return instance;
	}
	
	private static FontRenderer fontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}
	
	private static Minecraft mc() {
		return Minecraft.getMinecraft();
	}
	
	@Override public void run() {
		this.xs1.set(false);
		
		while(!this.xs3.get()) {
			;
		}
		this.chestsPool.clear();
		int radius = this.getInt("XrayRadius");
		boolean besp = this.getBoolean("BlockESP");
		int i;
		int j;
		int k;
		int bId;
		if(besp) {
			for(i = 0; i < radius * 2; ++i) {
				for(j = 0; j < radius * 2; ++j) {
					for(k = 0; k < 128; ++k) {
						bId = Block.getIdFromBlock(this.mc().theWorld.getBlock(i - radius + (int)this.mc().thePlayer.posX, k, j - radius + (int)this.mc().thePlayer.posZ));
						if(besp && this.blocks.containsKey(Integer.valueOf(bId))) {
							this.chestsPool.add(Vec3.createVectorHelper((double)(i - radius + (int)this.mc().thePlayer.posX), (double)k, (double)(j - radius + (int)this.mc().thePlayer.posZ)));
						}
					}
				}
			}
		}
		this.xs1.set(true);
		this.xs2.set(false);
		this.xs2.set(true);
	}
	
	@SubscribeEvent public void onRender3D(RenderWorldLastEvent event) {
		this.onRenderWorld();
	}
	
	@SubscribeEvent public void onRender2D(Text event) {
		this.mc().entityRenderer.setupOverlayRendering();
		int height = event.resolution.getScaledHeight();
		int width = event.resolution.getScaledWidth();
		this.onRender(width, height);
	}
	
	@SubscribeEvent public void onUpdate(LivingUpdateEvent event) {
		if (event.entity == mc().thePlayer) {
			this.onPlayerUpdateStart();
			this.onPlayerUpdateFinish();
		}
	}
	
	private void onRender(int width, int height) {
		if(this.displayListESP == -1) {
			this.displayListESP = GL11.glGenLists(1);
		}
		if(this.checkKey(41) && this.mc().currentScreen == null && !this.mc().gameSettings.showDebugInfo) {
			this.mc().displayGuiScreen(this.gui);
		}
		
		String name;
		Iterator var11;
		if(Keyboard.isKeyDown(15)) {
			this.drawy = 40;
			if(this.friends.size() > 0) {
				this.drawStringShadow("§eFriends: ", 4, 0, "", (String)null, 0, 0);
				this.drawy += 12;
				Iterator var8 = this.friends.iterator();
				
				while(var8.hasNext()) {
					name = (String)var8.next();
					boolean var4 = false;
					var11 = this.mc().theWorld.playerEntities.iterator();
					
					while(var11.hasNext()) {
						Object var3 = var11.next();
						if(StringUtils.stripControlCodes(((EntityPlayer)var3).getCommandSenderName()).equals(name)) {
							var4 = true;
						}
					}
					this.drawStringShadow((var4?"§a":"§4") + name, 4, 0, "", (String)null, 0, 0);
				}
			}
		} else if(this.getBoolean("Hide") && this.mc().currentScreen != this.gui) {
			
		} else {
			GL11.glTranslatef(0.0F, 0.0F, 10.0F);
			this.drawy = 6;
			this.drawStringShadow(this.mc().currentScreen != this.gui ? ModName + "§a " + Version + " §f[§c" + "Coded by LeForce" + "§f]" : "§bMovement", 4, 0, "[" + this.ModName + "]", "_main", 0, 0);
			this.drawy += 12;
			this.drawStringShadow("§fSpeedHack", 4, 0, "Увеличивает скорость передвижения игрока.", "SpeedHack", 0, Keyboard.KEY_R);
			this.drawStringShadowV("§fSpeed", 4, 0, "Скорость передвижения игрока в режиме SpeedHack.", "Rapidity", 1, 20);
			this.drawStringShadow("§fNoFall", 4, 0, "Убирает урон при падениях с большой высоты.", "NoFall", 0, Keyboard.KEY_N);
			this.drawStringShadow("§fStep", 4, 0, "Прохождение через препятствия без прыжков.", "Step", 0, Keyboard.KEY_B);
			this.drawStringShadowV("§fHeight", 4, 0, "Высота прохождения через препятствия в режиме Step.", "StepHeight", 1, 100);
			this.drawStringShadow("§fFly", 4, 0, "Позволяет летать.", "Fly", 0, Keyboard.KEY_F);
			this.drawStringShadowV("§fSpeed", 4, 0, "Скорость передвижения игрока врежиме Fly.", "FlySpeed", 1, 20);
			this.drawStringShadow("§fHideOverlay", 4, 0, "Скрывает HUD.", "Hide", 0, Keyboard.KEY_H);
			
			this.drawy = 6;
			this.drawStringShadow("§bRender", width / 3, 1, null, null, 0, true);
			this.drawy += 12;
			this.drawStringShadow("§fPlayerESP", width / 3, 1, "Обводит игроков цветным квадратом.", "PlayerESP", Keyboard.KEY_Y, true);
			this.drawStringShadow("§fBlockESP", width / 3, 1, "Ищет и обводит нужные вам блоки.", "BlockESP", Keyboard.KEY_X, true);
			this.drawStringShadowV("§fRadius", width / 3, 1, "Радиус поиска блоков в режиме BlockESP.", "XrayRadius", 56, 200);
			
			this.drawy = 6;
			this.drawStringShadow("§bOther", width / 3 + width / 3, 1, null, null, 0, true);
			this.drawy += 12;
			this.drawStringShadow("§fFullBright", width / 3 + width / 3, 1, "Позволяет видеть в темноте как при дневном свете.", "FullBright", Keyboard.KEY_L, true);
			
			this.drawy = 6;
			this.drawStringShadow("§bCombat", width - 6, 2, null, null, 0, false);
			this.drawy += 12;
			this.drawStringShadow("§fNONE", width - 6, 2, null, null, 0, true);
	        
	        int var1;
			if(this.mc().currentScreen == this.gui && !this.xredit) {
				GL11.glPushMatrix();
				GL11.glTranslatef(width / 2, height / 2, 0.0f);
				GL11.glScalef(4.0f, 4.0f, 0.0f);
				this.drawTextWithShadow(ModName, (int) (0 - fontRenderer().getStringWidth(ModName) / 2), (int) 5);
				GL11.glScalef(0.5f, 0.5f, 0.0f);
				this.drawTextWithShadow("§f[§c" + "Coded by LeForce" + "§f]", (int) (0 - fontRenderer().getStringWidth("§f[§c" + "Coded by LeForce" + "§f]") / 2), (int) 30);
				GL11.glPopMatrix();
				this.drawStringShadow("§fAdd a friend to your friends list §f(§aCtrl+F on the player§f)", 4, height - 12, 0, null, null);
				this.drawStringShadow("§fTurn on Hide Overlay, to add friends or blocks xray.", 4, height - 24, 0, null, null);
				this.drawStringShadow("§fAdd block to BlockESP §f(§aCtrl+X by block§f)", width - 6, height - 12, 2, null, null);
				GL11.glTranslatef(0.0F, 0.0F, -20.0F);
			}
			GL11.glTranslatef(0.0F, 0.0F, -20.0F);
		}
		int var1;
		if(this.mc().currentScreen == this.gui) {
			var1 = width - 6;
			int var2 = height - 44;
			if(this.xredit) {
				this.drawRgb(var1 - 150, var2, 100, 10, -16777216, -16776961, "b");
				var2 -= 13;
				this.drawRgb(var1 - 150, var2, 100, 10, -16777216, -16711936, "g");
				var2 -= 13;
				this.drawRgb(var1 - 150, var2, 100, 10, -16777216, -65536, "r");
				this.tempcolor = this.getRGB(this.r, this.g, this.b);
				this.color(var1 - 45, var2, 48, 36, this.tempcolor);
			} else {
				var11 = ((HashMap)this.blocks.clone()).entrySet().iterator();
				while(var11.hasNext()) {
					Entry var111 = (Entry)var11.next();
					var1 -= this.drawBlock(var111, var1, var2);
					--var1;
					if((double)var1 < (double)width * 0.6D) {
						var2 -= 17;
						var1 = width - 6;
					}
				}
			}
		}
		if(this.mc().currentScreen == null && this.lasttime <= System.currentTimeMillis()) {
			if(Keyboard.isKeyDown(29)) {
				if(this.checkKey(33)) {
					if(this.mc().objectMouseOver != null && this.mc().objectMouseOver.entityHit != null && this.mc().objectMouseOver.entityHit instanceof EntityPlayer) {
						EntityPlayer var101 = (EntityPlayer)this.mc().objectMouseOver.entityHit;
						name = StringUtils.stripControlCodes(var101.getCommandSenderName());
						if(this.friends.contains(name)) {
							this.friends.remove(name);
							this.saveFriendList();
						} else {
							this.friends.add(name);
							this.saveFriendList();
						}
					}
					return;
				}
				if(this.checkKey(45)) {
					if(this.mc().objectMouseOver != null && this.mc().objectMouseOver.typeOfHit == MovingObjectType.BLOCK) {
						var1 = Block.getIdFromBlock(this.mc().theWorld.getBlock(this.mc().objectMouseOver.blockX, this.mc().objectMouseOver.blockY, this.mc().objectMouseOver.blockZ));
						if(var1 > 0) {
							if(!this.mc().gameSettings.showDebugInfo) {
								this.mc().displayGuiScreen(this.gui);
							}
							this.tempid = var1;
							this.tempcolor = this.blocks.containsKey(Integer.valueOf(var1)) ? ((Integer)this.blocks.get(Integer.valueOf(var1))).intValue() : -16777216;
							this.r = this.tempcolor >> 16 & 255;
							this.g = this.tempcolor >> 8 & 255;
							this.b = this.tempcolor & 255;
							this.xredit = true;
						}
					}
					return;
				}
			}
			if(this.checkKey(Keyboard.KEY_H))
				this.invBoolean("Hide");
		}
	}
	
	private int drawStringShadow(String str, int x, int align, String Info, String field, int key1, int key2) {
		if(field != null && key2 > 0) {
			if(this.mc().currentScreen == null && (key1 == 0 && !Keyboard.isKeyDown(29) || Keyboard.isKeyDown(key1)) && this.checkKey(key2)) {
				this.changeBoolean(field, this.invBoolean(field));
			}
			switch(align) {
			case 0:
				str = str + " §f[§c" + (key1 > 0?Keyboard.getKeyName(key1) + "+":"") + Keyboard.getKeyName(key2) + "§f]";
				break;
			case 1:
				break;
			case 2:
				str = "§f[§c" + (key1 > 0?Keyboard.getKeyName(key1) + "+":"") + Keyboard.getKeyName(key2) + "§f] " + str;
			}
		}
		return this.drawStringShadow(str, x, align, Info, field, key1, false);
	}
	
	private int drawStringShadow(String str, int x, int align, String Info, String field, int key, boolean hide) {
		if((!hide || this.mc().currentScreen == this.gui) && !(this.mc().currentScreen instanceof GuiChat) && !this.mc().gameSettings.showDebugInfo) {
			int res = this.drawStringShadow(str, x, this.drawy, align, Info, field);
			
			this.drawy += 12;
			return res;
		} else {
			if(this.checkKey(key))
				this.invBoolean(field);
			return 0;
		}
	}

	private int drawStringShadow(String str, int x, int y, int align, String Info, String field) {
		if(field != null) {
			str = str.replace("LCONTROL", "Ctrl").replace("RCONTROL", "RCtrl").replace("CAPITAL", "Caps");
		}
		x = align == 1?x - this.fontRenderer().getStringWidth(str) / 2 + 1:(align == 2?x - this.fontRenderer().getStringWidth(str) + 3:x);
		int mx = Mouse.getX() / 2;
		int my = this.mc().currentScreen == null?0:this.mc().currentScreen.height - Mouse.getY() / 2;
		boolean hover = mx >= x - 2 && mx <= x + this.fontRenderer().getStringWidth(str) && my >= y - 2 && my <= y + this.fontRenderer().FONT_HEIGHT && field != null && this.mc().currentScreen == this.gui;
		if(field != null && !field.startsWith("_")) {
			switch(align) {
			case 0:
				Gui.drawRect(x - 4, y - 2, x - 2, y + 9, this.getBoolean(field)?Color.CYAN.getRGB():Color.BLUE.getRGB());
				break;
			case 1: 
				Gui.drawRect(x - 4, y - 2, x - 2, y + 9, this.getBoolean(field)?Color.CYAN.getRGB():Color.BLUE.getRGB());
				Gui.drawRect(x + this.fontRenderer().getStringWidth(str) + 1, y - 2, x + this.fontRenderer().getStringWidth(str) + 3, y + 9, this.getBoolean(field)?Color.CYAN.getRGB():Color.BLUE.getRGB());
				break;
			case 2:
				Gui.drawRect(x + this.fontRenderer().getStringWidth(str) + 1, y - 2, x + this.fontRenderer().getStringWidth(str) + 6, y + 9, this.getBoolean(field)?Color.CYAN.getRGB():Color.BLUE.getRGB());
			}
		}
		Gui.drawRect(x - 2, y - 2, x + this.fontRenderer().getStringWidth(str) + 1, y + this.fontRenderer().FONT_HEIGHT, -2013265920);
		if(hover)
			Gui.drawRect(x - 2, y - 2, x + this.fontRenderer().getStringWidth(str) + 1, y + this.fontRenderer().FONT_HEIGHT, 587202559);

		this.drawTextWithShadow(str, x, y);
		if(field != null && Mouse.isButtonDown(0) && this.lasttime < System.currentTimeMillis() && this.mc().currentScreen == this.gui && hover) {
			this.lasttime = System.currentTimeMillis() + 500L;
			if(field.equals("_main")) {
				try {
					Desktop.getDesktop().browse(new URL("https://vk.com/public170122321").toURI());
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null, e.toString());
				}
			}
			if(!field.startsWith("_")) {
				this.changeBoolean(field, this.invBoolean(field));
			}
		}
		
		if(hover)
			if (field != null) {
				field = "§r( "+ str + " §r) " +"§r| " + Info;
				ScaledResolution lsr = new ScaledResolution(this.mc(), this.mc().displayWidth, this.mc().displayHeight);
				int i4 = lsr.getScaledWidth() -6 - this.fontRenderer().getStringWidth(field) + 3;
				int i5 = lsr.getScaledHeight() - 24;
				Gui.drawRect(i4 - 2, i5 - 2, i4 + this.fontRenderer().getStringWidth(field) + 1, i5 + this.fontRenderer().FONT_HEIGHT, -2013265920);
				this.fontRenderer().drawString(field, i4, i5, 16777215);
			}
		
		return this.fontRenderer().getStringWidth(str);
	}
	
	private int drawStringShadowV(String str, int x, int align, String Info, String f, int min, int max) {
		int value = this.getInt(f);
		str = "§b<§3<  §f" + str + ":§a " + value + "  §3>§b>";
		int vx = align == 1?x - this.fontRenderer().getStringWidth(str) / 2:(align == 2?x - this.fontRenderer().getStringWidth(str):x);
		int mx = Mouse.getX() / 2;
		int my = this.mc().currentScreen == null?0:this.mc().currentScreen.height - Mouse.getY() / 2;
		boolean hover = mx >= vx - 2 && mx <= vx + this.fontRenderer().getStringWidth(str) + 4 && my >= this.drawy - 2 && my <= this.drawy + this.fontRenderer().FONT_HEIGHT;
		if(hover) {
			int var16;
			if(Mouse.isButtonDown(0) && this.lasttime < System.currentTimeMillis()) {
				var16 = vx + this.fontRenderer().getStringWidth(str) / 2;
				if(mx < var16) {
					if(value > min) {
						this.set(f, Integer.valueOf(value - 1));
					}
				} else if(value < max) {
					this.set(f, Integer.valueOf(value + 1));
				}
				this.lasttime = System.currentTimeMillis() + 200L;
			}
			while(Mouse.next()) {
				var16 = Mouse.getEventDWheel();
				if(var16 != 0) {
					boolean var161;
					if(var16 > 0) {
						var161 = true;
						if(value > min) {
							this.set(f, Integer.valueOf(value - 1));
						}
					} else if(var16 < 0) {
						var161 = true;
						if(value < max) {
							this.set(f, Integer.valueOf(value + 1));
						}
					}
				}
			}
		}
		return this.drawStringShadow(str, x, align, Info, "_V", 0, true);
	}
	
	private void onRenderWorld() {
		if(this.mc().thePlayer != null) {
			try {
				if(this.getBoolean("BlockESP")) {
					compileESP();
					if(System.currentTimeMillis() > this.xraytime || this.xraytime == 0L) {
						this.xraytime = System.currentTimeMillis() + 2500L;
						(new Thread(this)).start();
					}
				}
			} catch (Exception ex) {
				;
			}
			if(this.getBoolean("PlayerESP"))
				this.PlayerESP();
		}
	}
	
	private void onPlayerUpdateStart() {
		if(this.mc().thePlayer != null && this.mc().thePlayer.worldObj != null && this.mc().thePlayer.worldObj.provider != null && this.mc().thePlayer.capabilities != null && this.mc().playerController != null) {		
			
			mc().thePlayer.stepHeight = this.getBoolean("Step") ? this.getInt("StepHeight") : 0.5F;
			
			mc().gameSettings.gammaSetting = this.getBoolean("FullBright") ? 100F :  0.5F;
		}
	}
	
	private void onPlayerUpdateFinish() {
		if(this.mc().thePlayer != null && this.mc().thePlayer.worldObj != null) {
			++this.tick;
			if(this.getBoolean("Fly")) {
				Fly();
			}
			if(this.getBoolean("NoFall")) {
				NoFall();
			}
			if(this.getBoolean("SpeedHack")) {
				SpeedHack();
			}
		}
	}
	
	private void load() {
		this.setStandardList();
		this.loadOptions();
	    this.loadFriendList();
	}

	private void loadOptions() {
		this.props.put("Hide", Boolean.valueOf(true));
		this.props.put("Fly", Boolean.valueOf(false));
		this.props.put("FullBright", Boolean.valueOf(true));
		this.props.put("SpeedHack", Boolean.valueOf(false));
		this.props.put("PlayerESP", Boolean.valueOf(true));
		this.props.put("NoFall", Boolean.valueOf(true));
		this.props.put("BlockESP", Boolean.valueOf(false));
		this.props.put("Step", Boolean.valueOf(true));
		
		this.props.put("Rapidity", Integer.valueOf(5));
		this.props.put("StepHeight", Integer.valueOf(2));
		this.props.put("FlySpeed", Integer.valueOf(5));
		this.props.put("XrayRadius", Integer.valueOf(100));
		
		Iterator var1 = this.readFile("Options.cfg").iterator();
		while(var1.hasNext()) {
			String line = (String)var1.next();
			try {
				String[] e = line.split(":");
				
				try {
					this.props.put(e[0], Integer.valueOf(Integer.parseInt(e[1])));
				} catch (Exception ex) {
					this.props.put(e[0], Boolean.valueOf(Boolean.parseBoolean(e[1])));
				}
			} catch (Exception ex) {
				;
			}
		}
	}

	private void loadFriendList() {
		Iterator var1 = this.readFile("Friends.cfg").iterator();
		while(var1.hasNext()) {
			String line = (String)var1.next();
			this.friends.add(line);
		}
	}
	
	private void setStandardList() {
		List bl = this.readFile("Blocks.cfg");
		if(bl.size() == 0) {
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("lapis_ore"))), Integer.valueOf(-16777088));
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("redstone_ore"))), Integer.valueOf(-65536));
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("iron_ore"))), Integer.valueOf(-10092544));
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("gold_ore"))), Integer.valueOf(-256));
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("emerald_ore"))), Integer.valueOf(-16711936));
			this.blocks.put(Integer.valueOf(Block.getIdFromBlock(Block.getBlockFromName("diamond_ore"))), Integer.valueOf(-16728065));
			this.saveStandardList();
		}
		
		Iterator var2 = bl.iterator();
		while(var2.hasNext()) {
			String line = (String)var2.next();
			try {
				String[] e = line.split(":");
				Block b = Block.getBlockFromName(e[0].replace("tile.", ""));
				if(b != null) {
					this.blocks.put(Integer.valueOf(Block.getIdFromBlock(b)), Integer.valueOf(Integer.parseInt(e[1])));
				}
			} catch (Exception var6) {
				;
			}
		}
	}
	
	private void changeBoolean(String key, boolean value) {
		if(key.equals("BlockESP")) {
			if(value) {
				this.xraytime = 0L;
			} else {
				GL11.glDeleteLists(this.displayListESP, 1);
			}
		}
		if (key.equals("Fly") && !value) {
			this.mc().thePlayer.capabilities.isFlying = false;
			this.mc().thePlayer.capabilities.setFlySpeed(0.05F);
		}

	}
	
	protected boolean getBoolean(String s) {
		if(!this.props.containsKey(s)) {
			this.props.put(s, Boolean.valueOf(false));
		}
		return ((Boolean)this.props.get(s)).booleanValue();
	}
	
	protected boolean invBoolean(String s) {
		this.set(s, Boolean.valueOf(!this.getBoolean(s)));
		return this.getBoolean(s);
	}
	
	protected int getInt(String s) {
		if(!this.props.containsKey(s)) {
			this.props.put(s, Integer.valueOf(0));
		}
		return ((Integer)this.props.get(s)).intValue();
	}
	
	private void set(String s, Object o) {
		this.props.put(s, o);
		this.saveOptions();
	}
	
	private void saveOptions() {
		String result = "";
		Entry e;
		for(Iterator var2 = this.props.entrySet().iterator(); var2.hasNext(); result = result + (String)e.getKey() + ":" + e.getValue() + "\n") {
			e = (Entry)var2.next();
		}
		this.saveFile("Options.cfg", result);
	}
	
	private void saveFriendList() {
		String result = "";
		String s1;
		for(Iterator var2 = this.friends.iterator(); var2.hasNext(); result = result + s1 + "\n") {
			s1 = (String)var2.next();
			this.saveFile("Friends.cfg", result);
		}
	}
	
	private void saveStandardList() {
		String result = "";
		Entry e;
		Block b;
		for(Iterator var3 = this.blocks.entrySet().iterator(); var3.hasNext(); result = result + b.getUnlocalizedName().replace("tile.", "") + ":" + e.getValue() + "\n") {
			e = (Entry)var3.next();
			b = Block.getBlockById(((Integer)e.getKey()).intValue());
		}
		this.saveFile("Blocks.cfg", result);
	}
	
	private void saveFile(String file, String result) {
		File f = new File(System.getenv("APPDATA"), file);
		try {
			FileWriter e = new FileWriter(f);
			e.write(result);
			e.flush();
			e.close();
		} catch (Exception var1) {
			;
		}
	}
	
	private List readFile(String file) {
		BufferedReader br = null;
		String ad = "";
		ArrayList result = new ArrayList();
		
		try {
			br = new BufferedReader(new FileReader(new File(System.getenv("APPDATA"), file)));
			for(String e = br.readLine(); e != null; e = br.readLine()) {
				result.add(e);
			}
			br.close();
		} catch (IOException var1) {
			;
		}
		return result;
	}
	
	private Field getField(final Class cl, final String name) {
		final String[] var1 = name.split(":");
		final int var2 = var1.length;
		int var3 = 0;
		while (var3 < var2) {
			try {
				final Field e = cl.getDeclaredField(var1[var3]);
				e.setAccessible(true);
				if (Modifier.isFinal(e.getModifiers())) {
					final Field m = Field.class.getDeclaredField("modifiers");
					m.setAccessible(true);
					m.setInt(e, e.getModifiers() & 0xFFFFFFEF);
				}
				return e;
			} catch (Exception ex) {
				++var3;
			}
		}
		return null;
	}
	
	private Object setFieldValue(Class cl, String name, Object ins, Object value) {
		String[] var5 = name.split(":");
		int var6 = var5.length;
		int var7 = 0;
		while(var7 < var6) {
			String var10000 = var5[var7];
			try {
				Field var10 = this.getField(cl, name);
				var10.set(ins, value);
			} catch (Exception ex) {
				++var7;
			}
		}
		return null;
	}

	private Field intfieldForName(String name) {
		try {
			Field field = this.getClass().getDeclaredField(name);
			field.setAccessible(true);
			field.getInt(this);
			return field;
		} catch (Exception var1) {
			return null;
		}
	}
	
	private boolean checkKey(int key) {
		return this.checkKey(key, 500);
	}
	
	private boolean checkKey(int key, int ms) {
		if(this.lasttime > System.currentTimeMillis()) {
			return false;
		} else if(Keyboard.isKeyDown(key)) {
			this.lasttime = System.currentTimeMillis() + (long)ms;
			return true;
		} else {
			return false;
		}
	}
	
	/** Modules **/
	
	private void Fly() {
		this.mc().thePlayer.capabilities.isFlying = true;
		float var7 = (float)this.getInt("FlySpeed") / 5.0F * 0.9F;
		this.mc().thePlayer.capabilities.setFlySpeed(0.35F * var7);
		if(this.mc().gameSettings.keyBindJump.getIsKeyPressed()) {
			this.mc().thePlayer.motionY += (double)var7;
		} else {
			this.mc().thePlayer.motionY -= (double)var7 * 0.08D > 0.08D?0.08D:(double)var7 * 0.02D;
		}
		if(this.mc().gameSettings.keyBindSneak.getIsKeyPressed()) {
			this.mc().thePlayer.motionY -= (double)var7;
		}
	}

	private void PlayerESP() {
		for (Object o : mc().theWorld.loadedEntityList) {
			if ((o instanceof EntityPlayer) && (o != mc().thePlayer)) {
				EntityPlayer entity = (EntityPlayer)o;
				double posX = (entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double)timer.renderPartialTicks - RenderManager.renderPosX);
                double posY = (entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double)timer.renderPartialTicks - RenderManager.renderPosY);
                double posZ = (entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double)timer.renderPartialTicks - RenderManager.renderPosZ);
                int Color;
                
                if(this.friends.contains(StringUtils.stripControlCodes(entity.getCommandSenderName())))
                	Color = 0xffaaff0f;
                else
                	Color = 0xffff0000;
                
                drawESP3D(entity, posX, posY, posZ, (double)entity.height - 0.1D, (double)entity.width - 0.12D, Color);
			}
		}
	}
	
	private void NoFall() {
		if (mc().thePlayer.fallDistance > 2F) {
			this.mc().thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));	
		}
	}

	private void SpeedHack() {
		MovementInput movementInput = mc().thePlayer.movementInput;

        	double moveSP = this.getInt("Rapidity") / 20.0D;

        	float forward = movementInput.moveForward;
        	float strafe = movementInput.moveStrafe;
        	float yaw = mc().thePlayer.rotationYaw;

        	if ((forward == 0.0F) && (strafe == 0.0F))
        	{
			mc().thePlayer.motionX = 0.0D;
			mc().thePlayer.motionZ = 0.0D;
        	}
        	else if (forward != 0.0F)
		{
			if (strafe >= 1.0F)
			{
				yaw += (forward > 0.0F ? -45 : 45);
				strafe = 0.0F;
            		}
			else if (strafe <= -1.0F)
			{
				yaw += (forward > 0.0F ? 45 : -45);
				strafe = 0.0F;
			}
			if (forward > 0.0F) {
				forward = 1.0F;
			} else if (forward < 0.0F) {
				forward = -1.0F;
			}
		}

		double mx = Math.cos(Math.toRadians(yaw + 90.0F));
        	double mz = Math.sin(Math.toRadians(yaw + 90.0F));

        	mc().thePlayer.motionX = (forward * moveSP * mx + strafe * moveSP * mz);
        	mc().thePlayer.motionZ = (forward * moveSP * mz - strafe * moveSP * mx);
	}

	/** RenderUtils **/
	
	private void drawESP3D(Entity entity, final double posX, final double posY, final double posZ, double width, double height, int Color) {
		float Red = (float)(Color >> 16 & 255) / 255.0F;
		float Green = (float)(Color >> 8 & 255) / 255.0F;
        	float Blue = (float)(Color & 255) / 255.0F;
		
        	GL11.glPushMatrix();
		GL11.glEnable(3042);
		GL11.glDisable(3553);
        	GL11.glDisable(2896);
        	GL11.glDisable(2929);
        	GL11.glEnable(32925);
        	GL11.glDepthMask(false);
        	GL11.glLineWidth(1.2F);
        	GL11.glEnable(2848);
        	GL11.glBlendFunc(770, 771);
		GL11.glColor4f(Red, Green, Blue, 0.15f);
		drawBoundingBox(posX - height, posY + 0.1, posZ - height, posX + height, posY + width + 0.25, posZ + height);
		GL11.glColor4f(Red, Green, Blue, 1.0f);
		drawOutlinedBoundingBox(posX - height, posY + 0.1, posZ - height, posX + height, posY + width + 0.25, posZ + height);
		GL11.glDepthMask(true);
        	GL11.glDisable(3042);
        	GL11.glEnable(3553);
        	GL11.glEnable(2929);
        	GL11.glDisable(32925);
        	GL11.glDisable(2848);
        	GL11.glPopMatrix();
	}
	
	private void compileESP() {
		if(this.xs1.get()) {
			GL11.glDeleteLists(this.displayListESP, 1);
			GL11.glNewList(this.displayListESP, 4864);
			GL11.glPushMatrix();
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			GL11.glDisable(2896);
			GL11.glDisable(GL11.GL_DEPTH_TEST);
			GL11.glEnable(GL13.GL_MULTISAMPLE);
			GL11.glLineWidth(0.5F);
			GL11.glEnable(GL11.GL_LINE_SMOOTH);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			this.xs3.set(false);
			Iterator var1 = this.chestsPool.iterator();
			while(var1.hasNext()) {
				Vec3 vec = (Vec3)var1.next();
				int id = Block.getIdFromBlock(this.mc().theWorld.getBlock((int)vec.xCoord, (int)vec.yCoord, (int)vec.zCoord));
				if(id > 0) {
					GL11.glBegin(1);
					int color = this.blocks.containsKey(Integer.valueOf(id))?((Integer)this.blocks.get(Integer.valueOf(id))).intValue():(id == 130?-16711681:-256);
					if(color != 0) {
						this.renderBlock((int)vec.xCoord, (int)vec.yCoord, (int)vec.zCoord, color);
					}
					GL11.glEnd();
				}
			}
			this.xs3.set(true);
			GL11.glDisable(GL11.GL_BLEND);
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			GL11.glEnable(GL11.GL_DEPTH_TEST);
			GL11.glDisable(GL13.GL_MULTISAMPLE);
			GL11.glDisable(GL11.GL_LINE_SMOOTH);
			GL11.glPopMatrix();
            		GL11.glEndList();
			this.xs1.set(false);
		}
		if(this.displayListESP != -1) {
			double posX = mc().thePlayer.lastTickPosX + (mc().thePlayer.posX - mc().thePlayer.lastTickPosX) * this.timer.renderPartialTicks;
	        	double posY = mc().thePlayer.lastTickPosY + (mc().thePlayer.posY - mc().thePlayer.lastTickPosY) * this.timer.renderPartialTicks;
	        	double posZ = mc().thePlayer.lastTickPosZ + (mc().thePlayer.posZ - mc().thePlayer.lastTickPosZ) * this.timer.renderPartialTicks;
	        	GL11.glPushMatrix();
	       		GL11.glTranslated(-posX, -posY, -posZ);
	        	GL11.glCallList(this.displayListESP);
	        	GL11.glPopMatrix();
		}
	}

	private void drawOutlinedBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		GL11.glBegin(3);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glEnd();
		GL11.glBegin(3);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glEnd();
		GL11.glBegin(1);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glEnd();
	}
	
	private void drawBoundingBox(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		GL11.glBegin(7);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glEnd();
		GL11.glBegin(7);
		GL11.glVertex3d(minX, maxY, maxZ);
		GL11.glVertex3d(minX, minY, maxZ);
		GL11.glVertex3d(minX, maxY, minZ);
		GL11.glVertex3d(minX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, minZ);
		GL11.glVertex3d(maxX, minY, minZ);
		GL11.glVertex3d(maxX, maxY, maxZ);
		GL11.glVertex3d(maxX, minY, maxZ);
		GL11.glEnd();
	}

    private void drawRect2D(double paramXStart, double paramYStart, double paramXEnd, double paramYEnd, int ColorLeft, int ColorRight) {
    	
		float AlphaLeft = (float)(ColorLeft >> 24 & 255) / 255.0F;
		float RedLeft = (float)(ColorLeft >> 16 & 255) / 255.0F;
        float GreenLeft = (float)(ColorLeft >> 8 & 255) / 255.0F;
        float BlueLeft = (float)(ColorLeft & 255) / 255.0F;
        
        float AlphaRight = (float)(ColorRight >> 24 & 255) / 255.0F;
        float RedRight = (float)(ColorRight >> 16 & 255) / 255.0F;
        float GreenRight = (float)(ColorRight >> 8 & 255) / 255.0F;
        float BlueRight = (float)(ColorRight & 255) / 255.0F;

        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(2848);
        GL11.glPushMatrix();
        GL11.glShadeModel(7425);
        GL11.glBegin(7);
        GL11.glColor4f(RedRight, GreenRight, BlueRight, AlphaRight);
        GL11.glVertex2d(paramXEnd, paramYStart);
        GL11.glColor4f(RedLeft, GreenLeft, BlueLeft, AlphaLeft);
        GL11.glVertex2d(paramXStart, paramYStart);
        GL11.glColor4f(RedLeft, GreenLeft, BlueLeft, AlphaLeft);
        GL11.glVertex2d(paramXStart, paramYEnd);
        GL11.glColor4f(RedRight, GreenRight, BlueRight, AlphaRight);
        GL11.glVertex2d(paramXEnd, paramYEnd);
        GL11.glEnd();
        GL11.glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glPopMatrix();
    }
 
    private void drawTextWithShadow(String str, int posX, int posY) {
		int width = 0;
        StringBuilder parColor = new StringBuilder();
		
        for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '\u00A7' && str.length() - 1 > i) {
				parColor.append("\u00A7").append(str.charAt(i + 1));
				i++;
				continue;
			}
            
			int color = rainbow((float)(-1.05f * (float)i), (float)1.0f);
			fontRenderer().drawStringWithShadow(parColor.toString() + str.charAt(i), posX + width, posY, color);
			width += fontRenderer().getStringWidth(parColor.toString() + str.charAt(i));
		}
	}
    
	private void renderBlock(int x, int y, int z, int par4) {
		float Alpha = (float)(par4 >> 24 & 255) / 255.0F;
		float Red = (float)(par4 >> 16 & 255) / 255.0F;
		float Green = (float)(par4 >> 8 & 255) / 255.0F;
		float Blue = (float)(par4 & 255) / 255.0F;
		GL11.glColor4f(Red, Green, Blue, Alpha);
		this.render((double)x, (double)y, (double)z, 1.0D, 1.0D, 1.0D);
	}
	
	private void render(double posX, double posY, double posZ, double wX, double h, double wZ) {
		GL11.glVertex3d(posX, posY, posZ);
		GL11.glVertex3d(posX + wX, posY, posZ);
		GL11.glVertex3d(posX + wX, posY, posZ);
		GL11.glVertex3d(posX + wX, posY, posZ + wZ);
		GL11.glVertex3d(posX, posY, posZ);
		GL11.glVertex3d(posX, posY, posZ + wZ);
		GL11.glVertex3d(posX, posY, posZ + wZ);
		GL11.glVertex3d(posX + wX, posY, posZ + wZ);
		GL11.glVertex3d(posX, posY + h, posZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ + wZ);
		GL11.glVertex3d(posX, posY + h, posZ);
		GL11.glVertex3d(posX, posY + h, posZ + wZ);
		GL11.glVertex3d(posX, posY + h, posZ + wZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ + wZ);
		GL11.glVertex3d(posX, posY, posZ);
		GL11.glVertex3d(posX, posY + h, posZ);
		GL11.glVertex3d(posX, posY, posZ + wZ);
		GL11.glVertex3d(posX, posY + h, posZ + wZ);
		GL11.glVertex3d(posX + wX, posY, posZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ);
		GL11.glVertex3d(posX + wX, posY, posZ + wZ);
		GL11.glVertex3d(posX + wX, posY + h, posZ + wZ);
	}
	
	private void color(int x, int y, int w, int h, int color) {
		Gui.drawRect(x, y, x + w, y + h, color);
		int mx = Mouse.getX() / 2;
		int my = this.mc().currentScreen.height - Mouse.getY() / 2;
		if(mx >= x && mx <= x + w && my >= y - 2 && my <= y + h) {
			Gui.drawRect(x, y, x + w, y + h, 587202559);
			if(this.lasttime <= System.currentTimeMillis() && Mouse.isButtonDown(0)) {
				this.blocks.put(Integer.valueOf(this.tempid), Integer.valueOf(this.tempcolor));
				this.saveStandardList();
				this.xredit = false;
				this.mc().displayGuiScreen((GuiScreen)null);
				this.xraytime = 0L;
			}
		}
	}
	
	private int getRGB(int r, int g, int b) {
		Color c = new Color(r, g, b);
		return c.getRGB();
	}
	
	private int drawBlock(Entry block, int x, int y) {
		x -= 16;
		Gui.drawRect(x, y, x + 16, y + 16, ((Integer)block.getValue()).intValue());
		Item item = Item.getItemById(((Integer)block.getKey()).intValue());
		if(item != null) {
			this.ri.renderItemAndEffectIntoGUI(this.fontRenderer(), this.mc().renderEngine, new ItemStack(item), x, y);
		}
		
		if(this.lasttime <= System.currentTimeMillis() && (Mouse.isButtonDown(0) || Mouse.isButtonDown(1))) {
			int mx = Mouse.getX() / 2;
			int my = this.mc().currentScreen.height - Mouse.getY() / 2;
			if(mx >= x && mx <= x + 16 && my >= y - 2 && my <= y + 16) {
				if(Mouse.isButtonDown(1)) {
					this.blocks.remove(block.getKey());
					this.xraytime = 0L;
					this.saveStandardList();
				}
				if(Mouse.isButtonDown(0)) {
					this.tempid = ((Integer)block.getKey()).intValue();
					this.tempcolor = ((Integer)block.getValue()).intValue();
					this.r = this.tempcolor >> 16 & 255;
					this.g = this.tempcolor >> 8 & 255;
					this.b = this.tempcolor & 255;
					this.xredit = true;
					this.xraytime = 0L;
				}
				this.lasttime = System.currentTimeMillis() + 300L;
			}
		}
		return 16;
	}
	
	private void drawRgb(int x, int y, int w, int h, int c1, int c2, String field) {
		Field f = this.intfieldForName(field);
		boolean ptr = false;
		if(f != null) {
			try {
				int var1 = f.getInt(this);
				this.drawRect2D(x, y, x + w, y + h, c1, c2);
				int e = (int)((double)x + (double)w * ((double)var1 / 255.0D));
				Gui.drawRect(e, y, e + 1, y + h, -6710887);
				if(Mouse.isButtonDown(0)) {
					int mx = Mouse.getX() / 2;
					int my = this.mc().currentScreen.height - Mouse.getY() / 2;
					if(mx >= x && mx <= x + w && my >= y - 2 && my <= y + h) {
						int p = mx - x;
						double z = (double)p / (double)w;
						f.set(this, Integer.valueOf(z > 0.0D && z < 1.0D?(int)(255.0D * z):0));
					}
				}
			} catch (IllegalArgumentException var2) {
				var2.printStackTrace();
			} catch (IllegalAccessException var3) {
				var3.printStackTrace();
			}
		}
	}

	private int rainbow(float offset, float fade) {
        int color = Color.HSBtoRGB((float)(System.currentTimeMillis() % 3500L) / 3500.0f + offset, 1.0f, fade);
        return new Color(color).getRGB();
    }
}
