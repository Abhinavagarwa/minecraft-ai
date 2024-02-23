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
package ovh.tgrhavoc.aibot.protocol.v4x.play.server;

import java.io.*;

import ovh.tgrhavoc.aibot.protocol.*;
import ovh.tgrhavoc.aibot.protocol.ProtocolX.State;

public class PacketS0D_CollectItem extends AbstractPacketX implements ReadablePacket {
	private int itemEntityId, collectorEntityId;

	public PacketS0D_CollectItem() {
		super(0x0D, State.PLAY, Direction.DOWNSTREAM);
	}

	@Override
	public void readData(DataInputStream in) throws IOException {
		itemEntityId = in.readInt();
		collectorEntityId = in.readInt();
	}

	public int getItemEntityId() {
		return itemEntityId;
	}

	public int getCollectorEntityId() {
		return collectorEntityId;
	}
}
