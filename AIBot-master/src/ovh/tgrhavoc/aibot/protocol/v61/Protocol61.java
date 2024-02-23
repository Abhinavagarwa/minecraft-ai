/*******************************************************************************
 *     Copyright (C) 2015 Jordan Dalton (jordan.8474@gmail.com)
 *
 *     This program is free software; you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation; either version 2 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License along
 *     with this program; if not, write to the Free Software Foundation, Inc.,
 *     51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package ovh.tgrhavoc.aibot.protocol.v61;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.Arrays;

import javax.crypto.SecretKey;

import ovh.tgrhavoc.aibot.MinecraftBot;
import ovh.tgrhavoc.aibot.auth.AuthService;
import ovh.tgrhavoc.aibot.auth.AuthenticationException;
import ovh.tgrhavoc.aibot.auth.InvalidSessionException;
import ovh.tgrhavoc.aibot.auth.Session;
import ovh.tgrhavoc.aibot.event.EventBus;
import ovh.tgrhavoc.aibot.event.EventHandler;
import ovh.tgrhavoc.aibot.event.EventListener;
import ovh.tgrhavoc.aibot.event.io.PacketProcessEvent;
import ovh.tgrhavoc.aibot.event.io.PacketReceivedEvent;
import ovh.tgrhavoc.aibot.event.io.PacketSentEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.ArmSwingEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BedLeaveEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BlockBreakCompleteEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BlockBreakEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BlockBreakStartEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BlockBreakStopEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.BlockPlaceEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.ChatSentEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.CrouchUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.EntityHitEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.EntityInteractEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.EntityUseEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.HandshakeEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.HeldItemChangeEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.HeldItemDropEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.InventoryChangeEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.InventoryCloseEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.ItemUseEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.PlayerUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.RequestDisconnectEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.RequestRespawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.client.SprintUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.BlockChangeEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.ChatReceivedEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.ChunkLoadEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityCollectEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityDeathEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityDespawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityDismountEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityEatEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityHeadRotateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityHurtEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityMetadataUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityMountEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityMoveEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityRotateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityStopEatingEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityTeleportEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntityVelocityEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.ExpOrbSpawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.ExperienceUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.HealthUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.KickEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.LivingEntitySpawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.LoginEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.ObjectEntitySpawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.PaintingSpawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.PlayerEquipmentUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.PlayerListRemoveEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.PlayerListUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.PlayerSpawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.RespawnEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.SignUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.SleepEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.TeleportEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.TileEntityUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.TimeUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.WindowCloseEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.WindowOpenEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.WindowSlotChangeEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.WindowTransactionCompleteEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.WindowUpdateEvent;
import ovh.tgrhavoc.aibot.event.protocol.server.EntitySpawnEvent.SpawnLocation;
import ovh.tgrhavoc.aibot.event.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnData;
import ovh.tgrhavoc.aibot.event.protocol.server.LivingEntitySpawnEvent.LivingEntitySpawnLocation;
import ovh.tgrhavoc.aibot.event.protocol.server.ObjectEntitySpawnEvent.ObjectSpawnData;
import ovh.tgrhavoc.aibot.event.protocol.server.ObjectEntitySpawnEvent.ThrownObjectSpawnData;
import ovh.tgrhavoc.aibot.event.protocol.server.PaintingSpawnEvent.PaintingSpawnLocation;
import ovh.tgrhavoc.aibot.event.protocol.server.RotatedEntitySpawnEvent.RotatedSpawnLocation;
import ovh.tgrhavoc.aibot.event.world.EditCommandBlockEvent;
import ovh.tgrhavoc.aibot.event.world.EditSignEvent;
import ovh.tgrhavoc.aibot.protocol.AbstractProtocol;
import ovh.tgrhavoc.aibot.protocol.ConnectionHandler;
import ovh.tgrhavoc.aibot.protocol.EncryptionUtil;
import ovh.tgrhavoc.aibot.protocol.Packet;
import ovh.tgrhavoc.aibot.protocol.ProtocolProvider;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet0KeepAlive;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet100OpenWindow;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet101CloseWindow;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet102WindowClick;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet103SetSlot;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet104WindowItems;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet105UpdateProgressbar;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet106Transaction;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet107CreativeSetSlot;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet108EnchantItem;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet10Flying;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet11PlayerPosition;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet12PlayerLook;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet130UpdateSign;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet131MapData;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet132TileEntityData;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet13PlayerLookMove;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet14BlockDig;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet15Place;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet16BlockItemSwitch;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet17Sleep;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet18Animation;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet19EntityAction;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet1Login;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet200Statistic;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet201PlayerInfo;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet202PlayerAbilities;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet203AutoComplete;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet204ClientInfo;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet205ClientCommand;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet206SetObjective;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet207SetScore;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet208SetDisplayObjective;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet209SetPlayerTeam;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet20NamedEntitySpawn;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet21PickupSpawn;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet22Collect;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet23VehicleSpawn;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet24MobSpawn;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet250CustomPayload;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet252SharedKey;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet253EncryptionKeyRequest;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet254ServerPing;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet255KickDisconnect;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet25EntityPainting;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet26EntityExpOrb;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet28EntityVelocity;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet29DestroyEntity;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet2Handshake;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet30Entity;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet31RelEntityMove;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet32EntityLook;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet33RelEntityMoveLook;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet34EntityTeleport;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet35EntityHeadRotation;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet38EntityStatus;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet39AttachEntity;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet3Chat;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet40EntityMetadata;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet41EntityEffect;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet42RemoveEntityEffect;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet43Experience;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet4UpdateTime;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet51MapChunk;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet52MultiBlockChange;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet53BlockChange;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet54PlayNoteBlock;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet55BlockDestroy;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet56MapChunks;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet5PlayerInventory;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet60Explosion;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet61DoorChange;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet62NamedSoundEffect;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet63Particle;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet6SpawnPosition;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet70ChangeGameState;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet71Weather;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet7UseEntity;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet8UpdateHealth;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet9Respawn;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet18Animation.Animation;
import ovh.tgrhavoc.aibot.protocol.v61.packets.Packet19EntityAction.Action;
import ovh.tgrhavoc.aibot.world.block.BlockLocation;
import ovh.tgrhavoc.aibot.world.entity.MainPlayerEntity;
import ovh.tgrhavoc.aibot.world.item.BasicItemStack;
import ovh.tgrhavoc.aibot.world.item.ItemStack;

public final class Protocol61 extends AbstractProtocol implements EventListener {
	public static final int VERSION = 61;
	public static final String VERSION_NAME = "1.5.2";

	private final MinecraftBot bot;

	public Protocol61(MinecraftBot bot) {
		super(VERSION);
		this.bot = bot;

		register(Packet0KeepAlive.class);
		register(Packet1Login.class);
		register(Packet2Handshake.class);
		register(Packet3Chat.class);
		register(Packet4UpdateTime.class);
		register(Packet5PlayerInventory.class);
		register(Packet6SpawnPosition.class);
		register(Packet7UseEntity.class);
		register(Packet8UpdateHealth.class);
		register(Packet9Respawn.class);
		register(Packet10Flying.class);
		register(Packet11PlayerPosition.class);
		register(Packet12PlayerLook.class);
		register(Packet13PlayerLookMove.class);
		register(Packet14BlockDig.class);
		register(Packet15Place.class);
		register(Packet16BlockItemSwitch.class);
		register(Packet17Sleep.class);
		register(Packet18Animation.class);
		register(Packet19EntityAction.class);
		register(Packet20NamedEntitySpawn.class);
		register(Packet21PickupSpawn.class);
		register(Packet22Collect.class);
		register(Packet23VehicleSpawn.class);
		register(Packet24MobSpawn.class);
		register(Packet25EntityPainting.class);
		register(Packet26EntityExpOrb.class);
		register(Packet28EntityVelocity.class);
		register(Packet29DestroyEntity.class);
		register(Packet30Entity.class);
		register(Packet31RelEntityMove.class);
		register(Packet32EntityLook.class);
		register(Packet33RelEntityMoveLook.class);
		register(Packet34EntityTeleport.class);
		register(Packet35EntityHeadRotation.class);
		register(Packet38EntityStatus.class);
		register(Packet39AttachEntity.class);
		register(Packet40EntityMetadata.class);
		register(Packet41EntityEffect.class);
		register(Packet42RemoveEntityEffect.class);
		register(Packet43Experience.class);
		register(Packet51MapChunk.class);
		register(Packet52MultiBlockChange.class);
		register(Packet53BlockChange.class);
		register(Packet54PlayNoteBlock.class);
		register(Packet55BlockDestroy.class);
		register(Packet56MapChunks.class);
		register(Packet60Explosion.class);
		register(Packet61DoorChange.class);
		register(Packet62NamedSoundEffect.class);
		register(Packet63Particle.class);
		register(Packet70ChangeGameState.class);
		register(Packet71Weather.class);
		register(Packet100OpenWindow.class);
		register(Packet101CloseWindow.class);
		register(Packet102WindowClick.class);
		register(Packet103SetSlot.class);
		register(Packet104WindowItems.class);
		register(Packet105UpdateProgressbar.class);
		register(Packet106Transaction.class);
		register(Packet107CreativeSetSlot.class);
		register(Packet108EnchantItem.class);
		register(Packet130UpdateSign.class);
		register(Packet131MapData.class);
		register(Packet132TileEntityData.class);
		register(Packet200Statistic.class);
		register(Packet201PlayerInfo.class);
		register(Packet202PlayerAbilities.class);
		register(Packet203AutoComplete.class);
		register(Packet204ClientInfo.class);
		register(Packet205ClientCommand.class);
		register(Packet206SetObjective.class);
		register(Packet207SetScore.class);
		register(Packet208SetDisplayObjective.class);
		register(Packet209SetPlayerTeam.class);
		register(Packet250CustomPayload.class);
		register(Packet252SharedKey.class);
		register(Packet253EncryptionKeyRequest.class);
		register(Packet254ServerPing.class);
		register(Packet255KickDisconnect.class);

		bot.getEventBus().register(this);
	}

	@EventHandler
	public void onHandshake(HandshakeEvent event) {
		bot.getConnectionHandler().sendPacket(new Packet2Handshake(VERSION, event.getSession().getUsername(), event.getServer(), event.getPort()));
	}

	@EventHandler
	public void onInventoryChange(InventoryChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet102WindowClick packet = new Packet102WindowClick();
		packet.windowId = event.getInventory().getWindowId();
		packet.slot = event.getSlot();
		packet.button = event.getButton();
		packet.action = event.getTransactionId();
		packet.item = event.getItem();
		packet.shift = event.isShiftHeld();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onInventoryClose(InventoryCloseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet101CloseWindow(event.getInventory().getWindowId()));
	}

	@EventHandler
	public void onHeldItemDrop(HeldItemDropEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet14BlockDig(event.isEntireStack() ? 3 : 4, 0, 0, 0, 0));
	}

	@EventHandler
	public void onHeldItemChange(HeldItemChangeEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet16BlockItemSwitch(event.getNewSlot()));
	}

	@EventHandler
	public void onEntityInteract(EntityInteractEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		int mode;
		if(event instanceof EntityHitEvent)
			mode = 1;
		else if(event instanceof EntityUseEvent)
			mode = 0;
		else
			return;
		handler.sendPacket(new Packet7UseEntity(bot.getPlayer().getId(), event.getEntity().getId(), mode));
	}

	@EventHandler
	public void onArmSwing(ArmSwingEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet18Animation(bot.getPlayer().getId(), Animation.SWING_ARM));
	}

	@EventHandler
	public void onCrouchUpdate(CrouchUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), event.isCrouching() ? Action.CROUCH : Action.UNCROUCH));
	}

	@EventHandler
	public void onSprintUpdate(SprintUpdateEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), event.isSprinting() ? Action.START_SPRINTING : Action.STOP_SPRINTING));
	}

	@EventHandler
	public void onBedLeave(BedLeaveEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet19EntityAction(bot.getPlayer().getId(), Action.LEAVE_BED));
	}

	@EventHandler
	public void onChatSent(ChatSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet3Chat(event.getMessage()));
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		int code;
		if(event instanceof BlockBreakStartEvent)
			code = 0;
		else if(event instanceof BlockBreakStopEvent)
			code = 1;
		else if(event instanceof BlockBreakCompleteEvent)
			code = 2;
		else
			return;
		handler.sendPacket(new Packet14BlockDig(code, event.getX(), event.getY(), event.getZ(), event.getFace()));
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet15Place packet = new Packet15Place();
		packet.xPosition = event.getX();
		packet.yPosition = event.getY();
		packet.zPosition = event.getZ();
		packet.direction = event.getFace();
		packet.xOffset = event.getXOffset();
		packet.yOffset = event.getYOffset();
		packet.zOffset = event.getZOffset();
		packet.itemStack = event.getItem();
		handler.sendPacket(packet);
		if(event.getItem() != null) {
			EventBus eventBus = bot.getEventBus();
			if(event.getItem().getId() == 323)
				eventBus.fire(new EditSignEvent(new BlockLocation(event.getX(), event.getY(), event.getZ())));
			else if(event.getItem().getId() == 137)
				eventBus.fire(new EditCommandBlockEvent(new BlockLocation(event.getX(), event.getY(), event.getZ())));
		}
	}

	@EventHandler
	public void onPlayerUpdate(PlayerUpdateEvent event) {
		MainPlayerEntity player = event.getEntity();
		double x = player.getX(), y = player.getY(), z = player.getZ(), yaw = player.getYaw(), pitch = player.getPitch();
		boolean move = x != player.getLastX() || y != player.getLastY() || z != player.getLastZ();
		boolean rotate = yaw != player.getLastYaw() || pitch != player.getLastPitch();
		boolean onGround = player.isOnGround();
		Packet10Flying packet;
		if(move && rotate)
			packet = new Packet13PlayerLookMove(x, y, y + 1.62000000476837, z, (float) yaw, (float) pitch, onGround);
		else if(move)
			packet = new Packet11PlayerPosition(x, y, y + 1.62000000476837, z, onGround);
		else if(rotate)
			packet = new Packet12PlayerLook((float) yaw, (float) pitch, onGround);
		else
			packet = new Packet10Flying(onGround);
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(packet);
	}

	@EventHandler
	public void onItemUse(ItemUseEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet15Place placePacket = new Packet15Place();
		placePacket.xPosition = -1;
		placePacket.yPosition = -1;
		placePacket.zPosition = -1;
		placePacket.itemStack = event.getItem();
		if(placePacket.itemStack != null && placePacket.itemStack.getId() == 346)
			placePacket.direction = 255;
		else
			placePacket.direction = -1;
		handler.sendPacket(placePacket);
	}

	@EventHandler
	public void onRequestRespawn(RequestRespawnEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet205ClientCommand(1));
	}

	@EventHandler
	public void onRequestDisconnect(RequestDisconnectEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		handler.sendPacket(new Packet255KickDisconnect(event.getReason()));
	}

	@EventHandler
	public void onPacketProcess(PacketProcessEvent event) {
		Packet packet = event.getPacket();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();
		EventBus eventBus = bot.getEventBus();

		switch(packet.getId()) {
		// Awkward brace style to prevent accidental field name overlap, and
		// switch rather than instanceof for efficiency
		case 0: {
			Packet0KeepAlive keepAlivePacket = (Packet0KeepAlive) packet;
			connectionHandler.sendPacket(keepAlivePacket);
			break;
		}
		case 1: {
			Packet1Login loginPacket = (Packet1Login) packet;
			eventBus.fire(new LoginEvent(	loginPacket.playerId,
											loginPacket.worldType,
											loginPacket.gameMode,
											loginPacket.dimension,
											loginPacket.difficulty,
											loginPacket.worldHeight,
											loginPacket.maxPlayers));
			connectionHandler.sendPacket(new Packet204ClientInfo("en_US", 1, 0, true, 2, true));
			break;
		}
		case 3: {
			Packet3Chat chatPacket = (Packet3Chat) packet;
			eventBus.fire(new ChatReceivedEvent(chatPacket.message));
			break;
		}
		case 4: {
			Packet4UpdateTime timePacket = (Packet4UpdateTime) packet;
			eventBus.fire(new TimeUpdateEvent(timePacket.time, timePacket.otherTime));
			break;
		}
		case 5: {
			Packet5PlayerInventory inventoryPacket = (Packet5PlayerInventory) packet;
			eventBus.fire(new PlayerEquipmentUpdateEvent(inventoryPacket.entityID, inventoryPacket.slot, inventoryPacket.item));
			break;
		}
		case 8: {
			Packet8UpdateHealth updateHealthPacket = (Packet8UpdateHealth) packet;
			eventBus.fire(new HealthUpdateEvent(updateHealthPacket.healthMP, updateHealthPacket.food, updateHealthPacket.foodSaturation));
			break;
		}
		case 9: {
			Packet9Respawn respawnPacket = (Packet9Respawn) packet;
			eventBus.fire(new RespawnEvent(	respawnPacket.respawnDimension,
											respawnPacket.difficulty,
											respawnPacket.gameMode,
											respawnPacket.worldType,
											respawnPacket.worldHeight));
			break;
		}
		case 13: {
			Packet13PlayerLookMove lookMovePacket = (Packet13PlayerLookMove) packet;
			connectionHandler.sendPacket(lookMovePacket);
			eventBus.fire(new TeleportEvent(lookMovePacket.x,
											lookMovePacket.y,
											lookMovePacket.z,
											lookMovePacket.stance,
											lookMovePacket.yaw,
											lookMovePacket.pitch));
			break;
		}
		case 17: {
			Packet17Sleep sleepPacket = (Packet17Sleep) packet;
			eventBus.fire(new SleepEvent(sleepPacket.entityID, sleepPacket.bedX, sleepPacket.bedY, sleepPacket.bedZ));
			break;
		}
		case 18: {
			Packet18Animation animationPacket = (Packet18Animation) packet;
			if(animationPacket.animation == Animation.EAT_FOOD)
				eventBus.fire(new EntityEatEvent(animationPacket.entityId));
			break;
		}
		case 20: {
			Packet20NamedEntitySpawn spawnPacket = (Packet20NamedEntitySpawn) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(	spawnPacket.xPosition / 32D,
																		spawnPacket.yPosition / 32D,
																		spawnPacket.zPosition / 32D,
																		(spawnPacket.rotation * 360) / 256F,
																		(spawnPacket.pitch * 360) / 256F);
			ItemStack heldItem = new BasicItemStack(spawnPacket.currentItem, 1, 0);
			eventBus.fire(new PlayerSpawnEvent(spawnPacket.entityId, spawnPacket.name, heldItem, location, spawnPacket.data));
			break;
		}
		case 22: {
			Packet22Collect collectPacket = (Packet22Collect) packet;
			eventBus.fire(new EntityCollectEvent(collectPacket.collectedEntityId, collectPacket.collectorEntityId));
			break;
		}
		case 23: {
			Packet23VehicleSpawn spawnPacket = (Packet23VehicleSpawn) packet;
			RotatedSpawnLocation location = new RotatedSpawnLocation(	spawnPacket.xPosition / 32D,
																		spawnPacket.yPosition / 32D,
																		spawnPacket.zPosition / 32D,
																		(spawnPacket.yaw * 360) / 256F,
																		(spawnPacket.pitch * 360) / 256F);
			ObjectSpawnData spawnData;
			if(spawnPacket.throwerEntityId != 0)
				spawnData = new ThrownObjectSpawnData(	spawnPacket.type,
														spawnPacket.throwerEntityId,
														spawnPacket.speedX / 8000D,
														spawnPacket.speedY / 8000D,
														spawnPacket.speedZ / 8000D);
			else
				spawnData = new ObjectSpawnData(spawnPacket.type);
			eventBus.fire(new ObjectEntitySpawnEvent(spawnPacket.entityId, location, spawnData));
			break;
		}
		case 24: {
			Packet24MobSpawn spawnPacket = (Packet24MobSpawn) packet;
			LivingEntitySpawnLocation location = new LivingEntitySpawnLocation(	spawnPacket.xPosition / 32D,
																				spawnPacket.yPosition / 32D,
																				spawnPacket.zPosition / 32D,
																				(spawnPacket.yaw * 360) / 256F,
																				(spawnPacket.pitch * 360) / 256F,
																				(spawnPacket.headYaw * 360) / 256F);
			LivingEntitySpawnData data = new LivingEntitySpawnData(	spawnPacket.type,
																	spawnPacket.velocityX / 8000D,
																	spawnPacket.velocityY / 8000D,
																	spawnPacket.velocityZ / 8000D);
			eventBus.fire(new LivingEntitySpawnEvent(spawnPacket.entityId, location, data, spawnPacket.metadata));
			break;
		}
		case 25: {
			Packet25EntityPainting spawnPacket = (Packet25EntityPainting) packet;
			PaintingSpawnLocation location = new PaintingSpawnLocation(	spawnPacket.xPosition,
																		spawnPacket.yPosition,
																		spawnPacket.zPosition,
																		spawnPacket.direction);
			eventBus.fire(new PaintingSpawnEvent(spawnPacket.entityId, location, spawnPacket.title));
			break;
		}
		case 26: {
			Packet26EntityExpOrb spawnPacket = (Packet26EntityExpOrb) packet;
			SpawnLocation location = new SpawnLocation(spawnPacket.posX / 32D, spawnPacket.posY / 32D, spawnPacket.posZ / 32D);
			eventBus.fire(new ExpOrbSpawnEvent(spawnPacket.entityId, location, spawnPacket.xpValue));
			break;
		}
		case 28: {
			Packet28EntityVelocity velocityPacket = (Packet28EntityVelocity) packet;
			eventBus.fire(new EntityVelocityEvent(	velocityPacket.entityId,
													velocityPacket.motionX / 8000D,
													velocityPacket.motionY / 8000D,
													velocityPacket.motionZ / 8000D));
			break;
		}
		case 29: {
			Packet29DestroyEntity destroyEntityPacket = (Packet29DestroyEntity) packet;
			for(int id : destroyEntityPacket.entityIds)
				eventBus.fire(new EntityDespawnEvent(id));
			break;
		}
		case 30:
		case 31:
		case 32:
		case 33: {
			Packet30Entity entityPacket = (Packet30Entity) packet;
			if(packet instanceof Packet31RelEntityMove || packet instanceof Packet33RelEntityMoveLook)
				eventBus.fire(new EntityMoveEvent(	entityPacket.entityId,
													entityPacket.xPosition / 32D,
													entityPacket.yPosition / 32D,
													entityPacket.zPosition / 32D));
			if(packet instanceof Packet32EntityLook || packet instanceof Packet33RelEntityMoveLook)
				eventBus.fire(new EntityRotateEvent(entityPacket.entityId, (entityPacket.yaw * 360) / 256F, (entityPacket.pitch * 360) / 256F));
			break;
		}
		case 34: {
			Packet34EntityTeleport teleportPacket = (Packet34EntityTeleport) packet;
			eventBus.fire(new EntityTeleportEvent(	teleportPacket.entityId,
													teleportPacket.xPosition / 32D,
													teleportPacket.yPosition / 32D,
													teleportPacket.zPosition / 32D,
													(teleportPacket.yaw * 360) / 256F,
													(teleportPacket.pitch * 360) / 256F));
			break;
		}
		case 35: {
			Packet35EntityHeadRotation headRotatePacket = (Packet35EntityHeadRotation) packet;
			eventBus.fire(new EntityHeadRotateEvent(headRotatePacket.entityId, (headRotatePacket.headRotationYaw * 360) / 256F));
			break;
		}
		case 38: {
			Packet38EntityStatus statusPacket = (Packet38EntityStatus) packet;
			if(statusPacket.entityStatus == 2)
				eventBus.fire(new EntityHurtEvent(statusPacket.entityId));
			else if(statusPacket.entityStatus == 3)
				eventBus.fire(new EntityDeathEvent(statusPacket.entityId));
			else if(statusPacket.entityStatus == 9)
				eventBus.fire(new EntityStopEatingEvent(statusPacket.entityId));
			break;
		}
		case 39: {
			Packet39AttachEntity attachEntityPacket = (Packet39AttachEntity) packet;
			if(attachEntityPacket.vehicleEntityId != -1)
				eventBus.fire(new EntityMountEvent(attachEntityPacket.entityId, attachEntityPacket.vehicleEntityId));
			else
				eventBus.fire(new EntityDismountEvent(attachEntityPacket.entityId));
			break;
		}
		case 40: {
			Packet40EntityMetadata metadataPacket = (Packet40EntityMetadata) packet;
			eventBus.fire(new EntityMetadataUpdateEvent(metadataPacket.entityId, metadataPacket.metadata));
			break;
		}
		case 43: {
			Packet43Experience experiencePacket = (Packet43Experience) packet;
			eventBus.fire(new ExperienceUpdateEvent(experiencePacket.experienceLevel, experiencePacket.experienceTotal));
			break;
		}
		case 51: {
			Packet51MapChunk mapChunkPacket = (Packet51MapChunk) packet;
			processChunk(	mapChunkPacket.x,
							mapChunkPacket.z,
							mapChunkPacket.chunkData,
							mapChunkPacket.bitmask,
							mapChunkPacket.additionalBitmask,
							true,
							mapChunkPacket.biomes);
			break;
		}
		case 52: {
			Packet52MultiBlockChange multiBlockChangePacket = (Packet52MultiBlockChange) packet;
			if(multiBlockChangePacket.metadataArray == null)
				return;
			DataInputStream in = new DataInputStream(new ByteArrayInputStream(multiBlockChangePacket.metadataArray));
			try {
				for(int i = 0; i < multiBlockChangePacket.size; i++) {
					short word0 = in.readShort();
					short word1 = in.readShort();
					int id = (word1 & 0xfff) >> 4;
					int metadata = word1 & 0xf;
					int x = word0 >> 12 & 0xf;
					int z = word0 >> 8 & 0xf;
					int y = word0 & 0xff;
					eventBus.fire(new BlockChangeEvent(id, metadata, (multiBlockChangePacket.xPosition * 16) + x, y, (multiBlockChangePacket.zPosition * 16)
							+ z));
				}
			} catch(IOException exception) {
				exception.printStackTrace();
			}
			break;
		}
		case 53: {
			Packet53BlockChange blockChangePacket = (Packet53BlockChange) packet;
			eventBus.fire(new BlockChangeEvent(	blockChangePacket.type,
												blockChangePacket.metadata,
												blockChangePacket.xPosition,
												blockChangePacket.yPosition,
												blockChangePacket.zPosition));
			break;
		}
		case 56: {
			if(bot.isMovementDisabled())
				return;
			Packet56MapChunks chunkPacket = (Packet56MapChunks) packet;
			for(int i = 0; i < chunkPacket.primaryBitmap.length; i++)
				processChunk(	chunkPacket.chunkX[i],
								chunkPacket.chunkZ[i],
								chunkPacket.chunkData[i],
								chunkPacket.primaryBitmap[i],
								chunkPacket.secondaryBitmap[i],
								chunkPacket.skylight,
								true);
			break;
		}
		case 100: {
			Packet100OpenWindow openWindowPacket = (Packet100OpenWindow) packet;
			eventBus.fire(new WindowOpenEvent(openWindowPacket.windowId, openWindowPacket.inventoryType, openWindowPacket.flag ? openWindowPacket.windowTitle
					: "", openWindowPacket.slotsCount));
			break;
		}
		case 101: {
			Packet101CloseWindow closeWindowPacket = (Packet101CloseWindow) packet;
			eventBus.fire(new WindowCloseEvent(closeWindowPacket.windowId));
			break;
		}
		case 103: {
			Packet103SetSlot slotPacket = (Packet103SetSlot) packet;
			eventBus.fire(new WindowSlotChangeEvent(slotPacket.windowId, slotPacket.itemSlot, slotPacket.itemStack));
			break;
		}
		case 104: {
			Packet104WindowItems itemsPacket = (Packet104WindowItems) packet;
			eventBus.fire(new WindowUpdateEvent(itemsPacket.windowId, itemsPacket.itemStack));
			break;
		}
		case 132: {
			Packet132TileEntityData tileEntityPacket = (Packet132TileEntityData) packet;
			eventBus.fire(new TileEntityUpdateEvent(tileEntityPacket.xPosition,
													tileEntityPacket.yPosition,
													tileEntityPacket.zPosition,
													tileEntityPacket.actionType,
													tileEntityPacket.compound));
			break;
		}
		case 130: {
			Packet130UpdateSign signPacket = (Packet130UpdateSign) packet;
			eventBus.fire(new SignUpdateEvent(signPacket.x, signPacket.y, signPacket.z, signPacket.text));
			break;
		}
		case 201: {
			Packet201PlayerInfo infoPacket = (Packet201PlayerInfo) packet;
			if(infoPacket.isConnected)
				eventBus.fire(new PlayerListUpdateEvent(infoPacket.playerName, infoPacket.ping));
			else
				eventBus.fire(new PlayerListRemoveEvent(infoPacket.playerName));
			break;
		}
		case 252: {
			connectionHandler.sendPacket(new Packet205ClientCommand(0));
			break;
		}
		case 253: {
			handleServerAuthData((Packet253EncryptionKeyRequest) packet);
			break;
		}
		case 255: {
			Packet255KickDisconnect kickPacket = (Packet255KickDisconnect) packet;
			eventBus.fire(new KickEvent(kickPacket.reason));
			break;
		}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void handleServerAuthData(Packet253EncryptionKeyRequest keyRequest) {
		String serverId = keyRequest.serverId.trim();
		PublicKey publicKey = keyRequest.publicKey;
		SecretKey secretKey = EncryptionUtil.generateSecretKey();
		ConnectionHandler connectionHandler = bot.getConnectionHandler();

		if(!serverId.equals("-")) {
			try {
				AuthService service = bot.getAuthService();
				Session session = bot.getSession();

				String hash = new BigInteger(EncryptionUtil.encrypt(serverId, publicKey, secretKey)).toString(16);
				service.authenticate(service.validateSession(session), hash);
			} catch(InvalidSessionException exception) {
				connectionHandler.disconnect("Session invalid: " + exception);
			} catch(NoSuchAlgorithmException | UnsupportedEncodingException exception) {
				connectionHandler.disconnect("Unable to hash: " + exception);
			} catch(AuthenticationException | IOException exception) {
				connectionHandler.disconnect("Unable to authenticate: " + exception);
			}
		}

		connectionHandler.sendPacket(new Packet252SharedKey(secretKey, publicKey, keyRequest.verifyToken));
	}

	private void processChunk(int x, int z, byte[] data, int bitmask, int additionalBitmask, boolean addSkylight, boolean addBiomes) {
		if(data == null)
			return;
		int chunksChanged = 0;
		for(int i = 0; i < 16; i++)
			if((bitmask & (1 << i)) != 0)
				chunksChanged++;
		if(chunksChanged == 0)
			return;
		int[] yValues = new int[chunksChanged];
		byte[][] allBlocks = new byte[chunksChanged][], allMetadata = new byte[chunksChanged][], allLight = new byte[chunksChanged][], allSkylight = new byte[chunksChanged][];
		byte[] biomes = new byte[256];
		int i = 0;
		for(int y = 0; y < 16; y++) {
			if((bitmask & (1 << y)) == 0)
				continue;
			yValues[i] = y;
			int dataIndex = i * 4096;
			byte[] blocks = Arrays.copyOfRange(data, dataIndex, dataIndex + 4096);
			dataIndex += ((chunksChanged - i) * 4096) + (i * 2048);
			byte[] metadata = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);
			dataIndex += chunksChanged * 2048;
			byte[] light = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);
			dataIndex += chunksChanged * 2048;
			byte[] skylight = null;
			if(addSkylight)
				skylight = Arrays.copyOfRange(data, dataIndex, dataIndex + 2048);

			byte[] perBlockMetadata = new byte[4096];
			byte[] perBlockLight = new byte[4096];
			byte[] perBlockSkylight = new byte[4096];

			for(int j = 0; j < 2048; j++) {
				int k = j * 2;
				perBlockMetadata[k] = (byte) (metadata[j] & 0x0F);
				perBlockLight[k] = (byte) (light[j] & 0x0F);
				if(addSkylight)
					perBlockSkylight[k] = (byte) (skylight[j] & 0x0F);
				k++;
				perBlockMetadata[k] = (byte) (metadata[j] >> 4);
				perBlockLight[k] = (byte) (light[j] >> 4);
				if(addSkylight)
					perBlockSkylight[k] = (byte) (skylight[j] >> 4);
			}

			allBlocks[i] = blocks;
			allMetadata[i] = perBlockMetadata;
			allLight[i] = perBlockLight;
			allSkylight[i] = perBlockSkylight;
			i++;
		}
		System.arraycopy(data, data.length - 256, biomes, 0, 256);
		EventBus eventBus = bot.getEventBus();
		for(i = 0; i < chunksChanged; i++) {
			ChunkLoadEvent event = new ChunkLoadEvent(x, yValues[i], z, allBlocks[i], allMetadata[i], allLight[i], allSkylight[i], biomes.clone());
			eventBus.fire(event);
		}
	}

	@EventHandler
	public void onPacketReceived(PacketReceivedEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof Packet252SharedKey) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() == null) {
				handler.disconnect("No shared key!");
				return;
			}
			if(!handler.isDecrypting())
				handler.enableDecryption();
		} else if(packet instanceof Packet106Transaction) {
			Packet106Transaction transactionPacket = (Packet106Transaction) packet;
			bot.getEventBus().fire(new WindowTransactionCompleteEvent(transactionPacket.windowId, transactionPacket.shortWindowId, transactionPacket.accepted));
			transactionPacket.accepted = true;
			handler.sendPacket(transactionPacket);
		}
	}

	@EventHandler
	public void onPacketSent(PacketSentEvent event) {
		ConnectionHandler handler = bot.getConnectionHandler();
		Packet packet = event.getPacket();
		if(packet instanceof Packet252SharedKey) {
			if(!handler.supportsEncryption()) {
				handler.disconnect("ConnectionHandler does not support encryption!");
				return;
			}
			if(handler.getSharedKey() != null) {
				handler.disconnect("Shared key already installed!");
				return;
			}
			if(!handler.isEncrypting()) {
				handler.setSharedKey(((Packet252SharedKey) packet).sharedKey);
				handler.enableEncryption();
			}
		}
	}

	public static final class Provider extends ProtocolProvider<Protocol61> {
		@Override
		public Protocol61 getProtocolInstance(MinecraftBot bot) {
			return new Protocol61(bot);
		}

		@Override
		public int getSupportedVersion() {
			return VERSION;
		}

		@Override
		public String getMinecraftVersion() {
			return VERSION_NAME;
		}
	}
}
